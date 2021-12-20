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
		name = "findProjectLanguageByProject",
		query = "from ProjectLanguage projLang where projLang.project.id = :projectId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findProjectLanguageByProjectAndLang",
		query = "from ProjectLanguage projLang where projLang.project.id = :projectId"
				+ " and projLang.languages.langCode = :langCode",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findProjectLanguageByCompanyAndLangWithProjLangNameEmptyCheck",
		query = "from ProjectLanguage projLang where projLang.project.company.id = :companyId"
				+ " and projLang.languages.langCode = :langCode"
				+ " and projLang.project.deleted = false "
				+ " and projLang.projectName is not null and trim(projLang.projectName) != '' "
				+ " order by projLang.project.id desc",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findProjectLanguageByProjectNameAndLangCompany",
		query = "from ProjectLanguage projLang where projLang.projectName = :projectName"
				+ " and projLang.languages.langCode = :langCode"
				+ " and projLang.project.company.id = :companyId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)
})

@Entity
@Table(name = "project_language")
public class ProjectLanguage implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private Project project;
	private String projectName;
	private String description;
	private Languages languages;
	private Date createdDate;
	private Date modifiedDate;
	
	public ProjectLanguage() {
	}

	public ProjectLanguage(int id) {
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
	@JoinColumn(name = "id_project")
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	@Column(name = "project_name", length = 255)
	@Size(max = 255)
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@Column(name = "description", length = 255)
	@Size(max = 255)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_languages")
	public Languages getLanguages() {
		return languages;
	}

	public void setLanguages(Languages languages) {
		this.languages = languages;
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
