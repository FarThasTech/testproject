package com.billing.entity;

import java.util.Date;

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
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

@NamedQueries({
	@NamedQuery(
		name = "findRoleLanguageByCompany",
		query = "from UserRoleLanguage roleLang where roleLang.userRole.company.id = :companyId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findRoleLanguageByRoleAndLang",
		query = "from UserRoleLanguage roleLang where roleLang.userRole.id = :roleId "
				+ " and roleLang.languages.langCode = :langCode",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
			name = "findRoleLanguageByCompanyAndLang",
			query = "from UserRoleLanguage roleLang where roleLang.userRole.company.id = :companyId "
					+ " and roleLang.languages.langCode = :langCode",
			hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
		)
})
@Entity
@Table(name = "user_role_language")
public class UserRoleLanguage  implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	private int id;
	private UserRole userRole;
	private Languages languages;
	private String role;
	private Date createdDate;
	private Date modifiedDate;

	
	public UserRoleLanguage() {
	}

	public UserRoleLanguage(int id) {
		this.id = id;
	}
	public UserRoleLanguage(int id, UserRole idUserrole, Languages idLanguage,
			String role, Date createdDate, Date modifiedDate) {
		this.id = id;
		this.userRole = idUserrole;
		this.languages = idLanguage;
		this.role = role;
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
	
	@ManyToOne(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinColumn(name = "id_userrole")
	public UserRole getUserRole() {
		return this.userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_language")
	public Languages getLanguages() {
		return this.languages;
	}

	public void setLanguages(Languages languages) {
		this.languages = languages;
	}

	@Column(name = "user_role_value", length = 100)
	@Size(max = 100)
	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
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
