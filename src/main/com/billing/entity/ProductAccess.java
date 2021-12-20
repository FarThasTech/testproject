package com.billing.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "product_access")
public class ProductAccess implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private ProductAccessId id;

	public ProductAccess() {
	}

	public ProductAccess(ProductAccessId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "productId", column = @Column(name = "product_id")),
			@AttributeOverride(name = "productAccessId", column = @Column(name = "productAccess_id")),
			@AttributeOverride(name = "productTypeSubId", column = @Column(name = "product_type_sub_id"))})
	public ProductAccessId getId() {
		return this.id;
	}

	public void setId(ProductAccessId id) {
		this.id = id;
	}

}
