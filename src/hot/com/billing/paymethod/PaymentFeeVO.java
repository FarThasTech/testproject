package com.billing.paymethod;

import org.jboss.seam.annotations.Name;

@Name("paymentFeeVO")
public class PaymentFeeVO {

	public double appFee;
	
	public double payFee;
	
	private String feePatternInfo;

	public double getAppFee() {
		return appFee;
	}

	public void setAppFee(double appFee) {
		this.appFee = appFee;
	}

	public double getPayFee() {
		return payFee;
	}

	public void setPayFee(double payFee) {
		this.payFee = payFee;
	}

	public String getFeePatternInfo() {
		return feePatternInfo;
	}

	public void setFeePatternInfo(String feePatternInfo) {
		this.feePatternInfo = feePatternInfo;
	}
	
}
