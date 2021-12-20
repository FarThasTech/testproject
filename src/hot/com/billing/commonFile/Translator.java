package com.billing.commonFile;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jboss.seam.annotations.Name;
import org.json.JSONObject;	

@Name("translator")
public class Translator {
	
	public static String translate(String sourceLang,String destinationLang, String msg) {
		String translatedMsg = msg;
		try {
			if(sourceLang.equalsIgnoreCase(destinationLang))
				return msg;
			 
	        boolean translate = false;
	        if(translate) {
	        	return msg;
	        }else {
	        	CloseableHttpClient Client = HttpClients.createDefault();
		    	HttpPost httppost = new HttpPost("http://api.mymemory.translated.net/get");
		    	
		    	List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
				urlParameters.add(new BasicNameValuePair("q", msg));
				urlParameters.add(new BasicNameValuePair("langpair", sourceLang+"|"+destinationLang));
				urlParameters.add(new BasicNameValuePair("de", "info@softitservice.com"));
				httppost.setEntity(new UrlEncodedFormEntity(urlParameters));
				
		        CloseableHttpResponse response = Client.execute(httppost);
		        String bodyAsString = EntityUtils.toString(response.getEntity());
		        JSONObject jsonObj = new JSONObject(bodyAsString);
		        translatedMsg = (String) jsonObj.getJSONArray("matches").getJSONObject(0).get("translation");
		        if(translatedMsg.startsWith("%s")){
		        	translatedMsg = (String) jsonObj.getJSONArray("matches").getJSONObject(1).get("translation");
		        }
	        }
		}catch(Exception e) {
			translatedMsg = msg;
		}
		return translatedMsg;	
	}

}
