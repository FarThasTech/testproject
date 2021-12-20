package com.billing.recurring;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.crypto.Cipher;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.web.ServletContexts;

import com.billing.donor.DonationCart;
import com.billing.donor.DonorBean;
import com.billing.donor.EncryptDecrypt;
import com.billing.entity.Company;
import com.billing.entity.Currencies;
import com.billing.entity.FundDetails;
import com.billing.entity.FundGroup;
import com.billing.entity.PaymentMethod;
import com.billing.entity.PaymentType;
import com.billing.entity.UserAccountDetails;
import com.billing.entity.Users;
import com.billing.exceptions.ExceptionMsg;
import com.billing.paymethod.PayMethodBean;
import com.billing.paymethod.StripeBean;
import com.billing.paystatus.PaymentStatusBean;
import com.billing.util.DateUtil;
import com.billing.util.MainUtil;

@SuppressWarnings("unchecked")
@Name("recurringBean")
public class RecurringBean {

	@In
	public EntityManager entityManager;
	
	@In
	public LocaleSelector localeSelector;
	
	@In(create=true,required=false) 
    public Users activeUser;
	
	private boolean automaticRecurring;
	
	public List<RecurringVO> recurringList() {
		try {
			this.setAutomaticRecurring(false);
			return recurringSubList();
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return null;
	}
	
	public List<RecurringVO> recurringSubList() {
		try {
			String startDate = DateUtil.getDateToStringFormat(DateUtil.Yesterday(), DateUtil.DATE_FORMAT_US);
			String endDate = DateUtil.getDateToStringFormat(new Date(), DateUtil.DATE_FORMAT_US);
			startDate = endDate;
			HttpServletRequest request = ServletContexts.instance().getRequest();
			
			if(this.isAutomaticRecurring()) {
				Date recurringEndDate = DateUtil.getWeekDaysDate(Calendar.getInstance(), 2).getTime();
				endDate = DateUtil.getDateToStringFormat(recurringEndDate, DateUtil.DATE_FORMAT_US); 	
			}else {
				String stDate = request.getParameter("startDate");
				String edDate = request.getParameter("endDate");
				if(stDate != null && !stDate.isEmpty() && edDate != null && !edDate.isEmpty()) {
					Date sDate = DateUtil.getStringToDateFormat(stDate, DateUtil.DATE_FORMAT);
					Date eDate = DateUtil.getStringToDateFormat(edDate, DateUtil.DATE_FORMAT);
					if(sDate != null && sDate != null) {
						startDate = DateUtil.getDateToStringFormat(sDate, DateUtil.DATE_FORMAT_US);
						endDate = DateUtil.getDateToStringFormat(eDate, DateUtil.DATE_FORMAT_US);
					}
				}
			}
			
			List<FundDetails> recurringList = recurringListData(startDate, endDate);
			List<RecurringVO> recurringVOList = new ArrayList<RecurringVO>();
			LinkedHashMap  <Integer, List<FundDetails>> recurrMap = new LinkedHashMap <Integer, List<FundDetails>>();
			if(recurringList != null && recurringList.size() > 0){
				for(FundDetails fcd : recurringList){
					FundGroup fc = fcd.getFundGroup();
					Integer fcId = fc.getId();
					List<FundDetails> funddetails = new ArrayList<FundDetails>();
					if(!recurrMap.containsKey(fcId)){
						recurrMap.put(fcId, new ArrayList<FundDetails>());
					}
					if(recurrMap.containsKey(fcId)){
						 List<Integer> alKeys = new ArrayList<Integer>(recurrMap.keySet());
					     for(Integer fcKey : alKeys){
					    	 if(String.valueOf(fcKey).equals(String.valueOf(fcId))){
					    		 List<FundDetails> tempList = new ArrayList<FundDetails>();
					    		 tempList.addAll((List<FundDetails>) recurrMap.get(fcKey));
					    		 tempList.add(fcd);
					    		 if(tempList!=null && tempList.size() > 0){
					    			 funddetails.addAll(tempList);
					    		 }
					    	 }
					     }
					     recurrMap.put(fcId, funddetails);
					}
				}
				EncryptDecrypt encryptDecrypt = new EncryptDecrypt();
				Cipher decrypt = encryptDecrypt.InitiateDecryptProcess();
				/************** message proprties map *********************/
//				Map<String, String> msgprop = getMessagesProperties(localeSelector.getLocaleString());
				/************** message proprties map *********************/
				/************** user account details map ******************/
				LinkedHashMap <Integer, UserAccountDetails> uAcctMap = new LinkedHashMap <Integer, UserAccountDetails>();
				List<UserAccountDetails> uAcctList = entityManager.createNamedQuery("findAllUserAccountDetails").getResultList();
				for(UserAccountDetails uAcct: uAcctList) {
					uAcctMap.put(uAcct.getId(), uAcct);
				}
				/************** user account details map ******************/
				
	    		List<Integer> fcConsolidated = new ArrayList<Integer>(recurrMap.keySet());
		    	for(Integer fundDet : fcConsolidated){
					if(recurrMap.get(fundDet)!=null && recurrMap.get(fundDet).size()>0){
						List<FundDetails> fundDetRecurrGroup = recurrMap.get(fundDet);
						Collections.sort(fundDetRecurrGroup, new Comparator<FundDetails>() {
							public int compare(FundDetails o1, FundDetails o2) {
								return Integer.valueOf(o2.getId()).compareTo(Integer.valueOf(o1.getId()));
					
							}
						});
						
						LinkedHashMap  <String, List<FundDetails>> mapIban = new LinkedHashMap <String, List<FundDetails>>();
						
						if(fundDetRecurrGroup != null && fundDetRecurrGroup.size() > 0){
							for(FundDetails fcdR : fundDetRecurrGroup){
								Integer accId = fcdR.getUserAccId();
								double amount = fcdR.getAmount() != null ? fcdR.getAmount().doubleValue() : 0;
								if (accId != null && amount > 0) {
									UserAccountDetails acc = uAcctMap.get(accId);
									PaymentMethod pMeth = fcdR.getPaymentMethod();
									PaymentType payType = pMeth != null && pMeth.getPaymentType() != null ? pMeth.getPaymentType() : null;
									boolean otherStripeRecurr = payType.isOtherReuccring();
									String payTyeStr = payType != null && payType.getPaymentName() != null ? payType.getPaymentName().trim() : null;
									String iban = null;
									if(payTyeStr != null && payTyeStr.equalsIgnoreCase(StripeBean.STRIPE_SEPAPAY)){
										iban = acc.getIbanCode() != null ? new EncryptDecrypt().decryptData(decrypt, acc.getIbanCode().trim()) : null;
									}else if(otherStripeRecurr){
										iban = acc.getCustomerId() != null ? acc.getCustomerId().trim() : null;
										iban = iban != null && iban.startsWith(StripeBean.Stripe_Customer_Prefix) ? iban : null;
									}
									if(iban != null){
										List<FundDetails> funddetails = new ArrayList<FundDetails>();
										if(!mapIban.containsKey(iban)){
											mapIban.put(iban, new ArrayList<FundDetails>());
										}
										if(mapIban.containsKey(iban)){
											 List<String> alKeys = new ArrayList<String>(mapIban.keySet());
										     for(String ibanKey : alKeys){
										    	 if(ibanKey.equalsIgnoreCase(iban)){
										    		 List<FundDetails> tempList = new ArrayList<FundDetails>();
										    		 tempList.addAll((List<FundDetails>) mapIban.get(ibanKey));
										    		 tempList.add(fcdR);
										    		 if(tempList!=null && tempList.size() > 0){
										    			 funddetails.addAll(tempList);
										    		 }
										    	 }
										     }
										     mapIban.put(iban, funddetails);
										}
									}
								}
							}
							NumberFormat currencyFormatter = NumberFormat.getNumberInstance(localeSelector.getLocale());
							currencyFormatter.setMinimumFractionDigits(2);
							currencyFormatter.setMaximumFractionDigits(2);
							List<String> ibanConsolidated = new ArrayList<String>(mapIban.keySet());
					    	for(String iban : ibanConsolidated){
					    		if(mapIban.get(iban)!=null && mapIban.get(iban).size()>0){
									List<FundDetails> fundDetailsList = mapIban.get(iban);
									if(fundDetailsList != null && fundDetailsList.size() > 0){
										FundDetails fDetails = fundDetailsList.get(0);
										FundGroup fGroup = fDetails.getFundGroup();
										Users users = fGroup.getDonateUser();
										String firstName = users.getFirstName();
										String lastName = users.getLastName();
										firstName = firstName != null && !firstName.isEmpty() ? firstName.trim() : "";
										lastName = lastName != null && !lastName.isEmpty() ? lastName.trim() : "";
										String name = firstName + " " + lastName;
										String telephone = users.getTelephone();
										RecurringVO recurringVO = new RecurringVO();
										recurringVO.setFundGroupId(fDetails.getFundGroup() != null ? fDetails.getFundGroup().getId() : 0);
										recurringVO.setUsers(users);
										recurringVO.setName(name != null && !name.isEmpty() ? name.trim() : "");
										recurringVO.setTransactionDate(DateUtil.getDateToStringFormat(fDetails.getTransactionDate(), users.getCompany().getDatePattern()));
										recurringVO.setUserId(users.getId());
										recurringVO.setUserNr(users.getUserNr());
										recurringVO.setEmail(users.getPrimaryEmail());
										recurringVO.setTelephone(telephone != null && !telephone.isEmpty() ? telephone.trim() : "");
										recurringVO.setLanguages(users.getLanguages());
										recurringVO.setUserAccId(fDetails.getUserAccId());
										StringBuffer fdIdsBuffer = new StringBuffer();
										StringBuffer prodBuffer = new StringBuffer();
										StringBuffer groupIdBuffer = new StringBuffer();
										double totalAmount = 0.0;	
										for(FundDetails fd : fundDetailsList){
											totalAmount = totalAmount + (fd.getAmount() != null ? fd.getAmount().doubleValue() : 0.0);
											fdIdsBuffer = fdIdsBuffer.append(fd.getId() + ", ");
											groupIdBuffer = groupIdBuffer.append(fd.getGroupId()+ ", ");
											prodBuffer = prodBuffer.append(fd.getProductGroup().getProduct().getProductName() +  " (" + fd.getProductType() + "), ");
										}
										String fdIds = "", groupIds = "", prodName = "";
										if(fdIdsBuffer.toString().length() > 0){
											fdIds = fdIdsBuffer.toString().substring(0, fdIdsBuffer.toString().length() - 2);
							    		}
										
										if(groupIdBuffer.toString().length() > 0){
											groupIds = groupIdBuffer.toString().substring(0, groupIdBuffer.toString().length() - 2);
							    		}
										
										if(prodBuffer.toString().length() > 0){
											prodName = prodBuffer.toString().substring(0, prodBuffer.toString().length() - 2);
							    		}
										recurringVO.setAmount(currencyFormatter.format(totalAmount));
										recurringVO.setFundDetailsId(fdIds);
										recurringVO.setGroupId(groupIds);
										recurringVO.setProductName(prodName);
										recurringVO.setIban(iban);
										recurringVO.setIbanEncrypt(new DonorBean().ibanLastDigit(iban));
										recurringVOList.add(recurringVO);
									}
								}
					    	}
						}
			    	}
				}
			}
			return recurringVOList;
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return null;
	}
	
	public void chargeFromRecurringList() {
		try {
			HttpServletRequest request = ServletContexts.instance().getRequest();
			String fDetailsIds = request.getParameter("fDetailsIds");
			String fundGroupId = request.getParameter("fundGroupId"); 
			String userAccId = request.getParameter("userAccId");
			if(fDetailsIds != null && !fDetailsIds.trim().isEmpty()
					&& fundGroupId != null && !fundGroupId.trim().isEmpty()
					&& userAccId != null && !userAccId.trim().isEmpty()) {
				boolean manualCharge = true;
				prepareFinalRecurringList(fDetailsIds, fundGroupId, userAccId, manualCharge);
			}
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void prepareFinalRecurringList(String fDetailsIds, String fundGroupId, String userAccId, boolean manualCharge) {
		try {
			if(fDetailsIds != null && !fDetailsIds.trim().isEmpty()
					&& fundGroupId != null && !fundGroupId.trim().isEmpty()
					&& userAccId != null && !userAccId.trim().isEmpty()) {
				String fDSplit[] = fDetailsIds.split(",");
				List<FundDetails> fundDetailList = new ArrayList<FundDetails>();
				String paymentTypeName = null;
				PaymentMethod payMethod = null;
	    		for(String fDId : fDSplit){
	    			if(fDId != null && !fDId.trim().isEmpty()) {
	    				FundDetails fDetail = entityManager.find(FundDetails.class, Integer.valueOf(fDId.trim()));
	    				if(fDetail != null) {
	    					if(paymentTypeName == null) {
	    						payMethod = fDetail.getPaymentMethod();
	    						if(payMethod != null) {
	    							PaymentType pType = payMethod.getPaymentType();
	    							if(pType != null && pType.getPaymentName() != null) {
	    								paymentTypeName = pType.getPaymentName().trim();
	    							}
	    						}
	    					}
	    					fundDetailList.add(fDetail);
	    				}
	    			}
	    		}
	    		if(fundDetailList != null && fundDetailList.size() > 0) {
	    			FundGroup fundGroup = entityManager.find(FundGroup.class, Integer.valueOf(fundGroupId.trim()));
	    			if(fundGroup != null) {
	    				Users users = fundGroup.getDonateUser();
	    				Company company = users.getCompany();
	    				Currencies currencies = company.getCurrencies();
	    				if(localeSelector != null) {
	    					localeSelector.setLocaleString(users.getLanguages() != null ? users.getLanguages().getLangCode().toLowerCase() : "en");
	    				} else {
	    					LocaleSelector localLocale = new LocaleSelector();
	    					localLocale.setLocaleString(users.getLanguages() != null ? users.getLanguages().getLangCode().toLowerCase() : "en");
	    					localeSelector = localLocale;
	    				}
						DonationCart donationCart = new DonationCart();
						donationCart.resetData();
						donationCart.entityManager = entityManager;
						donationCart.activeUser = company.getUsers();
						donationCart.localeSelector = localeSelector;
						donationCart.setCompany(company);
						donationCart.setCurrencies(currencies);
						donationCart.setPaymentTypeName(paymentTypeName);
						donationCart.setPaymentMethod(payMethod);
						if(manualCharge) {
							donationCart.setManualCharge(manualCharge);
						} else {
							donationCart.setAutoCharge(!manualCharge);
						}
						UserAccountDetails userAccDetails = entityManager.find(UserAccountDetails.class, Integer.valueOf(userAccId.trim()));
						if(userAccDetails != null && paymentTypeName != null) {
							String customerId = userAccDetails.getCustomerId();
							String secretKeyUsed = userAccDetails.getSecretKeyUsed();
							String cardCountryCode = userAccDetails.getCountryCode();
			    			if(paymentTypeName.trim().equalsIgnoreCase(StripeBean.STRIPE_CreditCard)
			    					&& cardCountryCode != null && !cardCountryCode.isEmpty()) {
								StripeBean stripeBean = new StripeBean();
								boolean europeanCard = stripeBean.EUCountryCodeExists(cardCountryCode);
								donationCart.setCardCountryCode(cardCountryCode);
								donationCart.setEuropeanCard(europeanCard);
			    			}
							if(customerId != null && !customerId.trim().isEmpty() && 
									customerId.startsWith(StripeBean.Stripe_Customer_Prefix) &&
									secretKeyUsed != null && !secretKeyUsed.trim().isEmpty() && 
									secretKeyUsed.startsWith(StripeBean.Stripe_SecretKey_Prefix)){
								donationCart.setCustomerId(customerId);
								donationCart.setSecretKeyUsed(secretKeyUsed);
								donationCart.executeStripePayment(fundDetailList);
							}else if(paymentTypeName.trim().equalsIgnoreCase(StripeBean.STRIPE_SEPAPAY)) {
								String accountName = userAccDetails.getAccountName();
								String iban = userAccDetails.getIbanCode();
								EncryptDecrypt encryptDecrypt = new EncryptDecrypt();
								Cipher decrypt = encryptDecrypt.InitiateDecryptProcess();
								iban = iban != null && !iban.isEmpty() ? new EncryptDecrypt().decryptData(decrypt,iban.trim()) : null;
								if(accountName != null && !accountName.trim().isEmpty()
										&& iban != null && !iban.trim().isEmpty()) {
									donationCart.setIban(iban);
									donationCart.setAccountName(accountName);
									donationCart.setFundDetailsList(fundDetailList);
									donationCart.processStripePaymentType();
									userAccDetails = entityManager.find(UserAccountDetails.class, Integer.valueOf(userAccId.trim()));
									customerId = userAccDetails.getCustomerId();
									secretKeyUsed = userAccDetails.getSecretKeyUsed();
									if(userAccDetails != null) {
										if(customerId != null && !customerId.trim().isEmpty() && 
												customerId.startsWith(StripeBean.Stripe_Customer_Prefix) &&
												secretKeyUsed != null && !secretKeyUsed.trim().isEmpty() && 
												secretKeyUsed.startsWith(StripeBean.Stripe_SecretKey_Prefix)){
											donationCart.setCustomerId(customerId);
											donationCart.setSecretKeyUsed(secretKeyUsed);
											donationCart.executeStripePayment(fundDetailList);
										}
									}
								}
							}
						}
	    			}
	    		}
			}
			
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public List<FundDetails> recurringListData(String startDate, String endDate) {
		try {
			
			StringBuffer qry = new StringBuffer(); 
		 	qry.append("select fd from FundDetails fd where (");
			qry.append(" ( ");
			qry.append(" fd.paymentMethod.paymentType.id = " + PayMethodBean.Stripe_Sepa_Id);
			qry.append(" or fd.paymentMethod.paymentType.id = " + PayMethodBean.Stripe_CreditCard_Id);
			qry.append(" or fd.paymentMethod.paymentType.id = " + PayMethodBean.Stripe_Ideal_Id);
			qry.append(" or fd.paymentMethod.paymentType.id = " + PayMethodBean.Stripe_Bancontact_Id);
			qry.append(" or fd.paymentMethod.paymentType.id = " + PayMethodBean.Stripe_Sofort_Id);
			qry.append(" ) ");
			
			qry.append(" and (fd.transactionCode IS NULL or fd.transactionCode = '' or "
							+ " ("
							+ " fd.transactionCode not like '"+StripeBean.Stripe_Payment_Intent_Trans_Prefix+"%' and "
							+ " fd.transactionCode not like '"+StripeBean.Stripe_NonCard_Trans_Prefix+"%')"
							+ " ) ");
			
			qry.append(" and fd.paymentStatus.statusType='"+PaymentStatusBean.Pending+"' ");
			
			qry.append(" ) ");
			
			if(!new MainUtil().checkLocal()){
				qry.append(" and fd.createdDate IS NOT NULL and date(fd.createdDate) < CURDATE() ");
			}
			 
			qry.append(" and date(fd.transactionDate) >= '"+startDate+"' and date(fd.transactionDate) <= '"+endDate+"' ");		
			
			if(activeUser != null) {
				qry.append(" and fd.fundGroup.company.id = " + activeUser.getCompany().getId()+ " ");
			}
			
			qry.append(" and fd.paymentMethod.enable = true ");
			
			qry.append(" and fd.fundGroup.company.executeRecurring = true ");
			
			qry.append(" and fd.installment like '%/%' and fd.fundGroupDetails IS NOT NULL and fd.userAccId > 0 ");
	
			qry.append(" and fd.deleted ='false' order by fd.fundGroup.company.id desc");
				
			List<FundDetails> recurfdList = entityManager.createQuery(qry.toString()).getResultList();

			return recurfdList;
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return null;
	}
	
	public void automaticRecurringList() {
		try {
			this.setAutomaticRecurring(true);
			List<RecurringVO> recurringList = recurringSubList();
			String msg = "Recurring List size is : " + (recurringList != null ? recurringList.size() : 0);
			ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1],  msg);
			if(recurringList != null && recurringList.size() > 0) {
				for(RecurringVO recurringVO : recurringList) {
					try {
						RecurringClient recurringClient = new RecurringClient();
						recurringClient.chargeRecurringPaymentJMS(recurringVO);
					} catch (Exception e) {
						System.out.println("Error in AutochargeForrecurringListJMS ::: "+e.getMessage());
					}
				}
			}
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}

	public boolean isAutomaticRecurring() {
		return automaticRecurring;
	}

	public void setAutomaticRecurring(boolean automaticRecurring) {
		this.automaticRecurring = automaticRecurring;
	}
}
