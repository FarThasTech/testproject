package com.billing.recurring;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.international.LocaleSelector;

import com.billing.entity.Users;
import com.billing.exceptions.ExceptionMsg;

@Name("automaticTriggerBean")
public class AutomaticTriggerBean {
	
	@In
	public EntityManager entityManager;
	    
	@In
	public LocaleSelector localeSelector;
	
	@In(create=true,required=false) 
	public Users activeUser;
	
	public void executeAll() {
		try {
			
			Calendar calendar = Calendar.getInstance();
		    SimpleDateFormat sdf = new SimpleDateFormat("HH");
		    int hour = Integer.parseInt(sdf.format(calendar.getTime()));
		    
			/************************************ Create Recurring Entry For Next Month ************************************/
				
		    	if(hour > 5) {
		    		ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1], "Future Recurring Entry Creation Started...!");
					RecurringEntryGeneration recurringEntryGeneration = new RecurringEntryGeneration();
					recurringEntryGeneration.entityManager = entityManager;
					recurringEntryGeneration.generateNextDurationOnlyForAllOrganization();
					ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1], "Future Recurring Entry Creation Completed...!");
				}
				
			/************************************ Create Recurring Entry For Next Month ************************************/
				
			/***************************************** Automatic Recurring Payment *****************************************/
		    	
	    		ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1], "Automatic Recurring Payment Started...!");
	    		RecurringBean recurringBean = new RecurringBean();
	    		recurringBean.localeSelector = localeSelector;
	    		recurringBean.entityManager = entityManager;
	    		recurringBean.automaticRecurringList();
				ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1], "Automatic Recurring Payment Completed...!");
			
			/***************************************** Automatic Recurring Payment *****************************************/
		} catch(Exception e) {
			
		}
	}
}
