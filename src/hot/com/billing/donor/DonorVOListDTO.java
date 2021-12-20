package com.billing.donor;

import java.util.List;

public class DonorVOListDTO {

	int  iTotalRecords;
 
	int  iTotalDisplayRecords;
 
	String  sEcho;
 
	String sColumns;
 
	List<DonorVO> aaData;
 

	public int getiTotalRecords() {
		return iTotalRecords;
	}

	public void setiTotalRecords(int iTotalRecords) {
		this.iTotalRecords = iTotalRecords;
	}

	public int getiTotalDisplayRecords() {
		return iTotalDisplayRecords;
	}

	public void setiTotalDisplayRecords(int iTotalDisplayRecords) {
		this.iTotalDisplayRecords = iTotalDisplayRecords;
	}

	public String getsEcho() {
		return sEcho;
	}

	public void setsEcho(String sEcho) {
		this.sEcho = sEcho;
	}

	public String getsColumns() {
		return sColumns;
	}

	public void setsColumns(String sColumns) {
		this.sColumns = sColumns;
	}

	public List<DonorVO> getAaData() {
		return aaData;
	}

	public void setAaData(List<DonorVO> aaData) {
		this.aaData = aaData;
	}
	
}
