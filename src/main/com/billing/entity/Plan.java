package com.billing.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

@Entity
@Table(name = "plan")
public class Plan implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	private int id;
	private String plan;
	private String description;
	private double monthlyPrice;
	private double yearlyPrice;
	private Date createdDate;
	private Date modifiedDate;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id" ,unique = true ,nullable = false)
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name="plan",length = 255)
	@Size(max = 255)
	public String getPlan() {
		return plan;
	}
	
	public void setPlan(String plan) {
		this.plan = plan;
	}
	
	@Column(name = "description", length = 255)
	@Size(max = 255)
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(name="monthly_price",columnDefinition = "double default 0")
	public double getMonthlyPrice() {
		return monthlyPrice;
	}
	
	public void setMonthlyPrice(double monthlyPrice) {
		this.monthlyPrice = monthlyPrice;
	}
	
	@Column(name="yearly_price",columnDefinition = "double default 0")
	public double getYearlyPrice() {
		return yearlyPrice;
	}
	
	public void setYearlyPrice(double yearlyPrice) {
		this.yearlyPrice = yearlyPrice;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_date",length = 29)
	public Date getCreatedDate() {
		return createdDate;
	}
	
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modified_date", length = 29)
	public Date getModifiedDate() {
		return modifiedDate;
	}
	
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
