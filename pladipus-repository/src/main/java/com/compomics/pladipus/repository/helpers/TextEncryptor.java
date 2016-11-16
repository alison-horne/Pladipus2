package com.compomics.pladipus.repository.helpers;

/**
 * Tool for encrypting/decrypting password for database
 */
public interface TextEncryptor {
	public String encryptString(String plainText);
	public String decryptString(String encryptedText);
}
