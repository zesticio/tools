package com.zestic.coin.bouncycastle.asn1.mozilla;

import com.zestic.coin.bouncycastle.asn1.DERIA5String;
import com.zestic.coin.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import com.zestic.coin.bouncycastle.asn1.ASN1Encodable;
import com.zestic.coin.bouncycastle.asn1.ASN1Sequence;
import com.zestic.coin.bouncycastle.asn1.DERObject;

/*
 * This is designed to parse the PublicKeyAndChallenge created by the KEYGEN tag
 * included by Mozilla based browsers.
 * <pre>
 *  PublicKeyAndChallenge ::= SEQUENCE {
 *    spki SubjectPublicKeyInfo,
 *    challenge IA5STRING
 *  }
 *
 * </pre>
 */
public class PublicKeyAndChallenge
        extends ASN1Encodable {

    private ASN1Sequence pkacSeq;
    private SubjectPublicKeyInfo spki;
    private DERIA5String challenge;

    public static PublicKeyAndChallenge getInstance(Object obj) {
        if (obj instanceof PublicKeyAndChallenge) {
            return (PublicKeyAndChallenge) obj;
        } else if (obj instanceof ASN1Sequence) {
            return new PublicKeyAndChallenge((ASN1Sequence) obj);
        }

        throw new IllegalArgumentException("unkown object in factory: " + obj.getClass().getName());
    }

    public PublicKeyAndChallenge(ASN1Sequence seq) {
        pkacSeq = seq;
        spki = SubjectPublicKeyInfo.getInstance(seq.getObjectAt(0));
        challenge = DERIA5String.getInstance(seq.getObjectAt(1));
    }

    public DERObject toASN1Object() {
        return pkacSeq;
    }

    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return spki;
    }

    public DERIA5String getChallenge() {
        return challenge;
    }
}
