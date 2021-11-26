package com.zestic.crypto.digest.mac;

import com.zestic.crypto.SmUtil;
import com.zestic.crypto.digest.HmacAlgorithm;

import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

public class MacEngineFactory {

	public static MacEngine createEngine(String algorithm, Key key) {
		return createEngine(algorithm, key, null);
	}

	public static MacEngine createEngine(String algorithm, Key key, AlgorithmParameterSpec spec) {
		if (algorithm.equalsIgnoreCase(HmacAlgorithm.HmacSM3.getValue())) {
			// Hmac SM3 algorithm is BC library implementation, ignore the salt
			return SmUtil.createHmacSm3Engine(key.getEncoded());
		}
		return new DefaultHMacEngine(algorithm, key, spec);
	}
}
