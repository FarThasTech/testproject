package com.billing.exceptions;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Synchronized;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.exception.Exceptions;

@Name("org.jboss.seam.concurrentrequesttimeoutexception")
@Scope(ScopeType.APPLICATION)
@Install(precedence = Install.FRAMEWORK+1)
@Synchronized(timeout=5000)
@BypassInterceptors
public class ConcurrentRequestTimeoutException extends Exceptions {

	public String[] ignoreExceptions(){
		String[] exceptions = {"org.jboss.seam.core.LockTimeoutException"};
		return exceptions;
	}
	
	@Override
	public void handle(Exception e) throws Exception {
		try{
			if(e.getMessage() != null && !e.getMessage().trim().isEmpty()){
				boolean show = true;
				for(String exception : ignoreExceptions()){
					if(e.getMessage().contains(exception)){
						//show = false;
					}
				}
				if(show){
					System.err.println("Seam Exception Handler -- " + e.getMessage());
				}
			}
		}catch (Exception e1) {}
	}
}
