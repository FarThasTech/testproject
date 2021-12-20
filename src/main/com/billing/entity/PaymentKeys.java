package com.billing.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@NamedQueries({
	@NamedQuery(
		name = "findPaymentKeysByCompany",
		query = "from PaymentKeys payKeys where payKeys.company.id = :companyId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findPaymentKeysByCompanyByAccountId",
		query = "from PaymentKeys payKeys where payKeys.accountId LIKE :accountId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)		
	
})

@Entity
@Table(name = "payment_keys")
public class PaymentKeys implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private Company company;
	private String paymentType;
	private String accountId;
	private String payParam1;
	private String payParam2;
	private String payParam3;
	private String payParam4;
	private String testPayParam1;
	private String testPayParam2;
	private String testPayParam3;
	private String testPayParam4;
	private String accountCountry;
	private String accountCurrencyCode;
	private boolean accountConnected;
	private Date createdDate;
	private Date modifiedDate;

	public PaymentKeys() {
	}

	public PaymentKeys(int id) {
		this.id = id;
	}
	public PaymentKeys(int id, Company company,
			String paymentType, Date createdDate, Date modifiedDate) {
		this.id = id;
		this.company = company;
		this.paymentType = paymentType;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_company")
	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Column(name = "payment_type")
	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	@Column(name = "account_id")
	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	@Column(name = "pay_param1")
	public String getPayParam1() {
		return payParam1;
	}

	public void setPayParam1(String payParam1) {
		this.payParam1 = payParam1;
	}

	@Column(name = "pay_param2")
	public String getPayParam2() {
		return payParam2;
	}

	public void setPayParam2(String payParam2) {
		this.payParam2 = payParam2;
	}

	@Column(name = "pay_param3")
	public String getPayParam3() {
		return payParam3;
	}

	public void setPayParam3(String payParam3) {
		this.payParam3 = payParam3;
	}

	@Column(name = "pay_param4")
	public String getPayParam4() {
		return payParam4;
	}

	public void setPayParam4(String payParam4) {
		this.payParam4 = payParam4;
	}

	@Column(name = "test_pay_param1")
	public String getTestPayParam1() {
		return testPayParam1;
	}

	public void setTestPayParam1(String testPayParam1) {
		this.testPayParam1 = testPayParam1;
	}

	@Column(name = "test_pay_param2")
	public String getTestPayParam2() {
		return testPayParam2;
	}

	public void setTestPayParam2(String testPayParam2) {
		this.testPayParam2 = testPayParam2;
	}

	@Column(name = "test_pay_param3")
	public String getTestPayParam3() {
		return testPayParam3;
	}

	public void setTestPayParam3(String testPayParam3) {
		this.testPayParam3 = testPayParam3;
	}

	@Column(name = "test_pay_param4")
	public String getTestPayParam4() {
		return testPayParam4;
	}

	public void setTestPayParam4(String testPayParam4) {
		this.testPayParam4 = testPayParam4;
	}

	@Column(name = "account_country")
	public String getAccountCountry() {
		return accountCountry;
	}

	public void setAccountCountry(String accountCountry) {
		this.accountCountry = accountCountry;
	}

	@Column(name = "account_currency_code")
	public String getAccountCurrencyCode() {
		return accountCurrencyCode;
	}

	public void setAccountCurrencyCode(String accountCurrencyCode) {
		this.accountCurrencyCode = accountCurrencyCode;
	}
	
	@Column(name = "account_connected", columnDefinition="tinyint(1) default 1")
	public boolean isAccountConnected() {
		return accountConnected;
	}

	public void setAccountConnected(boolean accountConnected) {
		this.accountConnected = accountConnected;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", length = 29)
	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modified_date", length = 29)
	public Date getModifiedDate() {
		return this.modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

}
