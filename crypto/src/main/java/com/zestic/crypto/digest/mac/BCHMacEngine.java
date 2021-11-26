package com.zestic.crypto.digest.mac;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class BCHMacEngine implements MacEngine {

    private Mac mac;

    // ------------------------------------------------------------------------------------------- Constructor start

    public BCHMacEngine(Digest digest, byte[] key, byte[] iv) {
        this(digest, new ParametersWithIV(new KeyParameter(key), iv));
    }

    public BCHMacEngine(Digest digest, byte[] key) {
        this(digest, new KeyParameter(key));
    }

    public BCHMacEngine(Digest digest, CipherParameters params) {
        init(digest, params);
    }
    // ------------------------------------------------------------------------------------------- Constructor end

    public BCHMacEngine init(Digest digest, CipherParameters params) {
        mac = new HMac(digest);
        mac.init(params);
        return this;
    }

    public Mac getMac() {
        return mac;
    }

    @Override
    public void update(byte[] in, int inOff, int len) {
        this.mac.update(in, inOff, len);
    }

    @Override
    public byte[] doFinal() {
        final byte[] result = new byte[getMacLength()];
        this.mac.doFinal(result, 0);
        return result;
    }

    @Override
    public void reset() {
        this.mac.reset();
    }

    @Override
    public int getMacLength() {
        return mac.getMacSize();
    }

    @Override
    public String getAlgorithm() {
        return this.mac.getAlgorithmName();
    }
}
