package com.zestic.crypto.asymmetric;

import com.zestic.core.codec.BCD;
import com.zestic.core.codec.Base64;
import com.zestic.core.io.IORuntimeException;
import com.zestic.core.io.IoUtil;
import com.zestic.core.util.CharsetUtil;
import com.zestic.core.util.HexUtil;
import com.zestic.core.util.StrUtil;

import java.io.InputStream;
import java.nio.charset.Charset;

public interface AsymmetricEncryptor {

    byte[] encrypt(byte[] data, KeyType keyType);

    default String encryptHex(byte[] data, KeyType keyType) {
        return HexUtil.encodeHexStr(encrypt(data, keyType));
    }

    default String encryptBase64(byte[] data, KeyType keyType) {
        return Base64.encode(encrypt(data, keyType));
    }

    default byte[] encrypt(String data, String charset, KeyType keyType) {
        return encrypt(StrUtil.bytes(data, charset), keyType);
    }

    default byte[] encrypt(String data, Charset charset, KeyType keyType) {
        return encrypt(StrUtil.bytes(data, charset), keyType);
    }

    default byte[] encrypt(String data, KeyType keyType) {
        return encrypt(StrUtil.utf8Bytes(data), keyType);
    }

    default String encryptHex(String data, KeyType keyType) {
        return HexUtil.encodeHexStr(encrypt(data, keyType));
    }

    default String encryptHex(String data, Charset charset, KeyType keyType) {
        return HexUtil.encodeHexStr(encrypt(data, charset, keyType));
    }

    default String encryptBase64(String data, KeyType keyType) {
        return Base64.encode(encrypt(data, keyType));
    }

    default String encryptBase64(String data, Charset charset, KeyType keyType) {
        return Base64.encode(encrypt(data, charset, keyType));
    }

    default byte[] encrypt(InputStream data, KeyType keyType) throws IORuntimeException {
        return encrypt(IoUtil.readBytes(data), keyType);
    }

    default String encryptHex(InputStream data, KeyType keyType) {
        return HexUtil.encodeHexStr(encrypt(data, keyType));
    }

    default String encryptBase64(InputStream data, KeyType keyType) {
        return Base64.encode(encrypt(data, keyType));
    }

    default String encryptBcd(String data, KeyType keyType) {
        return encryptBcd(data, keyType, CharsetUtil.CHARSET_UTF_8);
    }

    default String encryptBcd(String data, KeyType keyType, Charset charset) {
        return BCD.bcdToStr(encrypt(data, charset, keyType));
    }
}
