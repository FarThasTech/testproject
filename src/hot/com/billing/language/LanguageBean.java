package com.billing.language;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.web.ServletContexts;

import com.billing.entity.Languages;
import com.billing.exceptions.ExceptionMsg;

@Scope(ScopeType.SESSION)
@Name("languageBean")
@SuppressWarnings({"unchecked","rawtypes"})
public class LanguageBean {
	
	@In
	public EntityManager entityManager;
	
	@In
	public LocaleSelector localeSelector;
	
	public List<Languages> langList(){
		return entityManager.createNamedQuery("findAllLanguages").getResultList();
	}
	
	public void updateLocaleString() {
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			String localeString = request.getParameter("localeString");
			if(localeString != null && !localeString.isEmpty()) {
				localeSelector.setLocaleString(localeString);
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public Languages getLangFromLangCode(String langCode) {
		try {
			return (Languages) entityManager.createNamedQuery("findAllLanguagesByLangCode")
												.setParameter("langCode", langCode)							
												.getSingleResult();
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return null;
	}
	
	public Map<String, String> getMessagesPropertiesFromLangCode(String langCode){
		try{
			ClassLoader classLoader = this.getClass().getClassLoader();
			try (InputStream utilsInput = classLoader.getResourceAsStream("/messages_"+langCode+".properties")) {
				Properties props = new Properties();
				props.load(utilsInput);
				Map<String, String> map = (Map)props;
				return map;
			}catch (Exception e) {
				ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			}
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return null;
	}
	
}
