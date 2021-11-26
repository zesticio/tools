package com.zestic.crypto.asymmetric;

import com.zestic.core.codec.BCD;
import com.zestic.core.io.IORuntimeException;
import com.zestic.core.io.IoUtil;
import com.zestic.core.lang.Assert;
import com.zestic.core.util.CharsetUtil;
import com.zestic.core.util.StrUtil;
import com.zestic.crypto.SecureUtil;

import java.io.InputStream;
import java.nio.charset.Charset;

public interface AsymmetricDecryptor {

    byte[] decrypt(byte[] bytes, KeyType keyType);

    default byte[] decrypt(InputStream data, KeyType keyType) throws IORuntimeException {
        return decrypt(IoUtil.readBytes(data), keyType);
    }

    default byte[] decrypt(String data, KeyType keyType) {
        return decrypt(SecureUtil.decode(data), keyType);
    }

    default String decryptStr(String data, KeyType keyType, Charset charset) {
        return StrUtil.str(decrypt(data, keyType), charset);
    }

    default String decryptStr(String data, KeyType keyType) {
        return decryptStr(data, keyType, CharsetUtil.CHARSET_UTF_8);
    }

    default byte[] decryptFromBcd(String data, KeyType keyType) {
        return decryptFromBcd(data, keyType, CharsetUtil.CHARSET_UTF_8);
    }

    default byte[] decryptFromBcd(String data, KeyType keyType, Charset charset) {
        Assert.notNull(data, "Bcd string must be not null!");
        final byte[] dataBytes = BCD.ascToBcd(StrUtil.bytes(data, charset));
        return decrypt(dataBytes, keyType);
    }

    default String decryptStrFromBcd(String data, KeyType keyType, Charset charset) {
        return StrUtil.str(decryptFromBcd(data, keyType, charset), charset);
    }

    default String decryptStrFromBcd(String data, KeyType keyType) {
        return decryptStrFromBcd(data, keyType, CharsetUtil.CHARSET_UTF_8);
    }
}
