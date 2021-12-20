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
		name = "findProductLanguageByProduct",
		query = "from ProductLanguage prodLang where prodLang.product.id = :productId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findProductLanguageByCompanyAndLang",
		query = "from ProductLanguage prodLang where prodLang.product.company.id = :companyId"
				+ " and prodLang.languages.langCode = :langCode",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findProductLanguageByProductAndLang",
		query = "from ProductLanguage prodLang where prodLang.product.id = :productId"
				+ " and prodLang.languages.langCode = :langCode",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findProductLanguageByCompanyAndLangWithProdLangNameEmptyCheck",
		query = "from ProductLanguage prodLang where prodLang.product.company.id = :companyId"
				+ " and prodLang.languages.langCode = :langCode "
				+ " and prodLang.product.deleted = false"
				+ " and prodLang.productName is not null and trim(prodLang.productName) != '' "
				+ " order by prodLang.product.id desc",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findProductLanguageByProductNameAndLangCompanyDeleted",
		query = "from ProductLanguage prodLang where trim(prodLang.productName) = :productName"
				+ " and prodLang.languages.langCode = :langCode"
				+ " and prodLang.product.company.id = :companyId"
				+ " and prodLang.product.deleted = :deleted",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)
})

@Entity
@Table(name = "product_language")
public class ProductLanguage implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private Product product;
	private String productName;
	private String description;
	private Languages languages;
	private Date createdDate;
	private Date modifiedDate;
	
	public ProductLanguage() {
	}

	public ProductLanguage(int id) {
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
	@JoinColumn(name = "id_product")
	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	@Column(name = "product_name", length = 255)
	@Size(max = 255)
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	@Column(name = "description")
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
