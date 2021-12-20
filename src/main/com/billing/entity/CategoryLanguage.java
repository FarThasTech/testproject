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
		name = "findCategoryLanguageByCategory",
		query = "from CategoryLanguage catLang where catLang.category.id = :categoryId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findCategoryLanguageByCategoryAndLang",
		query = "from CategoryLanguage catLang where catLang.category.id = :categoryId"
				+ " and catLang.languages.langCode = :langCode",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findCategoryLanguageByCompanyAndLangWithCatLangNameEmptyCheck",
		query = "from CategoryLanguage catLang where catLang.category.company.id = :companyId"
				+ " and catLang.languages.langCode = :langCode"
				+ " and catLang.category.deleted = false"
				+ " and catLang.categoryName is not null and trim(catLang.categoryName) != '' "
				+ " order by catLang.category.id desc",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findCategoryLanguageByCategoryAndLangCompanyDeleted",
		query = "from CategoryLanguage catLang "
				+ " where catLang.languages.langCode = :langCode"
				+ " and catLang.category.deleted = :deleted"
				+ " and catLang.category.company.id = :companyId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findCategoryLanguageByCategoryAndLangCompanyDeletedEnable",
		query = "from CategoryLanguage catLang "
				+ " where catLang.languages.langCode = :langCode"
				+ " and catLang.category.deleted = :deleted"
				+ " and catLang.category.enable = :enable"
				+ " and catLang.category.company.id = :companyId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findCategoryLanguageByCategoryAndLangCompanyDeletedEnableCatName",
		query = "from CategoryLanguage catLang "
				+ " where catLang.categoryName = :categoryName"
				+ " and catLang.languages.langCode = :langCode"
				+ " and catLang.category.deleted = :deleted"
				+ " and catLang.category.enable = :enable"
				+ " and catLang.category.company.id = :companyId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)
})

@Entity
@Table(name = "category_language")
public class CategoryLanguage implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private Category category;
	private String categoryName;
	private String description;
	private Languages languages;
	private Date createdDate;
	private Date modifiedDate;
	
	public CategoryLanguage() {
	}

	public CategoryLanguage(int id) {
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
	@JoinColumn(name = "id_category")
	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	@Column(name = "category_name", length = 255)
	@Size(max = 255)
	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
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
