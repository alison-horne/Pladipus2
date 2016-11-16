package com.compomics.pladipus.repository.helpers;

/**
 * Tool for dealing with encryption of user passwords
 */
public interface PasswordEncryptor {
	
	/**
	 * Encrypts password ready to be stored in the database
	 * @param password, string unencrypted
	 * @return encrypted password string
	 */
	public String encryptPassword(String password);
	
	/**
	 * Checks whether password is valid by comparing plain text string against encrypted one
	 * @param plainPassword
	 * @param encryptedPassword
	 * @return true if password is correct, false if they don't match
	 */
	public boolean checkPassword(String plainPassword, String encryptedPassword);
}
