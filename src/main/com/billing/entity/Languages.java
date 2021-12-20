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
		name = "findAllLanguages",
		query = "from Languages languages",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findAllLanguagesByLangCode",
		query = "from Languages languages where languages.langCode = :langCode",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)
})

@Entity
@Table(name = "languages")
public class Languages implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String langName;
	private String langCode;
	private Integer sortCode;
	private boolean enable;
	private Date createdDate;
	private Set<Users> usersList = new HashSet<Users>(0);

	public Languages() {
	}

	public Languages(int id) {
		this.id = id;
	}
	public Languages(int id, String langName, String langCode,
			Integer sortCode, Boolean enable, Date createdDate,
			Set<Users> usersList) {
		this.id = id;
		this.langName = langName;
		this.langCode = langCode;
		this.sortCode = sortCode;
		this.enable = enable;
		this.createdDate = createdDate;
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

	@Column(name = "lang_name", length = 40)
	@Size(max = 40)
	public String getLangName() {
		return this.langName;
	}

	public void setLangName(String langName) {
		this.langName = langName;
	}

	@Column(name = "lang_code", length = 40)
	@Size(max = 40)
	public String getLangCode() {
		return this.langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	@Column(name = "sort_code")
	public Integer getSortCode() {
		return this.sortCode;
	}

	public void setSortCode(Integer sortCode) {
		this.sortCode = sortCode;
	}

	@Column(name = "enable", columnDefinition="tinyint(1) default 1")
	public boolean getEnable() {
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "languages",cascade=CascadeType.ALL)
	public Set<Users> getUsersList() {
		return this.usersList;
	}

	public void setUsersList(Set<Users> usersList) {
		this.usersList = usersList;
	}

}
