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
import javax.persistence.OrderBy;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@NamedQueries({
	@NamedQuery(
		name = "findUsersByCompany",
		query = "from Users users where users.company.id = :companyId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findUsersByUserNamePasswordAndLoginAccess",
		query = "from Users users where users.userName = :userName and users.password = :password and users.loginAccess = true",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findUsersByCompanyUser",
		query = "from Users users where users.companyUser = true and users.company.id = :companyId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findUsersByUserNameAndCompanyUser",
		query = "from Users users where users.companyUser = true and trim(users.userName) = :userName",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findUsersByCompanyAndEmail",
		query = "from Users users where users.company.id = :companyId "
				+ " and users.primaryEmail = :email order by users.id asc",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)
})

@Entity
@Table(name = "users")
public class Users implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	private int id;
	private Company company;
	private String firstName;
	private String lastName;
	private String primaryEmail;
	private String telephone;
	private String mobile;
	private boolean companyUser;
	private Users createdUser;
	private Languages languages;
	private String userName;
	private String password;
	private Date dateofbirth;
	private String birthPlace;
	private String secondaryEmail;
	private boolean loginAccess;
	private boolean writePermission;
	private Fields title;
	private Fields professionTitle;
	private UserRole userRole;
	private String usersType;
	private double salary;
	private double commisson;
	private Date joinDate;
	private Date exitDate;
	private String department;
	private String designation;
	private String userNr;
	private boolean resetPassword;
	private Date createdDate;
	private Date modifiedDate;
	private Set<UsersAddress> usersAddressList = new HashSet<UsersAddress>(0);
	private Set<UsersContact> usersContactList = new HashSet<UsersContact>(0);
	private Set<ModuleAccess> usersAccess = new HashSet<ModuleAccess>(0);
	
	public Users() {
		
	}

	public Users(int id) {
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

	@Column(name = "first_name", length = 255)
	@Size(max = 255)
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(name = "last_name", length = 255)
	@Size(max = 255)
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(name = "primary_email", length = 255)
	@Size(max = 255)
	public String getPrimaryEmail() {
		return primaryEmail;
	}

	public void setPrimaryEmail(String primaryEmail) {
		this.primaryEmail = primaryEmail;
	}

	@Column(name = "telephone", length = 255)
	@Size(max = 255)
	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	@Column(name = "mobile", length = 255)
	@Size(max = 255)
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Column(name = "company_user", columnDefinition="tinyint(1) default 0")
	public boolean isCompanyUser() {
		return companyUser;
	}

	public void setCompanyUser(boolean companyUser) {
		this.companyUser = companyUser;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_user")
	public Users getCreatedUser() {
		return createdUser;
	}

	public void setCreatedUser(Users createdUser) {
		this.createdUser = createdUser;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_languages")
	public Languages getLanguages() {
		return languages;
	}

	public void setLanguages(Languages languages) {
		this.languages = languages;
	}

	@Column(name = "user_name", length = 255)
	@Size(max = 255)
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name = "password", length = 255)
	@Size(max = 255)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "date_of_birth", length = 13)
	public Date getDateofbirth() {
		return dateofbirth;
	}

	public void setDateofbirth(Date dateofbirth) {
		this.dateofbirth = dateofbirth;
	}

	@Column(name = "birth_place", length = 255)
	@Size(max = 255)
	public String getBirthPlace() {
		return birthPlace;
	}

	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}

	@Column(name = "secondary_email", length = 255)
	@Size(max = 255)
	public String getSecondaryEmail() {
		return secondaryEmail;
	}

	public void setSecondaryEmail(String secondaryEmail) {
		this.secondaryEmail = secondaryEmail;
	}

	@Column(name = "login_access", columnDefinition="tinyint(1) default 0")
	public boolean isLoginAccess() {
		return loginAccess;
	}

	public void setLoginAccess(boolean loginAccess) {
		this.loginAccess = loginAccess;
	}

	@Column(name = "write_permission", columnDefinition="tinyint(1) default 0")
	public boolean isWritePermission() {
		return writePermission;
	}

	public void setWritePermission(boolean writePermission) {
		this.writePermission = writePermission;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_title")
	public Fields getTitle() {
		return title;
	}

	public void setTitle(Fields title) {
		this.title = title;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_professiontitle")
	public Fields getProfessionTitle() {
		return professionTitle;
	}

	public void setProfessionTitle(Fields professionTitle) {
		this.professionTitle = professionTitle;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_userrole")
	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

	@Column(name ="users_type")
	public String getUsersType() {
		return usersType;
	}

	public void setUsersType(String usersType) {
		this.usersType = usersType;
	}

	@Column(name="salary" , columnDefinition = "double default 0")
	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}
	
	@Column(name="commisson" , columnDefinition = "double default 0")
	public double getCommisson() {
		return commisson;
	}

	public void setCommisson(double commisson) {
		this.commisson = commisson;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "join_date", length = 29)
	public Date getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "exit_date", length = 29)
	public Date getExitDate() {
		return exitDate;
	}

	public void setExitDate(Date exitDate) {
		this.exitDate = exitDate;
	}

	@Column(name = "department")
	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	@Column(name = "designation")
	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	@Column(name = "user_nr")
	public String getUserNr() {
		return userNr;
	}

	public void setUserNr(String userNr) {
		this.userNr = userNr;
	}

	@Column(name = "reset_password", columnDefinition="tinyint(1) default 0")
	public boolean isResetPassword() {
		return resetPassword;
	}

	public void setResetPassword(boolean resetPassword) {
		this.resetPassword = resetPassword;
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

	@OneToMany(mappedBy = "users", cascade=CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@OrderBy("id DESC")
	public Set<UsersAddress> getUsersAddressList() {
		return usersAddressList;
	}

	public void setUsersAddressList(Set<UsersAddress> usersAddressList) {
		this.usersAddressList = usersAddressList;
	}

	@OneToMany(mappedBy = "users", cascade=CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@OrderBy("id DESC")
	public Set<UsersContact> getUsersContactList() {
		return usersContactList;
	}

	public void setUsersContactList(Set<UsersContact> usersContactList) {
		this.usersContactList = usersContactList;
	}
	
	@ManyToMany(fetch = FetchType.LAZY,targetEntity = ModuleAccess.class, cascade=CascadeType.ALL)
	@JoinTable(name = "users_access")
	@OrderBy("id ASC")
	public Set<ModuleAccess> getUsersAccess() {
		return usersAccess;
	}

	public void setUsersAccess(Set<ModuleAccess> usersAccess) {
		this.usersAccess = usersAccess;
	}

}
