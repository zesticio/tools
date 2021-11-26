package com.zestic.coin.bouncycastle.asn1.cms;

import com.zestic.coin.bouncycastle.asn1.*;

public class OtherKeyAttribute
        extends ASN1Encodable {

    private final DERObjectIdentifier keyAttrId;
    private final DEREncodable keyAttr;

    public OtherKeyAttribute(
            ASN1Sequence seq) {
        keyAttrId = (DERObjectIdentifier) seq.getObjectAt(0);
        keyAttr = seq.getObjectAt(1);
    }

    public OtherKeyAttribute(
            DERObjectIdentifier keyAttrId,
            DEREncodable keyAttr) {
        this.keyAttrId = keyAttrId;
        this.keyAttr = keyAttr;
    }

    /*
     * return an OtherKeyAttribute object from the given object.
     *
     * @param o the object we want converted.
     * @throws IllegalArgumentException if the object cannot be converted.
     */
    public static OtherKeyAttribute getInstance(
            Object o) {
        if (o == null || o instanceof OtherKeyAttribute) {
            return (OtherKeyAttribute) o;
        }

        if (o instanceof ASN1Sequence) {
            return new OtherKeyAttribute((ASN1Sequence) o);
        }

        throw new IllegalArgumentException("unknown object in factory: " + o.getClass().getName());
    }

    public DERObjectIdentifier getKeyAttrId() {
        return keyAttrId;
    }

    public DEREncodable getKeyAttr() {
        return keyAttr;
    }

    /*
     * Produce an object suitable for an ASN1OutputStream.
     * <pre>
     * OtherKeyAttribute ::= SEQUENCE {
     *     keyAttrId OBJECT IDENTIFIER,
     *     keyAttr ANY DEFINED BY keyAttrId OPTIONAL
     * }
     * </pre>
     */
    public DERObject toASN1Object() {
        ASN1EncodableVector v = new ASN1EncodableVector();

        v.add(keyAttrId);
        v.add(keyAttr);

        return new DERSequence(v);
    }
}
