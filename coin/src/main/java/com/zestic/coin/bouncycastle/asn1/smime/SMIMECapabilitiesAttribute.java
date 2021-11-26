package com.zestic.coin.bouncycastle.asn1.smime;

import com.zestic.coin.bouncycastle.asn1.DERSequence;
import com.zestic.coin.bouncycastle.asn1.DERSet;
import com.zestic.coin.bouncycastle.asn1.cms.Attribute;

public class SMIMECapabilitiesAttribute
        extends Attribute {

    public SMIMECapabilitiesAttribute(
            SMIMECapabilityVector capabilities) {
        super(SMIMEAttributes.smimeCapabilities,
                new DERSet(new DERSequence(capabilities.toDEREncodableVector())));
    }
}
