package com.zestic.coin.bouncycastle.asn1.gnu;

import com.zestic.coin.bouncycastle.asn1.DERObjectIdentifier;

public interface GNUObjectIdentifiers {

    DERObjectIdentifier GNU = new DERObjectIdentifier("1.3.6.1.4.1.11591.1"); // GNU Radius
    DERObjectIdentifier GnuPG = new DERObjectIdentifier("1.3.6.1.4.1.11591.2"); // GnuPG (Ägypten)
    DERObjectIdentifier notation = new DERObjectIdentifier("1.3.6.1.4.1.11591.2.1"); // notation
    DERObjectIdentifier pkaAddress = new DERObjectIdentifier("1.3.6.1.4.1.11591.2.1.1"); // pkaAddress
    DERObjectIdentifier GnuRadar = new DERObjectIdentifier("1.3.6.1.4.1.11591.3"); // GNU Radar
    DERObjectIdentifier digestAlgorithm = new DERObjectIdentifier("1.3.6.1.4.1.11591.12"); // digestAlgorithm
    DERObjectIdentifier Tiger_192 = new DERObjectIdentifier("1.3.6.1.4.1.11591.12.2"); // TIGER/192
    DERObjectIdentifier encryptionAlgorithm = new DERObjectIdentifier("1.3.6.1.4.1.11591.13"); // encryptionAlgorithm
    DERObjectIdentifier Serpent = new DERObjectIdentifier("1.3.6.1.4.1.11591.13.2"); // Serpent
    DERObjectIdentifier Serpent_128_ECB = new DERObjectIdentifier("1.3.6.1.4.1.11591.13.2.1"); // Serpent-128-ECB
    DERObjectIdentifier Serpent_128_CBC = new DERObjectIdentifier("1.3.6.1.4.1.11591.13.2.2"); // Serpent-128-CBC
    DERObjectIdentifier Serpent_128_OFB = new DERObjectIdentifier("1.3.6.1.4.1.11591.13.2.3"); // Serpent-128-OFB
    DERObjectIdentifier Serpent_128_CFB = new DERObjectIdentifier("1.3.6.1.4.1.11591.13.2.4"); // Serpent-128-CFB
    DERObjectIdentifier Serpent_192_ECB = new DERObjectIdentifier("1.3.6.1.4.1.11591.13.2.21"); // Serpent-192-ECB
    DERObjectIdentifier Serpent_192_CBC = new DERObjectIdentifier("1.3.6.1.4.1.11591.13.2.22"); // Serpent-192-CBC
    DERObjectIdentifier Serpent_192_OFB = new DERObjectIdentifier("1.3.6.1.4.1.11591.13.2.23"); // Serpent-192-OFB
    DERObjectIdentifier Serpent_192_CFB = new DERObjectIdentifier("1.3.6.1.4.1.11591.13.2.24"); // Serpent-192-CFB
    DERObjectIdentifier Serpent_256_ECB = new DERObjectIdentifier("1.3.6.1.4.1.11591.13.2.41"); // Serpent-256-ECB
    DERObjectIdentifier Serpent_256_CBC = new DERObjectIdentifier("1.3.6.1.4.1.11591.13.2.42"); // Serpent-256-CBC
    DERObjectIdentifier Serpent_256_OFB = new DERObjectIdentifier("1.3.6.1.4.1.11591.13.2.43"); // Serpent-256-OFB
    DERObjectIdentifier Serpent_256_CFB = new DERObjectIdentifier("1.3.6.1.4.1.11591.13.2.44"); // Serpent-256-CFB
    DERObjectIdentifier CRC = new DERObjectIdentifier("1.3.6.1.4.1.11591.14"); // CRC algorithms
    DERObjectIdentifier CRC32 = new DERObjectIdentifier("1.3.6.1.4.1.11591.14.1"); // CRC 32
}
