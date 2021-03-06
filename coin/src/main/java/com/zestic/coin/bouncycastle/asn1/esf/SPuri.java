package com.zestic.coin.bouncycastle.asn1.esf;

import com.zestic.coin.bouncycastle.asn1.DERIA5String;
import com.zestic.coin.bouncycastle.asn1.DERObject;

public class SPuri {

    private final DERIA5String uri;

    public SPuri(
            DERIA5String uri) {
        this.uri = uri;
    }

    public static SPuri getInstance(
            Object obj) {
        if (obj instanceof SPuri) {
            return (SPuri) obj;
        } else if (obj instanceof DERIA5String) {
            return new SPuri((DERIA5String) obj);
        }

        throw new IllegalArgumentException(
                "unknown object in 'SPuri' factory: "
                        + obj.getClass().getName() + ".");
    }

    public DERIA5String getUri() {
        return uri;
    }

    /*
     * <pre>
     * SPuri ::= IA5String
     * </pre>
     */
    public DERObject toASN1Object() {
        return uri.getDERObject();
    }
}
