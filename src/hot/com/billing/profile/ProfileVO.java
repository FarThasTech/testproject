package com.billing.profile;

import java.util.List;

import org.jboss.seam.annotations.Name;

import com.billing.entity.Currencies;
import com.billing.entity.Fields;
import com.billing.entity.FieldsValue;
import com.billing.entity.Languages;
import com.billing.entity.PaymentMethod;
import com.billing.entity.PaymentType;

@Name("profileVO")
public class ProfileVO {
	
	private String companyName;
	
	private List<Currencies> currencyList;
	
	private Currencies currency;
	
	private String companyAddress;
	
	private String companyHouseNo;
	
	private String companyCity;
	
	private String companyState;
	
	private String companyZip;
	
	private String companyCountry;
	
	private String companyEmail;
	
	private String companyTelephone;
	
	private String companyMobile;
	
	private String companyWebsiteUrl;
	
	private String companyLogoUrl;
	
	private Fields title;
	
	private List<FieldsValue> titleList;
	
	private String firstName;
	
	private String lastName;
	
	private String dateOfBirth;
	
	private Fields profession;
	
	private List<FieldsValue> professionList;
	
	private String langCode;
	
	private List<Languages> langList;
	
	private String address;
	
	private String houseNo;
	
	private String city;
	
	private String state;
	
	private String zip;
	
	private String country;
	
	private String email;
	
	private String telephone;
	
	private String mobile;
	
	private String role;
	
	private boolean stripeConnected;
	
	private PaymentType paymentType;
	
	private boolean enable;
	
	List<PaymentMethod> payMethodList;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public List<Currencies> getCurrencyList() {
		return currencyList;
	}

	public void setCurrencyList(List<Currencies> currencyList) {
		this.currencyList = currencyList;
	}

	public Currencies getCurrency() {
		return currency;
	}

	public void setCurrency(Currencies currency) {
		this.currency = currency;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}

	public String getCompanyHouseNo() {
		return companyHouseNo;
	}

	public void setCompanyHouseNo(String companyHouseNo) {
		this.companyHouseNo = companyHouseNo;
	}

	public String getCompanyCity() {
		return companyCity;
	}

	public void setCompanyCity(String companyCity) {
		this.companyCity = companyCity;
	}

	public String getCompanyState() {
		return companyState;
	}

	public void setCompanyState(String companyState) {
		this.companyState = companyState;
	}

	public String getCompanyZip() {
		return companyZip;
	}

	public void setCompanyZip(String companyZip) {
		this.companyZip = companyZip;
	}

	public String getCompanyCountry() {
		return companyCountry;
	}

	public void setCompanyCountry(String companyCountry) {
		this.companyCountry = companyCountry;
	}

	public String getCompanyEmail() {
		return companyEmail;
	}

	public void setCompanyEmail(String companyEmail) {
		this.companyEmail = companyEmail;
	}

	public String getCompanyTelephone() {
		return companyTelephone;
	}

	public void setCompanyTelephone(String companyTelephone) {
		this.companyTelephone = companyTelephone;
	}

	public String getCompanyMobile() {
		return companyMobile;
	}

	public void setCompanyMobile(String companyMobile) {
		this.companyMobile = companyMobile;
	}

	public String getCompanyWebsiteUrl() {
		return companyWebsiteUrl;
	}

	public void setCompanyWebsiteUrl(String companyWebsiteUrl) {
		this.companyWebsiteUrl = companyWebsiteUrl;
	}

	public String getCompanyLogoUrl() {
		return companyLogoUrl;
	}

	public void setCompanyLogoUrl(String companyLogoUrl) {
		this.companyLogoUrl = companyLogoUrl;
	}

	public Fields getTitle() {
		return title;
	}

	public void setTitle(Fields title) {
		this.title = title;
	}

	public List<FieldsValue> getTitleList() {
		return titleList;
	}

	public void setTitleList(List<FieldsValue> titleList) {
		this.titleList = titleList;
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

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Fields getProfession() {
		return profession;
	}

	public void setProfession(Fields profession) {
		this.profession = profession;
	}

	public List<FieldsValue> getProfessionList() {
		return professionList;
	}

	public void setProfessionList(List<FieldsValue> professionList) {
		this.professionList = professionList;
	}

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	public List<Languages> getLangList() {
		return langList;
	}

	public void setLangList(List<Languages> langList) {
		this.langList = langList;
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

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
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

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isStripeConnected() {
		return stripeConnected;
	}

	public void setStripeConnected(boolean stripeConnected) {
		this.stripeConnected = stripeConnected;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public List<PaymentMethod> getPayMethodList() {
		return payMethodList;
	}

	public void setPayMethodList(List<PaymentMethod> payMethodList) {
		this.payMethodList = payMethodList;
	}
	
}
