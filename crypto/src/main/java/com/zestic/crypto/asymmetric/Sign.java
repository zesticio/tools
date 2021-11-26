package com.zestic.crypto.asymmetric;

import com.zestic.core.codec.Base64;
import com.zestic.core.collection.CollUtil;
import com.zestic.core.io.IoUtil;
import com.zestic.core.util.CharsetUtil;
import com.zestic.core.util.HexUtil;
import com.zestic.core.util.StrUtil;
import com.zestic.crypto.CryptoException;
import com.zestic.crypto.SecureUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Set;

public class Sign extends BaseAsymmetric<Sign> {
	private static final long serialVersionUID = 1L;

	protected Signature signature;

	// ------------------------------------------------------------------ Constructor start
	public Sign(SignAlgorithm algorithm) {
		this(algorithm, null, (byte[]) null);
	}

	public Sign(String algorithm) {
		this(algorithm, null, (byte[]) null);
	}

	public Sign(SignAlgorithm algorithm, String privateKeyStr, String publicKeyStr) {
		this(algorithm.getValue(), SecureUtil.decode(privateKeyStr), SecureUtil.decode(publicKeyStr));
	}

	public Sign(SignAlgorithm algorithm, byte[] privateKey, byte[] publicKey) {
		this(algorithm.getValue(), privateKey, publicKey);
	}

	public Sign(SignAlgorithm algorithm, KeyPair keyPair) {
		this(algorithm.getValue(), keyPair);
	}

	public Sign(SignAlgorithm algorithm, PrivateKey privateKey, PublicKey publicKey) {
		this(algorithm.getValue(), privateKey, publicKey);
	}

	public Sign(String algorithm, String privateKeyBase64, String publicKeyBase64) {
		this(algorithm, Base64.decode(privateKeyBase64), Base64.decode(publicKeyBase64));
	}

	public Sign(String algorithm, byte[] privateKey, byte[] publicKey) {
		this(algorithm, //
				SecureUtil.generatePrivateKey(algorithm, privateKey), //
				SecureUtil.generatePublicKey(algorithm, publicKey)//
		);
	}

	public Sign(String algorithm, KeyPair keyPair) {
		this(algorithm, keyPair.getPrivate(), keyPair.getPublic());
	}

	public Sign(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
		super(algorithm, privateKey, publicKey);
	}
	// ------------------------------------------------------------------ Constructor end

	@Override
	public Sign init(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
		signature = SecureUtil.createSignature(algorithm);
		super.init(algorithm, privateKey, publicKey);
		return this;
	}

	public Sign setParameter(AlgorithmParameterSpec params) {
		try {
			this.signature.setParameter(params);
		} catch (InvalidAlgorithmParameterException e) {
			throw new CryptoException(e);
		}
		return this;
	}

	// --------------------------------------------------------------------------------- Sign and Verify
	public byte[] sign(String data, Charset charset) {
		return sign(StrUtil.bytes(data, charset));
	}

	public byte[] sign(String data) {
		return sign(data, CharsetUtil.CHARSET_UTF_8);
	}

	public String signHex(String data, Charset charset) {
		return HexUtil.encodeHexStr(sign(data, charset));
	}

	public String signHex(String data) {
		return signHex(data, CharsetUtil.CHARSET_UTF_8);
	}

	public byte[] sign(byte[] data) {
		return sign(new ByteArrayInputStream(data), -1);
	}

	public String signHex(byte[] data) {
		return HexUtil.encodeHexStr(sign(data));
	}

	public String signHex(InputStream data) {
		return HexUtil.encodeHexStr(sign(data));
	}

	public byte[] sign(InputStream data) {
		return sign(data, IoUtil.DEFAULT_BUFFER_SIZE);
	}

	public String digestHex(InputStream data, int bufferLength) {
		return HexUtil.encodeHexStr(sign(data, bufferLength));
	}

	public byte[] sign(InputStream data, int bufferLength){
		if (bufferLength < 1) {
			bufferLength = IoUtil.DEFAULT_BUFFER_SIZE;
		}

		final byte[] buffer = new byte[bufferLength];
		lock.lock();
		try {
			signature.initSign(this.privateKey);
			byte[] result;
			try {
				int read = data.read(buffer, 0, bufferLength);
				while (read > -1) {
					signature.update(buffer, 0, read);
					read = data.read(buffer, 0, bufferLength);
				}
				result = signature.sign();
			} catch (Exception e) {
				throw new CryptoException(e);
			}
			return result;
		} catch (Exception e) {
			throw new CryptoException(e);
		} finally {
			lock.unlock();
		}
	}

	public boolean verify(byte[] data, byte[] sign) {
		lock.lock();
		try {
			signature.initVerify(this.publicKey);
			signature.update(data);
			return signature.verify(sign);
		} catch (Exception e) {
			throw new CryptoException(e);
		} finally {
			lock.unlock();
		}
	}

	public Signature getSignature() {
		return signature;
	}

	public Sign setSignature(Signature signature) {
		this.signature = signature;
		return this;
	}

	public Sign setCertificate(Certificate certificate) {
		// If the certificate is of type X509Certificate,
		// we should check whether it has a Key Usage
		// extension marked as critical.
		if (certificate instanceof X509Certificate) {
			// Check whether the cert has a key usage extension
			// marked as a critical extension.
			// The OID for KeyUsage extension is 2.5.29.15.
			final X509Certificate cert = (X509Certificate) certificate;
			final Set<String> critSet = cert.getCriticalExtensionOIDs();

			if (CollUtil.isNotEmpty(critSet) && critSet.contains("2.5.29.15")) {
				final boolean[] keyUsageInfo = cert.getKeyUsage();
				// keyUsageInfo[0] is for digitalSignature.
				if ((keyUsageInfo != null) && (keyUsageInfo[0] == false)) {
					throw new CryptoException("Wrong key usage");
				}
			}
		}
		this.publicKey = certificate.getPublicKey();
		return this;
	}
}
