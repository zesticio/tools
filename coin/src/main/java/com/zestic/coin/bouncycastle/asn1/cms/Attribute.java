package com.zestic.coin.bouncycastle.asn1.cms;

import com.zestic.coin.bouncycastle.asn1.*;

public class Attribute
        extends ASN1Encodable {

    private final DERObjectIdentifier attrType;
    private final ASN1Set attrValues;

    public Attribute(
            ASN1Sequence seq) {
        attrType = (DERObjectIdentifier) seq.getObjectAt(0);
        attrValues = (ASN1Set) seq.getObjectAt(1);
    }

    public Attribute(
            DERObjectIdentifier attrType,
            ASN1Set attrValues) {
        this.attrType = attrType;
        this.attrValues = attrValues;
    }

    /*
     * return an Attribute object from the given object.
     *
     * @param o the object we want converted.
     * @throws IllegalArgumentException if the object cannot be converted.
     */
    public static Attribute getInstance(
            Object o) {
        if (o == null || o instanceof Attribute) {
            return (Attribute) o;
        }

        if (o instanceof ASN1Sequence) {
            return new Attribute((ASN1Sequence) o);
        }

        throw new IllegalArgumentException("unknown object in factory: " + o.getClass().getName());
    }

    public DERObjectIdentifier getAttrType() {
        return attrType;
    }

    public ASN1Set getAttrValues() {
        return attrValues;
    }

    /*
     * Produce an object suitable for an ASN1OutputStream.
     * <pre>
     * Attribute ::= SEQUENCE {
     *     attrType OBJECT IDENTIFIER,
     *     attrValues SET OF AttributeValue
     * }
     * </pre>
     */
    public DERObject toASN1Object() {
        ASN1EncodableVector v = new ASN1EncodableVector();

        v.add(attrType);
        v.add(attrValues);

        return new DERSequence(v);
    }
}
