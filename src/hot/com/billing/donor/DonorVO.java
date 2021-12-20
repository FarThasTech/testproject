package com.billing.donor;

import java.util.List;

import org.jboss.seam.annotations.Name;

import com.billing.entity.FieldsValue;
import com.billing.entity.Languages;

@Name("donorVO")
public class DonorVO {
	
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
	
	private String dateofbirth;
	
	private String lang;
	
	private String langid;
	
	private String userNr;
	
	private String amount;
	
	private String lastDate;
	
	private String createdUser;
	
	private String uAddrId;
	
	private String lastColumn;
	
	private int genderId;
	
	private int id;
	
	private String contactDetails;
	
	private String contactType;
	
	private int userAccId;
	
	private String accName;
	
	private String iban;
	
	private String bic;
	
	private String bankName;
	
	private String wholeFee;
	
	private int userAddrId;
	
	private int fundDetailId;
	
	private int groupId;
	
	private String campaignName;
	
	private String paymentMethodName;
	
	private String paymentStatusName;
	
	private String transactionCode;
	
	private String transactionDate;
	
	private int quantity;
	
	private String totalAmount; 
	
	private List<DonorVO> userContactList;
	
	private List<DonorVO> userContactPhnList;
	
	private List<DonorVO> userAddressList;
	
	private List<DonorVO> userAccountList;
	
	private List<DonorVO> fundDetailsList;
	
	private List<Languages> langList;

	private List<DonorVO> donorList;
	
	private List<FieldsValue> genderList;
	
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

	public String getDateofbirth() {
		return dateofbirth;
	}

	public void setDateofbirth(String dateofbirth) {
		this.dateofbirth = dateofbirth;
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

	public int getGenderId() {
		return genderId;
	}

	public void setGenderId(int genderId) {
		this.genderId = genderId;
	}

	public List<Languages> getLangList() {
		return langList;
	}

	public void setLangList(List<Languages> langList) {
		this.langList = langList;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContactDetails() {
		return contactDetails;
	}

	public void setContactDetails(String contactDetails) {
		this.contactDetails = contactDetails;
	}

	public String getContactType() {
		return contactType;
	}

	public void setContactType(String contactType) {
		this.contactType = contactType;
	}

	public int getUserAccId() {
		return userAccId;
	}

	public void setUserAccId(int userAccId) {
		this.userAccId = userAccId;
	}

	public String getAccName() {
		return accName;
	}

	public void setAccName(String accName) {
		this.accName = accName;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getBic() {
		return bic;
	}

	public void setBic(String bic) {
		this.bic = bic;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getWholeFee() {
		return wholeFee;
	}

	public void setWholeFee(String wholeFee) {
		this.wholeFee = wholeFee;
	}

	public int getUserAddrId() {
		return userAddrId;
	}

	public void setUserAddrId(int userAddrId) {
		this.userAddrId = userAddrId;
	}

	public int getFundDetailId() {
		return fundDetailId;
	}

	public void setFundDetailId(int fundDetailId) {
		this.fundDetailId = fundDetailId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
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

	public String getTransactionCode() {
		return transactionCode;
	}

	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public List<DonorVO> getUserContactList() {
		return userContactList;
	}

	public void setUserContactList(List<DonorVO> userContactList) {
		this.userContactList = userContactList;
	}

	public List<DonorVO> getUserContactPhnList() {
		return userContactPhnList;
	}

	public void setUserContactPhnList(List<DonorVO> userContactPhnList) {
		this.userContactPhnList = userContactPhnList;
	}

	public List<DonorVO> getUserAddressList() {
		return userAddressList;
	}

	public void setUserAddressList(List<DonorVO> userAddressList) {
		this.userAddressList = userAddressList;
	}

	public List<DonorVO> getUserAccountList() {
		return userAccountList;
	}

	public void setUserAccountList(List<DonorVO> userAccountList) {
		this.userAccountList = userAccountList;
	}

	public List<DonorVO> getFundDetailsList() {
		return fundDetailsList;
	}

	public void setFundDetailsList(List<DonorVO> fundDetailsList) {
		this.fundDetailsList = fundDetailsList;
	}
	
	public List<DonorVO> getDonorList() {
		return donorList;
	}

	public void setDonorList(List<DonorVO> donorList) {
		this.donorList = donorList;
	}

	public List<FieldsValue> getGenderList() {
		return genderList;
	}

	public void setGenderList(List<FieldsValue> genderList) {
		this.genderList = genderList;
	}
}
