package com.billing.recurring;

import com.billing.entity.FundDetails;
import com.billing.entity.Languages;
import com.billing.entity.Users;

public class RecurringVO {
	
	private Users users;
	
	private FundDetails fundDetails;
	
	private String amount;
	
	private String productName;
	
	private String name;
	
	private String iban;
	
	private String ibanEncrypt;
	
	private String transactionDate;
	
	private String groupId;
	
	private String fundDetailsId;
	
	private int userId;
	
	private String userNr;
	
	private String email;
	
	private String telephone;
	
	private String address;
	
	private Languages languages;
	
	private int fundGroupId;
	
	private int userAccId;

	public Users getUsers() {
		return users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	public FundDetails getFundDetails() {
		return fundDetails;
	}

	public void setFundDetails(FundDetails fundDetails) {
		this.fundDetails = fundDetails;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getIbanEncrypt() {
		return ibanEncrypt;
	}

	public void setIbanEncrypt(String ibanEncrypt) {
		this.ibanEncrypt = ibanEncrypt;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getFundDetailsId() {
		return fundDetailsId;
	}

	public void setFundDetailsId(String fundDetailsId) {
		this.fundDetailsId = fundDetailsId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserNr() {
		return userNr;
	}

	public void setUserNr(String userNr) {
		this.userNr = userNr;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Languages getLanguages() {
		return languages;
	}

	public void setLanguages(Languages languages) {
		this.languages = languages;
	}

	public int getFundGroupId() {
		return fundGroupId;
	}

	public void setFundGroupId(int fundGroupId) {
		this.fundGroupId = fundGroupId;
	}

	public int getUserAccId() {
		return userAccId;
	}

	public void setUserAccId(int userAccId) {
		this.userAccId = userAccId;
	}
	

}
