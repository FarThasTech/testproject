package com.billing.onlinelist;

import java.util.List;

public class OnlineVOListDTO {

	int  iTotalRecords;
 
	int  iTotalDisplayRecords;
 
	String  sEcho;
 
	String sColumns;
 
	List<OnlineListVO> aaData;
 

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

	public List<OnlineListVO> getAaData() {
		return aaData;
	}

	public void setAaData(List<OnlineListVO> aaData) {
		this.aaData = aaData;
	}
	
}
