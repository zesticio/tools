package com.zestic.crypto.digest.mac;

import com.zestic.crypto.CryptoException;
import com.zestic.crypto.SecureUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

public class DefaultHMacEngine implements MacEngine {

    private Mac mac;

    // ------------------------------------------------------------------------------------------- Constructor start

    public DefaultHMacEngine(String algorithm, byte[] key) {
        this(algorithm, (null == key) ? null : new SecretKeySpec(key, algorithm));
    }

    public DefaultHMacEngine(String algorithm, Key key) {
        this(algorithm, key, null);
    }

    public DefaultHMacEngine(String algorithm, Key key, AlgorithmParameterSpec spec) {
        init(algorithm, key, spec);
    }
    // ------------------------------------------------------------------------------------------- Constructor end

    public DefaultHMacEngine init(String algorithm, byte[] key) {
        return init(algorithm, (null == key) ? null : new SecretKeySpec(key, algorithm));
    }

    public DefaultHMacEngine init(String algorithm, Key key) {
        return init(algorithm, key, null);
    }

    public DefaultHMacEngine init(String algorithm, Key key, AlgorithmParameterSpec spec) {
        try {
            mac = SecureUtil.createMac(algorithm);
            if (null == key) {
                key = SecureUtil.generateKey(algorithm);
            }
            if (null != spec) {
                mac.init(key, spec);
            } else {
                mac.init(key);
            }
        } catch (Exception e) {
            throw new CryptoException(e);
        }
        return this;
    }

    public Mac getMac() {
        return mac;
    }

    @Override
    public void update(byte[] in) {
        this.mac.update(in);
    }

    @Override
    public void update(byte[] in, int inOff, int len) {
        this.mac.update(in, inOff, len);
    }

    @Override
    public byte[] doFinal() {
        return this.mac.doFinal();
    }

    @Override
    public void reset() {
        this.mac.reset();
    }

    @Override
    public int getMacLength() {
        return mac.getMacLength();
    }

    @Override
    public String getAlgorithm() {
        return this.mac.getAlgorithm();
    }
}
