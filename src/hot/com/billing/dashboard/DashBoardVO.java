package com.billing.dashboard;

import org.jboss.seam.annotations.Name;

@Name("dashBoardVO")
public class DashBoardVO {
	
	private int campaignCount;
	
	private int donorCount;
	
	private String totalDonation;
	
	private String currentMonthDonation;

	public int getCampaignCount() {
		return campaignCount;
	}

	public void setCampaignCount(int campaignCount) {
		this.campaignCount = campaignCount;
	}

	public int getDonorCount() {
		return donorCount;
	}

	public void setDonorCount(int donorCount) {
		this.donorCount = donorCount;
	}

	public String getTotalDonation() {
		return totalDonation;
	}

	public void setTotalDonation(String totalDonation) {
		this.totalDonation = totalDonation;
	}

	public String getCurrentMonthDonation() {
		return currentMonthDonation;
	}

	public void setCurrentMonthDonation(String currentMonthDonation) {
		this.currentMonthDonation = currentMonthDonation;
	}
	
	
}
