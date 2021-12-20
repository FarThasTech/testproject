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
import javax.validation.constraints.Size;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@NamedQueries({
	@NamedQuery(
		name = "findCategoryByCompany",
		query = "from Category category where category.company.id = :companyId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findCategoryByCompanyAndDependsOnDeleteColumn",
		query = "from Category category where category.company.id = :companyId"
				+ " and category.deleted = :deleted",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findCategoryByCompanyAndDependsOnCategoryName",
		query = "from Category category where category.company.id = :companyId"
				+ " and trim(category.categoryName) = :catName",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findCategoryByCompanyAndDependsOnEnableDeleteColumn",
		query = "from Category category where category.company.id = :companyId"
				+ " and category.deleted = :deleted and category.enable = :enable",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)
})

@Entity
@Table(name = "category")
public class Category implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private Company company;
	private String categoryName;
	private String description;
	private boolean enable;
	private Date categoryStartDate;
	private Date categoryEndDate;
	private Users deletedBy;
	private boolean deleted;
	private Date deletionDate;
	private Date createdDate;
	private Date modifiedDate;
	private Set<CategoryLanguage> categoryLanguage= new HashSet<CategoryLanguage>(0);
	private Set<ProductGroup> productGroup = new HashSet<ProductGroup>(0);
	public Category() {
	}

	public Category(int id) {
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
	@JoinColumn(name = "id_company")
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
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

	@Column(name = "enable", columnDefinition="tinyint(1) default 1")
	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "category_start_date", length = 29)
	public Date getCategoryStartDate() {
		return categoryStartDate;
	}

	public void setCategoryStartDate(Date categoryStartDate) {
		this.categoryStartDate = categoryStartDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "category_end_date", length = 29)
	public Date getCategoryEndDate() {
		return categoryEndDate;
	}

	public void setCategoryEndDate(Date categoryEndDate) {
		this.categoryEndDate = categoryEndDate;
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

	@OneToMany(mappedBy = "category",cascade=CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@OrderBy("id")
	public Set<CategoryLanguage> getCategoryLanguage() {
		return categoryLanguage;
	}

	public void setCategoryLanguage(Set<CategoryLanguage> categoryLanguage) {
		this.categoryLanguage = categoryLanguage;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "category",cascade=CascadeType.ALL)
	public Set<ProductGroup> getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(Set<ProductGroup> productGroup) {
		this.productGroup = productGroup;
	}
}
