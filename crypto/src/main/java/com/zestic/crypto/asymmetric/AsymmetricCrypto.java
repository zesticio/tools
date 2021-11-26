package com.zestic.crypto.asymmetric;

import com.zestic.core.codec.Base64;
import com.zestic.core.io.FastByteArrayOutputStream;
import com.zestic.crypto.CryptoException;
import com.zestic.crypto.KeyUtil;
import com.zestic.crypto.SecureUtil;
import com.zestic.crypto.symmetric.SymmetricAlgorithm;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;

/*
 * Asymmetric encryption algorithm
 * <p>
 * 1、Signature: the use of private key encryption, public key to decrypt.
 * Used to make all public key owner to verify the identity of the owner of the private key and used to
 * prevent the owner of the private key content been tampered with, but don't get to keep the content from others.
 * <p>
 * 2、Encryption: with the public key encryption, decrypted.
 * To release to the public key owner information, this information may have been tampered with others,
 * but you can't get by other.
 */
public class AsymmetricCrypto extends AbstractAsymmetricCrypto<AsymmetricCrypto> {
    private static final long serialVersionUID = 1L;

    /*
     * Cipher for encryption or decryption
     */
    protected Cipher cipher;

    /*
     * Encrypted block size
     */
    protected int encryptBlockSize = -1;
    /*
     * Decryption block size
     */
    protected int decryptBlockSize = -1;

    /*
     * Algorithm parameters
     */
    private AlgorithmParameterSpec algorithmParameterSpec;

    // ------------------------------------------------------------------ Constructor start

    /*
     * Structure, to create a new private key public key
     *
     * @param algorithm {@link SymmetricAlgorithm}
     */
    @SuppressWarnings("RedundantCast")
    public AsymmetricCrypto(AsymmetricAlgorithm algorithm) {
        this(algorithm, (byte[]) null, (byte[]) null);
    }

    /*
     * Structure, to create a new private key public key
     *
     * @param algorithm
     */
    @SuppressWarnings("RedundantCast")
    public AsymmetricCrypto(String algorithm) {
        this(algorithm, (byte[]) null, (byte[]) null);
    }

    /*
     * Constructs the private key and public key is empty at the same time to generate a new private and
     * public keys
     * Private and public keys can pass in a alone, so you can only use this key to encrypt or decrypt
     *
     * @param algorithm
     * @param privateKeyStr
     * @param publicKeyStr
     */
    public AsymmetricCrypto(AsymmetricAlgorithm algorithm, String privateKeyStr, String publicKeyStr) {
        this(algorithm.getValue(), SecureUtil.decode(privateKeyStr), SecureUtil.decode(publicKeyStr));
    }

    /*
     * Constructs the private key and public key is empty at the same time to generate a new private and public keys
     * Private and public keys can pass in a alone, so you can only use this key to encrypt or decrypt
     *
     * @param algorithm  {@link SymmetricAlgorithm}
     * @param privateKey
     * @param publicKey
     */
    public AsymmetricCrypto(AsymmetricAlgorithm algorithm, byte[] privateKey, byte[] publicKey) {
        this(algorithm.getValue(), privateKey, publicKey);
    }

    /*
     * Constructs the private key and public key is empty at the same time to generate a new private and public keys < br >
     * Private and public keys can pass in a alone, so you can only use this key to encrypt or decrypt
     *
     * @param algorithm  {@link SymmetricAlgorithm}
     * @param privateKey
     * @param publicKey
     * @since 3.1.1
     */
    public AsymmetricCrypto(AsymmetricAlgorithm algorithm, PrivateKey privateKey, PublicKey publicKey) {
        this(algorithm.getValue(), privateKey, publicKey);
    }

    /*
     * Constructs the private key and public key is empty at the same time to generate a new private and public keys < br >
     * Private and public keys can pass in a alone, so you can only use this key to encrypt or decrypt
     *
     * @param algorithm        Asymmetric encryption algorithm
     * @param privateKeyBase64
     * @param publicKeyBase64
     */
    public AsymmetricCrypto(String algorithm, String privateKeyBase64, String publicKeyBase64) {
        this(algorithm, Base64.decode(privateKeyBase64), Base64.decode(publicKeyBase64));
    }

    /*
     * Private and public keys at the same time is empty generated when a pair of new private and public keys
     * Private and public keys can pass in a alone, so you can only use this key to encrypt or decrypt
     *
     * @param algorithm
     * @param privateKey
     * @param publicKey
     */
    public AsymmetricCrypto(String algorithm, byte[] privateKey, byte[] publicKey) {
        this(algorithm, //
                KeyUtil.generatePrivateKey(algorithm, privateKey), //
                KeyUtil.generatePublicKey(algorithm, publicKey)//
        );
    }

    /*
     * Private and public keys at the same time is empty generated when a pair of new private and public keys
     * Private and public keys can pass in a alone, so you can only use this key to encrypt or decrypt
     *
     * @param algorithm
     * @param privateKey
     * @param publicKey
     */
    public AsymmetricCrypto(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
        super(algorithm, privateKey, publicKey);
    }
    // ------------------------------------------------------------------ Constructor end

    /*
     * Access encrypted block size
     *
     * @return Encrypted block size
     */
    public int getEncryptBlockSize() {
        return encryptBlockSize;
    }

    /*
     * Set the encryption block size
     *
     * @param encryptBlockSize Encrypted block size
     */
    public void setEncryptBlockSize(int encryptBlockSize) {
        this.encryptBlockSize = encryptBlockSize;
    }

    /*
     * Access to decrypt the block size
     *
     * @return Decryption block size
     */
    public int getDecryptBlockSize() {
        return decryptBlockSize;
    }

    /*
     * Set the decryption block size
     *
     * @param decryptBlockSize Decryption block size
     */
    public void setDecryptBlockSize(int decryptBlockSize) {
        this.decryptBlockSize = decryptBlockSize;
    }

    public AlgorithmParameterSpec getAlgorithmParameterSpec() {
        return algorithmParameterSpec;
    }

    public void setAlgorithmParameterSpec(AlgorithmParameterSpec algorithmParameterSpec) {
        this.algorithmParameterSpec = algorithmParameterSpec;
    }

    @Override
    public AsymmetricCrypto init(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
        super.init(algorithm, privateKey, publicKey);
        initCipher();
        return this;
    }

    // --------------------------------------------------------------------------------- Encrypt

    @Override
    public byte[] encrypt(byte[] data, KeyType keyType) {
        final Key key = getKeyByType(keyType);
        lock.lock();
        try {
            initMode(Cipher.ENCRYPT_MODE, key);

            if (this.encryptBlockSize < 0) {
                // 在引入BC库情况下，自动获取块大小
                final int blockSize = this.cipher.getBlockSize();
                if (blockSize > 0) {
                    this.encryptBlockSize = blockSize;
                }
            }

            return doFinal(data, this.encryptBlockSize < 0 ? data.length : this.encryptBlockSize);
        } catch (Exception e) {
            throw new CryptoException(e);
        } finally {
            lock.unlock();
        }
    }

    // --------------------------------------------------------------------------------- Decrypt

    @Override
    public byte[] decrypt(byte[] data, KeyType keyType) {
        final Key key = getKeyByType(keyType);
        lock.lock();
        try {
            initMode(Cipher.DECRYPT_MODE, key);

            if (this.decryptBlockSize < 0) {
                // The introduction of the BC library case, to automatically block size
                final int blockSize = this.cipher.getBlockSize();
                if (blockSize > 0) {
                    this.decryptBlockSize = blockSize;
                }
            }

            return doFinal(data, this.decryptBlockSize < 0 ? data.length : this.decryptBlockSize);
        } catch (Exception e) {
            throw new CryptoException(e);
        } finally {
            lock.unlock();
        }
    }

    // --------------------------------------------------------------------------------- Getters and Setters

    public Cipher getCipher() {
        return cipher;
    }

    protected void initCipher() {
        this.cipher = SecureUtil.createCipher(algorithm);
    }

    private byte[] doFinal(byte[] data, int maxBlockSize) throws IllegalBlockSizeException, BadPaddingException, IOException {
        // length
        final int dataLength = data.length;

        // Insufficient segmentation
        if (dataLength <= maxBlockSize) {
            return this.cipher.doFinal(data, 0, dataLength);
        }

        // Segmented decryption
        return doFinalWithBlock(data, maxBlockSize);
    }

    private byte[] doFinalWithBlock(byte[] data, int maxBlockSize) throws IllegalBlockSizeException, BadPaddingException, IOException {
        final int dataLength = data.length;
        @SuppressWarnings("resource") final FastByteArrayOutputStream out = new FastByteArrayOutputStream();

        int offSet = 0;
        // The length of the remaining
        int remainLength = dataLength;
        int blockSize;
        // Segmenting the data processing
        while (remainLength > 0) {
            blockSize = Math.min(remainLength, maxBlockSize);
            out.write(cipher.doFinal(data, offSet, blockSize));
            offSet += blockSize;
            remainLength = dataLength - offSet;
        }
        return out.toByteArray();
    }

    private void initMode(int mode, Key key) throws InvalidAlgorithmParameterException, InvalidKeyException {
        if (null != this.algorithmParameterSpec) {
            cipher.init(mode, key, this.algorithmParameterSpec);
        } else {
            cipher.init(mode, key);
        }
    }
}
