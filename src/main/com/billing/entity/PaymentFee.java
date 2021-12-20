package com.billing.entity;

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


@NamedQueries({
	@NamedQuery(
	name = "findPaymentFeeByCompany",
	query = "from PaymentFee payfee where payfee.company.id = :companyId",
	hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),
	@NamedQuery(
		name = "findPaymentFeeByCompanyAndPaymentType",
		query = "from PaymentFee payfee where payfee.company.id = :companyId"
				+ " and payfee.paymentType.id= :payTypeId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)
	
})

@Entity
@Table(name = "payment_fee")
public class PaymentFee implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private Company company;
	private PaymentType paymentType;
	private String feeTakenOver;
	private String feeNotTakenOver;
	

	public PaymentFee() {
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
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_paymenttype")
	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	@Column(name = "fee_taken_over")
	public String getFeeTakenOver() {
		return feeTakenOver;
	}

	public void setFeeTakenOver(String feeTakenOver) {
		this.feeTakenOver = feeTakenOver;
	}

	@Column(name = "fee_not_taken_over")
	public String getFeeNotTakenOver() {
		return feeNotTakenOver;
	}

	public void setFeeNotTakenOver(String feeNotTakenOver) {
		this.feeNotTakenOver = feeNotTakenOver;
	}

}
