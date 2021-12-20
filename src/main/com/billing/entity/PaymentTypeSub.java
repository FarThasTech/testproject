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
		name = "findAllPaymentTypeSub",
		query = "from PaymentTypeSub payTypeSub",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
			name = "findAllPaymentTypeSubByPaymentType",
			query = "from PaymentTypeSub payTypeSub where payTypeSub.paymentType.id = :payTypeId",
			hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
			name = "findAllPaymentTypeSubByCountry",
			query = "from PaymentTypeSub payTypeSub where payTypeSub.country = :country",
			hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findAllPaymentTypeSubByPaymentTypeAndCountry",
		query = "from PaymentTypeSub payTypeSub where payTypeSub.paymentType.id = :payTypeId"
				+ " and payTypeSub.country = :country",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)	
})

@Entity
@Table(name = "payment_type_sub")
public class PaymentTypeSub implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private PaymentType paymentType;
	private String paymentFee;
	private String stripeConvertionFee;
	private String country;
	private Date createdDate;
	private Date modifiedDate;
	
	public PaymentTypeSub() {
	}
	
	public PaymentTypeSub(int id) {
		this.id = id;
	}
	
	public PaymentTypeSub(int id, PaymentType paymentType,
			String paymentFee, String stripeConvertionFee, String country,
			Date createdDate,Date modifiedDate) {
		this.id = id;
		this.paymentType = paymentType;
		this.paymentFee = paymentFee;
		this.stripeConvertionFee = stripeConvertionFee;
		this.country = country;
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
	@JoinColumn(name = "id_paymenttype")
	public PaymentType getPaymentType() {
		return this.paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}
	
	@Column(name = "payment_fee", length = 40)
	@Size(max = 40)
	public String getPaymentFee() {
		return paymentFee;
	}

	public void setPaymentFee(String paymentFee) {
		this.paymentFee = paymentFee;
	}

	@Column(name = "stripe_convertion_fee", length = 40)
	public String getStripeConvertionFee() {
		return stripeConvertionFee;
	}

	public void setStripeConvertionFee(String stripeConvertionFee) {
		this.stripeConvertionFee = stripeConvertionFee;
	}

	@Column(name = "country", length = 40)
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
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
