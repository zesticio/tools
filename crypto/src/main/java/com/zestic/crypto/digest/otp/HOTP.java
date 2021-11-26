package com.zestic.crypto.digest.otp;

import com.zestic.core.codec.Base32;
import com.zestic.core.util.RandomUtil;
import com.zestic.crypto.digest.HMac;
import com.zestic.crypto.digest.HmacAlgorithm;

public class HOTP {

    private static final int[] MOD_DIVISORS = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};
    public static final int DEFAULT_PASSWORD_LENGTH = 6;
    public static final HmacAlgorithm HOTP_HMAC_ALGORITHM = HmacAlgorithm.HmacSHA1;

    private final HMac mac;
    private final int passwordLength;
    private final int modDivisor;

    private final byte[] buffer;

    public HOTP(byte[] key) {
        this(DEFAULT_PASSWORD_LENGTH, key);
    }

    public HOTP(int passwordLength, byte[] key) {
        this(passwordLength, HOTP_HMAC_ALGORITHM, key);
    }

    public HOTP(int passwordLength, HmacAlgorithm algorithm, byte[] key) {
        if (passwordLength >= MOD_DIVISORS.length) {
            throw new IllegalArgumentException("Password length must be < " + MOD_DIVISORS.length);
        }
        this.mac = new HMac(algorithm, key);
        this.modDivisor = MOD_DIVISORS[passwordLength];
        this.passwordLength = passwordLength;
        this.buffer = new byte[8];
    }

    public synchronized int generate(long counter) {
        // C integer values need to be expressed in a binary string, such as an event count to three,
        // C was the "11" (omitted here in front of the binary number 0)
        this.buffer[0] = (byte) ((counter & 0xff00000000000000L) >>> 56);
        this.buffer[1] = (byte) ((counter & 0x00ff000000000000L) >>> 48);
        this.buffer[2] = (byte) ((counter & 0x0000ff0000000000L) >>> 40);
        this.buffer[3] = (byte) ((counter & 0x000000ff00000000L) >>> 32);
        this.buffer[4] = (byte) ((counter & 0x00000000ff000000L) >>> 24);
        this.buffer[5] = (byte) ((counter & 0x0000000000ff0000L) >>> 16);
        this.buffer[6] = (byte) ((counter & 0x000000000000ff00L) >>> 8);
        this.buffer[7] = (byte) (counter & 0x00000000000000ffL);

        final byte[] digest = this.mac.digest(this.buffer);

        return truncate(digest);
    }

    public static String generateSecretKey(int numBytes) {
        return Base32.encode(RandomUtil.getSHA1PRNGRandom(RandomUtil.randomBytes(256)).generateSeed(numBytes));
    }

    public int getPasswordLength() {
        return this.passwordLength;
    }

    public String getAlgorithm() {
        return this.mac.getAlgorithm();
    }

    private int truncate(byte[] digest) {
        final int offset = digest[digest.length - 1] & 0x0f;
        return ((digest[offset] & 0x7f) << 24 |
                (digest[offset + 1] & 0xff) << 16 |
                (digest[offset + 2] & 0xff) << 8 |
                (digest[offset + 3] & 0xff)) %
                this.modDivisor;
    }
}
