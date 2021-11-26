package com.zestic.coin.bouncycastle.asn1.cms;

import com.zestic.coin.bouncycastle.asn1.DERObjectIdentifier;
import com.zestic.coin.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;

public interface CMSObjectIdentifiers {

    DERObjectIdentifier data = PKCSObjectIdentifiers.data;
    DERObjectIdentifier signedData = PKCSObjectIdentifiers.signedData;
    DERObjectIdentifier envelopedData = PKCSObjectIdentifiers.envelopedData;
    DERObjectIdentifier signedAndEnvelopedData = PKCSObjectIdentifiers.signedAndEnvelopedData;
    DERObjectIdentifier digestedData = PKCSObjectIdentifiers.digestedData;
    DERObjectIdentifier encryptedData = PKCSObjectIdentifiers.encryptedData;
    DERObjectIdentifier authenticatedData = PKCSObjectIdentifiers.id_ct_authData;
    DERObjectIdentifier compressedData = PKCSObjectIdentifiers.id_ct_compressedData;
    DERObjectIdentifier authEnvelopedData = PKCSObjectIdentifiers.id_ct_authEnvelopedData;
}
