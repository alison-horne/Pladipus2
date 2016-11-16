package com.compomics.pladipus.repository.helpers.impl;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jasypt.util.text.BasicTextEncryptor;

import com.compomics.pladipus.repository.helpers.PasswordEncryptor;
import com.compomics.pladipus.repository.helpers.TextEncryptor;

public class BasicEncryptor implements PasswordEncryptor, TextEncryptor {
	
	private BasicPasswordEncryptor passwordEncryptor;
	private BasicTextEncryptor textEncryptor;
	private static final String ENCRYPTOR_PASSWORD = "PladipusPassword";
	
	public BasicEncryptor() {
		passwordEncryptor = new BasicPasswordEncryptor();
		textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword(getEncryptorPassword());
	}
	
	@Override
	public String encryptPassword(String password) {
        return passwordEncryptor.encryptPassword(password);
	}
	
	@Override
	public boolean checkPassword(String plainPassword, String encryptedPassword) {
		return passwordEncryptor.checkPassword(plainPassword, encryptedPassword);
	}

	@Override
	public String encryptString(String plainText) {
		return textEncryptor.encrypt(plainText);
	}

	@Override
	public String decryptString(String encryptedText) {
		return textEncryptor.decrypt(encryptedText);
	}

	private String getEncryptorPassword() {
		return ENCRYPTOR_PASSWORD;
	}
}
