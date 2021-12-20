package com.billing.category;

import java.util.List;

import org.jboss.seam.annotations.Name;

@Name("categoryVO")
public class CategoryVO {

	private int categoryId;
	
	private int categoryIds; 
	
	private int categoryIds1; 
	
	private String categoryName;
	
	private String description;
	
	private String startDate;
	
	private String endDate;
	
	private boolean enable;

	private int categoryLangId;
	
	private List<CategoryVO> categoryList;

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public int getCategoryLangId() {
		return categoryLangId;
	}

	public void setCategoryLangId(int categoryLangId) {
		this.categoryLangId = categoryLangId;
	}

	public List<CategoryVO> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<CategoryVO> categoryList) {
		this.categoryList = categoryList;
	}
	

}
