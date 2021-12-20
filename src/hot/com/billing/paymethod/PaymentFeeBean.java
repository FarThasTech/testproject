package com.billing.paymethod;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.LocaleSelector;

import com.billing.entity.Company;
import com.billing.entity.PaymentFee;
import com.billing.entity.PaymentType;
import com.billing.entity.PaymentTypeSub;
import com.billing.entity.Users;
import com.billing.exceptions.ExceptionMsg;
import com.billing.staticvalue.StaticValues;

@Scope(ScopeType.SESSION)
@Name("paymentFeeBean")
@SuppressWarnings({"unchecked"})
public class PaymentFeeBean {
	
	@In
	public EntityManager entityManager;
	
	@In
    public LocaleSelector localeSelector;
	
	@In(create=true,required=false) 
	public Users activeUser;
	
	public static String feePattern = "[0-9]+\\.[0-9]+\\s\\%\\s+\\++\\s[0-9]+\\.[0-9]{1,2}$";
	
	public PaymentFeeVO getAppAndPayFeeFromPattern(PaymentType payType, PaymentTypeSub payTypeSub, PaymentFee paymentFee, 
			Company company, double amount, boolean feeTakenOver, boolean europeanCard,
			String accountCurrencyCode, String localCurrencyCode) {
		try {
			PaymentFeeVO payFeeVO = null;
			double appFee = 0;
			double payFee = 0;
			String feePatternInfo = "";
			if(payType != null && amount > 0 && company != null) {
				
				if(paymentFee != null) {
					String feepattern = feeTakenOver ? paymentFee.getFeeTakenOver() : paymentFee.getFeeNotTakenOver();
					feePatternInfo = feePatternInfo + StaticValues.TotalAmount + " : "  + amount  + " , " + StaticValues.AppFeePattern + " : " + feepattern + " , " ;
					List<Double> splitFeeFromPattern = splitFeeFromPattern(feepattern);
					if(splitFeeFromPattern != null && splitFeeFromPattern.size() == 2) {
						double percentage = splitFeeFromPattern.get(0);
						double cents = splitFeeFromPattern.get(1);
						appFee = calculateFee(amount, percentage, cents);
					}
					feePatternInfo = feePatternInfo + StaticValues.AppFee + " : " + appFee + " , ";
				}
				
				if(payTypeSub != null) {
					String feePattern = payTypeSub.getPaymentFee();
					if(payType.getPaymentName().equalsIgnoreCase(StripeBean.STRIPE_CreditCard)
							|| payType.getPaymentName().equalsIgnoreCase(StripeBean.STRIPE_GOOGLEPAY)
							|| payType.getPaymentName().equalsIgnoreCase(StripeBean.STRIPE_APPLEPAY)) {
						feePattern = europeanCard ? feePattern : StripeBean.NonEUCardFee;
					}
					/* Enable the below code when using currency convertion */
					/* feePattern = getPaymentFeePattern(payTypeSub, payType, accountCurrencyCode, localCurrencyCode);
					if(feePattern == null || (feePattern != null && feePattern.isEmpty())) {
						feePattern = payTypeSub.getPaymentFee();
					}*/
					List<Double> splitFeeFromPattern = splitFeeFromPattern(feePattern);
					feePatternInfo = feePatternInfo + StaticValues.PayFeePattern + " : " + feePattern + " , ";
					if(splitFeeFromPattern != null && splitFeeFromPattern.size() == 2) {
						double percentage = splitFeeFromPattern.get(0);
						double cents = splitFeeFromPattern.get(1);
						payFee = calculateFee(amount, percentage, cents);
					}
					feePatternInfo = feePatternInfo + StaticValues.PayFee + " : " + payFee + " , ";
				}
			}
			payFeeVO = new PaymentFeeVO();
			payFeeVO.setAppFee(appFee);
			payFeeVO.setPayFee(payFee);
			payFeeVO.setFeePatternInfo(feePatternInfo);
			return payFeeVO;
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return null;
	}
	
	public static double calculateFee(double amount, double percentage, double cents){
    	double fee = 0.00;
		try{
			if(amount > 0){
				if(percentage > 0) {
					fee = (amount * percentage / 100);
				}
				if(cents > 0) {
					fee = fee + cents;
				}
			}
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
    	return fee;
	}
	
	public String getPaymentFeePattern(PaymentTypeSub payTypeSub, PaymentType payType,
			String accountCurrencyCode, String localCurrencyCode) {
		try {
    		if(payTypeSub != null && payType != null 
    				&& payTypeSub.getPaymentType() != null 
    				&& payTypeSub.getPaymentType().getId() == payType.getId()) {
    			if(accountCurrencyCode != null && !accountCurrencyCode.isEmpty() 
    					&& localCurrencyCode != null && !localCurrencyCode.isEmpty() 
    					&& !(localCurrencyCode.equalsIgnoreCase(accountCurrencyCode))) {
    				return appendConversionFeeToPaymentFee(payTypeSub.getPaymentFee(), payTypeSub.getStripeConvertionFee());
    			}else {
    				return payTypeSub.getPaymentFee();
    			}
			}
    	}catch(Exception e) {
    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
    	}
    	return "";
    }
	
	public PaymentTypeSub getPaymentFeeFromSub(List<PaymentTypeSub> paymentTypeSubList, PaymentType payType) {
		try {
			if(payType != null && paymentTypeSubList != null && paymentTypeSubList.size() > 0) {
		    	for(PaymentTypeSub payTypeSub : paymentTypeSubList) {
		    		if(payTypeSub != null && payTypeSub.getPaymentType() != null && payTypeSub.getPaymentType().getId() == payType.getId()) {
		    			return payTypeSub;
		    		}
		    	}
			}
    	}catch(Exception e) {
    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
    	}
    	return null;
    }
	
	public String appendConversionFeeToPaymentFee(String paymentFee, String conversionFee) {
		List<Double> splitFeeFromPattern = splitFeeFromPattern(paymentFee);
		if(splitFeeFromPattern != null && splitFeeFromPattern.size() == 2) {
			double percentage = splitFeeFromPattern.get(0);
			double cents = splitFeeFromPattern.get(1);
			if(conversionFee != null && !conversionFee.isEmpty()) {
				splitFeeFromPattern = splitFeeFromPattern(conversionFee);
				if(splitFeeFromPattern != null && splitFeeFromPattern.size() == 2) {
					percentage = percentage + splitFeeFromPattern.get(0);
					cents = cents + splitFeeFromPattern.get(1);
					paymentFee = percentage+" % + " + cents;
				}
			}
		}
		return paymentFee;
	}
	
	public static List<Double> splitFeeFromPattern(String feeWithPattern) {
		List<Double> list = null;
		try {
			if(feeWithPattern != null && !feeWithPattern.isEmpty()) {
				Matcher m = feePatternMatch(feeWithPattern);
				if(m.find( )) {
					list = new ArrayList<Double>();
					String feeStr[] = feeWithPattern.split("\\+");
					String percentage = feeStr[0].split("%")[0].trim();
					String cents = feeStr[1].trim();
					list.add(Double.valueOf(percentage));
					list.add(Double.valueOf(cents));
					return list;
				}
			}
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return null;
	}
	
	public List<PaymentTypeSub> getPaymentTypeSubListByCountry(String country) {
		try {
			List<PaymentTypeSub> payTypeList = entityManager.createNamedQuery("findAllPaymentTypeSubByCountry")
					.setParameter("country", country)
					.getResultList();
			if(payTypeList != null && payTypeList.size() > 0) {
				return payTypeList;
			}else {
				payTypeList =  entityManager.createNamedQuery("findAllPaymentTypeSubByCountry")
												.setParameter("country", StaticValues.DefaultStripeCountry)
												.getResultList();
				if(payTypeList != null && payTypeList.size() > 0) {
					return payTypeList;
				}
			}
		} catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return null;
	}
	
	public PaymentFee getPaymentFee(Company company, PaymentType payType) {
		try {
			return (PaymentFee) entityManager.createNamedQuery("findPaymentFeeByCompanyAndPaymentType")
					.setParameter("companyId", company.getId())
					.setParameter("payTypeId", payType.getId())
					.getSingleResult();
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return null;
	}
	
	public List<PaymentFee> paymentFeeList(int companyId){
		try {
			List<PaymentFee> paymentFeeList = entityManager.createNamedQuery("findPaymentFeeByCompany")
															.setParameter("companyId", companyId)
															.getResultList();
			return paymentFeeList;
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return null;
	}
	
	public static Matcher feePatternMatch(String value) {
		Pattern r = Pattern.compile(feePattern);
	    Matcher m = r.matcher(value);
	    return m;
	}

}
