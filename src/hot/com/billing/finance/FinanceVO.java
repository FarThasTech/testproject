package com.billing.finance;

import java.util.Date;

import org.jboss.seam.annotations.Name;

@Name("financeVO")
public class FinanceVO {
	
	private int fundGroupId;
	
	private int userId;
	
	private String fullName;
	
	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private String telephone;
	
	private String fullAddress;
	
	private String address;
	
	private String houseNo;
	
	private String city;
	
	private String state;
	
	private String country;
	
	private String zip;
	
	private String lang;
	
	private String langid;
	
	private String userNr;
	
	private String amount;
	
	private String lastDate;
	
	private String createdUser;
	
	private String uAddrId;
	
	private String lastColumn;
	
	private String wholeFee;
	
	private String appFee;
	
	private String transFee;
	
	private String orgAmt;
	
	private String transCode;
	
	private boolean transTakenOver;
	
	private String campaignName;
	
	private String paymentMethodName;
	
	private String paymentStatusName;
	
	private Date transactionDate;
	
	private String transDate;
	
	public int getFundGroupId() {
		return fundGroupId;
	}

	public void setFundGroupId(int fundGroupId) {
		this.fundGroupId = fundGroupId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
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

	public String getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
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

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getLangid() {
		return langid;
	}

	public void setLangid(String langid) {
		this.langid = langid;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getLastDate() {
		return lastDate;
	}

	public void setLastDate(String lastDate) {
		this.lastDate = lastDate;
	}

	public String getUserNr() {
		return userNr;
	}

	public void setUserNr(String userNr) {
		this.userNr = userNr;
	}

	public String getCreatedUser() {
		return createdUser;
	}

	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}

	public String getuAddrId() {
		return uAddrId;
	}

	public void setuAddrId(String uAddrId) {
		this.uAddrId = uAddrId;
	}

	public String getLastColumn() {
		return lastColumn;
	}

	public void setLastColumn(String lastColumn) {
		this.lastColumn = lastColumn;
	}

	public String getWholeFee() {
		return wholeFee;
	}

	public void setWholeFee(String wholeFee) {
		this.wholeFee = wholeFee;
	}

	public String getAppFee() {
		return appFee;
	}

	public void setAppFee(String appFee) {
		this.appFee = appFee;
	}

	public String getTransFee() {
		return transFee;
	}

	public void setTransFee(String transFee) {
		this.transFee = transFee;
	}

	public String getOrgAmt() {
		return orgAmt;
	}

	public void setOrgAmt(String orgAmt) {
		this.orgAmt = orgAmt;
	}

	public String getTransCode() {
		return transCode;
	}

	public void setTransCode(String transCode) {
		this.transCode = transCode;
	}

	public boolean isTransTakenOver() {
		return transTakenOver;
	}

	public void setTransTakenOver(boolean transTakenOver) {
		this.transTakenOver = transTakenOver;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public String getPaymentMethodName() {
		return paymentMethodName;
	}

	public void setPaymentMethodName(String paymentMethodName) {
		this.paymentMethodName = paymentMethodName;
	}

	public String getPaymentStatusName() {
		return paymentStatusName;
	}

	public void setPaymentStatusName(String paymentStatusName) {
		this.paymentStatusName = paymentStatusName;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getTransDate() {
		return transDate;
	}

	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}

}
