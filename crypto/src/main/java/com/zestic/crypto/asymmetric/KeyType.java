package com.zestic.crypto.asymmetric;

import javax.crypto.Cipher;

public enum KeyType {
    PublicKey(Cipher.PUBLIC_KEY),
    PrivateKey(Cipher.PRIVATE_KEY),
    SecretKey(Cipher.SECRET_KEY);

    KeyType(int value) {
        this.value = value;
    }

    private final int value;

    public int getValue() {
        return this.value;
    }
}
