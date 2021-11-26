package com.zestic.coin.bouncycastle.crypto.tls;

import com.zestic.coin.bouncycastle.asn1.x509.X509CertificateStructure;

/*
 * A certificate verifyer, that will always return true.
 * <pre>
 * DO NOT USE THIS FILE UNLESS YOU KNOW EXACTLY WHAT YOU ARE DOING.
 * </pre>
 */
public class AlwaysValidVerifyer implements CertificateVerifyer {

    /*
     * Return true.
     *
     * @see
     * CertificateVerifyer#isValid(X509CertificateStructure[])
     */
    public boolean isValid(X509CertificateStructure[] certs) {
        return true;
    }

}
