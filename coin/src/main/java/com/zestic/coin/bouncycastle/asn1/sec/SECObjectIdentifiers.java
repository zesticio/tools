package com.zestic.coin.bouncycastle.asn1.sec;

import com.zestic.coin.bouncycastle.asn1.DERObjectIdentifier;
import com.zestic.coin.bouncycastle.asn1.x9.X9ObjectIdentifiers;

public interface SECObjectIdentifiers {

    /*
     * ellipticCurve OBJECT IDENTIFIER ::= { iso(1) identified-organization(3)
     * certicom(132) curve(0) }
     */
    DERObjectIdentifier ellipticCurve = new DERObjectIdentifier("1.3.132.0");

    DERObjectIdentifier sect163k1 = new DERObjectIdentifier(ellipticCurve + ".1");
    DERObjectIdentifier sect163r1 = new DERObjectIdentifier(ellipticCurve + ".2");
    DERObjectIdentifier sect239k1 = new DERObjectIdentifier(ellipticCurve + ".3");
    DERObjectIdentifier sect113r1 = new DERObjectIdentifier(ellipticCurve + ".4");
    DERObjectIdentifier sect113r2 = new DERObjectIdentifier(ellipticCurve + ".5");
    DERObjectIdentifier secp112r1 = new DERObjectIdentifier(ellipticCurve + ".6");
    DERObjectIdentifier secp112r2 = new DERObjectIdentifier(ellipticCurve + ".7");
    DERObjectIdentifier secp160r1 = new DERObjectIdentifier(ellipticCurve + ".8");
    DERObjectIdentifier secp160k1 = new DERObjectIdentifier(ellipticCurve + ".9");
    DERObjectIdentifier secp256k1 = new DERObjectIdentifier(ellipticCurve + ".10");
    DERObjectIdentifier sect163r2 = new DERObjectIdentifier(ellipticCurve + ".15");
    DERObjectIdentifier sect283k1 = new DERObjectIdentifier(ellipticCurve + ".16");
    DERObjectIdentifier sect283r1 = new DERObjectIdentifier(ellipticCurve + ".17");
    DERObjectIdentifier sect131r1 = new DERObjectIdentifier(ellipticCurve + ".22");
    DERObjectIdentifier sect131r2 = new DERObjectIdentifier(ellipticCurve + ".23");
    DERObjectIdentifier sect193r1 = new DERObjectIdentifier(ellipticCurve + ".24");
    DERObjectIdentifier sect193r2 = new DERObjectIdentifier(ellipticCurve + ".25");
    DERObjectIdentifier sect233k1 = new DERObjectIdentifier(ellipticCurve + ".26");
    DERObjectIdentifier sect233r1 = new DERObjectIdentifier(ellipticCurve + ".27");
    DERObjectIdentifier secp128r1 = new DERObjectIdentifier(ellipticCurve + ".28");
    DERObjectIdentifier secp128r2 = new DERObjectIdentifier(ellipticCurve + ".29");
    DERObjectIdentifier secp160r2 = new DERObjectIdentifier(ellipticCurve + ".30");
    DERObjectIdentifier secp192k1 = new DERObjectIdentifier(ellipticCurve + ".31");
    DERObjectIdentifier secp224k1 = new DERObjectIdentifier(ellipticCurve + ".32");
    DERObjectIdentifier secp224r1 = new DERObjectIdentifier(ellipticCurve + ".33");
    DERObjectIdentifier secp384r1 = new DERObjectIdentifier(ellipticCurve + ".34");
    DERObjectIdentifier secp521r1 = new DERObjectIdentifier(ellipticCurve + ".35");
    DERObjectIdentifier sect409k1 = new DERObjectIdentifier(ellipticCurve + ".36");
    DERObjectIdentifier sect409r1 = new DERObjectIdentifier(ellipticCurve + ".37");
    DERObjectIdentifier sect571k1 = new DERObjectIdentifier(ellipticCurve + ".38");
    DERObjectIdentifier sect571r1 = new DERObjectIdentifier(ellipticCurve + ".39");

    DERObjectIdentifier secp192r1 = X9ObjectIdentifiers.prime192v1;
    DERObjectIdentifier secp256r1 = X9ObjectIdentifiers.prime256v1;

}
