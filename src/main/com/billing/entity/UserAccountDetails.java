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
import javax.validation.constraints.Size;


@NamedQueries({
	@NamedQuery(
		name = "findAllUserAccountDetails",
		query = "from UserAccountDetails userAccDet",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),
	@NamedQuery(
		name = "findUserAccountDetailsByCompany",
		query = "from UserAccountDetails userAccDet where userAccDet.users.company.id = :companyId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),
	@NamedQuery(
		name = "findUserAccountDetailsByCompanyAndUserId",
		query = "from UserAccountDetails userAccDet where userAccDet.users.company.id = :companyId and userAccDet.users.id= :userId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findUserAccountDetailsByUser",
		query = "from UserAccountDetails userAccDet where userAccDet.users.id = :userId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findUserAccountDetailsByUserWithIban",
		query = "from UserAccountDetails userAccDet where userAccDet.users.id = :userId and userAccDet.ibanCode = :iban",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findUserAccountDetailsByCompanyWithIban",
		query = "from UserAccountDetails userAccDet where userAccDet.users.company.id = :companyId and userAccDet.ibanCode = :iban",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findUserAccountDetailsByUserWithPaymentType",
		query = "from UserAccountDetails userAccDet where userAccDet.users.id = :userId and userAccDet.paymentType.id = :payTypeId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)
})
@Entity
@Table(name = "user_account_details")
public class UserAccountDetails implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private int id;
	private Users users;
	private String accountName;
	private String bankName;
	private String ibanCode;
	private String bicCode;
	private PaymentType paymentType;
	private String countryCode;
	private String customerId;
	private String secretKeyUsed;
	private Users deletedBy;
	private boolean deleted;
	private Date deletedDate;
	private Date createdDate;
	private Date modifiedDate;
	public UserAccountDetails() {
	}

	public UserAccountDetails(int id) {
		this.id = id;
	}
	public UserAccountDetails(int id, Users users, String accountName,
			String ibanCode, String bicCode, String bankName, 
			Date createdDate, Date modifiedDate,
			Users deletedBy, boolean deleted, Date deletedDate) {
		this.id = id;
		this.users = users;
		this.accountName = accountName;
		this.ibanCode = ibanCode;
		this.bicCode = bicCode;
		this.bankName = bankName;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.deletedBy=deletedBy;
		this.deleted=deleted;
		this.deletedDate=deletedDate;
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
	@JoinColumn(name = "id_users")
	public Users getUsers() {
		return this.users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	@Column(name = "account_name", length = 60)
	@Size(max = 60)
	public String getAccountName() {
		return this.accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	@Column(name = "iban_code", length = 255)
	@Size(max = 255)
	public String getIbanCode() {
		return this.ibanCode;
	}

	public void setIbanCode(String ibanCode) {
		this.ibanCode = ibanCode;
	}

	@Column(name = "bic_code", length = 40)
	@Size(max = 40)
	public String getBicCode() {
		return this.bicCode;
	}

	public void setBicCode(String bicCode) {
		this.bicCode = bicCode;
	}
	
	@Column(name = "bank_name", length = 60)
	@Size(max = 60)
	public String getBankName() {
		return this.bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_paymenttype")
	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	@Column(name = "country_code")
	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	@Column(name = "customer_id")
	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	@Column(name = "secret_key_used")
	public String getSecretKeyUsed() {
		return secretKeyUsed;
	}

	public void setSecretKeyUsed(String secretKeyUsed) {
		this.secretKeyUsed = secretKeyUsed;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "deletedBy")
	public Users getDeletedBy() {
		return deletedBy;
	}

	public void setDeletedBy(Users deletedBy) {
		this.deletedBy = deletedBy;
	}

	@Column(name = "deleted", columnDefinition="tinyint(1) default 0")
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "deleted_date", length = 29)	
	public Date getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(Date deletedDate) {
		this.deletedDate = deletedDate;
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
