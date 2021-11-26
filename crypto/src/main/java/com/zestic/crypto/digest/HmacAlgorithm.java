package com.zestic.crypto.digest;

public enum HmacAlgorithm {
	HmacMD5("HmacMD5"), 
	HmacSHA1("HmacSHA1"), 
	HmacSHA256("HmacSHA256"), 
	HmacSHA384("HmacSHA384"), 
	HmacSHA512("HmacSHA512"),
	/* Hmac SM3 algorithm implementation, need Bouncy Castle library support */
	HmacSM3("HmacSM3");

	private final String value;

	HmacAlgorithm(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
