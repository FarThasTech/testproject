package com.billing.recurring;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionManagement;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.In;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.international.LocaleSelector;

import com.billing.exceptions.ExceptionMsg;

@MessageDriven(
		 messageListenerInterface = javax.jms.MessageListener.class,
		 name = "RecurringMessagesBean",
	    activationConfig = {
	            @ActivationConfigProperty(
	                    propertyName = "connectionFactoryJndiName",
	                    propertyValue = "java:/ConnectionFactory"),
	            @ActivationConfigProperty(
	                    propertyName = "destinationType",
	                    propertyValue = "javax.jms.Queue"),
	            @ActivationConfigProperty(
	    	                    propertyName = "destination",	
	    	                    propertyValue = "queue/AutomaticTrigger"),
	    	    @ActivationConfigProperty(
	    	    		propertyName = "acknowledgeMode",
	    	    		propertyValue = "Auto-acknowledge")
	    })

@TransactionManagement(javax.ejb.TransactionManagementType.BEAN)
public class RecurringMessagesBean
	implements MessageListener {
	
	@PersistenceContext
	public EntityManager entityManager;
		
	@In(create = true) 
	private RecurringBean recurringBean = new RecurringBean();

	@Override
	public void onMessage(Message message) {
		try {
			Lifecycle.beginCall();
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
			recurringBean.entityManager = entityManager;
			LocaleSelector jmsLocalSelector = new LocaleSelector();
			jmsLocalSelector.setLocaleString("en");
			recurringBean.localeSelector = jmsLocalSelector;
			/*********************************************/
			/********* new *******************************/
			ObjectMessage objectMsg = ((ObjectMessage) message);
			String fDetailsIds = objectMsg.getObjectProperty("fDetailsIds").toString();
			String fundGroupId = objectMsg.getObjectProperty("fundGroupId").toString();
			String userAccId = objectMsg.getObjectProperty("userAccId").toString();
			boolean manualCharge = false;
	    	/*********************************************/
	    	try{
	    		if(fDetailsIds != null && !fDetailsIds.trim().isEmpty() 
    				&& fundGroupId != null && !fundGroupId.trim().isEmpty() && !fundGroupId.trim().equalsIgnoreCase("0") 
    				&& userAccId != null && !userAccId.trim().isEmpty()) {
	    			recurringBean.prepareFinalRecurringList(fDetailsIds, fundGroupId, userAccId, manualCharge);
	    		} else {
	    			System.out.println("Having issue with this fundGroupId : " + fundGroupId);
	    		}
	    	}catch (Exception e) {
	    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			}
			Lifecycle.endCall();
		} catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
}

// *** EO F ***
