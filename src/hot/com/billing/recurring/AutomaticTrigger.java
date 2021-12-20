package com.billing.recurring;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.ejb.Schedule;
import javax.ejb.Stateless;

import com.billing.util.MainUtil;

@Stateless(name="AutomaticTrigger")
public class AutomaticTrigger {
	
//	@Schedule(dayOfWeek = "*", hour = "*", minute = "*", second = "*/5", persistent = false)//Executes every 5 seconds
//	@Schedule(second = "*/30",minute = "*", hour = "*", info="Every 30 seconds", persistent = false)
//	@Schedule(dayOfWeek = "*", hour = "*", minute = "*/2", persistent = false)//Executes every 2 Minutes (even)
//	@Schedule(second="0", minute="*/5", hour="*", persistent=false)//every 5 minutes
//	@Schedule(dayOfWeek="*", hour="*/6", minute="0") //Executes every 6 hour on the hour.
//	@Schedule(hour = "1", minute = "0", second = "0", dayOfWeek = "*", persistent = true, info = "Every 1am")
//	@Schedule(dayOfWeek = "*",persistent = true)//every day at midnight.
//	@Schedule(dayOfWeek = "*", hour = "*", persistent = true)//Every half an hour
	@Schedule(dayOfWeek = "*", hour = "*", minute = "*/30", persistent = true)//Every half an hour
	public void backgroundProcessing(){
		
		try{
			String url = "";
			MainUtil mailUtil = new MainUtil();
			boolean local = mailUtil.checkLocal();
			boolean schedular = mailUtil.checkAutoTrigger();
			String domain = mailUtil.getInfoFromProperty("Domain_Name");
			if(!local && schedular && domain!= null && !domain.isEmpty()){
				url = domain + "/AutomaticTrigger.jsf";
				URL obj = new URL(url);
		        HttpURLConnection conn = (HttpURLConnection)obj.openConnection();
		        conn.setRequestMethod("GET");
		        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		        conn.setDoOutput(true);
		        conn.getResponseCode();
			}
		}catch (Exception e) {
			if(!new MainUtil().checkLocal())
				System.out.println("Error in AutomaticTriggerBean ::: "+e.getMessage());
		}
	}
	
}
