package com.zestic.crypto.digest;

import com.zestic.core.io.FileUtil;
import com.zestic.core.io.IORuntimeException;
import com.zestic.core.io.IoUtil;
import com.zestic.core.util.ArrayUtil;
import com.zestic.core.util.CharsetUtil;
import com.zestic.core.util.HexUtil;
import com.zestic.core.util.StrUtil;
import com.zestic.crypto.CryptoException;
import com.zestic.crypto.SecureUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

public class Digester implements Serializable {
	private static final long serialVersionUID = 1L;

	private MessageDigest digest;
	protected byte[] salt;
	protected int saltPosition;
	protected int digestCount;

	// ------------------------------------------------------------------------------------------- Constructor start
	public Digester(DigestAlgorithm algorithm) {
		this(algorithm.getValue());
	}
	public Digester(String algorithm) {
		this(algorithm, null);
	}
	public Digester(DigestAlgorithm algorithm, Provider provider) {
		init(algorithm.getValue(), provider);
	}
	public Digester(String algorithm, Provider provider) {
		init(algorithm, provider);
	}
	// ------------------------------------------------------------------------------------------- Constructor end

	public Digester init(String algorithm, Provider provider) {
		if(null == provider) {
			this.digest = SecureUtil.createMessageDigest(algorithm);
		}else {
			try {
				this.digest = MessageDigest.getInstance(algorithm, provider);
			} catch (NoSuchAlgorithmException e) {
				throw new CryptoException(e);
			}
		}
		return this;
	}
	
	public Digester setSalt(byte[] salt) {
		this.salt = salt;
		return this;
	}

	public Digester setSaltPosition(int saltPosition) {
		this.saltPosition = saltPosition;
		return this;
	}

	public Digester setDigestCount(int digestCount) {
		this.digestCount = digestCount;
		return this;
	}

	public Digester reset() {
		this.digest.reset();
		return this;
	}

	// ------------------------------------------------------------------------------------------- Digest
	public byte[] digest(String data, String charsetName) {
		return digest(data, CharsetUtil.charset(charsetName));
	}
	
	public byte[] digest(String data, Charset charset) {
		return digest(StrUtil.bytes(data, charset));
	}

	public byte[] digest(String data) {
		return digest(data, CharsetUtil.CHARSET_UTF_8);
	}

	public String digestHex(String data, String charsetName) {
		return digestHex(data, CharsetUtil.charset(charsetName));
	}
	
	public String digestHex(String data, Charset charset) {
		return HexUtil.encodeHexStr(digest(data, charset));
	}

	public String digestHex(String data) {
		return digestHex(data, CharsetUtil.UTF_8);
	}

	public byte[] digest(File file) throws CryptoException {
		InputStream in = null;
		try {
			in = FileUtil.getInputStream(file);
			return digest(in);
		} finally {
			IoUtil.close(in);
		}
	}

	public String digestHex(File file) {
		return HexUtil.encodeHexStr(digest(file));
	}

	public byte[] digest(byte[] data) {
		byte[] result;
		if (this.saltPosition <= 0) {
			// Add salt in the beginning, ignore empty salt value automatically
			result = doDigest(this.salt, data);
		} else if (this.saltPosition >= data.length) {
			// Salt salt at the end, automatically ignore empty values
			result = doDigest(data, this.salt);
		} else if (ArrayUtil.isNotEmpty(this.salt)) {
			// Add salt in the middle
			this.digest.update(data, 0, this.saltPosition);
			this.digest.update(this.salt);
			this.digest.update(data, this.saltPosition, data.length - this.saltPosition);
			result = this.digest.digest();
		} else {
			// There is no salt
			result = doDigest(data);
		}
		return resetAndRepeatDigest(result);
	}

	public String digestHex(byte[] data) {
		return HexUtil.encodeHexStr(digest(data));
	}

	public byte[] digest(InputStream data) {
		return digest(data, IoUtil.DEFAULT_BUFFER_SIZE);
	}

	public String digestHex(InputStream data) {
		return HexUtil.encodeHexStr(digest(data));
	}

	public byte[] digest(InputStream data, int bufferLength) throws IORuntimeException {
		if (bufferLength < 1) {
			bufferLength = IoUtil.DEFAULT_BUFFER_SIZE;
		}
		
		byte[] result;
		try {
			if (ArrayUtil.isEmpty(this.salt)) {
				result = digestWithoutSalt(data, bufferLength);
			} else {
				result = digestWithSalt(data, bufferLength);
			}
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
		
		return resetAndRepeatDigest(result);
	}

	public String digestHex(InputStream data, int bufferLength) {
		return HexUtil.encodeHexStr(digest(data, bufferLength));
	}

	public MessageDigest getDigest() {
		return digest;
	}

	public int getDigestLength() {
		return this.digest.getDigestLength();
	}

	// -------------------------------------------------------------------------------- Private method start
	private byte[] digestWithoutSalt(InputStream data, int bufferLength) throws IOException {
		final byte[] buffer = new byte[bufferLength];
		int read;
		while ((read = data.read(buffer, 0, bufferLength)) > -1) {
			this.digest.update(buffer, 0, read);
		}
		return this.digest.digest();
	}

	private byte[] digestWithSalt(InputStream data, int bufferLength) throws IOException {
		if (this.saltPosition <= 0) {
			// 加盐在开头
			this.digest.update(this.salt);
		}

		final byte[] buffer = new byte[bufferLength];
		int total = 0;
		int read;
		while ((read = data.read(buffer, 0, bufferLength)) > -1) {
			total += read;
			if (this.saltPosition > 0 && total >= this.saltPosition) {
				if (total != this.saltPosition) {
					digest.update(buffer, 0, total - this.saltPosition);
				}
				// 加盐在中间
				this.digest.update(this.salt);
				this.digest.update(buffer, total - this.saltPosition, read);
			} else {
				this.digest.update(buffer, 0, read);
			}
		}

		if (total < this.saltPosition) {
			// 加盐在末尾
			this.digest.update(this.salt);
		}

		return this.digest.digest();
	}

	private byte[] doDigest(byte[]... datas) {
		for (byte[] data : datas) {
			if (null != data) {
				this.digest.update(data);
			}
		}
		return this.digest.digest();
	}

	private byte[] resetAndRepeatDigest(byte[] digestData) {
		final int digestCount = Math.max(1, this.digestCount);
		reset();
		for (int i = 0; i < digestCount - 1; i++) {
			digestData = doDigest(digestData);
			reset();
		}
		return digestData;
	}
	// -------------------------------------------------------------------------------- Private method end
}
