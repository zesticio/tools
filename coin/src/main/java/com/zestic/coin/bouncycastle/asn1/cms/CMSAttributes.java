package com.zestic.coin.bouncycastle.asn1.cms;

import com.zestic.coin.bouncycastle.asn1.DERObjectIdentifier;
import com.zestic.coin.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;

public interface CMSAttributes {

    DERObjectIdentifier contentType = PKCSObjectIdentifiers.pkcs_9_at_contentType;
    DERObjectIdentifier messageDigest = PKCSObjectIdentifiers.pkcs_9_at_messageDigest;
    DERObjectIdentifier signingTime = PKCSObjectIdentifiers.pkcs_9_at_signingTime;
    DERObjectIdentifier counterSignature = PKCSObjectIdentifiers.pkcs_9_at_counterSignature;
    DERObjectIdentifier contentHint = PKCSObjectIdentifiers.id_aa_contentHint;
}
