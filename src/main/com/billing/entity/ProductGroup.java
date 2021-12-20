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

@NamedQueries({
	@NamedQuery(
		name = "findProductGroupByCompany",
		query = "from ProductGroup prodGroup where prodGroup.company.id = :companyId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findProductGroupByCompanyAndNotDeleted",
		query = "from ProductGroup prodGroup where prodGroup.company.id = :companyId"
				+ " and prodGroup.deleted = false ",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findProductGroupByCompanyAndEnable",
		query = "from ProductGroup prodGroup where prodGroup.company.id = :companyId"
				+ " and prodGroup.deleted = false "
				+ " and prodGroup.enable = :enable",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)
})

@Entity
@Table(name = "product_group")
public class ProductGroup implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private Company company;
	private Project project;
	private Category category;
	private Product product;
	private String imageUrl;
	private double amount;
	private double firstAmount;
	private double secondAmount;
	private double thirdAmount;
	private double fourthAmount;
	private double fifthAmount;
	private double targetAmount;
	private double collectedAmount;
	private double externalDonation;
	private String barCode;
	private double discountPercentage;
	private double discountPrice;
	private double availableStock;
	private double initialStock;
	private int sortCode;
	private String description;
	private boolean enable;
	private Users deletedBy;
	private boolean deleted;
	private Date deletionDate;
	private String productCode;
	private Date createdDate;
	private Date modifiedDate;
			
	public ProductGroup() {
	}

	public ProductGroup(int id) {
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_project")
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_category")
	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_product")
	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	@Column(name = "image_url")
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Column(name = "amount", columnDefinition="double default 1")
	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	@Column(name = "first_amount", columnDefinition="double default 1")
	public double getFirstAmount() {
		return firstAmount;
	}

	public void setFirstAmount(double firstAmount) {
		this.firstAmount = firstAmount;
	}

	@Column(name = "second_amount", columnDefinition="double default 1")
	public double getSecondAmount() {
		return secondAmount;
	}

	public void setSecondAmount(double secondAmount) {
		this.secondAmount = secondAmount;
	}

	@Column(name = "third_amount", columnDefinition="double default 1")
	public double getThirdAmount() {
		return thirdAmount;
	}

	public void setThirdAmount(double thirdAmount) {
		this.thirdAmount = thirdAmount;
	}

	@Column(name = "fourth_amount", columnDefinition="double default 1")
	public double getFourthAmount() {
		return fourthAmount;
	}

	public void setFourthAmount(double fourthAmount) {
		this.fourthAmount = fourthAmount;
	}

	@Column(name = "fifth_amount", columnDefinition="double default 1")
	public double getFifthAmount() {
		return fifthAmount;
	}

	public void setFifthAmount(double fifthAmount) {
		this.fifthAmount = fifthAmount;
	}

	@Column(name = "target_amount", columnDefinition="double default 0")
	public double getTargetAmount() {
		return targetAmount;
	}

	public void setTargetAmount(double targetAmount) {
		this.targetAmount = targetAmount;
	}

	@Column(name = "collected_amount", columnDefinition="double default 0")
	public double getCollectedAmount() {
		return collectedAmount;
	}

	public void setCollectedAmount(double collectedAmount) {
		this.collectedAmount = collectedAmount;
	}

	@Column(name = "external_donation", columnDefinition="double default 0")
	public double getExternalDonation() {
		return externalDonation;
	}

	public void setExternalDonation(double externalDonation) {
		this.externalDonation = externalDonation;
	}

	@Column(name = "bar_code")
	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	@Column(name = "discount_percentage", columnDefinition="double default 0")
	public double getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(double discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	@Column(name = "discount_price", precision = 20, scale = 4)
	public double getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(double discountPrice) {
		this.discountPrice = discountPrice;
	}

	@Column(name = "available_stock", columnDefinition="double default 0")
	public double getAvailableStock() {
		return availableStock;
	}

	public void setAvailableStock(double availableStock) {
		this.availableStock = availableStock;
	}

	@Column(name = "initial_stock", columnDefinition="double default 0")
	public double getInitialStock() {
		return initialStock;
	}

	public void setInitialStock(double initialStock) {
		this.initialStock = initialStock;
	}

	@Column(name = "sort_code")
	public int getSortCode() {
		return sortCode;
	}

	public void setSortCode(int sortCode) {
		this.sortCode = sortCode;
	}

	@Column(name = "description")
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

	@Column(name = "product_code")
	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
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
