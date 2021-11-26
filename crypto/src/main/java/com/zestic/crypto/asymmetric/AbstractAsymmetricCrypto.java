package com.zestic.crypto.asymmetric;

import java.security.PrivateKey;
import java.security.PublicKey;

/*
 * The abstract object of asymmetric encryption, wraps the encryption and decryption
 * for Hex and Base64 encapsulation
 *
 * @param <T> Return to its own type
 */
public abstract class AbstractAsymmetricCrypto<T extends AbstractAsymmetricCrypto<T>>
		extends BaseAsymmetric<T>
		implements AsymmetricEncryptor, AsymmetricDecryptor{
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------ Constructor start
	/*
	 * Private and public keys at the same time is empty generated when a pair of new private and public keys < br >
	 * Private and public keys can pass in a alone, so you can only use this key to encrypt or decrypt
	 *
	 * @param algorithm
	 * @param privateKey
	 * @param publicKey
	 */
	public AbstractAsymmetricCrypto(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
		super(algorithm, privateKey, publicKey);
	}
	// ------------------------------------------------------------------ Constructor end
}
