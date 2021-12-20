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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;


@NamedQueries({
	@NamedQuery(
		name = "findUserRoleByCompany",
		query = "from UserRole userRole where userRole.company.id = :companyId"
				+ " and userRole.hideRole = false",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findUserRoleByCompanyAndRole",
		query = "from UserRole userRole where userRole.company.id = :companyId"
				+ " and trim(upper(userRole.role)) LIKE  :role",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)
})

@Entity
@Table(name = "user_role")
public class UserRole implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private Company company;
	private String role;
	private int sortCode;
	private boolean enable;
	private boolean rowfreeze;
	private boolean hideRole;
	private Date createdDate;
	private Date modifiedDate;
	private Set<Users> usersList = new HashSet<Users>(0);
	private Set<UserRoleLanguage> userRoleLanguage = new HashSet<UserRoleLanguage>(0);
	private Set<ModuleAccess> userRoleAccess = new HashSet<ModuleAccess>(0);
	private Set<ModuleAccess> userRoleModule = new HashSet<ModuleAccess>(0);
	public UserRole() {
	}

	public UserRole(int id) {
		this.id = id;
	}
	public UserRole(int id, Company company, String role,
			int sortCode, Date createdDate, Date modifiedDate,
			Set<Users> usersList) {
		this.id = id;
		this.company = company;
		this.role = role;
		this.sortCode = sortCode;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.usersList = usersList;
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
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Column(name = "role", length = 40)
	@Size(max = 40)
	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Column(name = "sort_code")
	public int getSortCode() {
		return this.sortCode;
	}

	public void setSortCode(int sortCode) {
		this.sortCode = sortCode;
	}

	@Column(name="enable", columnDefinition = "tinyint(1) default 0")
	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	@Column(name="row_freeze", columnDefinition = "tinyint(1) default 0")
	public boolean isRowfreeze() {
		return rowfreeze;
	}

	public void setRowfreeze(boolean rowfreeze) {
		this.rowfreeze = rowfreeze;
	}

	@Column(name="hide_role", columnDefinition = "tinyint(1) default 0")
	public boolean isHideRole() {
		return hideRole;
	}

	public void setHideRole(boolean hideRole) {
		this.hideRole = hideRole;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "userRole",cascade=CascadeType.ALL)
	public Set<Users> getUsersList() {
		return this.usersList;
	}

	public void setUsersList(Set<Users> usersList) {
		this.usersList = usersList;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "userRole")
	public Set<UserRoleLanguage> getUserRoleLanguage() {
		return userRoleLanguage;
	}

	public void setUserRoleLanguage(Set<UserRoleLanguage> userRoleLanguage) {
		this.userRoleLanguage = userRoleLanguage;
	}

	@ManyToMany(fetch = FetchType.LAZY,targetEntity = ModuleAccess.class,cascade=CascadeType.ALL)
	@JoinTable(name = "userrole_access")
	public Set<ModuleAccess> getUserRoleAccess() {
		return userRoleAccess;
	}

	public void setUserRoleAccess(Set<ModuleAccess> userRoleAccess) {
		this.userRoleAccess = userRoleAccess;
	}

	@ManyToMany(fetch = FetchType.LAZY,targetEntity = ModuleAccess.class,cascade=CascadeType.ALL)
	@JoinTable(name = "userrole_module")
	public Set<ModuleAccess> getUserRoleModule() {
		return userRoleModule;
	}

	public void setUserRoleModule(Set<ModuleAccess> userRoleModule) {
		this.userRoleModule = userRoleModule;
	}
	
}
