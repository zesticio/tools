package com.zestic.crypto.asymmetric;

import com.zestic.core.codec.Base64;
import com.zestic.core.util.*;
import com.zestic.crypto.KeyUtil;
import com.zestic.crypto.SecureUtil;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PublicKey;

/*
 * RSA algorithm unit tests
 */
public class RSATest {

    @Test
    public void generateKeyPairTest() {
        KeyPair pair = KeyUtil.generateKeyPair("RSA");
        Assert.assertNotNull(pair.getPrivate());
        Assert.assertNotNull(pair.getPublic());
    }

    @Test
    public void rsaCustomKeyTest() {
        KeyPair pair = KeyUtil.generateKeyPair("RSA");
        byte[] privateKey = pair.getPrivate().getEncoded();
        byte[] publicKey = pair.getPublic().getEncoded();

        RSA rsa = SecureUtil.rsa(privateKey, publicKey);

        // Public-key encryption private key to decrypt
        byte[] encrypt = rsa.encrypt(StrUtil.bytes("I am a test aaaa", CharsetUtil.CHARSET_UTF_8), KeyType.PublicKey);
        byte[] decrypt = rsa.decrypt(encrypt, KeyType.PrivateKey);
        Assert.assertEquals("I am a test aaaa", StrUtil.str(decrypt, CharsetUtil.CHARSET_UTF_8));

        // The private key encryption, public key to decrypt
        byte[] encrypt2 = rsa.encrypt(StrUtil.bytes("I am a test aaaa", CharsetUtil.CHARSET_UTF_8), KeyType.PrivateKey);
        byte[] decrypt2 = rsa.decrypt(encrypt2, KeyType.PublicKey);
        Assert.assertEquals("I am a test aaaa", StrUtil.str(decrypt2, CharsetUtil.CHARSET_UTF_8));
    }

    @Test
    public void rsaTest() {
        final RSA rsa = new RSA();

        // Access to private and public keys
        Assert.assertNotNull(rsa.getPrivateKey());
        Assert.assertNotNull(rsa.getPrivateKeyBase64());
        Assert.assertNotNull(rsa.getPublicKey());
        Assert.assertNotNull(rsa.getPrivateKeyBase64());

        // Public-key encryption private key to decrypt
        byte[] encrypt = rsa.encrypt(StrUtil.bytes("I am a test aaaa", CharsetUtil.CHARSET_UTF_8), KeyType.PublicKey);

        byte[] decrypt = rsa.decrypt(encrypt, KeyType.PrivateKey);
        Assert.assertEquals("I am a test aaaa", StrUtil.str(decrypt, CharsetUtil.CHARSET_UTF_8));

        // The private key encryption, public key to decrypt
        byte[] encrypt2 = rsa.encrypt(StrUtil.bytes("I am a test aaaa", CharsetUtil.CHARSET_UTF_8), KeyType.PrivateKey);
        byte[] decrypt2 = rsa.decrypt(encrypt2, KeyType.PublicKey);
        Assert.assertEquals("I am a test aaaa", StrUtil.str(decrypt2, CharsetUtil.CHARSET_UTF_8));
    }

    @Test
    public void rsaECBTest() {
        final RSA rsa = new RSA(AsymmetricAlgorithm.RSA_ECB.getValue());

        // Access to private and public keys
        Assert.assertNotNull(rsa.getPrivateKey());
        Assert.assertNotNull(rsa.getPrivateKeyBase64());
        Assert.assertNotNull(rsa.getPublicKey());
        Assert.assertNotNull(rsa.getPrivateKeyBase64());

        // Public-key encryption private key to decrypt
        byte[] encrypt = rsa.encrypt(StrUtil.bytes("I am a test aaaa", CharsetUtil.CHARSET_UTF_8), KeyType.PublicKey);

        byte[] decrypt = rsa.decrypt(encrypt, KeyType.PrivateKey);
        Assert.assertEquals("I am a test aaaa", StrUtil.str(decrypt, CharsetUtil.CHARSET_UTF_8));

        // The private key encryption, public key to decrypt
        byte[] encrypt2 = rsa.encrypt(StrUtil.bytes("I am a test aaaa", CharsetUtil.CHARSET_UTF_8), KeyType.PrivateKey);
        byte[] decrypt2 = rsa.decrypt(encrypt2, KeyType.PublicKey);
        Assert.assertEquals("I am a test aaaa", StrUtil.str(decrypt2, CharsetUtil.CHARSET_UTF_8));
    }

    @Test
    public void rsaNoneTest() {
        final RSA rsa = new RSA(AsymmetricAlgorithm.RSA_None.getValue());

        // Access to private and public keys
        Assert.assertNotNull(rsa.getPrivateKey());
        Assert.assertNotNull(rsa.getPrivateKeyBase64());
        Assert.assertNotNull(rsa.getPublicKey());
        Assert.assertNotNull(rsa.getPrivateKeyBase64());

        // Public-key encryption private key to decrypt
        byte[] encrypt = rsa.encrypt(StrUtil.bytes("I am a test aaaa", CharsetUtil.CHARSET_UTF_8), KeyType.PublicKey);

        byte[] decrypt = rsa.decrypt(encrypt, KeyType.PrivateKey);
        Assert.assertEquals("I am a test aaaa", StrUtil.str(decrypt, CharsetUtil.CHARSET_UTF_8));

        // The private key encryption, public key to decrypt
        byte[] encrypt2 = rsa.encrypt(StrUtil.bytes("I am a test aaaa", CharsetUtil.CHARSET_UTF_8), KeyType.PrivateKey);
        byte[] decrypt2 = rsa.decrypt(encrypt2, KeyType.PublicKey);
        Assert.assertEquals("I am a test aaaa", StrUtil.str(decrypt2, CharsetUtil.CHARSET_UTF_8));
    }

    @Test
    public void rsaWithBlockTest2() {
        final RSA rsa = new RSA();
        rsa.setEncryptBlockSize(3);

        // Access to private and public keys
        Assert.assertNotNull(rsa.getPrivateKey());
        Assert.assertNotNull(rsa.getPrivateKeyBase64());
        Assert.assertNotNull(rsa.getPublicKey());
        Assert.assertNotNull(rsa.getPrivateKeyBase64());

        // Public-key encryption private key to decrypt
        byte[] encrypt = rsa.encrypt(StrUtil.bytes("I am a test aaaa", CharsetUtil.CHARSET_UTF_8), KeyType.PublicKey);
        byte[] decrypt = rsa.decrypt(encrypt, KeyType.PrivateKey);
        Assert.assertEquals("I am a test aaaa", StrUtil.str(decrypt, CharsetUtil.CHARSET_UTF_8));

        // The private key encryption, public key to decrypt
        byte[] encrypt2 = rsa.encrypt(StrUtil.bytes("I am a test aaaa", CharsetUtil.CHARSET_UTF_8), KeyType.PrivateKey);
        byte[] decrypt2 = rsa.decrypt(encrypt2, KeyType.PublicKey);
        Assert.assertEquals("I am a test aaaa", StrUtil.str(decrypt2, CharsetUtil.CHARSET_UTF_8));
    }

    @Test
    public void rsaBcdTest() {
        String text = "I am a test aaaa";

        final RSA rsa = new RSA();

        // Public-key encryption private key to decrypt
        String encryptStr = rsa.encryptBcd(text, KeyType.PublicKey);
        String decryptStr = StrUtil.utf8Str(rsa.decryptFromBcd(encryptStr, KeyType.PrivateKey));
        Assert.assertEquals(text, decryptStr);

        // The private key encryption, public key to decrypt
        String encrypt2 = rsa.encryptBcd(text, KeyType.PrivateKey);
        String decrypt2 = StrUtil.utf8Str(rsa.decryptFromBcd(encrypt2, KeyType.PublicKey));
        Assert.assertEquals(text, decrypt2);
    }

    @Test
    public void rsaBase64Test() {
        String textBase = "I am a special long test";
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            text.append(textBase);
        }

        final RSA rsa = new RSA();
        // Public-key encryption private key to decrypt
        String encryptStr = rsa.encryptBase64(text.toString(), KeyType.PublicKey);
        String decryptStr = StrUtil.utf8Str(rsa.decrypt(encryptStr, KeyType.PrivateKey));
        Assert.assertEquals(text.toString(), decryptStr);

        // The private key encryption, public key to decrypt
        String encrypt2 = rsa.encryptBase64(text.toString(), KeyType.PrivateKey);
        String decrypt2 = StrUtil.utf8Str(rsa.decrypt(encrypt2, KeyType.PublicKey));
        Assert.assertEquals(text.toString(), decrypt2);
    }

    @Test
    public void rsaDecodeTest() {
        String PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIL7pbQ+5KKGYRhw7jE31hmA" //
                + "f8Q60ybd+xZuRmuO5kOFBRqXGxKTQ9TfQI+aMW+0lw/kibKzaD/EKV91107xE384qOy6IcuBfaR5lv39OcoqNZ"//
                + "5l+Dah5ABGnVkBP9fKOFhPgghBknTRo0/rZFGI6Q1UHXb+4atP++LNFlDymJcPAgMBAAECgYBammGb1alndta" //
                + "xBmTtLLdveoBmp14p04D8mhkiC33iFKBcLUvvxGg2Vpuc+cbagyu/NZG+R/WDrlgEDUp6861M5BeFN0L9O4hz"//
                + "GAEn8xyTE96f8sh4VlRmBOvVdwZqRO+ilkOM96+KL88A9RKdp8V2tna7TM6oI3LHDyf/JBoXaQJBAMcVN7fKlYP" //
                + "Skzfh/yZzW2fmC0ZNg/qaW8Oa/wfDxlWjgnS0p/EKWZ8BxjR/d199L3i/KMaGdfpaWbYZLvYENqUCQQCobjsuCW"//
                + "nlZhcWajjzpsSuy8/bICVEpUax1fUZ58Mq69CQXfaZemD9Ar4omzuEAAs2/uee3kt3AvCBaeq05NyjAkBme8SwB0iK"//
                + "kLcaeGuJlq7CQIkjSrobIqUEf+CzVZPe+AorG+isS+Cw2w/2bHu+G0p5xSYvdH59P0+ZT0N+f9LFAkA6v3Ae56OrI"//
                + "wfMhrJksfeKbIaMjNLS9b8JynIaXg9iCiyOHmgkMl5gAbPoH/ULXqSKwzBw5mJ2GW1gBlyaSfV3AkA/RJC+adIjsRGg"//
                + "JOkiRjSmPpGv3FOhl9fsBPjupZBEIuoMWOC8GXK/73DHxwmfNmN7C9+sIi4RBcjEeQ5F5FHZ";

        RSA rsa = new RSA(PRIVATE_KEY, null);

        String a = "2707F9FD4288CEF302C972058712F24A5F3EC62C5A14AD2FC59DAB93503AA0FA17113A020EE4EA35EB53F" //
                + "75F36564BA1DABAA20F3B90FD39315C30E68FE8A1803B36C29029B23EB612C06ACF3A34BE815074F5EB5AA3A"//
                + "C0C8832EC42DA725B4E1C38EF4EA1B85904F8B10B2D62EA782B813229F9090E6F7394E42E6F44494BB8";

        byte[] aByte = HexUtil.decodeHex(a);
        byte[] decrypt = rsa.decrypt(aByte, KeyType.PrivateKey);

        //Assert.assertEquals("Tiger rushed through hangzhou, how to look up into the sky, not just farming", StrUtil.str(decrypt, CharsetUtil.CHARSET_UTF_8));
    }

    @Test
    public void rsaTest2() throws Exception {
        String publicKeyStr = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDgtQn2JZ34ZC28NWYpAUd98iZ37BUrX/aKzmFbt7clFSs6s" +
                "XqHauqKWqdtLkF2KexO40H1YTX8z2lSgBBOAxLsvaklV8k4cBFK9snQXE9/DDaFt6Rr7iVZMldczhC0JNgTz+SHXT6CBHuX3e9S" +
                "dB1Ua44oncaTWz7OBGLbCiK45wIDAQAB";

        byte[] keyBytes = Base64.decode(publicKeyStr);
        PublicKey publicKey = KeyUtil.generateRSAPublicKey(keyBytes);

        byte[] data = RandomUtil.randomString("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", 16).getBytes();
        //Does not meet the length 128 0
        byte[] finalData = ArrayUtil.resize(data, 128);

        //The JDK native encryption
        Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        String result1 = HexUtil.encodeHexStr(cipher.doFinal(finalData));

        //encryption
        RSA rsa = new RSA("RSA/ECB/NoPadding", null, publicKeyStr);
        rsa.setEncryptBlockSize(128);
        String result2 = rsa.encryptHex(finalData, KeyType.PublicKey);

        Assert.assertEquals(result1, result2);
    }

    @Test
    public void exponentTest() {
        String modulus = "BD99BAAB9E56B7FD85FB8BCF53CAD2913C1ACEF9063E7C913CD6FC4FEE040DA44D8" +
                "ADAA35A9DCABD6E936C402D47278049638407135BAB22BB091396CB6873195C8AC8B0B7AB123" +
                "C3BF7A6341A4419BDBC0EFB85DBCD9A3AD12C99E2265BDCC1197913749E2AFA568EB7623DA3A" +
                "361335AA1F9FFA6E1801DDC8228AA86306B87";
        String publicExponent = "65537";
        RSA rsa = new RSA(new BigInteger(modulus, 16), null, new BigInteger(publicExponent));

        final String encryptBase64 = rsa.encryptBase64("The test content", KeyType.PublicKey);
        Assert.assertNotNull(encryptBase64);
    }
}
