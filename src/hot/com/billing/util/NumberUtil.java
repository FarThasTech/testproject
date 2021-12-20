package com.billing.util;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.billing.exceptions.ExceptionMsg;

public class NumberUtil {

	public static boolean checkNumeric(String value){
		try {
			String expression = "^[0-9]*$";
	        CharSequence inputStr = value;
	        Pattern pattern = Pattern.compile(expression);
	        Matcher matcher = pattern.matcher(inputStr);
	        if(matcher.matches()){
	             return true; 
	        }else{
	        	return false;
	        }
		} catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return false;
	}
	
	public static boolean checkNumericWithDotSlashHyphen(String value){
		try {
			String expression = "^[0-9./-]*$";
	        CharSequence inputStr = value;
	        Pattern pattern = Pattern.compile(expression);
	        Matcher matcher = pattern.matcher(inputStr);
	        if(matcher.matches()){
	             return true; 
	        }else{
	        	return false;
	        }
		} catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return false;
	}
	
	public static boolean checkMobilePattern(String value){
		try {
			String expression = "^[+0-9]*$";
	        CharSequence inputStr = value;
	        Pattern pattern = Pattern.compile(expression);
	        Matcher matcher = pattern.matcher(inputStr);
	        if(matcher.matches()){
	             return true; 
	        }else{
	        	return false;
	        }
		} catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return false;
	}
	
	public static String doubleWith2Digit(double d) { 
    	try{
	        DecimalFormat fmt = new DecimalFormat("0.00");
	        String string = fmt.format(d);   
	        return string.replace(",", ".");
    	}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
    	return null;
    }
}
