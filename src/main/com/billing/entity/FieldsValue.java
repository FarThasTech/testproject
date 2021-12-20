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
		name = "findFieldsValueByFields",
		query = "from FieldsValue fieldsvalue where fieldsvalue.fields.id = :fieldsId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findFieldsValueByStandardTypeAndLang",
		query = "from FieldsValue fieldsvalue where fieldsvalue.fields.standardType = :standardType"
				+ " and fieldsvalue.language.langCode = :langCode",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findFieldsValueByFieldIdAndLang",
		query = "from FieldsValue fieldsvalue where fieldsvalue.fields.id = :fieldId"
				+ " and fieldsvalue.language.langCode = :langCode",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)
})

@Entity
@Table(name = "fields_value")
public class FieldsValue implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private Fields fields;
	private String fieldName;
	private int sortCode;
	private Languages language;
	private Date createdDate;
	private Date modifiedDate;

	public FieldsValue() {
	}

	public FieldsValue(int id) {
		this.id = id;
	}
	public FieldsValue(int id, Integer idOrganization, String fieldName,
			Date createdDate, Date modifiedDate) {
		this.id = id;
		this.fieldName = fieldName;
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
	@JoinColumn(name = "id_fields")
	public Fields getFields() {
		return this.fields;
	}

	public void setFields(Fields fields) {
		this.fields = fields;
	}

	@Column(name = "field_name", length = 255)
	@Size(max = 255)
	public String getFieldName() {
		return this.fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	@Column(name = "sort_code")
	public int getSortCode() {
		return sortCode;
	}

	public void setSortCode(int sortCode) {
		this.sortCode = sortCode;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_languages")
	public Languages getLanguage() {
		return language;
	}

	public void setLanguage(Languages language) {
		this.language = language;
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
