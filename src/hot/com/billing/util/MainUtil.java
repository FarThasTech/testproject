package com.billing.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.jboss.seam.annotations.Name;

import com.billing.exceptions.ExceptionMsg;

@Name("mainUtil")
@SuppressWarnings({"unchecked","rawtypes"})
public class MainUtil {

	public static Properties getProperties(){
		try {
			return getProperties("billing.properties");
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return null;
	}
	
	public static Properties getProperties(String propertyFileName) {
		InputStream resourceStream = null;
		try {
			resourceStream = new FileInputStream(getJbossConfDir() + getFileSeparator() + propertyFileName);
			Properties property = new Properties();
			try {
				property.load(resourceStream);
				resourceStream.close();
				return property;
			} catch (IOException ioe) {
				ExceptionMsg.ErrorMsg(ioe, Thread.currentThread().getStackTrace()[1]);
			}
		} catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return null;
	}
	
	public boolean checkLocal() {
		try {
			String local = getInfoFromProperty("Server_Mode");
			if(local != null && !local.isEmpty() && local.trim().equalsIgnoreCase("local")) {
				return true;
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return false;
	}
	
	public boolean checkAutoTrigger() {
		try {
			String autoTrigger = getInfoFromProperty("Auto_Trigger");
			if(autoTrigger != null && !autoTrigger.isEmpty() && autoTrigger.trim().equalsIgnoreCase("true")) {
				return true;
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return false;
	}
	
	public String getInfoFromProperty(String param) {
		String value = null;
		try {
			if(param != null && !param.isEmpty()) {
				Properties props = getProperties();
				Map<String, String> map = (Map)props;
				value = map.get(param);
			}
		}catch (Exception e) {
			value = "";
		}
		return value;
	}
	
	public static String getTempDirName() {

		String path = System.getProperty("java.io.tmpdir");
		return path;
	}
	
	public static String getFileSeparator() {
		return System.getProperty("file.separator");
	}
	
	public static String getJbossLogDir() {
		return System.getProperty("jboss.server.log.dir");
	}
	
	public static String getJbossConfDir() {
		return System.getProperty("jboss.server.config.dir");
	}
	
	public static String getJbossTempDir() {
		return System.getProperty("jboss.server.temp.dir");
	}
	
	public static String getImageStorePath(int companyId) {
		return getJbossConfDir() + getFileSeparator() + getImageSourceRealPath(companyId);
	}
	
	public static String getProductGroupImagePath(int companyId) {
		return getImageStorePath(companyId) + getFileSeparator() + "ProductGroup" + getFileSeparator() ;
	}
	
	public static String getLogoImagePath(int companyId) {
		return getImageStorePath(companyId) + getFileSeparator() + "Logo" + getFileSeparator() ;
	}
	
	public static String getImageSourceRealPath(int companyId) {
		return "images" + getFileSeparator() + "CompanyData" + getFileSeparator() + companyId ;
	}
	
	public static String getImageSourcePath(int companyId, String folderName) {
		return getFileSeparator() + getImageSourceRealPath(companyId) + getFileSeparator() + folderName + getFileSeparator() ;
	}
	
}
