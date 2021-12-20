package com.billing.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import com.billing.modules.Modules;

@NamedQueries({
	@NamedQuery(
		name = "findAllModuleAccessByCompany",
		query = "from ModuleAccess moduleAccess where moduleAccess.company.id= :companyId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findAllModuleAccessByCompanyAndDefaultAccess",
		query = "from ModuleAccess moduleAccess where moduleAccess.company.id= :companyId"
				+ " and moduleAccess.defaultAccess = true",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findAllModuleAccessByCompanyWithParentIdNotNull",
		query = "from ModuleAccess moduleAccess where moduleAccess.company.id= :companyId"
				+ " and moduleAccess.parentId IS NOT NULL order by moduleAccess.parentId asc",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findAllModuleAccessByCompanyWithChildId",
		query = "from ModuleAccess moduleAccess where moduleAccess.company.id= :companyId and moduleAccess.childId = :childId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findAllModuleAccessByCompanyWithParentId",
		query = "from ModuleAccess moduleAccess where moduleAccess.company.id= :companyId and moduleAccess.parentId = :parentId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)
})

@Entity
@Table(name = "module_access")
public class ModuleAccess implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private Company company;
	private Modules modules;
	private Integer parentId;
	private Integer childId;
	private int sortCode;
	private boolean enable;
	private boolean defaultAccess;
	private Date createdDate;
	private Date modifiedDate;
	
	public ModuleAccess() {
	}

	public ModuleAccess(int id) {
		this.id = id;
	}
	public ModuleAccess(int id,Company company,
			String moduleName, Integer parentId,
			int sortCode, boolean enable, Date createdDate,
			Date modifiedDate) {
		this.id = id;
		this.company = company;
		this.parentId = parentId;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_company")
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(name = "module_name")
	public Modules getModules() {
		return modules;
	}

	public void setModules(Modules modules) {
		this.modules = modules;
	}

	@Column(name = "parent_id")
	public Integer getParentId() {
		return this.parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	@Column(name = "child_id")
	public Integer getChildId() {
		return childId;
	}

	public void setChildId(Integer childId) {
		this.childId = childId;
	}

	@Column(name = "sort_code")
	public int getSortCode() {
		return this.sortCode;
	}

	public void setSortCode(int sortCode) {
		this.sortCode = sortCode;
	}

	@Column(name = "enable", columnDefinition = "tinyint(1) default 0")
	public boolean isEnable() {
		return this.enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	@Column(name = "default_access", columnDefinition = "tinyint(1) default 0")
	public boolean isDefaultAccess() {
		return defaultAccess;
	}

	public void setDefaultAccess(boolean defaultAccess) {
		this.defaultAccess = defaultAccess;
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
