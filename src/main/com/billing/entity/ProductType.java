package com.billing.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;


@NamedQueries({
	@NamedQuery(
			name = "findAllProductType",
			query = "from ProductType prodType",
			hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)
})

@Entity
@Table(name = "product_type")
public class ProductType implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String typeName;
	private String typeDescription;
	private int sortCode;
	private boolean enable;
	private Date createdDate;
	private Date modifiedDate;

	public ProductType() {
	}

	public ProductType(int id) {
		this.id = id;
	}
	public ProductType(int id, String typeName, String typeDescription,
			String typeValue, String typeComponent, boolean enable,
			Date createdDate, Date modifiedDate) {
		this.id = id;
		this.typeName = typeName;
		this.typeDescription = typeDescription;
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

	@Column(name = "type_name", length = 100)
	@Size(max = 100)
	public String getTypeName() {
		return this.typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	@Column(name = "type_description")
	public String getTypeDescription() {
		return this.typeDescription;
	}

	public void setTypeDescription(String typeDescription) {
		this.typeDescription = typeDescription;
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
