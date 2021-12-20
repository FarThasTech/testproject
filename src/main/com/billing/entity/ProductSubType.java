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
			name = "findAllProductSubType",
			query = "from ProductSubType pst",
			hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),
	@NamedQuery(
			name = "findAllProductSubTypeAndEnableWithOrderBySortCodeAsc",
			query = "from ProductSubType pst where pst.enable = :enable order by pst.sortCode asc",
			hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)
})

@Entity
@Table(name = "product_sub_type")
public class ProductSubType implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private ProductType productType;
	private String subTypeName;
	private String subTypeDescription;
	private int sortCode;
	private boolean enable;
	private Date createdDate;
	private Date modifiedDate;

	public ProductSubType() {
	}

	public ProductSubType(int id) {
		this.id = id;
	}
	public ProductSubType(int id, String typeName, String typeDescription,
			String subTypeName, String subTypeDescription, boolean enable,
			ProductType productType, Date createdDate, Date modifiedDate) {
		this.id = id;
		this.productType = productType;
		this.subTypeName = subTypeName;
		this.subTypeDescription = subTypeDescription;
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
	@JoinColumn(name = "id_producttype")
	public ProductType getProductType() {
		return productType;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;
	}

	@Column(name = "sub_type_name", length = 100)
	@Size(max = 100)
	public String getSubTypeName() {
		return this.subTypeName;
	}

	public void setSubTypeName(String subTypeName) {
		this.subTypeName = subTypeName;
	}

	@Column(name = "sub_type_description")
	public String getSubTypeDescription() {
		return this.subTypeDescription;
	}

	public void setSubTypeDescription(String subTypeDescription) {
		this.subTypeDescription = subTypeDescription;
	}
	
	@Column(name = "sort_code")
	public int getSortCode() {
		return sortCode;
	}

	public void setSortCode(int sortCode) {
		this.sortCode = sortCode;
	}

	@Column(name = "enable", columnDefinition="tinyint(1) default 1")
	public boolean isEnable() {
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modified_date", length = 29)
	public Date getModifiedDate() {
		return this.modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

}
