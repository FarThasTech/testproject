package com.billing.donor;

import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.billing.exceptions.ExceptionMsg;
import com.billing.util.StringUtil;

public class EncryptDecrypt {

	public static String password = "h0U530f|r()5T|-|E1p5Y00U";
	public static String salt = "5@1|-K3(|)";
	
	public Cipher InitiateEncryptProcess() {  
    	try {
    		byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    		IvParameterSpec ivspec = new IvParameterSpec(iv);
     
    		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
    		SecretKey tmp = factory.generateSecret(spec);
    		SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
     
    		Cipher cipher_encrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
    		cipher_encrypt.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
    		return cipher_encrypt;
		}catch (Exception e) {
	    	ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
    	return null;
    }
	
	public Cipher InitiateDecryptProcess() {  
    	try {
    		byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			IvParameterSpec ivspec = new IvParameterSpec(iv);
         
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
         
			Cipher cipher_decrypt = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher_decrypt.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
    		return cipher_decrypt;
		}catch (Exception e) {
	    	ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
    	return null;
    }
	
	public String encryptData(Cipher cipherEncrypt, String strToEncrypt) {
	    try{
	    	if(cipherEncrypt != null && strToEncrypt != null && !strToEncrypt.trim().isEmpty()) {
	    		strToEncrypt = strToEncrypt.trim().toUpperCase().replaceAll("\\s+","").replaceAll(" ","");
	    		String result = Base64.getEncoder().encodeToString(cipherEncrypt.doFinal(strToEncrypt.getBytes("UTF-8"))); 
	    		return result;
	    	}
	    }catch (Exception e){
	        ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
	    }
	    return null;
	}
	
	public String decryptData(Cipher cipherDecrypt, String strToDecrypt) {
		try{
			if(cipherDecrypt != null && strToDecrypt != null && !strToDecrypt.trim().isEmpty()) {
				String result = new String(cipherDecrypt.doFinal(Base64.getDecoder().decode(strToDecrypt.trim())));
				if(result != null && StringUtil.isPureAscii(result)) {
		        	return result;
		        }
			}
	    }catch (Exception e) {
	    	ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
	    }
	    return null;
	}
	
	public static String correctTheDecrypValue(String value) {
		try{
			if(value != null && !value.trim().isEmpty()) {
				if(value.contains(" ")) {
					value = value.replaceAll(" ", "+");
				}
			}
	    }catch (Exception e) {
	    	ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
	    }
	    return value;
	}
	
	
}
