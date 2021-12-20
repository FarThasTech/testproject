package com.billing.login;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public final class PasswordBean {
	
	private static PasswordBean instance = new PasswordBean();

	static final String AB = "passwordisvertystrongheresodonttrytobreak";
	static final String CHARNUMERIC = "0123456789abcdefghijklmnopqrstuvwxyz";
	static Random rnd = new Random();
	
	private static final char[] PASSWORD = AB.toCharArray();
    private static final String KEY = "PBEWithMD5AndDES";
    private static final byte[] SALT = { (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12, (byte) 0xde, (byte) 0x33,(byte) 0x10, (byte) 0x12, };

	public PasswordBean() {
	}

	/**
	 * Encrypt password
	 */
	public synchronized String encryptPassword(String plaintext) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA"); 
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		try {
			md.update(plaintext.getBytes("UTF-8")); 
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte raw[] = md.digest(); 
		String hash = Base64.getEncoder().encodeToString(raw); 
		return hash; 
	}

	public static synchronized String decryptWithoutAlgorithm(String ecryptedtext) {
		try {
			return new String(Base64.getDecoder().decode(ecryptedtext));
		} catch (Exception e) {
		}
		return null;
	}

	public static synchronized String encryptWithoutAlgorithm(String plaintext) {
		try {
			String hash = Base64.getEncoder().encodeToString(plaintext.getBytes());
			return hash;
		} catch (Exception e) {
		}
		return null;
	}
	
	public static synchronized String encryptWithMD5DES(String property){
		try {
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY);
			SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
			Cipher pbeCipher = Cipher.getInstance(KEY);
			pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT,	20));
			return base64Encode(pbeCipher.doFinal(property.getBytes("UTF-8"))).trim();
		} catch (Exception e) {
		}
		return null;
	}

	public static String base64Encode(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}

	public static synchronized String decryptWithMD5DES(String property){
		try {
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY);
			SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
			Cipher pbeCipher = Cipher.getInstance(KEY);
			pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT,	20));
			return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8").trim();
		} catch (Exception e) {
		}
		return null;
	}

	public static byte[] base64Decode(String property) throws IOException {
		return Base64.getDecoder().decode(property);
	}

	public static synchronized PasswordBean getInstance(){
		return instance;
	}
	
	public static String randomString(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(CHARNUMERIC.charAt(rnd.nextInt(CHARNUMERIC.length())));
		return sb.toString();
	}
}
