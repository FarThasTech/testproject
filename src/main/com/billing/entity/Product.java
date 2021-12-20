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
		name = "findProductByCompany",
		query = "from Product product where product.company.id = :companyId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findProductByCompanyAndDependsOnDeleteColumn",
		query = "from Product product where product.company.id = :companyId"
				+ " and product.deleted = :deleted",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findProductByCompanyAndDependsOnProductName",
		query = "from Product product where product.company.id = :companyId"
				+ " and trim(product.productName) = :prodName",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)
})

@Entity
@Table(name = "product")
public class Product implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private Company company;
	private String productName;
	private String description;
	private boolean enable;
	private Users deletedBy;
	private boolean deleted;
	private Date deletionDate;
	private Date createdDate;
	private Date modifiedDate;
	private Set<ProductLanguage> productLanguage= new HashSet<ProductLanguage>(0);
	private Set<ProductGroup> productGroup = new HashSet<ProductGroup>(0);
	private Set<ProductType> productAccess = new HashSet<ProductType>();
	
	public Product() {
	}

	public Product(int id) {
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

	@Column(name = "product_name", length = 255)
	@Size(max = 255)
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
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

	@OneToMany(mappedBy = "product",cascade=CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@OrderBy("id")
	public Set<ProductLanguage> getProductLanguage() {
		return productLanguage;
	}

	public void setProductLanguage(Set<ProductLanguage> productLanguage) {
		this.productLanguage = productLanguage;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "product",cascade=CascadeType.ALL)
	public Set<ProductGroup> getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(Set<ProductGroup> productGroup) {
		this.productGroup = productGroup;
	}
	
	@ManyToMany(targetEntity = ProductType.class,cascade=CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "product_access")
	public Set<ProductType> getProductAccess() {
		return productAccess;
	}

	public void setProductAccess(Set<ProductType> productAccess) {
		this.productAccess = productAccess;
	}

}
