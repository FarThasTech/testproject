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
		name = "findAllCurrencies",
		query = "from Currencies currencies",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findCurrenciesbyCurrencyCode",
		query = "from Currencies currencies where upper (TRIM(currencies.currencyCode)) LIKE :currencyCode",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
			name = "findCurrenciesbyCurrencyName",
			query = "from Currencies currencies where upper (TRIM(currencies.currencyName)) LIKE :currencyName",
			hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findCurrenciesByEnable",
		query = "from Currencies currencies where currencies.enable = :enable",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)
})
@Entity
@Table(name = "currencies")
public class Currencies implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String currencyName;
	private String currencySymbol;
	private String currencyCode;
	private int sortCode;
	private boolean enable;
	private Date createdDate;
	private Date modifiedDate;
	
	public Currencies() {
	}

	public Currencies(int id) {
		this.id = id;
	}
	public Currencies(int id, String currencyName, String currencySymbol,
			String currencyCode, int sortCode, boolean enable,
			Date createdDate, Date modifiedDate) {
		this.id = id;
		this.currencyName = currencyName;
		this.currencySymbol = currencySymbol;
		this.currencyCode = currencyCode;
		this.sortCode = sortCode;
		this.enable = enable;
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

	@Column(name = "currency_name")
	public String getCurrencyName() {
		return this.currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	@Column(name = "currency_symbol")
	public String getCurrencySymbol() {
		return this.currencySymbol;
	}

	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}

	@Column(name = "currency_code", length = 40)
	@Size(max = 40)
	public String getCurrencyCode() {
		return this.currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	@Column(name = "sort_code")
	public int getSortCode() {
		return this.sortCode;
	}

	public void setSortCode(int sortCode) {
		this.sortCode = sortCode;
	}

	@Column(name = "enable", columnDefinition = "tinyint(1) default 1")
	public boolean isEnable() {
		return this.enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
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
