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
		name = "findUsersAddressByCompany",
		query = "from UsersAddress usersAddr where usersAddr.users.company.id = :companyId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findUsersAddressByUsersAndNotDeleted",
		query = "from UsersAddress usersAddr where usersAddr.users.id = :usersId"
				+ " and usersAddr.deleted = false order by usersAddr.id desc",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)
})

@Entity
@Table(name = "users_address")
public class UsersAddress implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private Users users;
	private String houseNo;
	private String address1;
	private String address2;
	private String city;
	private String zip;
	private String state;
	private String country;
	private boolean permanentAddress ;
	private boolean presentAddress;
	private boolean verified;
	private Users deletedBy;
	private boolean deleted;
	private Date deletionDate;
	private Date createdDate;
	private Date modifiedDate;

	public UsersAddress() {
	}

	public UsersAddress(int id) {
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_users")
	public Users getUsers() {
		return this.users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	@Column(name = "house_no", length = 40)
	@Size(max = 40)
	public String getHouseNo() {
		return this.houseNo;
	}

	public void setHouseNo(String houseNo) {
		this.houseNo = houseNo;
	}

	@Column(name = "address_1", length = 100)
	@Size(max = 100)
	public String getAddress1() {
		return this.address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	@Column(name = "address_2", length = 100)
	@Size(max = 100)
	public String getAddress2() {
		return this.address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	@Column(name = "city", length = 40)
	@Size(max = 40)
	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(name = "zip", length = 40)
	@Size(max = 40)
	public String getZip() {
		return this.zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	@Column(name = "state", length = 60)
	@Size(max = 60)
	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Column(name = "country", length = 60)
	@Size(max = 60)
	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Column(name = "permanent_address", columnDefinition="tinyint(1) default 0")
	public boolean isPermanentAddress() {
		return permanentAddress;
	}

	public void setPermanentAddress(boolean permanentAddress) {
		this.permanentAddress = permanentAddress;
	}

	@Column(name = "present_address", columnDefinition="tinyint(1) default 0")
	public boolean isPresentAddress() {
		return presentAddress;
	}

	public void setPresentAddress(boolean presentAddress) {
		this.presentAddress = presentAddress;
	}

	@Column(name = "verified", columnDefinition="tinyint(1) default 0")
	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
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
	
	@Temporal(TemporalType.DATE)
	@Column(name = "created_date", length = 13)
	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "modified_date", length = 13)
	public Date getModifiedDate() {
		return this.modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

}
