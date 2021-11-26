package com.zestic.coin.bouncycastle.crypto.tls;

import com.zestic.coin.bouncycastle.crypto.digests.SHA1Digest;
import com.zestic.coin.bouncycastle.crypto.signers.DSADigestSigner;
import com.zestic.coin.bouncycastle.crypto.signers.DSASigner;

class TlsDSSSigner
        extends DSADigestSigner {

    TlsDSSSigner() {
        super(new DSASigner(), new SHA1Digest());
    }
}
