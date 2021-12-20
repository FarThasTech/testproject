package com.billing.company;

import org.jboss.seam.annotations.Name;

@Name("slmVO")
public class SLMVO {

	private String fileName;
	
	private String filePath;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
}
