package com.billing.donor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;

import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.international.LocaleSelector;

import com.billing.entity.Company;
import com.billing.entity.Languages;
import com.billing.entity.PaymentMethod;
import com.billing.entity.PaymentStatus;
import com.billing.entity.PaymentType;
import com.billing.entity.ProductGroup;
import com.billing.entity.Users;
import com.billing.exceptions.ExceptionMsg;
import com.billing.language.LanguageBean;
import com.billing.login.PasswordBean;
import com.billing.paymethod.PayMethodBean;
import com.billing.paymethod.StripeBean;
import com.billing.paystatus.PaymentStatusBean;
import com.billing.staticvalue.StaticValues;
import com.billing.util.MainUtil;
import com.billing.util.StringUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@SuppressWarnings({"unused","deprecation","unchecked"})
public class DonationWebhook extends HttpServlet{

	private static final long serialVersionUID = 1L;

	@PersistenceContext public EntityManager entityManager;
	
    @Resource UserTransaction userTransaction;
    
    public void doGet(HttpServletRequest req, HttpServletResponse res){
		try{
			userTransaction.begin();
    		Lifecycle.beginCall();
			res.addHeader("Access-Control-Allow-Origin", "*");
			res.addHeader("Access-Control-Allow-Credentials", "true");
			res.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS, HEAD");
			res.addHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With");
			res.setContentType("text/html; charset=UTF-8");
			res.getWriter().write("result".replace("\",\"", "\",\n\""));
    		Lifecycle.endCall();
    		userTransaction.commit();;
		}catch(Exception e){
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
    }
    
    public void doPost(HttpServletRequest req, HttpServletResponse res){
		try{
			String info = "";
			userTransaction.begin();
			String encoding = PasswordBean.base64Encode("dboxwebook:pwd".getBytes());
			String authorization = req.getHeader("Authorization");
			String userAgent = req.getHeader("User-Agent");
			JsonObject jsonContent = obtainContent(req);
			ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1], "Donation Webhook Json info : "+jsonContent);
			if (authorization != null && authorization.contains(encoding)) {
				info = processDonationData(jsonContent, "dBox", res, req);
				res.getWriter().write(info);
			} else {
				info = "Invalid Authorization";
				ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1], info);
				res.getWriter().write(info);
			}
			userTransaction.commit();
		}catch(Exception e){
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
    }
    
	public String processDonationData(JsonObject jsonContent, String type, HttpServletResponse res, HttpServletRequest req) {
    	try {
    		Lifecycle.beginCall();
    		String responseStr = "";
    		JsonObject jsonData = (JsonObject) jsonContent.get(type);
    		
    		String companyId = null; try{companyId =  jsonData.get("CompanyId").getAsString(); }catch(Exception e){ companyId = null; }
    		String userId = null; try{userId =  jsonData.get("UserId").getAsString(); }catch(Exception e){ userId = null; }
    		String langCode = null; try{langCode =  jsonData.get("LangCode").getAsString(); }catch(Exception e){ langCode = null; }
    		
    		String name = null; try{name =  jsonData.get("FirstName").getAsString(); }catch(Exception e){ name = null; }
    		//String lastName = null; try{lastName =  jsonData.get("LastName").getAsString(); }catch(Exception e){ lastName = null; }
    		String email = null; try{email =  jsonData.get("Email").getAsString(); }catch(Exception e){ email = null; }
    		String phone = null; try{phone =  jsonData.get("Phone").getAsString(); }catch(Exception e){ phone = null; }
    		String street = null; try{street =  jsonData.get("Street").getAsString(); }catch(Exception e){ street = null; }
    		String state = null; try{state =  jsonData.get("State").getAsString(); }catch(Exception e){ state = null; }
    		String houseNo = null; try{houseNo =  jsonData.get("HouseNo").getAsString(); }catch(Exception e){ houseNo = null; }
    		String city = null; try{city =  jsonData.get("City").getAsString(); }catch(Exception e){ city = null; }
    		String country = null; try{country =  jsonData.get("Country").getAsString(); }catch(Exception e){ country = null; }
    		String zip = null; try{zip =  jsonData.get("PostalCode").getAsString(); }catch(Exception e){ zip = null; }
    		
    		String paymentMethodId = null; try{paymentMethodId =  jsonData.get("paymentMethodId").getAsString(); }catch(Exception e){ paymentMethodId = null; }
    		String payTypeId = null; try{payTypeId =  jsonData.get("payTypeId").getAsString(); }catch(Exception e){ payTypeId = null; }
    		String prodGroupId = null; try{prodGroupId =  jsonData.get("prodGroupId").getAsString(); }catch(Exception e){ prodGroupId = null; }
    		String productType = null; try{productType =  jsonData.get("productType").getAsString(); }catch(Exception e){ productType = null; }
    		String transTakenOver = null; try{transTakenOver =  jsonData.get("transTakenOver").getAsString(); }catch(Exception e){ transTakenOver = null; }
    		String totalAmount = null; try{totalAmount =  jsonData.get("totalAmount").getAsString(); }catch(Exception e){ totalAmount = null; }
    		String stripeToken = null; try{stripeToken =  jsonData.get("stripeToken").getAsString(); }catch(Exception e){ stripeToken = null; }
    		
    		String accountName = null; try{accountName =  jsonData.get("stripeSepaName").getAsString(); }catch(Exception e){ accountName = null; }
    		String iban = null; try{iban =  jsonData.get("stripeSepaIban").getAsString(); }catch(Exception e){ iban = null; }
    		String cardCountryCode = null; try{cardCountryCode =  jsonData.get("cardCountryCode").getAsString(); }catch(Exception e){ cardCountryCode = null; }
    		
    		name = decodeURIComponent(name, "UTF-8");
    		//lastName = decodeURIComponent(lastName, "UTF-8");
    		email = decodeURIComponent(email, "UTF-8");
    		phone = decodeURIComponent(phone, "UTF-8");
    		street = decodeURIComponent(street, "UTF-8");
    		state = decodeURIComponent(state, "UTF-8");
    		houseNo = decodeURIComponent(houseNo, "UTF-8");
    		city = decodeURIComponent(city, "UTF-8");
    		zip = decodeURIComponent(zip, "UTF-8");
    		country = decodeURIComponent(country, "UTF-8");
    		accountName = decodeURIComponent(accountName, "UTF-8");
    				
    		if(companyId != null && !companyId.isEmpty() && userId != null && !userId.isEmpty()) {
    			Company company = entityManager.find(Company.class, Integer.valueOf(companyId.trim()));
    			Users users = entityManager.find(Users.class, Integer.valueOf(userId.trim()));
    			LocaleSelector localeSelector = new LocaleSelector();
    			Languages language = entityManager.find(Languages.class, 2);	// German is default language
    			Languages lang = null;
    			if(langCode != null && !langCode.isEmpty()) {
    				LanguageBean langBean = new LanguageBean();
    				langBean.entityManager = entityManager;
    				lang =  langBean.getLangFromLangCode(langCode);
    				localeSelector.setLocaleString(langCode);
    			}else {
    				localeSelector.setLocaleString("de");
    			}
    			PaymentMethod paymentMethod = entityManager.find(PaymentMethod.class, Integer.valueOf(paymentMethodId.trim()));
    			PaymentType paymentType = entityManager.find(PaymentType.class, Integer.valueOf(payTypeId.trim()));
    			ProductGroup productGroup = entityManager.find(ProductGroup.class, Integer.valueOf(prodGroupId.trim()));
    			
    			DonationCart donationCart = new DonationCart();
    			donationCart.entityManager = entityManager;
    			donationCart.localeSelector = localeSelector;
    			
    			/******************** Reset Data ********************/

    			donationCart.resetData();
    			
    			/****************************************************/
    			donationCart.setLanguages(language);
    			if(lang != null) {
    				donationCart.setLanguages(lang);
    			}
    			donationCart.setLangCode(langCode);
    			donationCart.setCompany(company);
    			donationCart.setDefaultUser(users);
    			donationCart.setCurrencies(company.getCurrencies());
    			String firstName = name, lastName = null;
    			if(name != null && name.trim().length() > 0){
					String[] nameArr = name.split("\\s+");
					if(nameArr.length != 0){
						firstName=nameArr[0].trim();
					}
					if(nameArr.length >= 2){
						lastName= "";
						for(int i=1; i<nameArr.length; i++){
							lastName = lastName.concat(" "+nameArr[i].trim());
						}
					}
				}
    			donationCart.setFirstName(firstName);
    			donationCart.setLastName(lastName);
    			donationCart.setEmail(email);
    			donationCart.setTelephone(phone);
    			donationCart.setStreet(street);
    			donationCart.setHouseNo(houseNo);
    			donationCart.setCity(city);
    			donationCart.setCountry(country);
    			donationCart.setZip(zip);
    			
    			List<PaymentStatus> paymentStatusList = entityManager.createNamedQuery("findPaymentStatusByCompanyAndStatusType")
																		.setParameter("companyId", company.getId())
																		.setParameter("statusType", PaymentStatusBean.Failure)
																		.getResultList();
    			
    			if(paymentStatusList != null && paymentStatusList.size() > 0) {
    				donationCart.setPaymentStatus(paymentStatusList.get(0));
    			}
    			
    			donationCart.setPaymentMethod(paymentMethod);
    			donationCart.setProductGroup(productGroup);
    			donationCart.setProductType(StringUtil.checkStringIsNull(productType) ? productType.trim() : StaticValues.OneTime);
    			donationCart.setTransTakenOver(false);
    			
    			donationCart.setAccountName(accountName);
    			donationCart.setIban(iban);
    			
    			try {
    				if(totalAmount != null && !totalAmount.isEmpty()) {
    					donationCart.setAmount(Double.valueOf(totalAmount.trim()));
    				}
    			}catch(Exception e) {
    				ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
    			}
    			
    			if(donationCart.getAmount() == 0) {
    				donationCart.setAmount(productGroup.getAmount());
    			}
    			
    			if(transTakenOver != null && !transTakenOver.isEmpty() && transTakenOver.equalsIgnoreCase("true")) {
    				donationCart.setTransTakenOver(true);
    			}
    			if(paymentMethod != null) {
    				if(paymentType != null) {
    					String pType = paymentType.getPaymentType();
    					String pTypeName = paymentType.getPaymentName();
		    			if(pTypeName.equalsIgnoreCase(StripeBean.STRIPE_CreditCard)
		    					&& cardCountryCode != null && !cardCountryCode.isEmpty()) {
							StripeBean stripeBean = new StripeBean();
							boolean europeanCard = stripeBean.EUCountryCodeExists(cardCountryCode);
							donationCart.setCardCountryCode(cardCountryCode);
							donationCart.setEuropeanCard(europeanCard);
		    			}
    				}
    			}
    			donationCart.persist();
    			
    			/****************************** redirect to payments *******************************/
	    		
    			if(paymentMethod != null) {
    				if(paymentType != null) {
    					String pType = paymentType.getPaymentType();
    					String pTypeName = paymentType.getPaymentName();
    					if(pType != null && !pType.isEmpty() && pTypeName != null && !pTypeName.isEmpty()) {
	    					if(pType.trim().equalsIgnoreCase(PayMethodBean.Stripe)) {
	    						if(pTypeName.equalsIgnoreCase(StripeBean.STRIPE_CreditCard)
	    								&& stripeToken != null && !stripeToken.isEmpty()) {
	    							donationCart.setStripeToken(stripeToken);
	    							donationCart.processStripeCard();
	    						}else {
	    							donationCart.processStripePaymentType();
	    						}
	    					}else if(pType.trim().equalsIgnoreCase(PayMethodBean.Cash)) {
	    						String responseUrl = new MainUtil().getInfoFromProperty("Domain_Name");
	    						String successUrl = responseUrl + DonationCart.Dbox_Result_Page;
	    						donationCart.setWebhookRedirectUrl(successUrl);
	    						paymentStatusList = entityManager.createNamedQuery("findPaymentStatusByCompanyAndStatusType")
										.setParameter("companyId", company.getId())
										.setParameter("statusType", PaymentStatusBean.Success)
										.getResultList();
								PaymentStatus payStatus = null;
								if(paymentStatusList != null && paymentStatusList.size() > 0) {
									payStatus = paymentStatusList.get(0); 
								}
								String transCode = "csh_";
								String randomStr = "";
								try {
									randomStr =  PasswordBean.randomString(23);
								} catch(Exception e) {
									randomStr = "";
								}
								transCode = transCode + randomStr;
	    						donationCart.updateTransactionCode(transCode, payStatus);
	    					}
    					}
    				}
    			}
	    		
	    		/***********************************************************************************/
	    		
    			return donationCart.getWebhookRedirectUrl();
    		}
    	} catch(Exception e) {
    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
    	}
    	return "";
    }
    
    public String decodeURIComponent(String s, String charset) {
        if (s == null) {
            return null;
        }
        String result = null;
        try {
            result = URLDecoder.decode(s, charset);
        }catch (Exception e) {
            result = s;
            ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
        }
        return result;
    }
    
	private JsonObject obtainContent(HttpServletRequest req) {
    	JsonObject jsonObject = null;
    	try {
    		StringBuffer jb = new StringBuffer();
    		  String line = null;
    		  try {
    		    BufferedReader reader =  new BufferedReader(new InputStreamReader(req.getInputStream()));
    		    while ((line = reader.readLine()) != null)
    		      jb.append(line);
    		  } catch (Exception e) { 
    			  ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]); 
    		  }
			  String replace = jb.toString().replaceAll("^\"|\"$", "").replace("\\\"", "\"").replaceAll("\\\\", "");
			  if(!replace.isEmpty()) {
				jsonObject =  new JsonParser().parse(replace).getAsJsonObject();
			  }  
    	}catch (Exception e) {
    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
    	return jsonObject;
    }
}
