package com.zestic.coin.bouncycastle.asn1.esf;

import com.zestic.coin.bouncycastle.asn1.DERObjectIdentifier;
import com.zestic.coin.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;

public interface ESFAttributes {

    DERObjectIdentifier sigPolicyId = PKCSObjectIdentifiers.id_aa_ets_sigPolicyId;
    DERObjectIdentifier commitmentType = PKCSObjectIdentifiers.id_aa_ets_commitmentType;
    DERObjectIdentifier signerLocation = PKCSObjectIdentifiers.id_aa_ets_signerLocation;
    DERObjectIdentifier signerAttr = PKCSObjectIdentifiers.id_aa_ets_signerAttr;
    DERObjectIdentifier otherSigCert = PKCSObjectIdentifiers.id_aa_ets_otherSigCert;
    DERObjectIdentifier contentTimestamp = PKCSObjectIdentifiers.id_aa_ets_contentTimestamp;
    DERObjectIdentifier certificateRefs = PKCSObjectIdentifiers.id_aa_ets_certificateRefs;
    DERObjectIdentifier revocationRefs = PKCSObjectIdentifiers.id_aa_ets_revocationRefs;
    DERObjectIdentifier certValues = PKCSObjectIdentifiers.id_aa_ets_certValues;
    DERObjectIdentifier revocationValues = PKCSObjectIdentifiers.id_aa_ets_revocationValues;
    DERObjectIdentifier escTimeStamp = PKCSObjectIdentifiers.id_aa_ets_escTimeStamp;
    DERObjectIdentifier certCRLTimestamp = PKCSObjectIdentifiers.id_aa_ets_certCRLTimestamp;
    DERObjectIdentifier archiveTimestamp = PKCSObjectIdentifiers.id_aa_ets_archiveTimestamp;
}
