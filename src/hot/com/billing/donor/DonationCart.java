package com.billing.donor;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.crypto.Cipher;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.Messages;
import org.jboss.seam.web.ServletContexts;

import com.billing.commonFile.MailClient;
import com.billing.commonFile.MailContent;
import com.billing.entity.Company;
import com.billing.entity.Currencies;
import com.billing.entity.FundDetails;
import com.billing.entity.FundDetailsGroup;
import com.billing.entity.FundGroup;
import com.billing.entity.Languages;
import com.billing.entity.PaymentFee;
import com.billing.entity.PaymentKeys;
import com.billing.entity.PaymentMethod;
import com.billing.entity.PaymentStatus;
import com.billing.entity.PaymentType;
import com.billing.entity.PaymentTypeSub;
import com.billing.entity.Product;
import com.billing.entity.ProductAccess;
import com.billing.entity.ProductGroup;
import com.billing.entity.ProductLanguage;
import com.billing.entity.ProductSubType;
import com.billing.entity.ProductType;
import com.billing.entity.UserAccountDetails;
import com.billing.entity.UserRole;
import com.billing.entity.Users;
import com.billing.entity.UsersAddress;
import com.billing.exceptions.ExceptionMsg;
import com.billing.language.LanguageBean;
import com.billing.login.PasswordBean;
import com.billing.paymethod.PayMethodBean;
import com.billing.paymethod.PaymentFeeBean;
import com.billing.paymethod.PaymentFeeVO;
import com.billing.paymethod.StripeBean;
import com.billing.paystatus.PaymentStatusBean;
import com.billing.product.ProductBean;
import com.billing.staticvalue.StaticValues;
import com.billing.util.DateUtil;
import com.billing.util.MainUtil;
import com.billing.util.NumberUtil;
import com.billing.util.StringUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Source;
import com.stripe.net.RequestOptions;

@Scope(ScopeType.SESSION)
@Name("donationCart")
@SuppressWarnings({"unchecked", "static-access", "unused"})
public class DonationCart {

	@In
	public EntityManager entityManager;
	    
	@In
	public LocaleSelector localeSelector;
	
	@In(create=true,required=false) 
    public Users activeUser;
	
	private int companyId;
	
	private int userId;
	
	private boolean loggedIn;
	
	private String companyLogo;
	
	private String campaignName;
	
	private String campaignImage;
	
	private String description;
	
	private double amount;
	
	private String firstAmountStr;
	
	private String secondAmountStr;
	
	private String thirdAmountStr;
	
	private String fourthAmountStr;
	
	private double firstAmount;
	
	private double secondAmount;
	
	private double thirdAmount;
	
	private double fourthAmount;
	
	private boolean monthlyDonation;
	
	private String progressValue;
	
	private int prodGroupId;
	
	private Currencies currencies;
	
	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private String telephone;
	
	private String street;
	
	private String houseNo;
	
	private String city;
	
	private String country;
	
	private String zip;
	
	private Users defaultUser;
	
	private Company company;
	
	private FundGroup fundGroup;
	
	private String langCode;
	
	private Languages Languages;
	
	private String paymentTypeName;
	
	private String accountName;
	
	private String iban;
	
	private FundDetailsGroup fundDetailsGroup;
	
	private PaymentMethod paymentMethod;
	
	private PaymentStatus paymentStatus;
	
	private ProductGroup productGroup;
	
	private String productType;
	
	private String appSource;
	
	private String deviceSource;
	
	private boolean transTakenOver;
	
	private double donationAmount;
	
	private double donationFeeAmount;
	
	private String webhookRedirectUrl;
	
	private String stripeSource;
	
	private String stripeToken;
	
	private String secretKeyUsed;
	
	private int userAccId;
	
	private boolean addressShow;
	
	private boolean addressRequired;
	
	private boolean phoneShow;
	
	private boolean phoneRequired;
	
	private String customerId;
	
	private boolean autoCharge;
	
	private boolean manualCharge;
	
	private boolean failure;
	
	private boolean europeanCard;
	
	private String cardCountryCode;
	
	private List<PaymentMethod> payMethodList;
	
	private List<FundDetails> fundDetailsList;
	
	private List<ProductSubType> prodSubTypeList;
	
	public static String Dbox_Result_Page = "/dboxResult.jsf";
	
	public static String Dbox_Response_Page = "/dboxResponse.jsf";
	
	/**
	 * Load the data for dbox through xml (Step 1)
	 */
	public void loadData() {
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			String compId = request.getQueryString();
			if(StringUtil.checkStringIsNull(compId)) {
				resetData();
				compId = compId.trim();
				compId = EncryptDecrypt.correctTheDecrypValue(compId);
				String commonData = PasswordBean.getInstance().decryptWithMD5DES(compId);
				if(commonData != null && !commonData.isEmpty() && commonData.contains(StaticValues.ProductGroup)) {
					String[] commonDataSplit = commonData.split(StaticValues.ProductGroup);
					if(commonDataSplit != null && commonDataSplit.length == 2) {
						String companyIdStr = commonDataSplit[0];
						String prodGroupIdStr = commonDataSplit[1];
						if(companyIdStr != null && !companyIdStr.isEmpty() 
								&& NumberUtil.checkNumeric(companyIdStr.trim()) && Integer.valueOf(companyIdStr.trim()) > 0
								&& prodGroupIdStr != null && !prodGroupIdStr.isEmpty() 
								&& NumberUtil.checkNumeric(prodGroupIdStr.trim()) && Integer.valueOf(prodGroupIdStr.trim()) > 0) {
							int companyId = Integer.valueOf(companyIdStr);
							int prodGroupId = Integer.valueOf(prodGroupIdStr);
							Company company = entityManager.find(Company.class, companyId);
							ProductGroup prodGroup = entityManager.find(ProductGroup.class, prodGroupId);
							if(company!= null && prodGroup != null && company.getId() == prodGroup.getCompany().getId()) {
								if(prodGroup.isEnable() && !prodGroup.isDeleted()) {
									boolean loggedIn = false;
									this.setCompanyId(company.getId());
									this.setUserId(company.getUsers().getId());
									if(activeUser != null) {
										loggedIn = true;
										this.setUserId(activeUser.getId());
									}
									this.setLoggedIn(loggedIn);
									this.setLangCode(localeSelector.getLocaleString());
									loadProductGroupData(company, prodGroup, loggedIn);
								}else {
									FacesContext.getCurrentInstance().getExternalContext().redirect("/dboxerror.jsf?error=closed");
								}
							}else {
								FacesContext.getCurrentInstance().getExternalContext().redirect("/dboxerror.jsf");
							}
						}else {
							FacesContext.getCurrentInstance().getExternalContext().redirect("/dboxerror.jsf");
						}
					}else {
						FacesContext.getCurrentInstance().getExternalContext().redirect("/dboxerror.jsf");
					}
				}else {
					FacesContext.getCurrentInstance().getExternalContext().redirect("/dboxerror.jsf");
				}
			}else {
				FacesContext.getCurrentInstance().getExternalContext().redirect("/dboxerror.jsf");
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect("/login.jsf");
			}catch(Exception e1) {
				ExceptionMsg.ErrorMsg(e1, Thread.currentThread().getStackTrace()[1]);	
			}
		}
	}
	
	/**
	 * Load the data for dbox through xml (Step 2)
	 */
	public void loadProductGroupData(Company company, ProductGroup prodGroup, boolean loggedIn) {
		try {
			ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1], 
					"Dbox is opened for this company - " + company.getCompanyName() + "( " + companyId + " )" +" and productGroupId is - " + prodGroup.getId());
			int companyId = company.getId();
			Currencies currencies = company.getCurrencies();
			Product product = prodGroup.getProduct();
			String localeString = localeSelector.getLocaleString();
			Locale locale = new Locale(localeString);
			NumberFormat currencyFormatter = NumberFormat.getNumberInstance(locale);
			currencyFormatter.setMinimumFractionDigits(2);
			currencyFormatter.setMaximumFractionDigits(2);
			this.setProdGroupId(prodGroup.getId());
			this.setCampaignName(new ProductBean().getProductNameLang(product, localeString));
			this.setDescription(prodGroup.getDescription());
			this.setFirstAmountStr(currencyFormatter.format(prodGroup.getAmount()));
			this.setSecondAmountStr(currencyFormatter.format(prodGroup.getSecondAmount()));
			this.setThirdAmountStr(currencyFormatter.format(prodGroup.getThirdAmount()));
			this.setFourthAmountStr(currencyFormatter.format(prodGroup.getFourthAmount()));
			this.setFirstAmount(prodGroup.getAmount());
			this.setSecondAmount(prodGroup.getSecondAmount());
			this.setThirdAmount(prodGroup.getThirdAmount());
			this.setFourthAmount(prodGroup.getFourthAmount());
			this.setAmount(prodGroup.getAmount());
			this.setCampaignImage(StringUtil.getImageToEncodeImage(prodGroup.getImageUrl()));
			this.setCurrencies(currencies);
			this.setProgressValue("0");
			this.setAddressShow(company.isAddressShow());
			this.setAddressRequired(company.isAddressRequired());
			this.setPhoneShow(company.isPhoneShow());
			this.setPhoneRequired(company.isPhoneRequired());
			if(prodGroup.getTargetAmount() > 0 && prodGroup.getCollectedAmount() > 0) {
				try {
					double progressValue = (prodGroup.getCollectedAmount() / prodGroup.getTargetAmount()) * 100;
					this.setProgressValue(StripeBean.roundOffDouble(progressValue));
				}catch(Exception e) {
		    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
				}
			}
			this.setCompanyLogo(StringUtil.getImageToEncodeImage(company.getLogoUrl()));
			String currencyCode = currencies.getCurrencyCode();
			List<PaymentMethod> payMethodList = payMethodList(loggedIn, currencyCode);
			this.setPayMethodList(payMethodList);
			this.setMonthlyDonation(checkProductType(product, StaticValues.Recurring));
			List<ProductSubType> prodSubTypeList = new ArrayList<ProductSubType>();
			if(this.isMonthlyDonation()) {
				ProductBean prodBean = new ProductBean();
				prodBean.entityManager = entityManager;
				prodSubTypeList.addAll(prodBean.getProdSubTypeList(product));
			}
			this.setProdSubTypeList(prodSubTypeList);
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public List<PaymentMethod> payMethodList(boolean loggedIn, String currencyCode){
		try {
			if(loggedIn) {
				return entityManager.createNamedQuery("findPaymentMethodByCompanyCurrencyAndOffline")
												.setParameter("companyId", companyId)
												.setParameter("supportCurrency", "%" + currencyCode.toUpperCase() + "%")
												.getResultList();
			}else {
				return entityManager.createNamedQuery("findPaymentMethodByCompanyCurrencyAndOnline")
												.setParameter("companyId", companyId)
												.setParameter("supportCurrency", "%" + currencyCode.toUpperCase() + "%")
												.getResultList();
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			return null;
		}
	}
	
	/**
	 * 
	 * @param product
	 * @param prodType
	 * @return boolean
	 * check the product is having that product Type or not
	 */
	public boolean checkProductType(Product product, String prodType) {
		try {
			if(product != null && StringUtil.checkStringIsNull(prodType)) {
				Set<ProductType> prodAccessList = product.getProductAccess();
				if(prodAccessList != null && prodAccessList.size() > 0) {
					for(ProductType pType: prodAccessList) {
						if(pType.getTypeName().equalsIgnoreCase(prodType.trim())) {
							return true;
						}
					}
				}
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return false;
	}

	/**
	 * reset the session data
	 */
	public void resetData() {
		try {
			this.setCompanyId(0);
			this.setUserId(0);
			this.setLoggedIn(false);
			this.setCompanyLogo("");
			this.setCampaignName("");
			this.setCampaignImage("");
			this.setDescription("");
			this.setAmount(0.0);
			this.setFirstAmountStr("0");
			this.setSecondAmountStr("0");
			this.setThirdAmountStr("0");
			this.setFourthAmountStr("0");
			this.setFirstAmount(0);
			this.setSecondAmount(0);
			this.setThirdAmount(0);
			this.setFourthAmount(0);
			this.setMonthlyDonation(false);
			this.setProgressValue("0");
			this.setProdGroupId(0);
			this.setCurrencies(null);
			this.setFirstName("");
			this.setLastName("");
			this.setEmail("");
			this.setTelephone("");
			this.setStreet("");
			this.setHouseNo("");
			this.setCity("");
			this.setCountry("");
			this.setZip("");
			this.setDefaultUser(null);
			this.setCompany(null);
			this.setLangCode("en");
			this.setLanguages(null);
			this.setPaymentTypeName("");
			this.setAccountName("");
			this.setIban("");
			this.setPayMethodList(null);
			this.setProductGroup(null);
			this.setPaymentMethod(null);
			this.setPaymentStatus(null);
			this.setFundDetailsGroup(null);
			this.setFundGroup(null);
			this.setProductType(null);
			this.setAppSource(null);
			this.setDeviceSource(null);
			this.setTransTakenOver(false);
			this.setWebhookRedirectUrl(null);
			this.setDonationAmount(0);
			this.setDonationFeeAmount(0);
			this.setStripeToken(null);
			this.setStripeSource(null);
			this.setUserAccId(0);
			this.setAddressShow(true);
			this.setAddressRequired(false);
			this.setPhoneShow(true);
			this.setPhoneRequired(false);
			this.setCustomerId(null);
			this.setAutoCharge(false);
			this.setManualCharge(true);
			this.setFailure(false);
			this.setEuropeanCard(false);
			this.setCardCountryCode(null);
			this.setPayMethodList(null);
			this.setFundDetailsList(null);
			this.setProdSubTypeList(null);
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	/**
	 * Load the data for dbox through ajax
	 */
	public void loadProdGroupDescription() {
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			String prodGroupId = request.getParameter("prodGroupId");
			String loggedIn = request.getParameter("loggedIn");
			String langCode = request.getParameter("langCode");
			if(prodGroupId != null && !prodGroupId.isEmpty()) {
				ProductGroup prodGroup = entityManager.find(ProductGroup.class, Integer.valueOf(prodGroupId.trim()));
				if(prodGroup != null) {
					String description = "";
					Company company = prodGroup.getCompany();
					Currencies currencies = company.getCurrencies();
					String currencyCode = currencies.getCurrencyCode();
					int companyId = company.getId();
					JsonArray paymentArray = new JsonArray();
					JsonObject jsonObject = new JsonObject();
					
					ProductBean prodBean = new ProductBean();
					prodBean.entityManager = entityManager;
					Product product = prodGroup.getProduct();
					List<ProductSubType> prodSubTypeList = prodBean.getProdSubTypeList(product);
					String prodSubTypeName = "";
					if(prodSubTypeList != null && prodSubTypeList.size() > 0) {
						ProductSubType prodSubType = prodSubTypeList.get(0);
						if(prodSubType != null) {
							prodSubTypeName = prodSubType.getSubTypeName();
						}
					}
					
					if(product != null) {
						Set<ProductLanguage> prodLangList = product.getProductLanguage();
						if(prodLangList != null && prodLangList.size() > 0) {
							for(ProductLanguage prodLang : prodLangList) {
								if(prodLang != null && prodLang.getLanguages() != null 
										&& prodLang.getLanguages().getLangCode().equalsIgnoreCase(langCode)) {
									description = prodLang.getDescription();
								}
							}
						}
					}
					
					boolean loggedInAcc = false;
					if(loggedIn != null && !loggedIn.isEmpty() && loggedIn.equalsIgnoreCase("true")) {
						loggedInAcc = true;
					}
					List<PaymentMethod> payMethodList = payMethodList(loggedInAcc, currencyCode);
					String stripePubkey = StripeBean.API_KEY_PUBLISHABLE(company);
					
					if(payMethodList != null && !payMethodList.isEmpty()) {
						Map<String, String> spkeyMap = null;
		    			for(PaymentMethod pay : payMethodList) {
		    				PaymentType pType = pay.getPaymentType();
		    				if(pType != null){
		    					if(spkeyMap == null && pType.getPaymentType() != null && pType.getPaymentType().equalsIgnoreCase(PayMethodBean.Stripe) && company.isSelfStripeAccount()) {
		        					spkeyMap = new StripeBean().SecretPublishableKey(pay);
		        					if(spkeyMap != null && !spkeyMap.isEmpty()) {
		        						stripePubkey = spkeyMap.get("publishableKey");
		        					}
		        				}
		    					JsonObject payObj = new JsonObject();
			    				String payType = pType.getPaymentType();
			    				String payName = pType.getPaymentName();
			    				payObj.addProperty("payMethodId", pay.getId());
			    				payObj.addProperty("payTypeId", pType.getId());
			    				payObj.addProperty("payTypeName", payName);
			    				payObj.addProperty("payType", payType);
			    				payObj.addProperty("recurring", pType.isSupportRecurring());
			    				paymentArray.add(payObj);
		    				}
		    			}
		    		}
					
					jsonObject.addProperty("prodSubTypeName", prodSubTypeName);
					jsonObject.addProperty("addressShow", company.isAddressShow());
					jsonObject.addProperty("addressRequired", company.isAddressRequired());
					jsonObject.addProperty("phoneShow", company.isPhoneShow());
					jsonObject.addProperty("phoneRequired", company.isPhoneRequired());
					
					
		    		JsonObject payTypeAndFee = payTypeAndAppFee(company);
		    		jsonObject.add("payTypeAndFee", payTypeAndFee);
					
		    		jsonObject.add("payList", paymentArray);
		    		jsonObject.addProperty("description", description);
		    		jsonObject.addProperty("stripePubkey", stripePubkey);
					response.setContentType("text/html; charset=UTF-8");
					response.getWriter().write(jsonObject.toString()+"\n");
				}
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	 public JsonObject payTypeAndAppFee(Company company) {
			JsonObject payFeeObj = new JsonObject(); 
	    	try {
	    		PaymentMethod stripePay = null;
	    		int companyId = company.getId();
	    		try {
	    			stripePay = (PaymentMethod) entityManager.createNamedQuery("findPaymentMethodByCompanyAndpaymentType")
																.setParameter("companyId", companyId)
																.setParameter("payTypeId", PayMethodBean.Stripe_Sepa_Id)
																.getSingleResult();
	    		}catch(Exception e) {
	    			stripePay = null;
	    		}
	    		
	    		String country = StaticValues.DefaultStripeCountry;
	    		if(stripePay != null && stripePay.getPaymentKeys() != null) {
	    			String stripeCountry = stripePay.getPaymentKeys().getAccountCountry();
	    			if(stripeCountry != null && !stripeCountry.isEmpty()) {
	    				country = stripeCountry.trim().toUpperCase();
	    			}
	    		}
	    		PaymentFeeBean payFeeBean = new PaymentFeeBean();
	    		payFeeBean.entityManager = entityManager;
	    		List<PaymentTypeSub> paymentTypeSubList = payFeeBean.getPaymentTypeSubListByCountry(country);	
	    		
	    		List<PaymentFee> payFeeList = payFeeBean.paymentFeeList(companyId);
	    		
	    		if(payFeeList != null && !payFeeList.isEmpty()) {
	    			for(PaymentFee oFee : payFeeList) {
						PaymentType payType = oFee.getPaymentType();
						JsonObject payFeeDet = new JsonObject();
						PaymentTypeSub paySub = payFeeBean.getPaymentFeeFromSub(paymentTypeSubList, payType);
						payFeeDet.addProperty("PayFee", paySub != null && paySub.getPaymentFee() != null ? paySub.getPaymentFee() : "");
						payFeeDet.addProperty("FeeTO", oFee.getFeeTakenOver());
						payFeeDet.addProperty("FeeNTO", oFee.getFeeNotTakenOver());
						payFeeDet.addProperty("ConvertFee", paySub != null && paySub.getStripeConvertionFee() != null ? paySub.getStripeConvertionFee() : "");
						payFeeObj.add(String.valueOf(payType.getId()), payFeeDet);
					}
	    		}
	    		
	    	}catch (Exception e) {
	    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			}
	    	return payFeeObj;
	    }
	
	/**
	 * Persist the data from dbox
	 */
	public String persist() {
		try {
			List<FundGroup> fundGroupList = new ArrayList<FundGroup>();
			Company company = this.getDefaultUser() != null ? this.getDefaultUser().getCompany() : null;
			if(company != null) {
				if(this.getEmail() != null && !this.getEmail().isEmpty()) {
					fundGroupList = entityManager.createNamedQuery("findFundGroupByCompanyAndDonateUserEmail")
											.setParameter("companyId", company.getId())
											.setParameter("email", this.getEmail())
						    				.getResultList();
				}
				PaymentMethod paymentMethod = this.getPaymentMethod();
				PaymentType paymentType = paymentMethod.getPaymentType();
				String paymentTypeName = paymentType.getPaymentName();
				if(paymentTypeName != null && !paymentTypeName.isEmpty() && paymentTypeName.equalsIgnoreCase(StripeBean.STRIPE_SEPAPAY)) {
					EncryptDecrypt encryptDecrypt = new EncryptDecrypt();
					Cipher cipherEncrypt = encryptDecrypt.InitiateEncryptProcess();
					String encryptedIban = encryptDecrypt.encryptData(cipherEncrypt, this.getIban());
					if(encryptedIban != null && !encryptedIban.isEmpty()) {
						List<UserAccountDetails> userAccDetailsList = entityManager.createNamedQuery("findUserAccountDetailsByCompanyWithIban")
							.setParameter("companyId", company.getId())
							.setParameter("iban", encryptedIban)
							.getResultList();
						if(userAccDetailsList != null && userAccDetailsList.size() > 0) {
							Users users = userAccDetailsList.get(0).getUsers();
							List<FundGroup> fundGroup = entityManager.createNamedQuery("findFundGroupByCompanyAndDonateUser")
																		.setParameter("companyId", company.getId())
																		.setParameter("donateUserId", users.getId())
																		.getResultList();
							if(fundGroup != null && fundGroup.size() > 0) {
								fundGroupList.addAll(fundGroup);
							}
						}
					}
				}
				FundDetailsGroup fundDetailsGroup = persistFundDetailsGroup();
				this.setFundDetailsGroup(fundDetailsGroup);
				FundGroup fundGroup = null;
				Users users = null;
				
	    		/********************************** Existing User **********************************/
	    		if(fundGroupList != null && fundGroupList.size() > 0) {
	    			fundGroup = fundGroupList.get(0);
	    			this.setFundGroup(fundGroup);
	    			if(fundGroup != null) {
	    				users = fundGroup.getDonateUser();
	    				ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1], "User exists. Id is: " + users.getUserNr());
		    			company = users.getCompany();
		    			persistOrUpdateUsers(false, users, company);
		    			
		    			if((this.getStreet() != null && !this.getStreet().isEmpty()) || (this.getHouseNo() != null && !this.getHouseNo().isEmpty()) ||
							(this.getCity() != null && !this.getCity().isEmpty()) || (this.getCountry() != null && !this.getCountry().isEmpty()) ||
							(this.getZip() != null && !this.getZip().isEmpty())){
		    				List<UsersAddress> userAddrList = entityManager.createNamedQuery("findUsersAddressByUsersAndNotDeleted")
		    																	.setParameter("usersId", users.getId())
		    																	.getResultList();
		    				if(userAddrList != null && userAddrList.size() > 0) {
		    					persistAddress(users, this.getStreet(), this.getHouseNo(),
		    							this.getCity(), this.getCountry(), this.getZip(),
		    							userAddrList.get(0), false);
		    				}else {
		    					persistAddress(users, this.getStreet(), this.getHouseNo(),
										this.getCity(), this.getCountry(), this.getZip(),
										new UsersAddress(), true);
		    				}
		    			}
	    			}else {
	    				return null;
	    			}
	    		}
	    		/***********************************************************************************/
	    		
	    		/************************************* New User ************************************/
	    		else {
	    			users = new Users();
	    			users = persistOrUpdateUsers(true, users, company);
	    			ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1], "New user. Id is: " + users.getUserNr() + " Name : " + users.getFirstName() + " " + users.getLastName() + " Mail : " + users.getPrimaryEmail());
	    			fundGroup = persistFundGroup(users);
	    			this.setFundGroup(fundGroup);
	    			
	    			if((this.getStreet() != null && !this.getStreet().isEmpty()) || (this.getHouseNo() != null && !this.getHouseNo().isEmpty()) ||
						(this.getCity() != null && !this.getCity().isEmpty()) || (this.getCountry() != null && !this.getCountry().isEmpty()) ||
						(this.getZip() != null && !this.getZip().isEmpty())){
	    				persistAddress(users, this.getStreet(), this.getHouseNo(),
	    						this.getCity(), this.getCountry(), this.getZip(),
	    						new UsersAddress(), true);
	    			}
	    		}
	    		/***********************************************************************************/
	    		
	    		/****************************** Persist Fund Details *******************************/
	    		
	    		persistFundDetails(users);
	    		
	    		/***********************************************************************************/
	    		
			}else {
				return null;
			}
    	} catch (Exception e) {
    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return null;
	}
	
	public void redirectToPaymentMethods() {
		try {
			PaymentMethod paymentMethod = this.getPaymentMethod();
			if(paymentMethod != null) {
				PaymentType paymentType = paymentMethod.getPaymentType();
				if(paymentType != null) {
					if(paymentType.getPaymentType().equalsIgnoreCase(PayMethodBean.Stripe)) {
						if(paymentType.getPaymentName().equalsIgnoreCase(StripeBean.STRIPE_CreditCard)) {
							processStripeCard();
						}else {
							processStripePaymentType();
						}
					}
				}
			}
		} catch (Exception e) {
    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	/**
	 * 
	 * @param newUser
	 * @param users
	 * @param company
	 * @return Users
	 * persist or update the users from dbox
	 */
	public Users persistOrUpdateUsers(boolean newUser, Users users, Company company) {
		try {
			if(newUser && company != null) {
				users = new Users();
				users.setFirstName(this.getFirstName());
    			users.setLastName(this.getLastName());
    			users.setPrimaryEmail(this.getEmail());
    			users.setTelephone(this.getTelephone());
    			users.setCompany(company);
    			users.setCompanyUser(false);
    			users.setLoginAccess(false);
    			users.setWritePermission(false);
    			users.setCreatedDate(new Date());
    			users.setModifiedDate(new Date());
    			List<UserRole> userRoleList = entityManager.createNamedQuery("findUserRoleByCompanyAndRole")
															.setParameter("companyId", company.getId())
															.setParameter("role", StaticValues.Donor)
															.getResultList();
    			if(userRoleList != null && userRoleList.size() > 0) {
    				users.setUserRole(userRoleList.get(0));
    			}
    			users.setLanguages(this.getLanguages());
    			entityManager.persist(users);
    			entityManager.flush();
    			String code = company.getCode();
    			code = code != null && !code.isEmpty() ? code.trim() : "";
    			users.setUserNr(code+users.getId());
    			entityManager.merge(users);
    			entityManager.flush();
    			return users;
			}
		} catch (Exception e) {
    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return users;
	}
	
	public void persistAddress(Users users, String street, String houseNo, String city,
			String country, String zip, UsersAddress userAddr, boolean newAddress) {
		try {
			userAddr.setAddress1(street);
			userAddr.setHouseNo(houseNo);
			userAddr.setCity(city);
			userAddr.setCountry(country);
			userAddr.setZip(zip);
			userAddr.setUsers(users);
			userAddr.setModifiedDate(new Date());
			if(newAddress) {
				userAddr.setCreatedDate(new Date());
				entityManager.persist(userAddr);
			} else {
				entityManager.merge(userAddr);
			}
			entityManager.flush();
		} catch (Exception e) {
    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	

	/**
	 * 
	 * @param users
	 * @return FundGroup
	 * create the new fund group
	 */
	public FundGroup persistFundGroup(Users users) {
		try {
			FundGroup fundGroup = new FundGroup();
			fundGroup.setCompany(this.getDefaultUser().getCompany());
			fundGroup.setCreatedUser(activeUser);
			fundGroup.setDonateUser(users);
			fundGroup.setCreatedDate(new Date());
			fundGroup.setModifiedDate(new Date());
			entityManager.persist(fundGroup);
			entityManager.flush();
			return fundGroup;
		} catch (Exception e) {
    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return null;
	}
	
	public void persistFundDetails(Users users) {
		try {
			if(this.getFundGroup() != null) {
				FundDetailsGroup fundGroupDetails = this.getFundDetailsGroup();
				int groupId = 0;
				ProductGroup productGroup = this.getProductGroup();
				PaymentMethod paymentMethod = this.getPaymentMethod();
				
				PaymentType paymentType = paymentMethod.getPaymentType();
				String paymentTypeName = paymentType.getPaymentName();
				
				this.setPaymentTypeName(paymentTypeName);
				
				PaymentStatus paymentStatus = this.getPaymentStatus();
				String productType = this.getProductType();
				boolean transTakenOver = this.isTransTakenOver();
				Company company = this.getDefaultUser().getCompany();
				
				String installment = "1";
				boolean mainEntry = true;
				if(StringUtil.checkStringIsNull(productType) 
						&& (productType.equalsIgnoreCase(StaticValues.Monthly) || 
								productType.equalsIgnoreCase(StaticValues.Annually))) {
					this.setMonthlyDonation(true);
					installment = "2";
					mainEntry = false;
				}
				List<FundDetails> fundDetailsList = new ArrayList<FundDetails>();
				
				int userAccId = 0;
				if(this.getIban() != null && !this.getIban().isEmpty() 
						&& paymentTypeName != null && !paymentTypeName.trim().isEmpty() 
						&& paymentTypeName.equalsIgnoreCase(StripeBean.STRIPE_SEPAPAY)) {
					EncryptDecrypt encryptDecrypt = new EncryptDecrypt();
					Cipher cipherEncrypt = encryptDecrypt.InitiateEncryptProcess();
					String encryptedIban = encryptDecrypt.encryptData(cipherEncrypt, this.getIban());
					if(encryptedIban != null && !encryptedIban.isEmpty()) {
						List<UserAccountDetails> userAccDetailsList = entityManager.createNamedQuery("findUserAccountDetailsByUserWithIban")
																					.setParameter("userId", users.getId())
																					.setParameter("iban", encryptedIban)
																					.getResultList();
						if(userAccDetailsList != null && userAccDetailsList.size() > 0) {
							userAccId = userAccDetailsList.get(0).getId();
						}else {
							userAccId = persistUserAccountDetails(users, paymentMethod.getPaymentType(), encryptedIban);
						}
					}
				}else {
					EncryptDecrypt encryptDecrypt = new EncryptDecrypt();
					Cipher cipherEncrypt = encryptDecrypt.InitiateEncryptProcess();
					String encryptedIban = encryptDecrypt.encryptData(cipherEncrypt, this.getIban());
					userAccId = persistUserAccountDetails(users, paymentMethod.getPaymentType(), encryptedIban);
				}
				FundDetails fundDetails = persistFundDetailsSub(productGroup, paymentMethod, paymentStatus, fundGroupDetails, users, groupId, productType, transTakenOver, installment, userAccId, mainEntry);
				if(installment.equalsIgnoreCase("2")) {
					installment = "1/1";
					mainEntry = true;
					groupId = fundDetails.getGroupId();
					fundDetails = persistFundDetailsSub(productGroup, paymentMethod, paymentStatus, fundGroupDetails, users, groupId, productType, transTakenOver, installment, userAccId, mainEntry);
					fundDetailsList.add(fundDetails);
				}else {
					fundDetailsList.add(fundDetails);
				}
				this.setFundDetailsList(fundDetailsList);
				if(fundDetailsList != null && fundDetailsList.size() > 0) {
					updateAppAndPayFee(fundDetailsList);
				}
			}
		} catch (Exception e) {
    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void updateAppAndPayFee(List<FundDetails> fundDetailsList) {
		try {
			if(fundDetailsList != null && fundDetailsList.size() > 0) {
				PaymentMethod paymentMethod = fundDetailsList.get(0).getPaymentMethod();
				Company company = paymentMethod.getCompany();
				PaymentFeeBean payFeeBean = new PaymentFeeBean();
				payFeeBean.entityManager = entityManager;
				payFeeBean.activeUser = activeUser;
				
				Currencies currencies = this.getCurrencies();
				String localCurrencyCode = currencies.getCurrencyCode();
				String accountCurrencyCode = localCurrencyCode; 
				String country = StaticValues.DefaultStripeCountry;
				if(paymentMethod != null && paymentMethod.getPaymentKeys() != null) {
					String stripeCurrency = paymentMethod.getPaymentKeys().getAccountCurrencyCode();
					if(stripeCurrency != null && !stripeCurrency.isEmpty()) {
						accountCurrencyCode = stripeCurrency;
					}
					String stripeCountry = paymentMethod.getPaymentKeys().getAccountCountry();
					if(stripeCountry != null && !stripeCountry.isEmpty()) {
						country = stripeCountry;
					}
				}
				
				PaymentType payTypeOverride = (PaymentType) entityManager.createNamedQuery("findPaymentTypeById")
																				.setParameter("payTypeId", PayMethodBean.Stripe_Sepa_Id)
																				.getSingleResult();	
				PaymentType payType = this.getPaymentTypeName() != null && this.getPaymentTypeName().equals(StripeBean.STRIPE_SEPAPAY) ? payTypeOverride : paymentMethod.getPaymentType();
				
				PaymentTypeSub payTypeSub = null;
				try {
					List<PaymentTypeSub> payTypeList = payFeeBean.getPaymentTypeSubListByCountry(country);
					if(payTypeList != null && payTypeList.size() > 0) {
						payTypeSub = new PaymentFeeBean().getPaymentFeeFromSub(payTypeList, payType);
					}
				} catch(Exception e) {
					ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
					payTypeSub = null;
				}
				
				PaymentFee paymentFee = null;
				try {
					paymentFee = payFeeBean.getPaymentFee(company, payType);
				} catch(Exception e) {
					ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
					paymentFee = null;
				}
				
				try {
					int count = fundDetailsList.size();
					double totalAmount = 0.0 , feeAmount = 0.0;
					boolean appendTransFee = false;
					for(FundDetails fDetails: fundDetailsList) {
						boolean transTakenOver = fDetails.isTransTakenOver();
						if(transTakenOver && !appendTransFee) {
							appendTransFee = true;
						}
					}
					for(FundDetails fDetails: fundDetailsList) {
						boolean transTakenOver = appendTransFee;
						PaymentFeeVO payFeeVO = payFeeBean.getAppAndPayFeeFromPattern(payType, payTypeSub, paymentFee, company, fDetails.getAmount().doubleValue(),
								transTakenOver, this.isEuropeanCard(), accountCurrencyCode, localCurrencyCode);
						if(payFeeVO != null) {
							fDetails.setApplicationFee(BigDecimal.valueOf(payFeeVO.getAppFee()));
							fDetails.setTransactionFee(BigDecimal.valueOf(payFeeVO.getPayFee() / count));
							if(fDetails.getFundGroupDetails() != null) {
								fDetails.getFundGroupDetails().setTransactionInfo(payFeeVO.getFeePatternInfo());
							}
							double feeAmt = fDetails.getApplicationFee().doubleValue() + fDetails.getTransactionFee().doubleValue();
							feeAmount = feeAmount + feeAmt;
							totalAmount = totalAmount + fDetails.getAmount().doubleValue();
							fDetails.setTransTakenOver(transTakenOver);
							entityManager.merge(fDetails);
							entityManager.flush();
						}
					}
					
					this.setTransTakenOver(appendTransFee);
					this.setDonationFeeAmount(feeAmount);
					this.setDonationAmount(totalAmount);
				}catch(Exception e){
					ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
				}
			}
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public FundDetailsGroup persistFundDetailsGroup() {
		try {
			FundDetailsGroup fundDetailsGroup = new FundDetailsGroup();
			fundDetailsGroup.setAppSource(this.getAppSource());
			fundDetailsGroup.setDeviceSource(this.getDeviceSource());
			fundDetailsGroup.setCreatedDate(new Date());
			fundDetailsGroup.setModifiedDate(new Date());
			entityManager.persist(fundDetailsGroup);
			entityManager.flush();
			return fundDetailsGroup;
		} catch (Exception e) {
    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return null;
	}
	
	public FundDetails persistFundDetailsSub(ProductGroup productGroup, PaymentMethod paymentMethod,
			PaymentStatus paymentStatus, FundDetailsGroup fundGroupDetails, Users users,
			int groupId,String productType, boolean transTakenOver, String installment, int userAccId, boolean mainEntry) {
		FundDetails fundDetails = new FundDetails();
		fundDetails.setCompany(this.getDefaultUser() != null ? this.getDefaultUser().getCompany() : null);
		fundDetails.setCreatedUser(this.getDefaultUser());
		fundDetails.setFundGroup(this.getFundGroup());
		fundDetails.setAmount(BigDecimal.valueOf(this.getAmount()));
		fundDetails.setExtPrice(BigDecimal.valueOf(this.getAmount()));
		fundDetails.setIntPrice(BigDecimal.valueOf(productGroup.getAmount()));
		fundDetails.setProductGroup(productGroup);
		fundDetails.setPaymentMethod(paymentMethod);
		fundDetails.setPaymentStatus(paymentStatus);
		fundDetails.setProductType(productType);
		fundDetails.setQuantity(1);
		fundDetails.setTransTakenOver(transTakenOver);
		fundDetails.setApplicationFee(BigDecimal.ZERO);
		fundDetails.setTransactionFee(BigDecimal.ZERO);
		fundDetails.setDisputeCount(0);
		fundDetails.setInstallment(installment);
		fundDetails.setStartDate(new Date());
		fundDetails.setBookingDate(new Date());
		fundDetails.setTransactionDate(new Date());
		fundDetails.setCreatedDate(new Date());
		fundDetails.setModifiedDate(new Date());
		fundDetails.setUserAccId(userAccId);
		fundDetails.setFundGroupDetails(fundGroupDetails);
		fundDetails.setMainEntry(mainEntry);
		if(groupId > 0) {
			fundDetails.setGroupId(groupId);
		}
		entityManager.persist(fundDetails);
		entityManager.flush();
		if(groupId == 0) {
			fundDetails.setGroupId(fundDetails.getId());
			entityManager.merge(fundDetails);
			entityManager.flush();
		}
		return fundDetails;
	}
	
	public int persistUserAccountDetails(Users users, PaymentType paymentType, String encryptedIban) {
		try {
			if((this.getAccountName() != null && !this.getAccountName().isEmpty() 
				&& this.getIban() != null && !this.getIban().isEmpty()) 
					|| (this.isMonthlyDonation() && paymentType.isOtherReuccring())) {
				UserAccountDetails userAcc = new UserAccountDetails();
				if(this.getAccountName() != null && !this.getAccountName().isEmpty() 
					&& this.getIban() != null && !this.getIban().isEmpty()) {
					userAcc.setAccountName(this.getAccountName());
					userAcc.setIbanCode(encryptedIban);
				}
				userAcc.setUsers(users);
				userAcc.setPaymentType(paymentType);
				userAcc.setCreatedDate(new Date());
				userAcc.setModifiedDate(new Date());
				entityManager.persist(userAcc);
				entityManager.flush();
				return userAcc.getId();
			}
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return 0;
	}
	
	public String checkForApikey(PaymentMethod stripePay){
	    String key = null;
	    try {
	    	PaymentKeys payParam = null;									
			if(stripePay != null && stripePay.getPaymentKeys() != null){
				Company company = stripePay.getCompany();
				payParam = entityManager.find(PaymentKeys.class, stripePay.getPaymentKeys().getId());
				if(payParam != null) {
					if(payParam.getPayParam1()!=null && company.isSelfStripeAccount()) {		// check for using own stripe key
						 key = payParam.getPayParam1().trim();
					}
					boolean local = new MainUtil().checkLocal() || !company.isLiveAccount();
					if(local){
						key = payParam.getTestPayParam1() != null ? payParam.getTestPayParam1().trim() : null;
					}
				}
			}		
		} catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return key;
	}
	
	public String identifyKey(String key, boolean print) {
		try {
			if(key != null && !key.isEmpty()) {
		    	int keyLength = key.length();
		    	if(keyLength > 10) {
		    		String first = key.substring(0,7);
		    		int subLength = keyLength - 6;
		    		String second = key.substring(subLength, keyLength);
		    		key = first + ".........." + second;
		    		if(print)
		    			ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1], ("Key is : " + key));
		    	}else {
		    		if(print)
		    			ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1], ("Key is : " + key));
		    	}
		    }else {
	    		ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1], "Key is null ");
		    }
		} catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return key;
	}
	
	public void processStripeCard() {
		String failureUrl = "";
		try {
			HttpServletRequest request = ServletContexts.instance().getRequest();
			String responseUrl = new MainUtil().getInfoFromProperty("Domain_Name");
			String successUrl = responseUrl + Dbox_Result_Page;
			String redirectUrl = responseUrl + Dbox_Response_Page;
			String stripeOwnerEmail = new MainUtil().getInfoFromProperty("Stripe_Owner_Email");
			
			String companyName = this.getCompany() != null && this.getCompany().getCompanyName() != null ? this.getCompany().getCompanyName().trim() : ""; 
			
			Currencies currency = this.getCurrencies();
			PaymentMethod paymentMethod = this.getPaymentMethod();
			Company company = paymentMethod.getCompany();
			
			String apikey = StripeBean.API_KEY_SECRET(company);
			
			if(this.isMonthlyDonation() && this.getSecretKeyUsed() != null) {
				apikey = this.getSecretKeyUsed();
			}else {
				String apikey1 = checkForApikey(paymentMethod);
				if(apikey1!=null && apikey1.trim().length()>0 && paymentMethod.getCompany().isSelfStripeAccount()){
					apikey = apikey1.trim();					
				}
			}
			
			com.stripe.Stripe.apiKey = apikey;
			
			identifyKey(apikey, true);
			
			String tokenStr = request != null ? request.getParameter("stripeToken") : null;
			if(tokenStr == null || tokenStr.trim().isEmpty()){
				tokenStr = this.getStripeToken();
			}
			
			String convertedSource = tokenStr;
			StripeBean sConnect = new StripeBean();
			if(tokenStr != null && tokenStr.startsWith(StripeBean.Stripe_Token_Prefix)) {
				convertedSource = sConnect.convertTokenToSource(tokenStr, this.getEmail(), apikey);
			}
			
			String customerId = null;
			if(this.isMonthlyDonation() && this.getFundDetailsList() != null && this.getFundDetailsList().size() > 0){
				customerId = sConnect.createCustomerForSepaRecurring(convertedSource, this.getEmail(), apikey);
				int acctId = this.getFundDetailsList().get(0).getUserAccId();
				if(acctId > 0) {
					UserAccountDetails stripeAcc = entityManager.find(UserAccountDetails.class, acctId);
					stripeAcc.setSecretKeyUsed(apikey);
					stripeAcc.setCustomerId(customerId);
					stripeAcc.setCountryCode(this.getCardCountryCode());
					entityManager.merge(stripeAcc);
					entityManager.flush();
				}
			}
			
			Map<String, Object> params = new HashMap<String, Object>();
			double amount = this.getDonationAmount();
 			double fee = this.getDonationFeeAmount();
			if(this.isTransTakenOver()){
				amount = amount + fee;
			}
			Integer amountInCents = StripeBean.convertToCents(amount);
			
    		params.put("amount", amountInCents);
    		params.put("currency", currency.getCurrencyCode().toLowerCase());
    		if(this.isMonthlyDonation() && customerId != null && !customerId.trim().isEmpty()){
    			params.put("customer", customerId);
    		}else{
    			params.put("source", convertedSource);
    		}
    		if(!company.isSelfStripeAccount()){
    			double applicationfee = this.getDonationFeeAmount();
    			String selfStripeKey = paymentMethod.getPaymentKeys().getAccountId();
    			if(applicationfee > 0) {
    				params.put("application_fee_amount", Math.round(applicationfee * 100));
    				Map<String, Object> destinationParams = new HashMap<String, Object>();
		    		destinationParams.put("destination", selfStripeKey);
		    		params.put("transfer_data", destinationParams);
    			}
    			params.put("on_behalf_of", selfStripeKey);
    		} 

    		params.put("statement_descriptor", statementDescriptorCharLengthSet(company.getCompanyName()));
			
    		/************ update Description in stripe *********************/
    		StringBuffer str = new StringBuffer();	
    		String result = null;
    			if(this.getProductGroup() != null){
    				String causeCode = this.getProductGroup().getProductCode();
    				str = str.append(causeCode);
   			 		str=str.append(",");
    			}
    		if(str.toString().length()>0){
    			result = str.toString().substring(0,str.toString().length()-1);
    		}
    		String updateDescription =( (this.getFundGroup() != null ? this.getFundGroup().getDonateUser().getUserNr() : "") 
    										+ " - "+ (this.getFundDetailsGroup() != null ? this.getFundDetailsGroup().getId() : "") );
    		if(result != null){
    			updateDescription = updateDescription +" - ( "+ result + " )"; 
    		}
    		
    		params.put("description", updateDescription);
    		
    		/************ update Description in stripe *********************/
    		
    		List<Object> paymentMethodTypes = new ArrayList<>();
			paymentMethodTypes.add("card");
			
			params.put("payment_method_types",paymentMethodTypes);
			params.put("confirm",true);
    		
			PaymentIntent paymentIntent = null;
			try {
				com.stripe.Stripe.apiKey = apikey;
				paymentIntent = PaymentIntent.create(params);
			}catch(StripeException e) {
				ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
				this.setWebhookRedirectUrl(failureUrl);
			}
    		
			if(paymentIntent != null) {
	    		String Status = paymentIntent != null ? paymentIntent.getStatus() : "";
	    		String transCode = paymentIntent != null ? paymentIntent.getId() : null;
	    		
	    		String payStatusName = PaymentStatusBean.Failure;
	    		if(Status.equalsIgnoreCase("succeeded")){
	    			payStatusName = PaymentStatusBean.Success;
	    			this.setWebhookRedirectUrl(successUrl);
	    		}else if(Status.equalsIgnoreCase("processing")){
	    			payStatusName = PaymentStatusBean.Success;
	    			this.setWebhookRedirectUrl(successUrl);
	    		}else if(Status.equalsIgnoreCase("pending")){
	    			payStatusName = PaymentStatusBean.Success;
	    			this.setWebhookRedirectUrl(successUrl);
	    		}else if(Status.equalsIgnoreCase("failed")){
	    			payStatusName = PaymentStatusBean.Failure;
	    			this.setWebhookRedirectUrl(failureUrl);
	    		}else if(Status.equalsIgnoreCase("refunded")){
	    			payStatusName = PaymentStatusBean.Refund;
	    			this.setWebhookRedirectUrl(failureUrl);
	    		}
	    		List<PaymentStatus> paymentStatusList = entityManager.createNamedQuery("findPaymentStatusByCompanyAndStatusType")
	    															.setParameter("companyId", company.getId())
	    															.setParameter("statusType", payStatusName)
	    															.getResultList();
	    		PaymentStatus payStatus = null;
	    		if(paymentStatusList != null && paymentStatusList.size() > 0) {
	    			payStatus = paymentStatusList.get(0); 
	    		}
	    		updateTransactionCode(transCode, payStatus);
			}
		} catch (Exception e) {
    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
    		this.setWebhookRedirectUrl(failureUrl);
		}
	}
	
	public void updateTransactionCode(String transCode, PaymentStatus payStatus) {
		try {
			if(this.getFundDetailsList() != null && this.getFundDetailsList().size() > 0) {
				String campaignDetails = "", receiptNo = "";
				Users users = null;
				String localeString = localeSelector.getLocaleString();
				Locale locale = new Locale(localeString);
				NumberFormat currencyFormatter = NumberFormat.getNumberInstance(locale);
				currencyFormatter.setMinimumFractionDigits(2);
				currencyFormatter.setMaximumFractionDigits(2);
				for(FundDetails fDetails : this.getFundDetailsList()) {
					if(users == null) {
						FundGroup fundGroup = fDetails.getFundGroup();
						users = fundGroup.getDonateUser();
						if(users != null) {
							receiptNo = generateReceiptNo(String.valueOf(users.getId()));
						}
					}
					fDetails.setStripeSrc(null);
					fDetails.setInvoiceNo(receiptNo);
					fDetails.setTransactionCode(transCode);
					fDetails.setPaymentStatus(payStatus);
					entityManager.merge(fDetails);
					entityManager.flush();
					
					try {
						ProductGroup prodGroup = fDetails.getProductGroup();
						String campaignName = prodGroup != null ? new ProductBean().getProductNameLang(prodGroup.getProduct(), this.getLangCode()) : "";
						campaignDetails = campaignDetails + 
								"<tr>"+
								"	<td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 5px 10px 5px 10px; border-radius: 0px 0px 4px 4px; color: #666666; "+
								"		font-family: 'Poppins', sans-serif; font-size: 16px; font-weight: 400; line-height: 25px;  border: 1px solid #0bb2d4;\">"+
								"			<p style=\"margin: 0;\">"+campaignName+"</p>"+
								"	</td>"+
								"	<td bgcolor=#ffffff align=\"left\" style=\"padding: 5px 10px 5px 10px; border-radius: 0px 0px 4px 4px; color: #666666; "+
								"		font-family: 'Poppins', sans-serif; font-size: 16px; font-weight: 400; line-height: 25px;  border: 1px solid #0bb2d4;\">"+
								"			<p style=\"margin: 0;\">"+currencyFormatter.format(fDetails.getAmount().doubleValue())+"</p>"+
								"	</td>"+
								"</tr>";
					} catch(Exception e) {
						
					}
					
					try {
						ProductGroup prodGroup = fDetails.getProductGroup();
						int prodGroupId = prodGroup.getId();
						String query = "select sum(fd.amount) from fund_details fd "
								+ " left join fund_group fg on fd.id_fundgroup = fg.id where "
								+ " fd.id_paymentstatus = " + payStatus.getId() 
								+ " and (fd.installment IS NOT NULL and fd.installment != '' "
								+ " and ((CONVERT(fd.installment , UNSIGNED integer) <= 1 and fd.installment not LIKE '%/%') or fd.installment LIKE '%/%'))"
								+ " and fd.id_productgroup = " + prodGroupId;
						BigDecimal amount = (BigDecimal) entityManager.createNativeQuery(query).getSingleResult();
		
						query = "select (sum(trans_fee) + sum(app_fee)) from fund_details fd "
								+ " left join fund_group fg on fd.id_fundgroup = fg.id where "
								+ " fd.id_paymentstatus = " + payStatus.getId() 
								+ " and (fd.installment IS NOT NULL and fd.installment != '' "
								+ " and ((CONVERT(fd.installment , UNSIGNED integer) <= 1 and fd.installment not LIKE '%/%') or fd.installment LIKE '%/%')) "
								+ " and fd.trans_taken_over = true "
								+ " and fd.id_productgroup = " + prodGroupId;
						
						BigDecimal wholeFee = (BigDecimal) entityManager.createNativeQuery(query).getSingleResult();
						
		
						double collectedAmount = 0.0;
						if (amount != null) {
							collectedAmount = amount.doubleValue();
						}
						if (wholeFee != null) {
							collectedAmount = collectedAmount + wholeFee.doubleValue();
						}
						
						if(collectedAmount > 0) {
							prodGroup.setCollectedAmount(collectedAmount);
							entityManager.merge(prodGroup);
							entityManager.flush();
						}
					} catch (Exception e) {
			    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
					}
				}
				sendDonationReceipt(campaignDetails, users, receiptNo, transCode);
			}
		} catch (Exception e) {
    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void sendDonationReceipt(String campaignDetails, Users users, String receiptNo, String transCode) {
		try {
			if(users != null) {
				Map<String, String> messages = Messages.instance();
				try {
					if(this.getLangCode() != null) {
						messages = new LanguageBean().getMessagesPropertiesFromLangCode(this.getLangCode().toLowerCase());
					}
				} catch(Exception e) {
					ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
				}
				MailContent mailContent = new MailContent();
				mailContent.setMessages(messages);
				
				String name = users.getFirstName();
				name = name != null && !name.trim().isEmpty() ? name.trim() : "";
				Company company = users.getCompany();
				String street = company.getAddress();
				String houseNo = company.getHouseno();
				String city = company.getCity();
				String country = company.getCountry();
				String zip = company.getZip();
				String companyName = company.getCompanyName();
				
				street = street != null && !street.isEmpty() ? street.trim() : "";
				houseNo = houseNo != null && !houseNo.isEmpty() ? houseNo.trim() : "";
				city = city != null && !city.isEmpty() ? city.trim() : "";
				country = country != null && !country.isEmpty() ? country.trim() : "";
				zip = zip != null && !zip.isEmpty() ? ("- " + zip.trim()) : "";
				companyName = companyName != null && !companyName.isEmpty() ? companyName.trim() : "";
				
				String transDate = DateUtil.getDateToStringFormat(new Date(), company.getDatePattern());
				
				String message = mailContent.getEmailContentForDonationReceipt(name, companyName, street, houseNo, city, country, zip, campaignDetails, transDate, receiptNo, transCode);
				
				String subject = messages.get("Donation_Receipt");
				
				String logo = MainUtil.getLogoImagePath(company.getId()) + company.getId() + ".png";
				
				String fromEmail = "info@softitservice.de";
				String toMail = this.getEmail();
				String bccMail = "farthastech1994@gmail.com";
				String ccMail = "";
				
				Users companyUser = company.getUsers();
				if(companyUser != null && companyUser.getPrimaryEmail() != null && !companyUser.getPrimaryEmail().trim().isEmpty()) {
					fromEmail = companyUser.getPrimaryEmail();
				}
				boolean local = new MainUtil().checkLocal();
				if(local) {
					fromEmail = "info@softitservice.de";
					toMail = new MainUtil().getInfoFromProperty("Email");
					bccMail = "";
				}
				
				boolean sendDonationReceipt = company.isSendDboxEmail();
				if(sendDonationReceipt) {
					String mailInfo = "DONATION RECEIPT MAIL INFO. FROM : " + fromEmail + " TO : "+ toMail + " CC : " + ccMail + " BCC : " + bccMail;
					ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1], mailInfo);
					if(fromEmail != null && !fromEmail.trim().isEmpty() && toMail != null && !toMail.trim().isEmpty()) {
						MailClient mailClient = new MailClient();
						mailClient.sendMail(fromEmail , toMail, ccMail, bccMail, subject, message, logo, "");
					}else {
						mailInfo = "DONATION RECEIPT MAIL HAVING ISSUE";
						ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1], mailInfo);
					}
				}else {
					String mailInfo = "DONATION RECEIPT MAIL BLOCKED. FROM : " + fromEmail + " TO : "+ toMail + " CC : " + ccMail + " BCC : " + bccMail;
					ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1], mailInfo);
				}
			}
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public String generateReceiptNo(String userId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String invoiceNo = sdf.format(new Date());
		sdf = new SimpleDateFormat("HHmmssSS");
		return "RE_"+userId+"_"+invoiceNo+"_"+sdf.format(new Date());
	}
	
	public void processStripePaymentType(){
		try {
			String responseUrl = new MainUtil().getInfoFromProperty("Domain_Name");
			String failureUrl = responseUrl + Dbox_Result_Page + "?error=error";
			String redirectUrl = responseUrl + Dbox_Response_Page;
			String stripeOwnerEmail = new MainUtil().getInfoFromProperty("Stripe_Owner_Email");
			
			String companyName = this.getCompany() != null && this.getCompany().getCompanyName() != null ? this.getCompany().getCompanyName().trim() : ""; 
			
			Currencies currency = this.getCurrencies();
			PaymentMethod paymentMethod = this.getPaymentMethod();
			Company company = paymentMethod.getCompany();
			
			String param = PayMethodBean.Stripe + "," + paymentMethod.getPaymentType().getPaymentName();
			redirectUrl = redirectUrl + "?" + StaticValues.PaymentSrc + "=" +param;
			
			String apikeyCommon = StripeBean.API_KEY_SECRET(company);
			String apikey = checkForApikey(paymentMethod);
			if(apikey != null && apikey.trim().length() > 0 && paymentMethod.getCompany().isSelfStripeAccount()){
				apikeyCommon = apikey.trim();					
			}else{
				apikeyCommon = StripeBean.API_KEY_SECRET(company);
			}
				
			Map<String, Object> sourceParams = new HashMap<String, Object>();
			if(this.getPaymentTypeName().trim().equals(StripeBean.STRIPE_SEPAPAY)){
				String iban = this.getIban() != null ? this.getIban().toUpperCase().replaceAll("\\s+","") : this.getIban();
				iban = iban.toUpperCase().replaceAll(" ","");
				Map<String, Object> sepaParams = new HashMap<String, Object>();
				sepaParams.put("iban", iban.toUpperCase());	  
				sourceParams.put("type", "sepa_debit");
				sourceParams.put("sepa_debit", sepaParams);					
			}else{
				sourceParams.put("type", this.getPaymentTypeName().toLowerCase());
			}			
			
			double amount = this.getDonationAmount();
 			double fee = this.getDonationFeeAmount();
			if(this.isTransTakenOver()){
				amount = amount + fee;
			}
			Integer amountInCents = StripeBean.convertToCents(amount);
			
			if(!this.getPaymentTypeName().trim().equals(StripeBean.STRIPE_SEPAPAY)) {
				sourceParams.put("amount", amountInCents);
			}
			sourceParams.put("currency", currency.getCurrencyCode().toLowerCase());

			Map<String, Object> redirectParams = new HashMap<String, Object>();
			redirectParams.put("return_url", redirectUrl);
			sourceParams.put("redirect", redirectParams);
			
			Map<String, Object> countryParams = new HashMap<String, Object>();
			
			if(this.getPaymentTypeName().trim().equals(StripeBean.STRIPE_SOFORT)){
				String country = /* this.getSofortCountry() != null && !this.getSofortCountry().isEmpty() ? this.getSofortCountry() : */ "DE";
				countryParams.put("country", country);
			}else if(this.getPaymentTypeName().trim().equals(StripeBean.STRIPE_GIROPAY) || this.getPaymentTypeName().trim().equals(StripeBean.STRIPE_EPS) ||
					this.getPaymentTypeName().trim().equals(StripeBean.STRIPE_BANCONTACT) || this.getPaymentTypeName().trim().equals(StripeBean.STRIPE_MULTIBANCO) || 
					this.getPaymentTypeName().trim().equals(StripeBean.STRIPE_P24)){
				if(this.getFirstName() != null && !this.getFirstName().trim().isEmpty() && this.getEmail() != null && !this.getEmail().isEmpty()) {
					Map<String, Object> ownerParams = new HashMap<String, Object>();	
					ownerParams.put("name", this.getFirstName());
					if(this.getPaymentTypeName().trim().equals(StripeBean.STRIPE_MULTIBANCO) || this.getPaymentTypeName().trim().equals(StripeBean.STRIPE_P24)){
						ownerParams.put("email", this.getEmail()); 
					}
					sourceParams.put("owner", ownerParams);
				}else if(stripeOwnerEmail != null && !stripeOwnerEmail.trim().isEmpty()) {
					Map<String, Object> ownerParams = new HashMap<String, Object>();	
					ownerParams.put("name", stripeOwnerEmail);
					if(this.getPaymentTypeName().trim().equals(StripeBean.STRIPE_MULTIBANCO) || this.getPaymentTypeName().trim().equals(StripeBean.STRIPE_P24)){
						ownerParams.put("email", stripeOwnerEmail); 
					}
					sourceParams.put("owner", ownerParams);
				}
			}else if(this.getPaymentTypeName().trim().equals(StripeBean.STRIPE_SEPAPAY)){
				String ownerName = StringUtil.fixed3DotLengthString(this.getAccountName());
				if(ownerName != null && !ownerName.trim().isEmpty()) {		
					Map<String, Object> ownerParams = new HashMap<String, Object>();						
					ownerParams.put("name", ownerName.trim()); 
					sourceParams.put("owner", ownerParams);
				}
			}
			
			if(!this.getPaymentTypeName().trim().equals(StripeBean.STRIPE_SEPAPAY) && !this.getPaymentTypeName().trim().equals(StripeBean.STRIPE_P24)){
				countryParams.put("statement_descriptor", statementDescriptorCharLengthSet(companyName));
				sourceParams.put(this.getPaymentTypeName().toLowerCase(), countryParams);
				sourceParams.put("statement_descriptor", statementDescriptorCharLengthSet(companyName));
			}
			
			com.stripe.Stripe.apiKey = apikeyCommon;
			identifyKey(apikeyCommon, true);
			Source source = Source.create(sourceParams);
			if(this.getPaymentTypeName().trim().equals(StripeBean.STRIPE_SEPAPAY) && source != null) {
				String customerID = null;
				StripeBean sConnect = new StripeBean();
				if(this.getFundDetailsList()!=null && this.getFundDetailsList().size() > 0){
					Integer acctId = this.getFundDetailsList().get(0).getUserAccId();
					UserAccountDetails stripeAcc = entityManager.find(UserAccountDetails.class, acctId);
					String keyUsed = stripeAcc.getSecretKeyUsed();
			    	String custId = stripeAcc.getCustomerId();
			    	if(keyUsed != null && !keyUsed.trim().isEmpty() && keyUsed.trim().startsWith(StripeBean.Stripe_SecretKey_Prefix) &&
			    			custId != null && !custId.trim().isEmpty() && custId.trim().startsWith(StripeBean.Stripe_Customer_Prefix)) {
			    		customerID = custId;
			    	}else {
						customerID = sConnect.createCustomerForSepaRecurring(source.getId(), this.getEmail(), apikeyCommon);	//Create a customer to reuse for recurring payments
						stripeAcc.setSecretKeyUsed(apikeyCommon);
						stripeAcc.setCustomerId(customerID);
						stripeAcc.setCountryCode(this.getCardCountryCode());
						entityManager.merge(stripeAcc);
						entityManager.flush();
			    	}
				}
			}
		
			if(source != null){
				String sourceId = source.getId();
				String webhookRedirectUrl = "";
				if(this.getPaymentTypeName().trim().equals(StripeBean.STRIPE_SEPAPAY)) {
					webhookRedirectUrl = redirectUrl + "&source=" + sourceId;
				}else {
					webhookRedirectUrl = source.getRedirect().getUrl();
				}
				this.setWebhookRedirectUrl(webhookRedirectUrl);
				
				updateSourceCodeInFDetails(sourceId);
				
				if(!this.getPaymentTypeName().trim().equals(StripeBean.STRIPE_SEPAPAY)) {
					PaymentType payType = paymentMethod.getPaymentType();
					PaymentType paymenType =  payType !=null && payType.isSupportRecurring() ? payType : null;
					if(this.isMonthlyDonation() && paymenType != null && this.getFundDetailsList() != null && this.getFundDetailsList().size() > 0){
						Integer acctId = this.getFundDetailsList().get(0).getUserAccId();
						if(acctId != null){
							UserAccountDetails userAcct = entityManager.find(UserAccountDetails.class, acctId);
							userAcct.setCustomerId(sourceId);
							userAcct.setCountryCode(this.getCardCountryCode());
							entityManager.merge(userAcct);
							entityManager.flush();
						}
					}
				}
			}
		} catch (Exception e) {
    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public String responseFromDboxResponsePage() {
		ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1], "Response called");
		resetData();
    	String responseStr = Dbox_Result_Page;
    	try {
	    	String userId = null;
	    	HttpServletRequest request = ServletContexts.instance().getRequest();
			String paymentSrc = request.getParameter(StaticValues.PaymentSrc);
			
			if(paymentSrc != null) {
				responseStr = Dbox_Result_Page;
				this.setFailure(false);
				String[] wsrcsplit = paymentSrc.split(",");
				if(wsrcsplit != null && wsrcsplit.length == 2 && wsrcsplit[0].equalsIgnoreCase(PayMethodBean.Stripe)) {
					String payType = wsrcsplit[1];
					String stripeSource = request.getParameter("source");
					responseFromDboxResponsePage_Sub(payType, stripeSource);
				}else {
					this.setFailure(true);
				}
				responseStr = dboxResultPage(userId, this.isFailure(), responseStr);
			}
    	}catch (Exception e) {
    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
    	ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1], "Response Url is: " + responseStr);
		return responseStr;
    }
	
	public void responseFromDboxResponsePage_Sub(String payType, String stripeSource) {
		try {
			if(stripeSource != null) {
				List<FundDetails> fcdSrcEntries = null;
				try{
					String query = "select fcd from FundDetails fcd where fcd.stripeSrc LIKE '%"+stripeSource+"%'";
					fcdSrcEntries = entityManager.createQuery(query).getResultList();
				}catch (Exception e) {
					fcdSrcEntries = null;
				}
				if(fcdSrcEntries != null && fcdSrcEntries.size() > 0){
					this.setFundDetailsList(fcdSrcEntries);
					PaymentMethod pay = fcdSrcEntries.get(0).getPaymentMethod();
					Company company = pay.getCompany();
					this.setCurrencies(company.getCurrencies());
					String apikey = checkForApikey(pay);
					if(apikey!=null && apikey.trim().length() > 0 && company.isSelfStripeAccount()){
						com.stripe.Stripe.apiKey = apikey.trim();
						identifyKey(apikey, true);
					}else{
						String key = StripeBean.API_KEY_SECRET(company);
						com.stripe.Stripe.apiKey = key;
						identifyKey(key, true);
					}
					Source checkSource = Source.retrieve(stripeSource);
					boolean local = new MainUtil().checkLocal() || !company.isLiveAccount();
					boolean charge_thru_webhook = new StripeBean().checkSingleUsagePayType(payType);
					boolean charge = (!charge_thru_webhook || local);
					
	    			if(checkSource != null 
	    					&& (checkSource.getStatus().equalsIgnoreCase(StripeBean.sourceStatus_chargeable) 
    							|| checkSource.getStatus().equalsIgnoreCase(StripeBean.sourceStatus_consumed))){
	    				if(charge) {
	    					this.setStripeSource(stripeSource);
	    					executeStripePayment(fcdSrcEntries);
	    				}else {
	    					this.setFailure(false);
	    				}
	    			}else {
	    				this.setFailure(true);
	    				ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1], "Cancelled Transaction : "+ stripeSource);
	    			}
				}
			}
		} catch(Exception e) {
    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void executeStripePayment(List<FundDetails> fcdSrcEntries) {
		try {
			if(fcdSrcEntries != null && fcdSrcEntries.size() > 0){
				this.setFundDetailsList(fcdSrcEntries);
				FundDetails fDetail = fcdSrcEntries.get(0);
				FundDetailsGroup fDetailGroup = fDetail.getFundGroupDetails();
				FundGroup fundGroup = fDetail.getFundGroup();
				Users users = fundGroup.getDonateUser();
				Company company = users.getCompany();
				ProductGroup productGroup = fDetail.getProductGroup();
				this.setDefaultUser(users);
				this.setCompany(company);
				this.setCurrencies(company.getCurrencies());
				PaymentMethod paymentMethod = fDetail.getPaymentMethod();
				this.setPaymentMethod(paymentMethod);
				PaymentType paymentType = paymentMethod.getPaymentType();
				String paymentTypeName = paymentType.getPaymentName();
				this.setPaymentTypeName(paymentTypeName);
				this.setProductGroup(productGroup);
				this.setFundDetailsGroup(fDetailGroup);
				this.setFundGroup(fundGroup);
				this.setUserAccId(fDetail.getUserAccId());
				
				this.setFirstName(users.getFirstName());
				this.setLastName(users.getLastName());
				this.setEmail(users.getPrimaryEmail());
				this.setLangCode(users.getLanguages() != null ? users.getLanguages().getLangCode() : "EN");
				
				updateAppAndPayFee(fcdSrcEntries);
				
				if(paymentType != null){
					String payType = paymentType.getPaymentType() != null ? paymentType.getPaymentType().trim() : "";
					String payTypeName = paymentType.getPaymentName() != null ? paymentType.getPaymentName().trim() : "";
					boolean stripePayment = payType.equalsIgnoreCase(PayMethodBean.Stripe) && PayMethodBean.isStripeType(payTypeName);
					boolean isrecurring = paymentType.isOtherReuccring() && this.getUserAccId() > 0;
					if(stripePayment) {
						if((!isrecurring && paymentType != null &&  !paymentTypeName.equalsIgnoreCase(StripeBean.STRIPE_CreditCard)) 
								|| this.isAutoCharge() || this.isManualCharge()) {
							doStripePaymentType();
						} else if(payType != null && paymentTypeName != null && isrecurring){
							processOtherStripePayments();
						}
					}
				}
			}
		} catch (Exception e) {
			this.setFailure(true);
    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void processOtherStripePayments() {
		try {
			PaymentMethod paymentMethod = this.getPaymentMethod();
			Company company = this.getCompany();
			PaymentType paymentType = paymentMethod.getPaymentType();
			String paymentMethodName = paymentType.getPaymentName();
			Users users = this.getFundGroup().getDonateUser();
			String customerEmail = users.getPrimaryEmail();
			
			String apikey = checkForApikey(paymentMethod);
			String apikeyCommon = "";
		 	if(apikey != null && apikey.trim().length() > 0 && company.isSelfStripeAccount()){
		 		apikeyCommon = apikey.trim();     
			}else{
				if(!company.isSelfStripeAccount())
					apikeyCommon = StripeBean.API_KEY_SECRET(company);
			}
		 	
		 	String stripeSource = this.getStripeSource();
		 	
			String sepa_source = new StripeBean().changeNormalSourceToSepaRecurring(paymentMethodName.toLowerCase(), stripeSource,
						company.getCurrencies().getCurrencyCode(), users.getPrimaryEmail());
			String stripeCustomerId = new StripeBean().createCustomerForSepaRecurring(sepa_source, customerEmail, apikeyCommon);
			
			if(stripeCustomerId != null){
				int acctId = this.getUserAccId();
				if(acctId > 0) {
					UserAccountDetails stripeAcc = entityManager.find(UserAccountDetails.class, acctId);
					String src = sepa_source;
					stripeAcc.setSecretKeyUsed(apikeyCommon);
					stripeAcc.setCustomerId(stripeCustomerId);
					stripeAcc.setCountryCode(this.getCardCountryCode());
					entityManager.merge(stripeAcc);
					entityManager.flush();
				}
				this.setCustomerId(stripeCustomerId);
				this.setSecretKeyUsed(apikeyCommon);
				doStripePaymentType();
			}
		} catch (Exception e) {
			this.setFailure(true);
    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	
	public void doStripePaymentType(){
		String apikeyCommon = "";
		try{
			UserTransaction utx = null;
			try {
				if(this.isAutoCharge()) {
					utx = (UserTransaction) new InitialContext().lookup("java:jboss/UserTransaction");
					utx.begin();	
				}
			}catch (Exception e) {
				ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			}
			
			PaymentMethod paymentMethod = this.getPaymentMethod();
			PaymentType paymentType = paymentMethod.getPaymentType();
			String paymentTypeName = paymentType.getPaymentName();
			Company company = paymentMethod.getCompany();
			Currencies curr = this.getCompany().getCurrencies();
			boolean transOverTaken = this.isTransTakenOver();
			HttpServletRequest request = ServletContexts.instance().getRequest();
			double stripecommision=0.0;
			String iban = this.getIban();
			String apikey = null;
			boolean otherStripeRecurring = false;
			if(this.getCustomerId() != null && !this.getCustomerId().trim().isEmpty() && 
				this.getCustomerId().startsWith(StripeBean.Stripe_Customer_Prefix) &&
				this.getSecretKeyUsed() != null && !this.getSecretKeyUsed().trim().isEmpty() && 
				this.getSecretKeyUsed().startsWith(StripeBean.Stripe_SecretKey_Prefix)){
				otherStripeRecurring = true;
			}
			
			String sourceToken = this.getStripeToken() != null && !this.getStripeToken().isEmpty() ? this.getStripeToken() : request.getParameter("source");
			boolean selfStripeAccount = company.isSelfStripeAccount();
			if(otherStripeRecurring){
				apikeyCommon = this.getSecretKeyUsed();
				iban = this.getCustomerId();
			}else{
				apikey = this.checkForApikey(paymentMethod);
				if(apikey != null && apikey.trim().length()>0 && selfStripeAccount){
					apikeyCommon = apikey.trim();     
				}else{
					if(!selfStripeAccount)
						apikeyCommon = StripeBean.API_KEY_SECRET(company);
				}
			}
			com.stripe.Stripe.apiKey = apikeyCommon;
			identifyKey(apikeyCommon, true);
			
			boolean allowCharge = false;
		 	String customerId = null;
	 		if(sourceToken != null && sourceToken.startsWith(StripeBean.Stripe_Customer_Prefix)) {
	 			allowCharge = true;
	 			customerId = sourceToken.trim();
	 		}else {
	 			if(sourceToken != null && sourceToken.startsWith(StripeBean.Stripe_Source_Prefix)) {
			 		Source checkSource = Source.retrieve(StripeBean.stripeSourceSplit(sourceToken));
			 		if(checkSource != null && checkSource.getCustomer() != null && checkSource.getCustomer().startsWith(StripeBean.Stripe_Customer_Prefix)){
			 			customerId = checkSource.getCustomer();
			 		}
	    			if(checkSource != null && checkSource.getStatus().equalsIgnoreCase(StripeBean.sourceStatus_chargeable)){
	    				allowCharge = true;
	    			}
	 			}else{
			 		allowCharge = true;
			 	}
	 		}
	 		
	 		if(allowCharge){

				PaymentType payTypeOverride = (PaymentType) entityManager.createNamedQuery("findPaymentTypeById")
																				.setParameter("payTypeId", PayMethodBean.Stripe_Sepa_Id)
																				.getSingleResult();	
				PaymentType payType = paymentMethod.getPaymentType();
				payType = paymentTypeName.equalsIgnoreCase(StripeBean.STRIPE_SEPAPAY) ? payTypeOverride : payType;
				
				
				
				Map<String, Object> chargeParams = new HashMap<String, Object>();
				if(otherStripeRecurring){
					chargeParams.put("customer", this.getCustomerId());
				}else if(customerId != null && customerId.startsWith(StripeBean.Stripe_Customer_Prefix)) {
					chargeParams.put("customer", customerId);
				}else{
					chargeParams.put("source", StripeBean.stripeSourceSplit(sourceToken));
				}
				
				double amount = this.getDonationAmount();
	 			double fee = this.getDonationFeeAmount();
	 			if(transOverTaken){
	 				amount = amount + fee;
				}
				Integer amountInCents = StripeBean.totalamountChargable(amount); 
				
				chargeParams.put("amount", amountInCents);
				chargeParams.put("currency", curr.getCurrencyCode().toLowerCase());
				
				/************ update Description in stripe *********************/	
				
				StringBuffer str = new StringBuffer();	
	    		String result = null;
	    			if(this.getProductGroup() != null){
	    				String causeCode = this.getProductGroup().getProductCode();
	    				str = str.append(causeCode);
	   			 		str=str.append(",");
	    			}
	    		if(str.toString().length()>0){
	    			result = str.toString().substring(0,str.toString().length()-1);
	    		}
	    		String updateDescription =( (this.getFundGroup() != null ? this.getFundGroup().getDonateUser().getUserNr() : "") 
	    										+ " - "+ (this.getFundDetailsGroup() != null ? this.getFundDetailsGroup().getId() : "") );
	    		
	    		String idempotencyKey = updateDescription;
	    		
	    		if(result != null){
	    			updateDescription = updateDescription +" - ( "+ result + " )"; 
	    		}
	    		chargeParams.put("description", updateDescription);
	    		
	    		/************ update Description in stripe *********************/		
				
	    		Map<String, Object> destinationParams = new HashMap<String, Object>();  
	    		
	    		if(!company.isSelfStripeAccount()){
		    		double piFee = this.getDonationFeeAmount();
					if(piFee > 0) {
						chargeParams.put("application_fee_amount", Math.round(piFee * 100));
	    				destinationParams = new HashMap<String, Object>();
			    		destinationParams.put("destination", paymentMethod.getPaymentKeys().getAccountId());
			    		chargeParams.put("transfer_data", destinationParams);
	    			}
	    		}
				
				if(paymentTypeName.trim().equals(StripeBean.STRIPE_SEPAPAY)){
	    			chargeParams.put("statement_descriptor", statementDescriptorCharLengthSet(company.getCompanyName()));
	    		}
				
				String stpay = paymentTypeName.trim().toLowerCase(); 
	    		if(stpay.equalsIgnoreCase(StripeBean.STRIPE_SEPAPAY) || otherStripeRecurring) {
	    			if(stpay.equalsIgnoreCase(StripeBean.STRIPE_CreditCard)) {
	    				stpay = "card";
	    			}else {
	    				stpay = "sepa_debit";
	    			}
	    		}
	    		List<Object> paymentMethodTypes = new ArrayList<>();
	    		paymentMethodTypes.add(stpay);
	    		chargeParams.put("payment_method_types",paymentMethodTypes);
	    		chargeParams.put("confirm",true);
	    		
	    		PaymentIntent paymentIntent = null;
	    		if(otherStripeRecurring){
    				com.stripe.Stripe.apiKey = this.getSecretKeyUsed();
    			}else{
					apikey = this.checkForApikey(paymentMethod);
			 		if(apikey!=null && apikey.trim().length()>0 && selfStripeAccount){
			 			apikeyCommon = apikey.trim();     
			 		}else{
						if(!selfStripeAccount)
							apikeyCommon = StripeBean.API_KEY_SECRET(company);
			 		}
    			}
	    		
	    		identifyKey(apikeyCommon, true);
	    		
	    		boolean local = new MainUtil().checkLocal() || !company.isLiveAccount();
				if(local) {
					if(apikeyCommon != null && apikeyCommon.trim().startsWith(StripeBean.Stripe_SecretKey_Prefix)) {
						com.stripe.Stripe.apiKey = apikeyCommon;
						paymentIntent = PaymentIntent.create(chargeParams);
					}
				}else {	
					com.stripe.Stripe.apiKey = apikeyCommon;
					RequestOptions options = RequestOptions.builder().setIdempotencyKey(idempotencyKey).build();
					try {
						paymentIntent = PaymentIntent.create(chargeParams,options);
					}catch(StripeException e) {
						ExceptionMsg.ErrorMsg(e,Thread.currentThread().getStackTrace()[1]);
					}
				}
				
				if(paymentIntent != null) {
		    		String Status = paymentIntent != null ? paymentIntent.getStatus() : "";
		    		String transactionCode = paymentIntent != null ? paymentIntent.getId() : null;
					String responseUrl = new MainUtil().getInfoFromProperty("Domain_Name");
					String successUrl = responseUrl + Dbox_Result_Page;
					String failureUrl = successUrl + "?error=error";
							
		    		String payStatusName = PaymentStatusBean.Failure;
		    		if(Status.equalsIgnoreCase("succeeded")){
		    			payStatusName = PaymentStatusBean.Success;
		    			this.setWebhookRedirectUrl(successUrl);
		    		}else if(Status.equalsIgnoreCase("processing")){
		    			payStatusName = PaymentStatusBean.Success;
		    			this.setWebhookRedirectUrl(successUrl);
		    		}else if(Status.equalsIgnoreCase("pending")){
		    			payStatusName = PaymentStatusBean.Success;
		    			this.setWebhookRedirectUrl(successUrl);
		    		}else if(Status.equalsIgnoreCase("failed")){
		    			payStatusName = PaymentStatusBean.Failure;
		    			this.setWebhookRedirectUrl(failureUrl);
		    		}else if(Status.equalsIgnoreCase("refunded")){
		    			payStatusName = PaymentStatusBean.Refund;
		    			this.setWebhookRedirectUrl(failureUrl);
		    		}
		    		List<PaymentStatus> paymentStatusList = entityManager.createNamedQuery("findPaymentStatusByCompanyAndStatusType")
		    															.setParameter("companyId", company.getId())
		    															.setParameter("statusType", payStatusName)
		    															.getResultList();
		    		PaymentStatus payStatus = null;
		    		if(paymentStatusList != null && paymentStatusList.size() > 0) {
		    			payStatus = paymentStatusList.get(0); 
		    		}
		    		
		    		updateTransactionCode(transactionCode, payStatus);
				}
	    		
	 		}
		 		
			try {
				if(utx != null && this.isAutoCharge())
					utx.commit();	
			}catch (Exception e) {
				ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			}
		} catch(Exception e){
			this.setFailure(true);
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public String dboxResultPage(String userId, boolean failure, String responseStr) {
		if(failure)
			responseStr = responseStr + "?error=error";
		return responseStr;
	}
	
	public void updateSourceCodeInFDetails(String transactionCode) {
		try {
			if(this.getFundDetailsList() != null && this.getFundDetailsList().size() > 0) {
				for(FundDetails fDetails : this.getFundDetailsList()) {
					fDetails.setStripeSrc(transactionCode);
					fDetails.setLangCode(this.getLangCode());
					fDetails.setLoggedIn(this.isLoggedIn());
					entityManager.merge(fDetails);
					entityManager.flush();
				}
			}
		} catch (Exception e) {
    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public String statementDescriptorCharLengthSet(String value){ 	// Statement descriptor must be 22 char stripe behaviour
		if(value!=null && !value.isEmpty() && value.trim().length() > 22){
			return value.trim().substring(0, 22);
		}
		return value;
	}
	
	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public String getCompanyLogo() {
		return companyLogo;
	}

	public void setCompanyLogo(String companyLogo) {
		this.companyLogo = companyLogo;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public String getCampaignImage() {
		return campaignImage;
	}

	public void setCampaignImage(String campaignImage) {
		this.campaignImage = campaignImage;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getFirstAmountStr() {
		return firstAmountStr;
	}

	public void setFirstAmountStr(String firstAmountStr) {
		this.firstAmountStr = firstAmountStr;
	}

	public String getSecondAmountStr() {
		return secondAmountStr;
	}

	public void setSecondAmountStr(String secondAmountStr) {
		this.secondAmountStr = secondAmountStr;
	}

	public String getThirdAmountStr() {
		return thirdAmountStr;
	}

	public void setThirdAmountStr(String thirdAmountStr) {
		this.thirdAmountStr = thirdAmountStr;
	}

	public String getFourthAmountStr() {
		return fourthAmountStr;
	}

	public void setFourthAmountStr(String fourthAmountStr) {
		this.fourthAmountStr = fourthAmountStr;
	}

	public double getFirstAmount() {
		return firstAmount;
	}

	public void setFirstAmount(double firstAmount) {
		this.firstAmount = firstAmount;
	}

	public double getSecondAmount() {
		return secondAmount;
	}

	public void setSecondAmount(double secondAmount) {
		this.secondAmount = secondAmount;
	}

	public double getThirdAmount() {
		return thirdAmount;
	}

	public void setThirdAmount(double thirdAmount) {
		this.thirdAmount = thirdAmount;
	}

	public double getFourthAmount() {
		return fourthAmount;
	}

	public void setFourthAmount(double fourthAmount) {
		this.fourthAmount = fourthAmount;
	}

	public boolean isMonthlyDonation() {
		return monthlyDonation;
	}

	public void setMonthlyDonation(boolean monthlyDonation) {
		this.monthlyDonation = monthlyDonation;
	}

	public String getProgressValue() {
		return progressValue;
	}

	public void setProgressValue(String progressValue) {
		this.progressValue = progressValue;
	}

	public int getProdGroupId() {
		return prodGroupId;
	}

	public void setProdGroupId(int prodGroupId) {
		this.prodGroupId = prodGroupId;
	}

	public Currencies getCurrencies() {
		return currencies;
	}

	public void setCurrencies(Currencies currencies) {
		this.currencies = currencies;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getHouseNo() {
		return houseNo;
	}

	public void setHouseNo(String houseNo) {
		this.houseNo = houseNo;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public Users getDefaultUser() {
		return defaultUser;
	}

	public void setDefaultUser(Users defaultUser) {
		this.defaultUser = defaultUser;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public FundGroup getFundGroup() {
		return fundGroup;
	}

	public void setFundGroup(FundGroup fundGroup) {
		this.fundGroup = fundGroup;
	}

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	public Languages getLanguages() {
		return Languages;
	}

	public void setLanguages(Languages languages) {
		Languages = languages;
	}

	public String getPaymentTypeName() {
		return paymentTypeName;
	}

	public void setPaymentTypeName(String paymentTypeName) {
		this.paymentTypeName = paymentTypeName;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public FundDetailsGroup getFundDetailsGroup() {
		return fundDetailsGroup;
	}

	public void setFundDetailsGroup(FundDetailsGroup fundDetailsGroup) {
		this.fundDetailsGroup = fundDetailsGroup;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public ProductGroup getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(ProductGroup productGroup) {
		this.productGroup = productGroup;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getAppSource() {
		return appSource;
	}

	public void setAppSource(String appSource) {
		this.appSource = appSource;
	}

	public String getDeviceSource() {
		return deviceSource;
	}

	public void setDeviceSource(String deviceSource) {
		this.deviceSource = deviceSource;
	}

	public boolean isTransTakenOver() {
		return transTakenOver;
	}

	public void setTransTakenOver(boolean transTakenOver) {
		this.transTakenOver = transTakenOver;
	}

	public double getDonationAmount() {
		return donationAmount;
	}

	public void setDonationAmount(double donationAmount) {
		this.donationAmount = donationAmount;
	}

	public double getDonationFeeAmount() {
		return donationFeeAmount;
	}

	public void setDonationFeeAmount(double donationFeeAmount) {
		this.donationFeeAmount = donationFeeAmount;
	}

	public String getWebhookRedirectUrl() {
		return webhookRedirectUrl;
	}

	public void setWebhookRedirectUrl(String webhookRedirectUrl) {
		this.webhookRedirectUrl = webhookRedirectUrl;
	}

	public String getStripeSource() {
		return stripeSource;
	}

	public void setStripeSource(String stripeSource) {
		this.stripeSource = stripeSource;
	}

	public String getStripeToken() {
		return stripeToken;
	}

	public void setStripeToken(String stripeToken) {
		this.stripeToken = stripeToken;
	}

	public String getSecretKeyUsed() {
		return secretKeyUsed;
	}

	public void setSecretKeyUsed(String secretKeyUsed) {
		this.secretKeyUsed = secretKeyUsed;
	}

	public int getUserAccId() {
		return userAccId;
	}

	public void setUserAccId(int userAccId) {
		this.userAccId = userAccId;
	}

	public boolean isAddressShow() {
		return addressShow;
	}

	public void setAddressShow(boolean addressShow) {
		this.addressShow = addressShow;
	}

	public boolean isAddressRequired() {
		return addressRequired;
	}

	public void setAddressRequired(boolean addressRequired) {
		this.addressRequired = addressRequired;
	}

	public boolean isPhoneShow() {
		return phoneShow;
	}

	public void setPhoneShow(boolean phoneShow) {
		this.phoneShow = phoneShow;
	}

	public boolean isPhoneRequired() {
		return phoneRequired;
	}

	public void setPhoneRequired(boolean phoneRequired) {
		this.phoneRequired = phoneRequired;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public boolean isAutoCharge() {
		return autoCharge;
	}

	public void setAutoCharge(boolean autoCharge) {
		this.autoCharge = autoCharge;
	}

	public boolean isManualCharge() {
		return manualCharge;
	}

	public void setManualCharge(boolean manualCharge) {
		this.manualCharge = manualCharge;
	}

	public boolean isFailure() {
		return failure;
	}

	public void setFailure(boolean failure) {
		this.failure = failure;
	}

	public boolean isEuropeanCard() {
		return europeanCard;
	}

	public void setEuropeanCard(boolean europeanCard) {
		this.europeanCard = europeanCard;
	}

	public String getCardCountryCode() {
		return cardCountryCode;
	}

	public void setCardCountryCode(String cardCountryCode) {
		this.cardCountryCode = cardCountryCode;
	}

	public List<PaymentMethod> getPayMethodList() {
		return payMethodList;
	}

	public void setPayMethodList(List<PaymentMethod> payMethodList) {
		this.payMethodList = payMethodList;
	}

	public List<FundDetails> getFundDetailsList() {
		return fundDetailsList;
	}

	public void setFundDetailsList(List<FundDetails> fundDetailsList) {
		this.fundDetailsList = fundDetailsList;
	}

	public List<ProductSubType> getProdSubTypeList() {
		return prodSubTypeList;
	}

	public void setProdSubTypeList(List<ProductSubType> prodSubTypeList) {
		this.prodSubTypeList = prodSubTypeList;
	}
	
}
