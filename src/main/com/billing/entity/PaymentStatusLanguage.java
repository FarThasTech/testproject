package com.billing.entity;

import java.util.Date;

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
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;


@NamedQueries({
	@NamedQuery(
		name = "findPaymentStatusLanguageByCompany",
		query = "from PaymentStatusLanguage payStatLang where payStatLang.paymentStatus.company.id = :companyId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)
})

@Entity
@Table(name = "payment_status_language")
public class PaymentStatusLanguage implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private Languages languages;
	private PaymentStatus paymentStatus;
	private String status;
	private Date createdDate;
	private Date modifiedDate;

	public PaymentStatusLanguage() {
	}

	public PaymentStatusLanguage(int id) {
		this.id = id;
	}
	public PaymentStatusLanguage(int id, Languages languages,
			PaymentStatus paymentStatus, String status, Date createdDate,
			Date modifiedDate) {
		this.id = id;
		this.languages = languages;
		this.paymentStatus = paymentStatus;
		this.status = status;
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
	@JoinColumn(name = "id_language")
	public Languages getLanguages() {
		return this.languages;
	}

	public void setLanguages(Languages languages) {
		this.languages = languages;
	}

	@ManyToOne(fetch = FetchType.LAZY ,  cascade= CascadeType.ALL)
	@JoinColumn(name = "id_paymentstatus")
	public PaymentStatus getPaymentStatus() {
		return this.paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	@Column(name = "status", length = 100)
	@Size(max = 100)
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
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
