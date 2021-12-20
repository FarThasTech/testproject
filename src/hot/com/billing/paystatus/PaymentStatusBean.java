package com.billing.paystatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.international.LocaleSelector;

import com.billing.commonFile.Translator;
import com.billing.entity.Company;
import com.billing.entity.Languages;
import com.billing.entity.PaymentStatus;
import com.billing.entity.PaymentStatusLanguage;
import com.billing.entity.Users;
import com.billing.exceptions.ExceptionMsg;
import com.billing.language.LanguageBean;

@Name("paymentStatusBean")
@SuppressWarnings("unchecked")
public class PaymentStatusBean {
	

	@In
	public EntityManager entityManager;
	    
	@In
	public LocaleSelector localeSelector;
	
	@In(create=true,required=false) 
    public Users activeUser;
	
	public static String Success = "Success";
	
	public static String Pending = "Pending";
	
	public static String Failure = "Failure";
	
	public static String Cancel = "Cancel";
	
	public static String Refund = "Refund";
	
	public static String RefundCancel = "RefundCancel";
	
	public static String Dispute = "Dispute";
	
	public static String DisputeResolved = "DisputeResolved";
	
	public void createPaymentStatus(String status, Company company) {
		try {
			if(status != null && !status.isEmpty() && company != null) {
				List<PaymentStatus> paystatusList = new ArrayList<PaymentStatus>();
				try{
					paystatusList = entityManager.createNamedQuery("findPaymentStatusByCompanyAndStatusType")
							.setParameter("companyId", company.getId())
							.setParameter("statusType", status.trim()).getResultList();
					if(paystatusList == null)
						paystatusList = new ArrayList<PaymentStatus>();
				}catch (Exception e) {
					paystatusList = new ArrayList<PaymentStatus>();
				}
				if(paystatusList != null && paystatusList.size() == 0){
					PaymentStatus payStatus = new PaymentStatus();
					payStatus.setCompany(company);
					payStatus.setEnable(true);
					payStatus.setRowfreeze(true);
					payStatus.setStatusType(status);
					payStatus.setSortCode(1);
					payStatus.setCreatedDate(new Date());
					payStatus.setModifiedDate(new Date());
					entityManager.persist(payStatus);
					entityManager.flush();
					createPayStatusLang(payStatus);
				}
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void createPayStatusLang(PaymentStatus paymentStatus) {
		try {
			String sourceLang = localeSelector.getLocaleString();
			LanguageBean langBean = new LanguageBean();
			langBean.entityManager = entityManager;
			String value = paymentStatus.getStatusType();
			for(Languages lang : langBean.langList()) {
				PaymentStatusLanguage payStatLang = new PaymentStatusLanguage();
				String langCode = lang.getLangCode().toLowerCase();
				Map<String, String> msgprop = new LanguageBean().getMessagesPropertiesFromLangCode(langCode);
				String status = msgprop.get("Pay_Status_"+value);
				if(status != null && !status.trim().isEmpty() && !status.contains("Pay_Status_")){
					payStatLang.setStatus(status);
				}else{
					payStatLang.setStatus(Translator.translate(sourceLang, langCode , value	));
				}
				payStatLang.setPaymentStatus(paymentStatus);
				payStatLang.setLanguages(lang);
				payStatLang.setCreatedDate(new Date());
				payStatLang.setModifiedDate(new Date());
				entityManager.persist(payStatLang);
				entityManager.flush();
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}

	public String getPayStatusLangName(PaymentStatus payStatus, String localeString) {
		String payMethodName = "";
		try{
			if (payStatus != null && localeString != null && !localeString.trim().isEmpty()) {
				Set<PaymentStatusLanguage> payStatusList = payStatus.getPaymentStatusLanguages();
				if(payStatusList != null && payStatusList.size() > 0) {
					for (PaymentStatusLanguage psLang : payStatusList) {
						Languages lang = psLang.getLanguages();
						if (psLang.getStatus() != null && lang != null && lang.getLangCode() != null && lang.getLangCode().trim().equalsIgnoreCase(localeString.trim())) {
							payMethodName = psLang.getStatus().trim();
							break;
						}
					}
				}
			}
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return payMethodName;
	}
}
