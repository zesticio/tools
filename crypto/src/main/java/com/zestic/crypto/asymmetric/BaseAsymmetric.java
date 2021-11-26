package com.zestic.crypto.asymmetric;

import com.zestic.core.codec.Base64;
import com.zestic.core.lang.Assert;
import com.zestic.crypto.CryptoException;
import com.zestic.crypto.KeyUtil;

import java.io.Serializable;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BaseAsymmetric<T extends BaseAsymmetric<T>> implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String algorithm;
    protected PublicKey publicKey;
    protected PrivateKey privateKey;
    protected final Lock lock = new ReentrantLock();

    // ------------------------------------------------------------------ Constructor start
    public BaseAsymmetric(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
        init(algorithm, privateKey, publicKey);
    }
    // ------------------------------------------------------------------ Constructor end

    @SuppressWarnings("unchecked")
    protected T init(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
        this.algorithm = algorithm;

        if (null == privateKey && null == publicKey) {
            initKeys();
        } else {
            if (null != privateKey) {
                this.privateKey = privateKey;
            }
            if (null != publicKey) {
                this.publicKey = publicKey;
            }
        }
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T initKeys() {
        KeyPair keyPair = KeyUtil.generateKeyPair(this.algorithm);
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
        return (T) this;
    }

    // --------------------------------------------------------------------------------- Getters and Setters

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public String getPublicKeyBase64() {
        final PublicKey publicKey = getPublicKey();
        return (null == publicKey) ? null : Base64.encode(publicKey.getEncoded());
    }

    @SuppressWarnings("unchecked")
    public T setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
        return (T) this;
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public String getPrivateKeyBase64() {
        final PrivateKey privateKey = getPrivateKey();
        return (null == privateKey) ? null : Base64.encode(privateKey.getEncoded());
    }

    @SuppressWarnings("unchecked")
    public T setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
        return (T) this;
    }

    public T setKey(Key key) {
        Assert.notNull(key, "key must be not null !");

        if (key instanceof PublicKey) {
            return setPublicKey((PublicKey) key);
        } else if (key instanceof PrivateKey) {
            return setPrivateKey((PrivateKey) key);
        }
        throw new CryptoException("Unsupported key type: {}", key.getClass());
    }

    protected Key getKeyByType(KeyType type) {
        switch (type) {
            case PrivateKey:
                if (null == this.privateKey) {
                    throw new NullPointerException("Private key must not null when use it !");
                }
                return this.privateKey;
            case PublicKey:
                if (null == this.publicKey) {
                    throw new NullPointerException("Public key must not null when use it !");
                }
                return this.publicKey;
        }
        throw new CryptoException("Unsupported key type: " + type);
    }
}
