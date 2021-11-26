package com.zestic.coin.bouncycastle.crypto.params;

import com.zestic.coin.bouncycastle.crypto.CipherParameters;

public class AsymmetricKeyParameter
        implements CipherParameters {

    boolean privateKey;

    public AsymmetricKeyParameter(
            boolean privateKey) {
        this.privateKey = privateKey;
    }

    public boolean isPrivate() {
        return privateKey;
    }
}
