package com.zestic.crypto.asymmetric;

import com.zestic.crypto.CryptoException;
import com.zestic.crypto.GlobalBouncyCastleProvider;
import com.zestic.crypto.SecureUtil;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

public class RSA extends AsymmetricCrypto {
    private static final long serialVersionUID = 1L;

    private static final AsymmetricAlgorithm ALGORITHM_RSA = AsymmetricAlgorithm.RSA_ECB_PKCS1;

    // ------------------------------------------------------------------ Static method start
    public static PrivateKey generatePrivateKey(BigInteger modulus, BigInteger privateExponent) {
        return SecureUtil.generatePrivateKey(ALGORITHM_RSA.getValue(), new RSAPrivateKeySpec(modulus, privateExponent));
    }

    public static PublicKey generatePublicKey(BigInteger modulus, BigInteger publicExponent) {
        return SecureUtil.generatePublicKey(ALGORITHM_RSA.getValue(), new RSAPublicKeySpec(modulus, publicExponent));
    }
    // ------------------------------------------------------------------ Static method end

    // ------------------------------------------------------------------ Constructor start
    public RSA() {
        super(ALGORITHM_RSA);
    }

    public RSA(String rsaAlgorithm) {
        super(rsaAlgorithm);
    }

    public RSA(String privateKeyStr, String publicKeyStr) {
        super(ALGORITHM_RSA, privateKeyStr, publicKeyStr);
    }

    public RSA(String rsaAlgorithm, String privateKeyStr, String publicKeyStr) {
        super(rsaAlgorithm, privateKeyStr, publicKeyStr);
    }

    public RSA(byte[] privateKey, byte[] publicKey) {
        super(ALGORITHM_RSA, privateKey, publicKey);
    }

    public RSA(BigInteger modulus, BigInteger privateExponent, BigInteger publicExponent) {
        this(generatePrivateKey(modulus, privateExponent), generatePublicKey(modulus, publicExponent));
    }

    public RSA(PrivateKey privateKey, PublicKey publicKey) {
        super(ALGORITHM_RSA, privateKey, publicKey);
    }

    public RSA(String rsaAlgorithm, PrivateKey privateKey, PublicKey publicKey) {
        super(rsaAlgorithm, privateKey, publicKey);
    }
    // ------------------------------------------------------------------ Constructor end

    @Override
    public byte[] encrypt(byte[] data, KeyType keyType) {
        // Under the condition of not using BC library, use the default algorithm block Size
        if (this.encryptBlockSize < 0 && null == GlobalBouncyCastleProvider.INSTANCE.getProvider()) {
            // Encrypt the data length < = length - 11
            this.encryptBlockSize = ((RSAKey) getKeyByType(keyType)).getModulus().bitLength() / 8 - 11;
        }
        return super.encrypt(data, keyType);
    }

    @Override
    public byte[] decrypt(byte[] bytes, KeyType keyType) {
        // Under the condition of not using BC library, use the default algorithm block Size
        if (this.decryptBlockSize < 0 && null == GlobalBouncyCastleProvider.INSTANCE.getProvider()) {
            // Encrypt the data length < = length - 11
            this.decryptBlockSize = ((RSAKey) getKeyByType(keyType)).getModulus().bitLength() / 8;
        }
        return super.decrypt(bytes, keyType);
    }

    @Override
    protected void initCipher() {
        try {
            super.initCipher();
        } catch (CryptoException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof NoSuchAlgorithmException) {
                // Under Linux, the introduction of BC library may result in RSA/ECB/PKCS1
                // Padding algorithm cannot find, this time using the default algorithm
                this.algorithm = AsymmetricAlgorithm.RSA.getValue();
                super.initCipher();
            }
            throw e;
        }
    }
}
