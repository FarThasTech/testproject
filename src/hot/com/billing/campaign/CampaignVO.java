package com.billing.campaign;

import java.util.List;

import org.jboss.seam.annotations.Name;

import com.billing.entity.CategoryLanguage;
import com.billing.entity.ProductSubType;
import com.billing.entity.ProductType;

@Name("campaignVO")
public class CampaignVO {
	
	private String campaignName;
	
	private String campaignName1;
	
	private int prodGroupId;
	
	private String imageUrl;
	
	private double amount;
	
	private double firstAmount;
	
	private double secondAmount;
	
	private double thirdAmount;
	
	private double fourthAmount;
	
	private double fifthAmount;
	
	private double targetAmount;
	
	private double collectedAmount;
	
	private String description;
	
	private String lastColumn;
	
	private String prodGroupIdStr;
	
	private String amountStr;
	
	private String firstAmountStr;
	
	private String secondAmountStr;
	
	private String thirdAmountStr;
	
	private String fourthAmountStr;
	
	private String fifthAmountStr;
	
	private String targetAmountStr;
	
	private String collectedAmountStr;
	
	private int durationTypeId;
	
	private boolean prodGroupStatus;
	
	private String prodGroupStatusStr;
	
	private String param;
	
	private String categoryName;
	
	private int categoryId;
	
	private int prodTypeId;
	
	private String recurringTypeId;
	
	private String recurringTypeSubName;
	
	private int prodOtherTypeId;
	
	private boolean disableProdOtherType;
	
	private boolean disableCampaignDetails;
	
	private boolean disableAll;
	
	private List<CategoryLanguage> categoryLangList;

	private List<ProductSubType> recurringSubTypeList;
	
	private List<ProductType> prodOtherTypeList;
	
	public int getProdGroupId() {
		return prodGroupId;
	}

	public void setProdGroupId(int prodGroupId) {
		this.prodGroupId = prodGroupId;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getFirstAmount() {
		return firstAmount;
	}

	public void setFirstAmount(double firstAmount) {
		this.firstAmount = firstAmount;
	}

	public double getSecondAmount() {
		return secondAmount;
	}

	public void setSecondAmount(double secondAmount) {
		this.secondAmount = secondAmount;
	}

	public double getThirdAmount() {
		return thirdAmount;
	}

	public void setThirdAmount(double thirdAmount) {
		this.thirdAmount = thirdAmount;
	}

	public double getFourthAmount() {
		return fourthAmount;
	}

	public void setFourthAmount(double fourthAmount) {
		this.fourthAmount = fourthAmount;
	}

	public double getFifthAmount() {
		return fifthAmount;
	}

	public void setFifthAmount(double fifthAmount) {
		this.fifthAmount = fifthAmount;
	}

	public double getTargetAmount() {
		return targetAmount;
	}

	public void setTargetAmount(double targetAmount) {
		this.targetAmount = targetAmount;
	}

	public double getCollectedAmount() {
		return collectedAmount;
	}

	public void setCollectedAmount(double collectedAmount) {
		this.collectedAmount = collectedAmount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLastColumn() {
		return lastColumn;
	}

	public void setLastColumn(String lastColumn) {
		this.lastColumn = lastColumn;
	}

	public String getProdGroupIdStr() {
		return prodGroupIdStr;
	}

	public void setProdGroupIdStr(String prodGroupIdStr) {
		this.prodGroupIdStr = prodGroupIdStr;
	}

	public String getAmountStr() {
		return amountStr;
	}

	public void setAmountStr(String amountStr) {
		this.amountStr = amountStr;
	}

	public String getFirstAmountStr() {
		return firstAmountStr;
	}

	public void setFirstAmountStr(String firstAmountStr) {
		this.firstAmountStr = firstAmountStr;
	}

	public String getSecondAmountStr() {
		return secondAmountStr;
	}

	public void setSecondAmountStr(String secondAmountStr) {
		this.secondAmountStr = secondAmountStr;
	}

	public String getThirdAmountStr() {
		return thirdAmountStr;
	}

	public void setThirdAmountStr(String thirdAmountStr) {
		this.thirdAmountStr = thirdAmountStr;
	}

	public String getFourthAmountStr() {
		return fourthAmountStr;
	}

	public void setFourthAmountStr(String fourthAmountStr) {
		this.fourthAmountStr = fourthAmountStr;
	}

	public String getFifthAmountStr() {
		return fifthAmountStr;
	}

	public void setFifthAmountStr(String fifthAmountStr) {
		this.fifthAmountStr = fifthAmountStr;
	}

	public String getTargetAmountStr() {
		return targetAmountStr;
	}

	public void setTargetAmountStr(String targetAmountStr) {
		this.targetAmountStr = targetAmountStr;
	}

	public String getCollectedAmountStr() {
		return collectedAmountStr;
	}

	public void setCollectedAmountStr(String collectedAmountStr) {
		this.collectedAmountStr = collectedAmountStr;
	}

	public int getDurationTypeId() {
		return durationTypeId;
	}

	public void setDurationTypeId(int durationTypeId) {
		this.durationTypeId = durationTypeId;
	}

	public boolean isProdGroupStatus() {
		return prodGroupStatus;
	}

	public void setProdGroupStatus(boolean prodGroupStatus) {
		this.prodGroupStatus = prodGroupStatus;
	}

	public String getProdGroupStatusStr() {
		return prodGroupStatusStr;
	}

	public void setProdGroupStatusStr(String prodGroupStatusStr) {
		this.prodGroupStatusStr = prodGroupStatusStr;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public int getProdTypeId() {
		return prodTypeId;
	}

	public void setProdTypeId(int prodTypeId) {
		this.prodTypeId = prodTypeId;
	}

	public String getRecurringTypeId() {
		return recurringTypeId;
	}

	public void setRecurringTypeId(String recurringTypeId) {
		this.recurringTypeId = recurringTypeId;
	}

	public String getRecurringTypeSubName() {
		return recurringTypeSubName;
	}

	public void setRecurringTypeSubName(String recurringTypeSubName) {
		this.recurringTypeSubName = recurringTypeSubName;
	}

	public int getProdOtherTypeId() {
		return prodOtherTypeId;
	}

	public void setProdOtherTypeId(int prodOtherTypeId) {
		this.prodOtherTypeId = prodOtherTypeId;
	}

	public boolean isDisableProdOtherType() {
		return disableProdOtherType;
	}

	public void setDisableProdOtherType(boolean disableProdOtherType) {
		this.disableProdOtherType = disableProdOtherType;
	}

	public boolean isDisableCampaignDetails() {
		return disableCampaignDetails;
	}

	public void setDisableCampaignDetails(boolean disableCampaignDetails) {
		this.disableCampaignDetails = disableCampaignDetails;
	}

	public boolean isDisableAll() {
		return disableAll;
	}

	public void setDisableAll(boolean disableAll) {
		this.disableAll = disableAll;
	}

	public List<CategoryLanguage> getCategoryLangList() {
		return categoryLangList;
	}

	public void setCategoryLangList(List<CategoryLanguage> categoryLangList) {
		this.categoryLangList = categoryLangList;
	}

	public List<ProductSubType> getRecurringSubTypeList() {
		return recurringSubTypeList;
	}

	public void setRecurringSubTypeList(List<ProductSubType> recurringSubTypeList) {
		this.recurringSubTypeList = recurringSubTypeList;
	}

	public List<ProductType> getProdOtherTypeList() {
		return prodOtherTypeList;
	}

	public void setProdOtherTypeList(List<ProductType> prodOtherTypeList) {
		this.prodOtherTypeList = prodOtherTypeList;
	}

}
