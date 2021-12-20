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
		name = "findUsersContactByCompany",
		query = "from UsersContact uc where uc.company.id = :companyId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findUsersContactByContactOptions",
		query = "from UsersContact uc where uc.contactOptions.contactType = :contactType",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findUsersContactByUsers",
		query = "from UsersContact uc where uc.users.id = :usersId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findUsersContactByUsersContactTypeAndContactDetails",
		query = "from UsersContact uc where uc.users.id = :usersId "
				+ " and uc.contactOptions.contactType = :contactType"
				+ " and uc.contactDetails = :contactDetails",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findUsersContactByUsersAndNotDeleted",
		query = "from UsersContact uc where uc.users.id = :usersId"
				+ " and uc.deleted = false",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)
})

@Entity
@Table(name="users_contact")
public class UsersContact implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	private int id;
	private Company company;
	private Users users;
	private ContactOptions contactOptions;
	private String contactDetails;
	private Users deletedBy;
	private boolean deleted;
	private Date deletionDate;
	private Date createdDate;
	private Date modifiedDate;
	
	public UsersContact() {
		
	}
	
	public UsersContact(int id) {
		this.id = id;
	}
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return id;
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
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_users")
	public Users getUsers() {
		return users;
	}
	
	public void setUsers(Users users) {
		this.users = users;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_contactoptions")
	public ContactOptions getContactOptions() {
		return contactOptions;
	}
	
	public void setContactOptions(ContactOptions contactOptions) {
		this.contactOptions = contactOptions;
	}
	
	@Column(name = "contact_details", length = 255)
	@Size(max = 255)
	public String getContactDetails() {
		return contactDetails;
	}
	
	public void setContactDetails(String contactDetails) {
		this.contactDetails = contactDetails;
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
	
}
