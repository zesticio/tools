package com.zestic.coin.bouncycastle.asn1.pkcs;

import com.zestic.coin.bouncycastle.asn1.ASN1EncodableVector;
import com.zestic.coin.bouncycastle.asn1.DERSequence;
import com.zestic.coin.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.zestic.coin.bouncycastle.asn1.ASN1Sequence;
import com.zestic.coin.bouncycastle.asn1.DERObject;

public class EncryptionScheme
        extends AlgorithmIdentifier {

    DERObject objectId;
    DERObject obj;

    EncryptionScheme(
            ASN1Sequence seq) {
        super(seq);

        objectId = (DERObject) seq.getObjectAt(0);
        obj = (DERObject) seq.getObjectAt(1);
    }

    public DERObject getObject() {
        return obj;
    }

    public DERObject getDERObject() {
        ASN1EncodableVector v = new ASN1EncodableVector();

        v.add(objectId);
        v.add(obj);

        return new DERSequence(v);
    }
}
