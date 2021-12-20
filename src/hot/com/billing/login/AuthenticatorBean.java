package com.billing.login;

import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;

import com.billing.entity.ModuleAccess;
import com.billing.entity.Users;

@Scope(ScopeType.SESSION)
@Name("authenticator")
@SuppressWarnings("unchecked")
public class AuthenticatorBean implements Authenticator {
	@Logger
	public Log log;

	@In
	public CustomIdentity identity;
	
	@In
	public Credentials credentials;
	
	@In
	public EntityManager entityManager;
	
	@Out(required=false, value="activeUser",scope=ScopeType.SESSION)
	public Users activeUser = new Users();
	
	@In
	public LocaleSelector localeSelector;	
	
	public boolean authenticate() {
		String userName = null, password = null;
		try {
			userName = credentials.getUsername();
			password = credentials.getPassword();
			if(userName != null  && !userName.isEmpty()
					&& password != null && !password.isEmpty()) {
				log.info("authenticating {0}", userName);
				activeUser = findActiveUser(userName, PasswordBean.getInstance().encryptPassword(password.trim()));
				if(activeUser != null) {
					String langCode = activeUser.getLanguages() != null ? activeUser.getLanguages().getLangCode() : "EN";
					Locale.setDefault(new Locale(langCode));
					System.setProperty("user.language", langCode);
					localeSelector.setLocaleString(langCode);
					loadModules(activeUser);
					return true;
				}else {
					return false;
				}
			}else {
				System.err.println("Invalid Credentials or Empty Credentials.");
			}
			return false;
		} catch (Exception e) {
			log.warn("An error occured while authenticating user: " + userName, e);
		}
		return false;
	}
	
	private Users findActiveUser(String userName, String password) {
		List<Users> usersList = entityManager.createNamedQuery("findUsersByUserNamePasswordAndLoginAccess")
				.setParameter("userName", userName)
				.setParameter("password", password)
				.getResultList();
		if(usersList != null && usersList.size() == 1) {
			return usersList.get(0);
		}else if(usersList == null || (usersList != null && usersList.size() == 0)){
			System.err.println("No user found");
		}else {
			System.err.println("More than one user havaing the same username..!");
		}
		return null;
	}

	private void loadModules(Users users) {
		for (ModuleAccess p : users.getUsersAccess()) {
			if(p.isEnable())
				identity.addRole(p.getModules().name());
		}
	}
	
}
