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
		name = "findContactOptionsByCompany",
		query = "from ContactOptions contactOptions where contactOptions.company.id = :companyId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findContactOptionsByCompanyAndContactType",
		query = "from ContactOptions contactOptions where contactOptions.company.id = :companyId"
				+ " and contactOptions.contactType = :contactType",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)
})

@Entity
@Table(name = "contact_options")
public class ContactOptions implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private int id;
	private Company company;
	private String contactType;
	private int sortCode;
	private boolean rowfreeze;
	private boolean enable;
	private Date createdDate;
	private Date modifiedDate;

	public ContactOptions() {
	}

	public ContactOptions(int id) {
		this.id = id;
	}
	public ContactOptions(int id, Company company, String contactType,
			int sortCode,boolean rowfreeze, boolean enable, 
			Date createdDate, Date modifiedDate) {
		this.id = id;
		this.company = company;
		this.contactType = contactType;
		this.sortCode = sortCode;
		this.rowfreeze = rowfreeze;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_company")
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Column(name = "contact_type", length = 255)
	@Size(max = 255)
	public String getContactType() {
		return contactType;
	}

	public void setContactType(String contactType) {
		this.contactType = contactType;
	}

	@Column(name = "sort_code")
	public int getSortCode() {
		return sortCode;
	}

	public void setSortCode(int sortCode) {
		this.sortCode = sortCode;
	}

	@Column(name = "row_freeze", columnDefinition="tinyint(1) default 0")
	public boolean isRowfreeze() {
		return rowfreeze;
	}

	public void setRowfreeze(boolean rowfreeze) {
		this.rowfreeze = rowfreeze;
	}

	@Column(name = "enable", columnDefinition="tinyint(1) default 0")
	public boolean isEnable() {
		return enable;
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
