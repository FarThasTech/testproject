package com.billing.exceptions;

public class ExceptionMsg {
	
	public static void ErrorMsg(Exception e, StackTraceElement stackTraceElement){
		try{
			if (e != null) {
				e.printStackTrace();
			}
			if(stackTraceElement!=null){
				System.err.println(e.getMessage() 
						+" - ( "+ stackTraceElement.getFileName()
						+" - "+ stackTraceElement.getMethodName()
						+" - "+ stackTraceElement.getLineNumber()
						+" ) ");
			}
		}catch (Exception e1){
			e1.printStackTrace();
		}
	}
	
	public static void InfoMsg(StackTraceElement stackTraceElement, String message){	
		try{
			if(stackTraceElement!=null){
				System.out.println( message +" - ( "+ stackTraceElement.getFileName()
					+" - "+ stackTraceElement.getMethodName()
					+" - "+ stackTraceElement.getLineNumber()
					+" ) ");
			}
		}catch (Exception e1){
			
		}
	}
}
