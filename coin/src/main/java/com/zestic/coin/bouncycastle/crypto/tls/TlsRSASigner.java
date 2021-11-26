package com.zestic.coin.bouncycastle.crypto.tls;

import com.zestic.coin.bouncycastle.crypto.encodings.PKCS1Encoding;
import com.zestic.coin.bouncycastle.crypto.engines.RSABlindedEngine;
import com.zestic.coin.bouncycastle.crypto.signers.GenericSigner;

class TlsRSASigner
        extends GenericSigner {

    TlsRSASigner() {
        super(new PKCS1Encoding(new RSABlindedEngine()), new CombinedHash());
    }
}
