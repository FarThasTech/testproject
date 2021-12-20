package com.billing.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ProductAccessId implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int productId;
	private int productAccessId;
	private String productTypeSubId;

	public ProductAccessId() {
	}

	public ProductAccessId(int productId, int productAccessId) {
		this.productId = productId;
		this.productAccessId = productAccessId;
	}

	@Column(name = "product_id")
	public int getproductId() {
		return this.productId;
	}

	public void setproductId(int productId) {
		this.productId = productId;
	}

	@Column(name = "productAccess_id")
	public int getProductAccessId() {
		return this.productAccessId;
	}

	public void setProductAccessId(int productAccessId) {
		this.productAccessId = productAccessId;
	}

	@Column(name = "product_type_sub_id")
	public String getProductTypeSubId() {
		return productTypeSubId;
	}

	public void setProductTypeSubId(String productTypeSubId) {
		this.productTypeSubId = productTypeSubId;
	}

}
