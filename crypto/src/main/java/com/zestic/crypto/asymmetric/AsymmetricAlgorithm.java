package com.zestic.crypto.asymmetric;

/*
 * Asymmetric algorithm type
 * see: https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyPairGenerator
 *
 */
public enum AsymmetricAlgorithm {
	/* RSA algorithm */
	RSA("RSA"), 
	/* RSA algorithm, this algorithm USES the default a way for RSA/ECB/PKCS1 Padding */
	RSA_ECB_PKCS1("RSA/ECB/PKCS1Padding"), 
	/* RSA algorithm, this algorithm USES the default a way for RSA/ECB/No Padding */
	RSA_ECB("RSA/ECB/NoPadding"),
	/* RSA algorithm, this algorithm with the RSA/None/No Padding */
	RSA_None("RSA/None/NoPadding");

	private final String value;

	/*
	 * structure
	 * @param value Algorithm character representation, case sensitive
	 */
	AsymmetricAlgorithm(String value) {
		this.value = value;
	}

	/*
	 * Get algorithm string representation, case sensitive
	 * @return Algorithm string representation
	 */
	public String getValue() {
		return this.value;
	}
}
