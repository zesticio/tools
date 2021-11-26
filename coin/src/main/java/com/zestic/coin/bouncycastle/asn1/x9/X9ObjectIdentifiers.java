package com.zestic.coin.bouncycastle.asn1.x9;

import com.zestic.coin.bouncycastle.asn1.DERObjectIdentifier;

public interface X9ObjectIdentifiers {

    //
    // X9.62
    //
    // ansi-X9-62 OBJECT IDENTIFIER ::= { iso(1) member-body(2)
    //            us(840) ansi-x962(10045) }
    //
    String ansi_X9_62 = "1.2.840.10045";
    String id_fieldType = ansi_X9_62 + ".1";

    DERObjectIdentifier prime_field
            = new DERObjectIdentifier(id_fieldType + ".1");

    DERObjectIdentifier characteristic_two_field
            = new DERObjectIdentifier(id_fieldType + ".2");

    DERObjectIdentifier gnBasis
            = new DERObjectIdentifier(id_fieldType + ".2.3.1");

    DERObjectIdentifier tpBasis
            = new DERObjectIdentifier(id_fieldType + ".2.3.2");

    DERObjectIdentifier ppBasis
            = new DERObjectIdentifier(id_fieldType + ".2.3.3");

    String id_ecSigType = ansi_X9_62 + ".4";

    DERObjectIdentifier ecdsa_with_SHA1
            = new DERObjectIdentifier(id_ecSigType + ".1");

    String id_publicKeyType = ansi_X9_62 + ".2";

    DERObjectIdentifier id_ecPublicKey
            = new DERObjectIdentifier(id_publicKeyType + ".1");

    DERObjectIdentifier ecdsa_with_SHA2
            = new DERObjectIdentifier(id_ecSigType + ".3");

    DERObjectIdentifier ecdsa_with_SHA224
            = new DERObjectIdentifier(ecdsa_with_SHA2 + ".1");

    DERObjectIdentifier ecdsa_with_SHA256
            = new DERObjectIdentifier(ecdsa_with_SHA2 + ".2");

    DERObjectIdentifier ecdsa_with_SHA384
            = new DERObjectIdentifier(ecdsa_with_SHA2 + ".3");

    DERObjectIdentifier ecdsa_with_SHA512
            = new DERObjectIdentifier(ecdsa_with_SHA2 + ".4");

    //
    // named curves
    //
    String ellipticCurve = ansi_X9_62 + ".3";

    //
    // Two Curves
    //
    String cTwoCurve = ellipticCurve + ".0";

    DERObjectIdentifier c2pnb163v1 = new DERObjectIdentifier(cTwoCurve + ".1");
    DERObjectIdentifier c2pnb163v2 = new DERObjectIdentifier(cTwoCurve + ".2");
    DERObjectIdentifier c2pnb163v3 = new DERObjectIdentifier(cTwoCurve + ".3");
    DERObjectIdentifier c2pnb176w1 = new DERObjectIdentifier(cTwoCurve + ".4");
    DERObjectIdentifier c2tnb191v1 = new DERObjectIdentifier(cTwoCurve + ".5");
    DERObjectIdentifier c2tnb191v2 = new DERObjectIdentifier(cTwoCurve + ".6");
    DERObjectIdentifier c2tnb191v3 = new DERObjectIdentifier(cTwoCurve + ".7");
    DERObjectIdentifier c2onb191v4 = new DERObjectIdentifier(cTwoCurve + ".8");
    DERObjectIdentifier c2onb191v5 = new DERObjectIdentifier(cTwoCurve + ".9");
    DERObjectIdentifier c2pnb208w1 = new DERObjectIdentifier(cTwoCurve + ".10");
    DERObjectIdentifier c2tnb239v1 = new DERObjectIdentifier(cTwoCurve + ".11");
    DERObjectIdentifier c2tnb239v2 = new DERObjectIdentifier(cTwoCurve + ".12");
    DERObjectIdentifier c2tnb239v3 = new DERObjectIdentifier(cTwoCurve + ".13");
    DERObjectIdentifier c2onb239v4 = new DERObjectIdentifier(cTwoCurve + ".14");
    DERObjectIdentifier c2onb239v5 = new DERObjectIdentifier(cTwoCurve + ".15");
    DERObjectIdentifier c2pnb272w1 = new DERObjectIdentifier(cTwoCurve + ".16");
    DERObjectIdentifier c2pnb304w1 = new DERObjectIdentifier(cTwoCurve + ".17");
    DERObjectIdentifier c2tnb359v1 = new DERObjectIdentifier(cTwoCurve + ".18");
    DERObjectIdentifier c2pnb368w1 = new DERObjectIdentifier(cTwoCurve + ".19");
    DERObjectIdentifier c2tnb431r1 = new DERObjectIdentifier(cTwoCurve + ".20");

    //
    // Prime
    //
    String primeCurve = ellipticCurve + ".1";

    DERObjectIdentifier prime192v1 = new DERObjectIdentifier(primeCurve + ".1");
    DERObjectIdentifier prime192v2 = new DERObjectIdentifier(primeCurve + ".2");
    DERObjectIdentifier prime192v3 = new DERObjectIdentifier(primeCurve + ".3");
    DERObjectIdentifier prime239v1 = new DERObjectIdentifier(primeCurve + ".4");
    DERObjectIdentifier prime239v2 = new DERObjectIdentifier(primeCurve + ".5");
    DERObjectIdentifier prime239v3 = new DERObjectIdentifier(primeCurve + ".6");
    DERObjectIdentifier prime256v1 = new DERObjectIdentifier(primeCurve + ".7");

    //
    // Diffie-Hellman
    //
    // dhpublicnumber OBJECT IDENTIFIER ::= { iso(1) member-body(2)
    //            us(840) ansi-x942(10046) number-type(2) 1 }
    //
    DERObjectIdentifier dhpublicnumber = new DERObjectIdentifier("1.2.840.10046.2.1");

    //
    // DSA
    //
    // dsapublicnumber OBJECT IDENTIFIER ::= { iso(1) member-body(2)
    //            us(840) ansi-x957(10040) number-type(4) 1 }
    DERObjectIdentifier id_dsa = new DERObjectIdentifier("1.2.840.10040.4.1");

    /*
     * id-dsa-with-sha1 OBJECT IDENTIFIER ::= { iso(1) member-body(2) us(840)
     * x9-57 (10040) x9cm(4) 3 }
     */
    DERObjectIdentifier id_dsa_with_sha1 = new DERObjectIdentifier("1.2.840.10040.4.3");

    /*
     * X9.63
     */
    DERObjectIdentifier x9_63_scheme = new DERObjectIdentifier("1.3.133.16.840.63.0");
    DERObjectIdentifier dhSinglePass_stdDH_sha1kdf_scheme = new DERObjectIdentifier(x9_63_scheme + ".2");
    DERObjectIdentifier dhSinglePass_cofactorDH_sha1kdf_scheme = new DERObjectIdentifier(x9_63_scheme + ".3");
    DERObjectIdentifier mqvSinglePass_sha1kdf_scheme = new DERObjectIdentifier(x9_63_scheme + ".16");

    /*
     * X9.42
     */
    DERObjectIdentifier x9_42_schemes = new DERObjectIdentifier("1.2.840.10046.3");
    DERObjectIdentifier dhStatic = new DERObjectIdentifier(x9_42_schemes + ".1");
    DERObjectIdentifier dhEphem = new DERObjectIdentifier(x9_42_schemes + ".2");
    DERObjectIdentifier dhOneFlow = new DERObjectIdentifier(x9_42_schemes + ".3");
    DERObjectIdentifier dhHybrid1 = new DERObjectIdentifier(x9_42_schemes + ".4");
    DERObjectIdentifier dhHybrid2 = new DERObjectIdentifier(x9_42_schemes + ".5");
    DERObjectIdentifier dhHybridOneFlow = new DERObjectIdentifier(x9_42_schemes + ".6");
    DERObjectIdentifier mqv2 = new DERObjectIdentifier(x9_42_schemes + ".7");
    DERObjectIdentifier mqv1 = new DERObjectIdentifier(x9_42_schemes + ".8");
}
