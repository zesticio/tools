package com.zestic.coin.bouncycastle.asn1.oiw;

import com.zestic.coin.bouncycastle.asn1.DERObjectIdentifier;

public interface OIWObjectIdentifiers {

    // id-SHA1 OBJECT IDENTIFIER ::=    
    //   {iso(1) identified-organization(3) oiw(14) secsig(3) algorithms(2) 26 }    //
    DERObjectIdentifier md4WithRSA = new DERObjectIdentifier("1.3.14.3.2.2");
    DERObjectIdentifier md5WithRSA = new DERObjectIdentifier("1.3.14.3.2.3");
    DERObjectIdentifier md4WithRSAEncryption = new DERObjectIdentifier("1.3.14.3.2.4");

    DERObjectIdentifier desECB = new DERObjectIdentifier("1.3.14.3.2.6");
    DERObjectIdentifier desCBC = new DERObjectIdentifier("1.3.14.3.2.7");
    DERObjectIdentifier desOFB = new DERObjectIdentifier("1.3.14.3.2.8");
    DERObjectIdentifier desCFB = new DERObjectIdentifier("1.3.14.3.2.9");

    DERObjectIdentifier desEDE = new DERObjectIdentifier("1.3.14.3.2.17");

    DERObjectIdentifier idSHA1 = new DERObjectIdentifier("1.3.14.3.2.26");

    DERObjectIdentifier dsaWithSHA1 = new DERObjectIdentifier("1.3.14.3.2.27");

    DERObjectIdentifier sha1WithRSA = new DERObjectIdentifier("1.3.14.3.2.29");

    // ElGamal Algorithm OBJECT IDENTIFIER ::=    
    // {iso(1) identified-organization(3) oiw(14) dirservsig(7) algorithm(2) encryption(1) 1 }
    //
    DERObjectIdentifier elGamalAlgorithm = new DERObjectIdentifier("1.3.14.7.2.1.1");

}
