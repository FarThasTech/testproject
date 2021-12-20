package com.billing.util;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import org.apache.commons.io.FileUtils;

import com.billing.exceptions.ExceptionMsg;

public class StringUtil {

	public static String fixed3DotLengthString(String string) {
		try {
			if(string != null && !string.trim().isEmpty()) {
				string = string.trim();
				if(string.length() >= 3) {
					return string;
				}else if(string.length() == 2) {
					string = string + ".";
				}else if(string.length() == 1) {
					string = string + "..";
				}
					
			}
			return string;
		} catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return "...";
	}
	
	public static boolean isPureAscii(String v) {
		try{
			byte bytearray []  = v.getBytes();
			CharsetDecoder d = Charset.forName("US-ASCII").newDecoder();
			try {
				CharBuffer r = d.decode(ByteBuffer.wrap(bytearray));
				r.toString();
			} catch(CharacterCodingException e) {
				return false;
			}
			return true;
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return false;
	}
	
	public static String readFileAsString(String fileName){
		String data = ""; 
	    try {
		    data = new String(Files.readAllBytes(Paths.get(fileName))); 
		    return data;
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	    return data;
	}
	
	public static boolean checkStringIsNull(String value){
	    try {
		    return value != null && !value.trim().isEmpty();
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	    return false;
	}
	
	public static boolean checkStringIsNotNullAndInteger(String value){
		try {
		    return value != null && !value.trim().isEmpty() && NumberUtil.checkNumeric(value.trim()) && Integer.valueOf(value.trim()) > 0;
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	    return false;
	}
	
	public static String getImageToEncodeImage(String imagePath) {
		try{
			if(StringUtil.checkStringIsNull(imagePath)) {
				File file = new File(MainUtil.getJbossConfDir()+ "\\"+ imagePath);
				if(file.exists()) {
					byte[] image = FileUtils.readFileToByteArray(file);
					if(image != null){
						String encodedImage =Base64.getEncoder().encodeToString(image);
						return "data:image/png;base64,"+encodedImage;
					}
				}
			}
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return null;
	}
	
}
