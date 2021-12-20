package com.billing.paymethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.Name;

@Name("payMethodVO")
public class PayMethodVO {
	
	private String payMethodName;
	
	private int payMethodId;

	private String accountId;
	
	private String paymentKey1;
	
	private String paymentKey2;
	
	private String paymentKey3;
	
	private String paymentKey4;
	
	private boolean accountConnected;
	
	private boolean online;
	
	private boolean offline;
	
	private boolean enable;
	
	private boolean disablePayment;
	
	private boolean showKeys;
	
	private String paymentTypeName;
	
	private List<PayMethodVO> payMethodList;
	
	private Map<String, List<PayMethodVO>> payMethodHashMap = new HashMap<String, List<PayMethodVO>>();

	public String getPayMethodName() {
		return payMethodName;
	}

	public void setPayMethodName(String payMethodName) {
		this.payMethodName = payMethodName;
	}

	public int getPayMethodId() {
		return payMethodId;
	}

	public void setPayMethodId(int payMethodId) {
		this.payMethodId = payMethodId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getPaymentKey1() {
		return paymentKey1;
	}

	public void setPaymentKey1(String paymentKey1) {
		this.paymentKey1 = paymentKey1;
	}

	public String getPaymentKey2() {
		return paymentKey2;
	}

	public void setPaymentKey2(String paymentKey2) {
		this.paymentKey2 = paymentKey2;
	}

	public String getPaymentKey3() {
		return paymentKey3;
	}

	public void setPaymentKey3(String paymentKey3) {
		this.paymentKey3 = paymentKey3;
	}

	public String getPaymentKey4() {
		return paymentKey4;
	}

	public void setPaymentKey4(String paymentKey4) {
		this.paymentKey4 = paymentKey4;
	}

	public boolean isAccountConnected() {
		return accountConnected;
	}

	public void setAccountConnected(boolean accountConnected) {
		this.accountConnected = accountConnected;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public boolean isOffline() {
		return offline;
	}

	public void setOffline(boolean offline) {
		this.offline = offline;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public boolean isDisablePayment() {
		return disablePayment;
	}

	public void setDisablePayment(boolean disablePayment) {
		this.disablePayment = disablePayment;
	}

	public boolean isShowKeys() {
		return showKeys;
	}

	public void setShowKeys(boolean showKeys) {
		this.showKeys = showKeys;
	}

	public String getPaymentTypeName() {
		return paymentTypeName;
	}

	public void setPaymentTypeName(String paymentTypeName) {
		this.paymentTypeName = paymentTypeName;
	}

	public List<PayMethodVO> getPayMethodList() {
		return payMethodList;
	}

	public void setPayMethodList(List<PayMethodVO> payMethodList) {
		this.payMethodList = payMethodList;
	}

	public Map<String, List<PayMethodVO>> getPayMethodHashMap() {
		return payMethodHashMap;
	}

	public void setPayMethodHashMap(Map<String, List<PayMethodVO>> payMethodHashMap) {
		this.payMethodHashMap = payMethodHashMap;
	}
	
}
