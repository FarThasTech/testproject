package com.billing.fields;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.LocaleSelector;

import com.billing.commonFile.Translator;
import com.billing.entity.Fields;
import com.billing.entity.FieldsValue;
import com.billing.entity.Languages;
import com.billing.entity.Users;
import com.billing.exceptions.ExceptionMsg;
import com.billing.language.LanguageBean;

@Scope(ScopeType.SESSION)
@Name("fieldsBean")
@SuppressWarnings("unchecked")
public class FieldsBean {

	@In
	public EntityManager entityManager;
	    
	@In
	public LocaleSelector localeSelector;
	
	@In(create=true,required=false) 
    public Users activeUser;
	
	public List<FieldsValue> fieldsValueListDependsOnFiels(String standardType, String langCode){
		try {
			return entityManager.createNamedQuery("findFieldsValueByStandardTypeAndLang")
				.setParameter("standardType", standardType)
				.setParameter("langCode", langCode)
				.getResultList();
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			return null;
		}
	}
	
	public void createFieldsValueLang(Fields fields, String value) {
		try {
			String sourceLang = localeSelector.getLocaleString();
			LanguageBean langBean = new LanguageBean();
			langBean.entityManager = entityManager;
			for(Languages lang : langBean.langList()) {
				FieldsValue fieldsValue = new FieldsValue();
				fieldsValue.setFieldName(Translator.translate(sourceLang, lang.getLangCode(), value));
				fieldsValue.setFields(fields);
				fieldsValue.setLanguage(lang);
				fieldsValue.setSortCode(1);
				fieldsValue.setCreatedDate(new Date());
				fieldsValue.setModifiedDate(new Date());
				entityManager.persist(fieldsValue);
				entityManager.flush();
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
}
