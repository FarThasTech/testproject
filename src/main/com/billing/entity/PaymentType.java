package com.billing.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

@NamedQueries({
	@NamedQuery(
		name = "findAllPaymentType",
		query = "from PaymentType payType order by payType.id asc",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findPaymentTypeByPaymentType",
		query = "from PaymentType payType where payType.paymentType = :paymentType",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findPaymentTypeById",
		query = "from PaymentType payType where payType.id = :payTypeId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findPaymentTypeByGroupOfPaymentType",
		query = "from PaymentType payType group by payType.paymentType "
				+ " order by payType.paymentType asc",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)
})

@Entity
@Table(name = "payment_type")
public class PaymentType implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String paymentName;
	private String paymentType;
	private boolean supportRecurring;
	private boolean otherReuccring;
	private String supportCurrency;
	private Date createdDate;
	private Date modifiedDate;

	public PaymentType() {
	}

	public PaymentType(int id) {
		this.id = id;
	}
	public PaymentType(int id, String paymentName,
			String paymentType, Date createdDate,
			Date modifiedDate) {
		this.id = id;
		this.paymentName = paymentName;
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

	@Column(name = "payment_name", length = 60)
	@Size(max = 60)
	public String getPaymentName() {
		return this.paymentName;
	}

	public void setPaymentName(String paymentName) {
		this.paymentName = paymentName;
	}

	@Column(name = "payment_type", length = 60)
	@Size(max = 60)
	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	@Column(name = "support_recurring", columnDefinition="tinyint(1) default 0")
	public boolean isSupportRecurring() {
		return supportRecurring;
	}

	public void setSupportRecurring(boolean supportRecurring) {
		this.supportRecurring = supportRecurring;
	}

	@Column(name = "other_recurring", columnDefinition="tinyint(1) default 0")
	public boolean isOtherReuccring() {
		return otherReuccring;
	}

	public void setOtherReuccring(boolean otherReuccring) {
		this.otherReuccring = otherReuccring;
	}
	
	@Column(name = "support_currency")
	public String getSupportCurrency() {
		return supportCurrency;
	}

	public void setSupportCurrency(String supportCurrency) {
		this.supportCurrency = supportCurrency;
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
