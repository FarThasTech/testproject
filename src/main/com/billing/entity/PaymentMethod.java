package com.billing.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@NamedQueries({
	@NamedQuery(
		name = "findPaymentMethodByCompany",
		query = "from PaymentMethod payMethod where payMethod.company.id = :companyId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findPaymentMethodByCompanyAndpaymentType",
		query = "from PaymentMethod payMethod where payMethod.company.id = :companyId"
				+ " and payMethod.paymentType.id = :payTypeId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findPaymentMethodByCompanyAndOnline",
		query = "from PaymentMethod payMethod where payMethod.company.id = :companyId"
				+ " and payMethod.enable = true and payMethod.online = true order by payMethod.sortCode asc",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findPaymentMethodByCompanyAndOffline",
		query = "from PaymentMethod payMethod where payMethod.company.id = :companyId"
				+ " and payMethod.enable = true and payMethod.offline = true order by payMethod.sortCode asc",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findPaymentMethodByCompanyCurrencyAndOnline",
		query = "from PaymentMethod payMethod where payMethod.company.id = :companyId"
				+ " and payMethod.enable = true and payMethod.online = true"
				+ " and payMethod.paymentType.supportCurrency LIKE :supportCurrency"
				+ " order by payMethod.sortCode asc",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findPaymentMethodByCompanyCurrencyAndOffline",
		query = "from PaymentMethod payMethod where payMethod.company.id = :companyId"
				+ " and payMethod.enable = true and payMethod.offline = true "
				+ " and payMethod.paymentType.supportCurrency LIKE :supportCurrency"
				+ " order by payMethod.sortCode asc",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findPaymentMethodByCompanyAndpaymentTypeValue",
		query = "from PaymentMethod payMethod where payMethod.company.id = :companyId"
				+ " and payMethod.paymentType.paymentType = :paymentType"
				+ " order by payMethod.sortCode asc",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)			
})

@Entity
@Table(name = "payment_method")
public class PaymentMethod implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private Company company;
	private int sortCode;
	private boolean enable;
	private boolean online;
	private boolean offline;
	private String paymentName;
	private PaymentType paymentType;
	private PaymentKeys paymentKeys;
	private Date createdDate;
	private Date modifiedDate;
	private Set<PaymentMethodLanguage> paymentMethodLanguages = new HashSet<PaymentMethodLanguage>(0);

	public PaymentMethod() {
	}

	public PaymentMethod(int id) {
		this.id = id;
	}
	public PaymentMethod(int id, Company company,
			int sortCode, boolean enable, boolean online, boolean offline,
			String paymentName, PaymentType paymentType, PaymentKeys paymentKeys,
			Date createdDate, Date modifiedDate,
			Set<PaymentMethodLanguage> paymentMethodLanguages) {
		this.id = id;
		this.company = company;
		this.sortCode = sortCode;
		this.enable = enable;
		this.online = online;
		this.offline = offline;
		this.paymentName = paymentName;
		this.paymentType = paymentType;
		this.paymentKeys = paymentKeys;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.paymentMethodLanguages = paymentMethodLanguages;
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

	@Column(name = "sort_code")
	public int getSortCode() {
		return this.sortCode;
	}

	public void setSortCode(int sortCode) {
		this.sortCode = sortCode;
	}

	@Column(name = "enable", columnDefinition="tinyint(1) default 1")
	public boolean isEnable() {
		return this.enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	@Column(name = "online", columnDefinition="tinyint(1) default 0")
	public boolean isOnline() {
		return this.online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	@Column(name = "offline", columnDefinition="tinyint(1) default 0")
	public boolean isOffline() {
		return offline;
	}

	public void setOffline(boolean offline) {
		this.offline = offline;
	}

	@Column(name = "payment_name", length = 60)
	@Size(max = 60)
	public String getPaymentName() {
		return this.paymentName;
	}

	public void setPaymentName(String paymentName) {
		this.paymentName = paymentName;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_paymenttype")
	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_paymentkeys")
	public PaymentKeys getPaymentKeys() {
		return paymentKeys;
	}

	public void setPaymentKeys(PaymentKeys paymentKeys) {
		this.paymentKeys = paymentKeys;
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

	@OneToMany(mappedBy = "paymentMethod",cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	public Set<PaymentMethodLanguage> getPaymentMethodLanguages() {
		return paymentMethodLanguages;
	}

	public void setPaymentMethodLanguages(Set<PaymentMethodLanguage> paymentMethodLanguages) {
		this.paymentMethodLanguages = paymentMethodLanguages;
	}


}
