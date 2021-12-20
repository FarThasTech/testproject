package com.billing.jms;

import com.billing.entity.Category;
import com.billing.entity.Product;
import com.billing.entity.UserRole;

public class JMSVO {

	private String param;
	
	private String localeString;
	
	private String primaryId;
	
	private Category category;
	
	private Product product;
	
	private UserRole userRole;	

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getLocaleString() {
		return localeString;
	}

	public void setLocaleString(String localeString) {
		this.localeString = localeString;
	}

	public String getPrimaryId() {
		return primaryId;
	}

	public void setPrimaryId(String primaryId) {
		this.primaryId = primaryId;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}
}
