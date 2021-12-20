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
import javax.persistence.OrderBy;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@NamedQueries({
	@NamedQuery(
		name = "findFundGroupByCompany",
		query = "from FundGroup fundGroup where fundGroup.company.id = :companyId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findFundGroupByCompanyAndDonateUser",
		query = "from FundGroup fundGroup where fundGroup.company.id = :companyId "
				+ " and fundGroup.donateUser.id = :donateUserId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findFundGroupByCompanyAndDonateUserEmail",
		query = "from FundGroup fundGroup where fundGroup.company.id = :companyId "
				+ " and fundGroup.donateUser.primaryEmail = :email",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)
})

@Entity
@Table(name = "fund_group")
public class FundGroup implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	private int id;
	private Company company;
	private Users donateUser;
	private Users createdUser;
	private Users deletedBy;
	private boolean deleted;
	private Date deletionDate;
	private Date createdDate;
	private Date modifiedDate;
	Set<FundDetails> fundDetailsList = new HashSet<FundDetails>();
	
	public FundGroup() {
		
	}

	public FundGroup(int id) {
		this.id = id;
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
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_company")
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_users")
	public Users getDonateUser() {
		return donateUser;
	}

	public void setDonateUser(Users donateUser) {
		this.donateUser = donateUser;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_createduser")
	public Users getCreatedUser() {
		return createdUser;
	}

	public void setCreatedUser(Users createdUser) {
		this.createdUser = createdUser;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "deleted_by")
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
	@Column(name = "deletion_date", length = 29)
	public Date getDeletionDate() {
		return deletionDate;
	}

	public void setDeletionDate(Date deletionDate) {
		this.deletionDate = deletionDate;
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

	@OneToMany(mappedBy = "fundGroup",cascade=CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@OrderBy("id")
	public Set<FundDetails> getFundDetailsList() {
		return fundDetailsList;
	}

	public void setFundDetailsList(Set<FundDetails> fundDetailsList) {
		this.fundDetailsList = fundDetailsList;
	}
	
}
