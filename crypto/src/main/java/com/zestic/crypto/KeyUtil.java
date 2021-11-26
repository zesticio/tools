package com.zestic.crypto;

import com.zestic.core.io.FileUtil;
import com.zestic.core.io.IoUtil;
import com.zestic.core.lang.Assert;
import com.zestic.core.util.ArrayUtil;
import com.zestic.core.util.CharUtil;
import com.zestic.core.util.RandomUtil;
import com.zestic.core.util.StrUtil;
import com.zestic.crypto.asymmetric.AsymmetricAlgorithm;
import com.zestic.crypto.symmetric.SymmetricAlgorithm;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.*;

/*
 * The key tools
 */
public class KeyUtil {

    /*
     * Java Key Store，JKS
     */
    public static final String KEY_TYPE_JKS = "JKS";

    /*
     * jceks
     */
    public static final String KEY_TYPE_JCEKS = "jceks";

    /*
     * As PKCS12 is public key encryption standard, it specifies can contain all the private key, public key and certificate.
     * It is stored in a binary format, also known as PFX file
     */
    public static final String KEY_TYPE_PKCS12 = "pkcs12";

    /*
     * Certification type: x. 509
     */
    public static final String CERT_TYPE_X509 = "X.509";

    /*
     * The default key bytes
     * <p>
     * RSA/DSA
     * Default Keysize 1024
     * Keysize must be a multiple of 64, ranging from 512 to 1024 (inclusive).
     */
    public static final int DEFAULT_KEY_SIZE = 1024;

    /*
     * The default curve SM2
     * <p>
     * Default SM2 curve
     */
    public static final String SM2_DEFAULT_CURVE = SmUtil.SM2_CURVE_NAME;

    /*
     * only used for symmetric encryption and the Key generation algorithm
     */
    public static SecretKey generateKey(String algorithm) {
        return generateKey(algorithm, -1);
    }

    /*
     * When specifying key Size&lt; The default length of 128 0, AES, other algorithm is not specified.
     *
     * @param algorithm
     * @param keySize
     * @return {@link SecretKey}
     */
    public static SecretKey generateKey(String algorithm, int keySize) {
        return generateKey(algorithm, keySize, null);
    }

    /*
     * When specifying key Size, The default length of 128 0, AES, other algorithm is not specified.
     *
     * @param algorithm Algorithm, support PBE algorithm
     * @param keySize   The key length, & lt; 0 means don't set the key length, that is, use the default length
     * @param random    Random number generator, null by default
     * @return {@link SecretKey}
     */
    public static SecretKey generateKey(String algorithm, int keySize, SecureRandom random) {
        algorithm = getMainAlgorithm(algorithm);

        final KeyGenerator keyGenerator = getKeyGenerator(algorithm);
        if (keySize <= 0 && SymmetricAlgorithm.AES.getValue().equals(algorithm)) {
            // 对于AES的密钥，除非指定，否则强制使用128位
            keySize = 128;
        }

        if (keySize > 0) {
            if (null == random) {
                keyGenerator.init(keySize);
            } else {
                keyGenerator.init(keySize, random);
            }
        }
        return keyGenerator.generateKey();
    }

    public static SecretKey generateKey(String algorithm, byte[] key) {
        Assert.notBlank(algorithm, "Algorithm is blank!");
        SecretKey secretKey;
        if (algorithm.startsWith("PBE")) {
            // PBE密钥
            secretKey = generatePBEKey(algorithm, (null == key) ? null : StrUtil.utf8Str(key).toCharArray());
        } else if (algorithm.startsWith("DES")) {
            // DES密钥
            secretKey = generateDESKey(algorithm, key);
        } else {
            // 其它算法密钥
            secretKey = (null == key) ? generateKey(algorithm) : new SecretKeySpec(key, algorithm);
        }
        return secretKey;
    }

    public static SecretKey generateDESKey(String algorithm, byte[] key) {
        if (StrUtil.isBlank(algorithm) || false == algorithm.startsWith("DES")) {
            throw new CryptoException("Algorithm [{}] is not a DES algorithm!");
        }

        SecretKey secretKey;
        if (null == key) {
            secretKey = generateKey(algorithm);
        } else {
            KeySpec keySpec;
            try {
                if (algorithm.startsWith("DESede")) {
                    // DESede兼容
                    keySpec = new DESedeKeySpec(key);
                } else {
                    keySpec = new DESKeySpec(key);
                }
            } catch (InvalidKeyException e) {
                throw new CryptoException(e);
            }
            secretKey = generateKey(algorithm, keySpec);
        }
        return secretKey;
    }

    public static SecretKey generatePBEKey(String algorithm, char[] key) {
        if (StrUtil.isBlank(algorithm) || false == algorithm.startsWith("PBE")) {
            throw new CryptoException("Algorithm [{}] is not a PBE algorithm!");
        }

        if (null == key) {
            key = RandomUtil.randomString(32).toCharArray();
        }
        PBEKeySpec keySpec = new PBEKeySpec(key);
        return generateKey(algorithm, keySpec);
    }

    public static SecretKey generateKey(String algorithm, KeySpec keySpec) {
        final SecretKeyFactory keyFactory = getSecretKeyFactory(algorithm);
        try {
            return keyFactory.generateSecret(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new CryptoException(e);
        }
    }

    public static PrivateKey generateRSAPrivateKey(byte[] key) {
        return generatePrivateKey(AsymmetricAlgorithm.RSA.getValue(), key);
    }

    public static PrivateKey generatePrivateKey(String algorithm, byte[] key) {
        if (null == key) {
            return null;
        }
        return generatePrivateKey(algorithm, new PKCS8EncodedKeySpec(key));
    }

    public static PrivateKey generatePrivateKey(String algorithm, KeySpec keySpec) {
        if (null == keySpec) {
            return null;
        }
        algorithm = getAlgorithmAfterWith(algorithm);
        try {
            return getKeyFactory(algorithm).generatePrivate(keySpec);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    public static PrivateKey generatePrivateKey(KeyStore keyStore, String alias, char[] password) {
        try {
            return (PrivateKey) keyStore.getKey(alias, password);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    public static PublicKey generateRSAPublicKey(byte[] key) {
        return generatePublicKey(AsymmetricAlgorithm.RSA.getValue(), key);
    }

    public static PublicKey generatePublicKey(String algorithm, byte[] key) {
        if (null == key) {
            return null;
        }
        return generatePublicKey(algorithm, new X509EncodedKeySpec(key));
    }

    public static PublicKey generatePublicKey(String algorithm, KeySpec keySpec) {
        if (null == keySpec) {
            return null;
        }
        algorithm = getAlgorithmAfterWith(algorithm);
        try {
            return getKeyFactory(algorithm).generatePublic(keySpec);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    public static KeyPair generateKeyPair(String algorithm) {
        int keySize = DEFAULT_KEY_SIZE;
        if ("ECIES".equalsIgnoreCase(algorithm)) {
            // ECIES algorithm, the length of the KEY requirements, the default 256
            keySize = 256;
        }
        return generateKeyPair(algorithm, keySize);
    }

    public static KeyPair generateKeyPair(String algorithm, int keySize) {
        return generateKeyPair(algorithm, keySize, null);
    }

    public static KeyPair generateKeyPair(String algorithm, int keySize, byte[] seed) {
        // SM2 algorithm need to define its curve generated separately
        if ("SM2".equalsIgnoreCase(algorithm)) {
            final ECGenParameterSpec sm2p256v1 = new ECGenParameterSpec(SM2_DEFAULT_CURVE);
            return generateKeyPair(algorithm, keySize, seed, sm2p256v1);
        }
        return generateKeyPair(algorithm, keySize, seed, (AlgorithmParameterSpec[]) null);
    }

    public static KeyPair generateKeyPair(String algorithm, AlgorithmParameterSpec params) {
        return generateKeyPair(algorithm, null, params);
    }

    public static KeyPair generateKeyPair(String algorithm, byte[] seed, AlgorithmParameterSpec param) {
        return generateKeyPair(algorithm, DEFAULT_KEY_SIZE, seed, param);
    }

    public static KeyPair generateKeyPair(String algorithm, int keySize, byte[] seed, AlgorithmParameterSpec... params) {
        return generateKeyPair(algorithm, keySize, RandomUtil.createSecureRandom(seed), params);
    }

    public static KeyPair generateKeyPair(String algorithm, int keySize, SecureRandom random, AlgorithmParameterSpec... params) {
        algorithm = getAlgorithmAfterWith(algorithm);
        final KeyPairGenerator keyPairGen = getKeyPairGenerator(algorithm);

        // Initialization key modulus (modulus) length definition
        if (keySize > 0) {
            // The key length adaptive correction
            if ("EC".equalsIgnoreCase(algorithm) && keySize > 256) {
                // For EC (Elliptic Curve algorithm, the key length limit, use the default 256 here
                keySize = 256;
            }
            if (null != random) {
                keyPairGen.initialize(keySize, random);
            } else {
                keyPairGen.initialize(keySize);
            }
        }

        // Custom initialization parameter
        if (ArrayUtil.isNotEmpty(params)) {
            for (AlgorithmParameterSpec param : params) {
                if (null == param) {
                    continue;
                }
                try {
                    if (null != random) {
                        keyPairGen.initialize(param, random);
                    } else {
                        keyPairGen.initialize(param);
                    }
                } catch (InvalidAlgorithmParameterException e) {
                    throw new CryptoException(e);
                }
            }
        }
        return keyPairGen.generateKeyPair();
    }

    public static KeyPairGenerator getKeyPairGenerator(String algorithm) {
        final Provider provider = GlobalBouncyCastleProvider.INSTANCE.getProvider();

        KeyPairGenerator keyPairGen;
        try {
            keyPairGen = (null == provider) //
                    ? KeyPairGenerator.getInstance(getMainAlgorithm(algorithm)) //
                    : KeyPairGenerator.getInstance(getMainAlgorithm(algorithm), provider);//
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }
        return keyPairGen;
    }

    public static KeyFactory getKeyFactory(String algorithm) {
        final Provider provider = GlobalBouncyCastleProvider.INSTANCE.getProvider();

        KeyFactory keyFactory;
        try {
            keyFactory = (null == provider) //
                    ? KeyFactory.getInstance(getMainAlgorithm(algorithm)) //
                    : KeyFactory.getInstance(getMainAlgorithm(algorithm), provider);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }
        return keyFactory;
    }

    public static SecretKeyFactory getSecretKeyFactory(String algorithm) {
        final Provider provider = GlobalBouncyCastleProvider.INSTANCE.getProvider();

        SecretKeyFactory keyFactory;
        try {
            keyFactory = (null == provider) //
                    ? SecretKeyFactory.getInstance(getMainAlgorithm(algorithm)) //
                    : SecretKeyFactory.getInstance(getMainAlgorithm(algorithm), provider);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }
        return keyFactory;
    }

    public static KeyGenerator getKeyGenerator(String algorithm) {
        final Provider provider = GlobalBouncyCastleProvider.INSTANCE.getProvider();

        KeyGenerator generator;
        try {
            generator = (null == provider) //
                    ? KeyGenerator.getInstance(getMainAlgorithm(algorithm)) //
                    : KeyGenerator.getInstance(getMainAlgorithm(algorithm), provider);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }
        return generator;
    }

    public static String getMainAlgorithm(String algorithm) {
        Assert.notBlank(algorithm, "Algorithm must be not blank!");
        final int slashIndex = algorithm.indexOf(CharUtil.SLASH);
        if (slashIndex > 0) {
            return algorithm.substring(0, slashIndex);
        }
        return algorithm;
    }

    public static String getAlgorithmAfterWith(String algorithm) {
        Assert.notNull(algorithm, "algorithm must be not null !");

        if (StrUtil.startWithIgnoreCase(algorithm, "ECIESWith")) {
            return "EC";
        }

        int indexOfWith = StrUtil.lastIndexOfIgnoreCase(algorithm, "with");
        if (indexOfWith > 0) {
            algorithm = StrUtil.subSuf(algorithm, indexOfWith + "with".length());
        }
        if ("ECDSA".equalsIgnoreCase(algorithm)
                || "SM2".equalsIgnoreCase(algorithm)
                || "ECIES".equalsIgnoreCase(algorithm)
        ) {
            algorithm = "EC";
        }
        return algorithm;
    }

    public static KeyStore readJKSKeyStore(File keyFile, char[] password) {
        return readKeyStore(KEY_TYPE_JKS, keyFile, password);
    }

    public static KeyStore readJKSKeyStore(InputStream in, char[] password) {
        return readKeyStore(KEY_TYPE_JKS, in, password);
    }

    public static KeyStore readPKCS12KeyStore(File keyFile, char[] password) {
        return readKeyStore(KEY_TYPE_PKCS12, keyFile, password);
    }

    public static KeyStore readPKCS12KeyStore(InputStream in, char[] password) {
        return readKeyStore(KEY_TYPE_PKCS12, in, password);
    }

    public static KeyStore readKeyStore(String type, File keyFile, char[] password) {
        InputStream in = null;
        try {
            in = FileUtil.getInputStream(keyFile);
            return readKeyStore(type, in, password);
        } finally {
            IoUtil.close(in);
        }
    }

    public static KeyStore readKeyStore(String type, InputStream in, char[] password) {
        KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance(type);
            keyStore.load(in, password);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
        return keyStore;
    }

    public static KeyPair getKeyPair(String type, InputStream in, char[] password, String alias) {
        final KeyStore keyStore = readKeyStore(type, in, password);
        return getKeyPair(keyStore, password, alias);
    }

    public static KeyPair getKeyPair(KeyStore keyStore, char[] password, String alias) {
        PublicKey publicKey;
        PrivateKey privateKey;
        try {
            publicKey = keyStore.getCertificate(alias).getPublicKey();
            privateKey = (PrivateKey) keyStore.getKey(alias, password);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
        return new KeyPair(publicKey, privateKey);
    }

    public static Certificate readX509Certificate(InputStream in, char[] password, String alias) {
        return readCertificate(CERT_TYPE_X509, in, password, alias);
    }

    public static PublicKey readPublicKeyFromCert(InputStream in) {
        final Certificate certificate = readX509Certificate(in);
        if (null != certificate) {
            return certificate.getPublicKey();
        }
        return null;
    }

    public static Certificate readX509Certificate(InputStream in) {
        return readCertificate(CERT_TYPE_X509, in);
    }

    public static Certificate readCertificate(String type, InputStream in, char[] password, String alias) {
        final KeyStore keyStore = readKeyStore(type, in, password);
        try {
            return keyStore.getCertificate(alias);
        } catch (KeyStoreException e) {
            throw new CryptoException(e);
        }
    }

    public static Certificate readCertificate(String type, InputStream in) {
        try {
            return getCertificateFactory(type).generateCertificate(in);
        } catch (CertificateException e) {
            throw new CryptoException(e);
        }
    }

    public static Certificate getCertificate(KeyStore keyStore, String alias) {
        try {
            return keyStore.getCertificate(alias);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    public static CertificateFactory getCertificateFactory(String type) {
        final Provider provider = GlobalBouncyCastleProvider.INSTANCE.getProvider();

        CertificateFactory factory;
        try {
            factory = (null == provider) ? CertificateFactory.getInstance(type) : CertificateFactory.getInstance(type, provider);
        } catch (CertificateException e) {
            throw new CryptoException(e);
        }
        return factory;
    }

    public static byte[] encodeECPublicKey(PublicKey publicKey) {
        return BCUtil.encodeECPublicKey(publicKey);
    }

    public static PublicKey decodeECPoint(String encode, String curveName) {
        return BCUtil.decodeECPoint(encode, curveName);
    }

    public static PublicKey decodeECPoint(byte[] encodeByte, String curveName) {
        return BCUtil.decodeECPoint(encodeByte, curveName);
    }

    public static PublicKey getRSAPublicKey(PrivateKey privateKey) {
        if (privateKey instanceof RSAPrivateCrtKey) {
            final RSAPrivateCrtKey privk = (RSAPrivateCrtKey) privateKey;
            return getRSAPublicKey(privk.getModulus(), privk.getPublicExponent());
        }
        return null;
    }

    public static PublicKey getRSAPublicKey(String modulus, String publicExponent) {
        return getRSAPublicKey(
                new BigInteger(modulus, 16), new BigInteger(publicExponent, 16));
    }

    public static PublicKey getRSAPublicKey(BigInteger modulus, BigInteger publicExponent) {
        final RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, publicExponent);
        try {
            return getKeyFactory("RSA").generatePublic(publicKeySpec);
        } catch (InvalidKeySpecException e) {
            throw new CryptoException(e);
        }
    }
}
