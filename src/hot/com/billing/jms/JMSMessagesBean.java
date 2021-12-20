package com.billing.jms;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionManagement;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.international.LocaleSelector;

import com.billing.category.CategoryBean;
import com.billing.entity.Category;
import com.billing.entity.Product;
import com.billing.entity.UserRole;
import com.billing.exceptions.ExceptionMsg;
import com.billing.product.ProductBean;
import com.billing.role.RoleBean;
import com.billing.util.NumberUtil;
import com.billing.util.StringUtil;

@MessageDriven(
		 messageListenerInterface = javax.jms.MessageListener.class,
		 name = "JMSMessagesBean",
	    activationConfig = {
            @ActivationConfigProperty(
                    propertyName = "connectionFactoryJndiName",
                    propertyValue = "java:/ConnectionFactory"),
            @ActivationConfigProperty(
                    propertyName = "destinationType",
                    propertyValue = "javax.jms.Queue"),
            @ActivationConfigProperty(
                    propertyName = "destination",	
                    propertyValue = "queue/JMSTrigger"),
    	    @ActivationConfigProperty(
    	    		propertyName = "acknowledgeMode",
    	    		propertyValue = "Auto-acknowledge")
	    })

@TransactionManagement(javax.ejb.TransactionManagementType.BEAN)
public class JMSMessagesBean
	implements MessageListener {
	
	@PersistenceContext
	public EntityManager entityManager;
		
	@Override
	public void onMessage(Message message) {
		try {
			Lifecycle.beginCall();
			UserTransaction utx=(UserTransaction)new InitialContext().lookup("java:jboss/UserTransaction");
			utx.begin();
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

			/***************************************************************************************************************************************/
			ObjectMessage objectMsg = ((ObjectMessage) message);
			
			String param = objectMsg.getObjectProperty("param").toString();
			String localeString = objectMsg.getObjectProperty("localeString").toString();
			String primaryId = objectMsg.getObjectProperty("primaryId").toString();
			System.out.println(param + " language creation Called");
			if(StringUtil.checkStringIsNull(param)) {
				if(param.equalsIgnoreCase("Role")) {
					try {
						if(localeString != null && !localeString.trim().isEmpty()
								&& primaryId != null && !primaryId.trim().isEmpty() 
								&& NumberUtil.checkNumeric(primaryId.trim()) && Integer.valueOf(primaryId.trim()) > 0) {
							UserRole userRole = entityManager.find(UserRole.class, Integer.valueOf(primaryId.trim()));
							System.out.println("UserRole Language Creation Started");
							boolean create = false;
							if(userRole != null) {
								create = true;
							}else {
								userRole = (UserRole) objectMsg.getObject();
								if(userRole != null) {
									create = true;
								}
							}
							if(create) {
								LocaleSelector jmsLocalSelector = new LocaleSelector();
								jmsLocalSelector.setLocaleString(localeString);
								RoleBean roleBean = new RoleBean();
								roleBean.entityManager = entityManager;
								roleBean.localeSelector = jmsLocalSelector;
								roleBean.createUserRoleLang(userRole, false);
							}else {
								System.err.println("UserRole Language not created for this user role id : " + primaryId);
							}
						}
					}catch(Exception e) {
						System.err.println("Error is : " + e.getMessage()+". UserRole Language not created for this user role id : " + primaryId);
					} finally {
						entityManager.flush();
					}
				} else if(param.equalsIgnoreCase("Category")) {
					try {
						if(localeString != null && !localeString.trim().isEmpty()
								&& primaryId != null && !primaryId.trim().isEmpty() 
								&& NumberUtil.checkNumeric(primaryId.trim()) && Integer.valueOf(primaryId.trim()) > 0) {
							System.out.println("Category Language Creation Started");
							Category category = entityManager.find(Category	.class, Integer.valueOf(primaryId.trim()));
							boolean create = false;
							if(category != null) {
								create = true;
							}else {
								category = (Category) objectMsg.getObject();
								if(category != null) {
									create = true;
								}
							}
							if(create) {
								LocaleSelector jmsLocalSelector = new LocaleSelector();
								jmsLocalSelector.setLocaleString(localeString);
								CategoryBean catBean = new CategoryBean();
								catBean.entityManager = entityManager;
								catBean.localeSelector = jmsLocalSelector;
								catBean.createCategoryLang(category, false);
							}else {
								System.err.println("Category Language not created for this category id : " + primaryId);
							}
						}
					}catch(Exception e) {
						System.err.println("Error is : " + e.getMessage()+". Category Language not created for this category id : " + primaryId);
					} finally {
						entityManager.flush();
					}
				} else if(param.equalsIgnoreCase("Product")) {
					try {
						if(localeString != null && !localeString.trim().isEmpty()
								&& primaryId != null && !primaryId.trim().isEmpty() 
								&& NumberUtil.checkNumeric(primaryId.trim()) && Integer.valueOf(primaryId.trim()) > 0) {
							Product product = entityManager.find(Product.class, Integer.valueOf(primaryId.trim()));
							System.out.println("Product Language Creation Started");
							boolean create = false;
							if(product != null) {
								create = true;
							}else {
								product = (Product) objectMsg.getObject();
								if(product != null) {
									create = true;
								}
							}
							if(create) {
								LocaleSelector jmsLocalSelector = new LocaleSelector();
								jmsLocalSelector.setLocaleString(localeString);
								ProductBean prodBean = new ProductBean();
								prodBean.entityManager = entityManager;
								prodBean.localeSelector = jmsLocalSelector;
								prodBean.createProductLang(product, false);
							}else {
								System.err.println("Product Language not created for this product id : " + primaryId);
							}
						}
					}catch(Exception e) {
						System.err.println("Error is : " + e.getMessage()+". Product Language not created for this product id : " + primaryId);
					} finally {
						entityManager.flush();
					}
				}
				System.out.println(param + " language creation done");
			}
	    	/***************************************************************************************************************************************/
	    	
	    	utx.commit();
			Lifecycle.endCall();
		} catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
}

// *** EO F ***
