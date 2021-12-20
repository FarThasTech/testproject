package com.billing.company;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.web.ServletContexts;

import com.billing.exceptions.ExceptionMsg;
import com.billing.login.PasswordBean;
import com.billing.util.MainUtil;
import com.billing.util.StringUtil;

@Name("slmBean")
@SuppressWarnings({"resource"})
public class SLMBean {

	
	String slmpwd = "slmpwdisnothingbut"; 
	
	public boolean verifyAccess(String value){
		try{
			if(value != null && !value.trim().isEmpty() && PasswordBean.decryptWithoutAlgorithm(value).equals(slmpwd))
				return true;
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return false;
	}
	
	public List<SLMVO> listOfLogFiles(){
		List<SLMVO> list = new ArrayList<SLMVO>();
		try{
			HttpServletRequest request = ServletContexts.instance().getRequest();
			String link = request.getParameter("slmpwd");
			if(link != null && verifyAccess(link)){
				String logPath = MainUtil.getJbossLogDir();
				File txtfiles = new File(logPath);
				File[] txtfilesList = txtfiles.listFiles();
				if(txtfilesList!=null){
					Arrays.sort(txtfilesList, new Comparator<File>() {
					public int compare(File f1, File f2) {
					        return Long.compare(f2.lastModified(), f1.lastModified());
					    }
					});
					for(File f: txtfilesList){
						SLMVO fVO = new SLMVO();
						fVO.setFileName(f.getName());
						fVO.setFilePath(f.getAbsolutePath());
						list.add(fVO);
					}
				}
			}
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			list = new ArrayList<SLMVO>();
		}
		return list;
	}
	
	public void loadLogFile(){
		try{
			HttpServletRequest request = ServletContexts.instance().getRequest();
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			String logPath = request.getParameter("logPath");
			String sqlqryFilePath = request.getParameter("sqlqryFilePath");
			String data = "";
			Integer size_limit = 10;
			try {
				File file = new File(logPath);
				if(file != null){
					double fileSize = getFileSizeMegaBytes(file);
					if(fileSize < size_limit){
						if(sqlqryFilePath != null && sqlqryFilePath.equalsIgnoreCase("sqlqryFilePath")){
							data = StringUtil.readFileAsString(logPath);
						}else{
							data = StringUtil.readFileAsString(logPath);
						}
					}else if(fileSize > size_limit){
						String str = "Content Is not Loaded (Reason : Selected File Size seems to be greater than "+size_limit+" mb" +
										" and Browser would hang while loading such files)";
						data = "\n\n\n"+str;
					}else{
						data = "";
					}
				}
			} catch (Exception e) {
			   
			}
			response.setContentType("text/html; charset=UTF-8");
			response.getWriter().write(data+"");
		}catch (Exception e) {
		}
	}
	
	private static double getFileSizeMegaBytes(File file) {
		return (double) file.length() / (1024 * 1024);
	}
	
	public String getLastNLogLines(File file, int nLines) {
	    StringBuilder s = new StringBuilder();
	    try {
	    	 int n_lines = 30;
	    	    ReversedLinesFileReader object = new ReversedLinesFileReader(file);
	    	    String result="";
	    	    for(int i=0;i<n_lines;i++){
	    	        String line=object.readLine();
	    	        if(line==null)
	    	            break;
	    	        result+=line+"\n";
	    	    }
	    	    return result;
	    } catch (java.io.IOException e) {
	    	ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
	    }
	    return s.toString();
	}
}
