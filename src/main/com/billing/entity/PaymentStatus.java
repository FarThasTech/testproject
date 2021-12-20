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
		name = "findPaymentStatusByCompany",
		query = "from PaymentStatus payStatus where payStatus.company.id = :companyId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findPaymentStatusByCompanyAndStatusType",
		query = "from PaymentStatus payStatus where payStatus.company.id = :companyId"
				+ " and payStatus.statusType = :statusType",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)	
})

@Entity
@Table(name = "payment_status")
public class PaymentStatus implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private Company company;
	private int sortCode;
	private boolean enable;
	private boolean rowfreeze;
	private String statusType;
	private Date createdDate;
	private Date modifiedDate;
	private Set<PaymentStatusLanguage> paymentStatusLanguages = new HashSet<PaymentStatusLanguage>(0);

	public PaymentStatus() {
	}

	public PaymentStatus(int id) {
		this.id = id;
	}
	public PaymentStatus(int id, Company company,
			int sortCode, boolean enable, boolean rowfreeze,
			String statusType, Date createdDate, Date modifiedDate,
			Set<PaymentStatusLanguage> paymentStatusLanguages) {
		this.id = id;
		this.company = company;
		this.sortCode = sortCode;
		this.enable = enable;
		this.rowfreeze = rowfreeze;
		this.statusType = statusType;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.paymentStatusLanguages = paymentStatusLanguages;
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
	public boolean getEnable() {
		return this.enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	@Column(name = "rowfreeze", columnDefinition="tinyint(1) default 0")
	public boolean getRowfreeze() {
		return this.rowfreeze;
	}

	public void setRowfreeze(boolean rowfreeze) {
		this.rowfreeze = rowfreeze;
	}

	@Column(name = "status_type", length = 60)
	@Size(max = 60)
	public String getStatusType() {
		return this.statusType;
	}

	public void setStatusType(String statusType) {
		this.statusType = statusType;
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

	@OneToMany(mappedBy = "paymentStatus",cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	public Set<PaymentStatusLanguage> getPaymentStatusLanguages() {
		return this.paymentStatusLanguages;
	}

	public void setPaymentStatusLanguages(Set<PaymentStatusLanguage> paymentStatusLanguages) {
		this.paymentStatusLanguages = paymentStatusLanguages;
	}

}
