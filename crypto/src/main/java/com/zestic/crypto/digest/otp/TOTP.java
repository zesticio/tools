package com.zestic.crypto.digest.otp;

import com.zestic.core.util.StrUtil;
import com.zestic.crypto.digest.HmacAlgorithm;

import java.time.Duration;
import java.time.Instant;

public class TOTP extends HOTP {

    public static final Duration DEFAULT_TIME_STEP = Duration.ofSeconds(30);

    private final Duration timeStep;

    public TOTP(byte[] key) {
        this(DEFAULT_TIME_STEP, key);
    }

    public TOTP(Duration timeStep, byte[] key) {
        this(timeStep, DEFAULT_PASSWORD_LENGTH, key);
    }

    public TOTP(Duration timeStep, int passwordLength, byte[] key) {
        this(timeStep, passwordLength, HOTP_HMAC_ALGORITHM, key);
    }

    public TOTP(Duration timeStep, int passwordLength, HmacAlgorithm algorithm, byte[] key) {
        super(passwordLength, algorithm, key);
        this.timeStep = timeStep;
    }

    public int generate(Instant timestamp) {
        return this.generate(timestamp.toEpochMilli() / this.timeStep.toMillis());
    }

    public boolean validate(Instant timestamp, int offsetSize, int code) {
        if (offsetSize == 0) {
            return generate(timestamp) == code;
        }
        for (int i = -offsetSize; i <= offsetSize; i++) {
            if (generate(timestamp.plus(getTimeStep().multipliedBy(i))) == code) {
                return true;
            }
        }
        return false;
    }

    public static String generateGoogleSecretKey(String account, int numBytes) {
        return StrUtil.format("otpauth://totp/{}?secret={}", account, generateSecretKey(numBytes));
    }

    public Duration getTimeStep() {
        return this.timeStep;
    }
}
