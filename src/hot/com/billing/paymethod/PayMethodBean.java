package com.billing.paymethod;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.web.ServletContexts;

import com.billing.commonFile.Translator;
import com.billing.donor.DonationCart;
import com.billing.entity.Company;
import com.billing.entity.Languages;
import com.billing.entity.PaymentKeys;
import com.billing.entity.PaymentMethod;
import com.billing.entity.PaymentMethodLanguage;
import com.billing.entity.PaymentType;
import com.billing.entity.Users;
import com.billing.exceptions.ExceptionMsg;
import com.billing.language.LanguageBean;
import com.billing.util.NumberUtil;

@Name("payMethodBean")
@SuppressWarnings("unchecked")
public class PayMethodBean {

	@In
	public EntityManager entityManager;
	    
	@In
	public LocaleSelector localeSelector;
	
	@In(create=true,required=false) 
    public Users activeUser;
	
	@In(create=true)
	@Out(scope=ScopeType.SESSION)
	public PayMethodVO payMethodVO;
	
	public static String Cash = "Cash";
	
	public static String Stripe = "Stripe";
	
	public static int Stripe_Sepa_Id = 2;
	
	public static int Stripe_CreditCard_Id = 3;
	
	public static int Stripe_Sofort_Id = 4;
	
	public static int Stripe_Ideal_Id = 5;
	
	public static int Stripe_Bancontact_Id = 6;
	
	public static String defaultFee = "1.0 % + 0.0";
	
	public List<PaymentType> getAllPaymentType(){
		try {
			return entityManager.createNamedQuery("findPaymentType").getResultList();
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			return new ArrayList<PaymentType>();
		}
	}
	
	public void createPaymentMethods(PaymentType paymentType, Company company, PaymentKeys paymentKeys) {
		try {
			if(paymentType != null && company != null) {
				List<PaymentMethod> payMethodList = new ArrayList<PaymentMethod>();
				int compnayId = company.getId();
				try{
					payMethodList = entityManager.createNamedQuery("findPaymentMethodByCompanyAndpaymentType")
							.setParameter("companyId", compnayId)
							.setParameter("payTypeId", paymentType.getId()).getResultList();
					if(payMethodList == null)
						payMethodList = new ArrayList<PaymentMethod>();
				}catch (Exception e) {
					payMethodList = new ArrayList<PaymentMethod>();
				}
				int sortCode = 0;
				try {
					String query = "select count(*) from payment_method pm where pm.id_company = "+ compnayId;
					BigInteger count = (BigInteger) entityManager.createNativeQuery(query).getSingleResult();
					if (count != null)
						sortCode = count.intValue();
				} catch(Exception e) {
					ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
				}
				
				if(payMethodList != null && payMethodList.size() == 0){
					PaymentMethod payMethod = new PaymentMethod();
					payMethod.setCompany(company);
					String paymentName = paymentType.getPaymentName();
					payMethod.setPaymentName(paymentName);
					payMethod.setPaymentType(paymentType);
					payMethod.setEnable(true);
					if(paymentName.equalsIgnoreCase(Cash) 
							|| paymentName.equalsIgnoreCase(StripeBean.STRIPE_SEPAPAY)) {
						payMethod.setOffline(true);
					} else {
						payMethod.setOffline(false);
					}
					
					if(!paymentName.equalsIgnoreCase(Cash)) {
						payMethod.setOnline(true);
					} else {
						payMethod.setOnline(false);
					}
					if(paymentName.equalsIgnoreCase(StripeBean.STRIPE_GOOGLEPAY) || 
							paymentName.equalsIgnoreCase(StripeBean.STRIPE_APPLEPAY)) {
						payMethod.setEnable(false);
					}
					payMethod.setPaymentKeys(paymentKeys);
					payMethod.setSortCode(++sortCode);
					payMethod.setCreatedDate(new Date());
					payMethod.setModifiedDate(new Date());
					payMethod = createPayMethodLang(payMethod);
					entityManager.persist(payMethod);
					entityManager.flush();
				}
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public PaymentMethod createPayMethodLang(PaymentMethod paymentMethod) {
		try {
			String sourceLang = localeSelector.getLocaleString();
			LanguageBean langBean = new LanguageBean();
			langBean.entityManager = entityManager;
			String value = paymentMethod.getPaymentName();
			for(Languages lang : langBean.langList()) {
				String langCode = lang.getLangCode().toLowerCase();
				PaymentMethodLanguage payMethodLang = new PaymentMethodLanguage();
				Map<String, String> msgprop = new LanguageBean().getMessagesPropertiesFromLangCode(langCode);
				String methodName = msgprop.get("Payment_"+value);
				if(methodName != null && !methodName.trim().isEmpty() && !methodName.contains("Payment_")){
					payMethodLang.setMethodName(methodName);
				}else{
					payMethodLang.setMethodName(Translator.translate(sourceLang, langCode, value));
				}
				payMethodLang.setPaymentMethod(paymentMethod);
				payMethodLang.setLanguages(lang);
				payMethodLang.setCreatedDate(new Date());
				payMethodLang.setModifiedDate(new Date());	
				entityManager.persist(payMethodLang);
				entityManager.flush();
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return paymentMethod;
	}
	
	public static List<String> stripePaymentTypes(){
		try {
			List<String> stripePayType = new ArrayList<String>();
			stripePayType.add(StripeBean.STRIPE_SEPAPAY);
			stripePayType.add(StripeBean.STRIPE_CreditCard);
			stripePayType.add(StripeBean.STRIPE_EPS);
			stripePayType.add(StripeBean.STRIPE_IDEAL);
			stripePayType.add(StripeBean.STRIPE_SOFORT);
			stripePayType.add(StripeBean.STRIPE_BANCONTACT);
			stripePayType.add(StripeBean.STRIPE_P24);
			stripePayType.add(StripeBean.STRIPE_GIROPAY);
			stripePayType.add(StripeBean.STRIPE_GOOGLEPAY);
			stripePayType.add(StripeBean.STRIPE_APPLEPAY);
			return stripePayType;
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			return new ArrayList<String>();
		}
	}
	
	public static boolean isStripeType(String paymentType){
		try{
			if (paymentType != null && !paymentType.trim().isEmpty()) {
				for (String type : stripePaymentTypes()) {
					if (type != null && type.trim().equalsIgnoreCase(paymentType.trim()))
						return true;
				}
			}
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return false;
    }
	
	public String getPayMethodLangName(PaymentMethod payMethod, String localeString) {
		String payMethodName = "";
		try{
			if (payMethod != null && localeString != null && !localeString.trim().isEmpty()) {
				Set<PaymentMethodLanguage> payMethodList = payMethod.getPaymentMethodLanguages();
				if(payMethodList != null && payMethodList.size() > 0) {
					for (PaymentMethodLanguage pmLang : payMethodList) {
						Languages lang = pmLang.getLanguages();
						if (pmLang.getMethodName() != null && lang != null && lang.getLangCode() != null && lang.getLangCode().trim().equalsIgnoreCase(localeString.trim())) {
							payMethodName = pmLang.getMethodName().trim();
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return payMethodName;
	}
	
	public void resetData() {
		try {
			payMethodVO.setPayMethodHashMap(null);
		} catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public Map<List<PayMethodVO>, List<PayMethodVO>> getPaymentInformation() {
		Map<List<PayMethodVO>, List<PayMethodVO>> payMethodHashMap = new HashMap<List<PayMethodVO>, List<PayMethodVO>>();
		try{
			ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1], ("Payment Gateway List opened for this Company Id : " + activeUser.getCompany().getId()));
			resetData();
			int companyId = activeUser.getCompany().getId();
			List<PaymentType> payTypeList = entityManager.createNamedQuery("findPaymentTypeByGroupOfPaymentType").getResultList();
			if(payTypeList != null && payTypeList.size() > 0) {
				for(PaymentType payType : payTypeList) {
					List<PayMethodVO> payMethodVOList = new ArrayList<PayMethodVO>();
					List<PayMethodVO> payMethodVOMainList = new ArrayList<PayMethodVO>();
					PayMethodVO payMethodVOMain = new PayMethodVO();
					String paymentType = payType.getPaymentType();
					payMethodVOMain.setPayMethodName(paymentType);
					List<PaymentMethod> payMethodList = entityManager.createNamedQuery("findPaymentMethodByCompanyAndpaymentTypeValue")
																.setParameter("companyId", companyId)
																.setParameter("paymentType", paymentType)
																.getResultList();
					if(payMethodList != null && payMethodList.size() > 0) {
						boolean showKeys = true;
						for(PaymentMethod pMethod: payMethodList) {
							PayMethodVO payMethodVO = new PayMethodVO();
							PaymentKeys paymentKeys = pMethod.getPaymentKeys();
							if(paymentKeys != null && paymentType != null && paymentType.equalsIgnoreCase(Stripe) && showKeys) {
								payMethodVOMain.setAccountConnected(paymentKeys.isAccountConnected());
								payMethodVO.setShowKeys(showKeys);
								payMethodVO.setAccountConnected(paymentKeys.isAccountConnected());
								payMethodVO.setAccountId(new DonationCart().identifyKey(paymentKeys.getAccountId(), false));
								payMethodVO.setPaymentKey1(new DonationCart().identifyKey(paymentKeys.getPayParam1(), false));
								payMethodVO.setPaymentKey2(new DonationCart().identifyKey(paymentKeys.getPayParam2(), false));
								showKeys = false;
							} else if(paymentType.equalsIgnoreCase(Stripe) && showKeys){
								payMethodVOMain.setAccountConnected(false);
							}
							if(paymentType != null && paymentType.equalsIgnoreCase(Cash)) {
								payMethodVOMain.setAccountConnected(true);
							}
							payMethodVO.setEnable(pMethod.isEnable());
							payMethodVO.setOnline(pMethod.isOnline());
							payMethodVO.setOffline(pMethod.isOffline());
							payMethodVO.setPayMethodName(paymentType);
							payMethodVO.setPayMethodId(pMethod.getId());
							PaymentType pType = pMethod.getPaymentType();
							String pTypeName = pType != null ? pType.getPaymentName() : "";
							payMethodVO.setPaymentTypeName(pTypeName);
							if(pTypeName != null && (pTypeName.equalsIgnoreCase(StripeBean.STRIPE_GOOGLEPAY)
									|| pTypeName.equalsIgnoreCase(StripeBean.STRIPE_APPLEPAY))) {
								payMethodVO.setDisablePayment(true);
							} else {
								payMethodVOList.add(payMethodVO);
							}
						}
					}
					payMethodVOMainList.add(payMethodVOMain);
					payMethodHashMap.put(payMethodVOMainList, payMethodVOList);
				}
				if(payMethodHashMap != null && payMethodHashMap.size() > 0) {
					List<Map.Entry<List<PayMethodVO>, List<PayMethodVO>>> list =
			                new LinkedList<Map.Entry<List<PayMethodVO>, List<PayMethodVO>>>(payMethodHashMap.entrySet());
			        Collections.sort(list, new Comparator<Map.Entry<List<PayMethodVO>, List<PayMethodVO>>>() {
			            public int compare(Map.Entry<List<PayMethodVO>, List<PayMethodVO>> o1,
			                               Map.Entry<List<PayMethodVO>, List<PayMethodVO>> o2) {
			                return (o1.getValue().get(0).getPayMethodName()).compareTo(o2.getValue().get(0).getPayMethodName());
			            }
			        });
			        payMethodHashMap = new HashMap<List<PayMethodVO>, List<PayMethodVO>>();
			        for (Map.Entry<List<PayMethodVO>, List<PayMethodVO>> entry : list) {
			        	payMethodHashMap.put(entry.getKey(), entry.getValue());
			        }
				}
			}
		} catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return payMethodHashMap;
	}
	
	public void updatePaymentMethodDetails() {
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			String param = request.getParameter("param");
			String payMethodId = request.getParameter("payMethodId");
			String status = request.getParameter("status");
			if(param != null && !param.isEmpty() 
					&& payMethodId != null && !payMethodId.isEmpty() 
					&& NumberUtil.checkNumeric(payMethodId.trim()) && Integer.valueOf(payMethodId.trim()) > 0
					&& status != null && !status.isEmpty()) {
				PaymentMethod paymentMethod = entityManager.find(PaymentMethod.class, Integer.valueOf(payMethodId.trim()));
				if(paymentMethod != null) {
					if(param.equalsIgnoreCase("Online")) {
						if(status.trim().equalsIgnoreCase("true"))
							paymentMethod.setOnline(true);
						else
							paymentMethod.setOnline(false);
					}else if(param.equalsIgnoreCase("Offline")) {
						if(status.trim().equalsIgnoreCase("true"))
							paymentMethod.setOffline(true);
						else
							paymentMethod.setOffline(false);
					}else if(param.equalsIgnoreCase("Enable")) {
						if(status.trim().equalsIgnoreCase("true"))
							paymentMethod.setEnable(true);
						else
							paymentMethod.setEnable(false);
					}
					entityManager.merge(paymentMethod);
					entityManager.flush();
				}
			}
		} catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
}
