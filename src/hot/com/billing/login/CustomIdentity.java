package com.billing.login;

import javax.security.auth.login.LoginException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Synchronized;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.NotLoggedInException;

@Name("org.jboss.seam.security.identity")
@Scope(ScopeType.SESSION)
@Install(precedence = Install.APPLICATION)
@Synchronized(timeout=5000)
@BypassInterceptors
@Startup
@SuppressWarnings("unused")
public class CustomIdentity extends Identity {
	private static final LogProvider log = Logging
			.getLogProvider(CustomIdentity.class);
	
	@In
	Credentials credentials;

	// Context variables
	private static final String LOGIN_TRIED = "org.jboss.seam.security.loginTried";
	private static final String SILENT_LOGIN = "org.jboss.seam.security.silentLogin";
	   
	private static final long serialVersionUID = -9154737979944336061L;

	private String loginErrorMessage;

	/**
	 * This is used to save off an error message in case of a login.
	 * 
	 * @param message
	 */
	@Observer("invalidLogin")
	public void writeInvalidLogin(String message) {
		loginErrorMessage = message;
	}
	
	/**
	    * Performs an authorization check, based on the specified security expression.
	    * 
	    * @param expr The security expression to evaluate
	    * @throws NotLoggedInException Thrown if the authorization check fails and 
	    * the user is not authenticated
	    * @throws AuthorizationException Thrown if the authorization check fails and
	    * the user is authenticated
	    */
	   public void checkRestriction(String expr)
	   {      
	      if (!securityEnabled) return;
	      
	      if ( !evaluateExpression(expr) )
	      {
	         if ( !isLoggedIn() )
	         {           
	            if (Events.exists()) Events.instance().raiseEvent(EVENT_NOT_LOGGED_IN);
	            log.debug(String.format(
	               "Error evaluating expression [%s] - User not logged in", expr));
	            /*throw new NotLoggedInException();*/
	         }
	         else
	         {
	            if (Events.exists()) Events.instance().raiseEvent(EVENT_NOT_AUTHORIZED);
	            /*throw new AuthorizationException(String.format(
	               "Authorization check failed for expression [%s]", expr));*/
	         }
	      }
	   }

	   /**
	    * Attempts to authenticate the user.  This method is distinct to the 
	    * authenticate() method in that it raises events in response to whether
	    * authentication is successful or not.  The following events may be raised
	    * by calling login():
	    * 
	    * org.jboss.seam.security.loginSuccessful - raised when authentication is successful
	    * org.jboss.seam.security.loginFailed - raised when authentication fails
	    * org.jboss.seam.security.alreadyLoggedIn - raised if the user is already authenticated
	    * 
	    * @return String returns "loggedIn" if user is authenticated, or null if not.
	    */
	   public String login()
	   {
	      try
	      {            
	         if (isLoggedIn())
	         {
	            // If authentication has already occurred during this request via a silent login,
	            // and login() is explicitly called then we still want to raise the LOGIN_SUCCESSFUL event,
	            // and then return.
	            if (Contexts.isEventContextActive() && Contexts.getEventContext().isSet(SILENT_LOGIN))
	            {
	               if (Events.exists()) Events.instance().raiseEvent(EVENT_LOGIN_SUCCESSFUL);
	               return "loggedIn";            
	            }            
	            
	            if (Events.exists()) /*Events.instance().raiseEvent(EVENT_ALREADY_LOGGED_IN);*/
	            return "loggedIn";           
	         }
	         
	         authenticate();
	         
	         if (!isLoggedIn())
	         {
	            throw new LoginException();
	         }
	         
	         if ( log.isDebugEnabled() )
	         {
	            log.debug("Login successful for: " + getCredentials().getUsername());
	         }

	         if (Events.exists()) Events.instance().raiseEvent(EVENT_LOGIN_SUCCESSFUL);
	         return "loggedIn";
	      }
	      catch (LoginException ex)
	      {
	    	  if(credentials != null)
	    		  credentials.invalidate();
	         
	         if ( log.isDebugEnabled() )
	         {
	             log.debug("Login failed for: " + getCredentials().getUsername(), ex);
	         }
	         if (Events.exists()) Events.instance().raiseEvent(EVENT_LOGIN_FAILED, ex);
	      }
	      
	      return null;      
	   }
	   
	   public void loginFP() {
		   unAuthenticate();
		   login();
	   }
	   
}
