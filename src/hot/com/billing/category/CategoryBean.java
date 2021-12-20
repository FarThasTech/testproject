package com.billing.category;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.web.ServletContexts;

import com.billing.commonFile.Translator;
import com.billing.entity.Category;
import com.billing.entity.CategoryLanguage;
import com.billing.entity.Languages;
import com.billing.entity.Users;
import com.billing.exceptions.ExceptionMsg;
import com.billing.jms.JMSBean;
import com.billing.jms.JMSClient;
import com.billing.jms.JMSVO;
import com.billing.language.LanguageBean;
import com.billing.util.DateUtil;
import com.billing.util.NumberUtil;
import com.billing.util.StringUtil;
import com.google.gson.JsonObject;

@Scope(ScopeType.SESSION)
@Name("categoryBean")
@SuppressWarnings("unchecked")
public class CategoryBean {

	@In
	public EntityManager entityManager;
	    
	@In
	public LocaleSelector localeSelector;
	
	@In(create=true,required=false) 
    public Users activeUser;
	
	@In(create=true)
	@Out(scope=ScopeType.SESSION)
	public CategoryVO categoryVO;
	
	public void resetData() {
		try {
			categoryVO.setCategoryId(0);
			categoryVO.setCategoryName("");
			categoryVO.setDescription("");
			categoryVO.setEnable(false);
			categoryVO.setStartDate("");
			categoryVO.setEndDate("");
			categoryVO.setCategoryLangId(0);
			categoryVO.setCategoryList(null);
		} catch(Exception e) {
			categoryVO = new CategoryVO();
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void showValues() {
		try {
			resetData();
			if(activeUser != null) {
				int companyId = activeUser.getCompany().getId();
				HttpServletRequest request =ServletContexts.instance().getRequest();
				String param = request.getParameter("status");
				List<CategoryLanguage> cateLangList = new ArrayList<CategoryLanguage>();
				String langCode = localeSelector.getLocaleString();
				boolean deleted = false;
				if(StringUtil.checkStringIsNull(param)) {
					boolean enable = true, statusLoop = false;
					if(param.trim().equalsIgnoreCase("false")) {
						enable = false;
						statusLoop = true;
					} else if(param.trim().equalsIgnoreCase("false")) {
						enable = true;
						statusLoop = true;
					} else {
						statusLoop = false;
					}
					if(statusLoop) {
						cateLangList = getCategoryLanguageByCategoryAndLangCompanyDeletedEnable(companyId, deleted, langCode, enable);
					} else {
						cateLangList = getCategoryLanguageByCategoryAndLangCompanyDeleted(companyId, deleted, langCode);
					}
				} else {
					cateLangList = getCategoryLanguageByCategoryAndLangCompanyDeleted(companyId, deleted, langCode);
				}
				
				if(cateLangList != null && cateLangList.size() > 0) {
					String datePattern = activeUser.getCompany().getDatePattern();
					List<CategoryVO> categoryList = new ArrayList<CategoryVO>();
					for(CategoryLanguage cateLang : cateLangList) {
						CategoryVO catVO = new CategoryVO();
						catVO.setCategoryLangId(cateLang.getId());
						Category category = cateLang.getCategory();
						catVO.setCategoryId(category.getId());
						catVO.setCategoryName(cateLang.getCategoryName());
						catVO.setDescription(cateLang.getDescription());
						catVO.setStartDate(DateUtil.getDateToStringFormat(category.getCategoryStartDate(), datePattern));
						catVO.setEndDate(DateUtil.getDateToStringFormat(category.getCategoryEndDate(), datePattern));
						catVO.setEnable(category.isEnable());
						categoryList.add(catVO);
					}
					categoryVO.setCategoryList(categoryList);
				}
			}
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void persistUserData() {
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			response.setContentType("text/html; charset=UTF-8");
			String categoryName = request.getParameter("categoryName");
			String categoryDesc = request.getParameter("categoryDesc");
			String categoryStartDate = request.getParameter("categoryStartDate");
			String categoryEndDate = request.getParameter("categoryEndDate");
			String categoryLangId = request.getParameter("categoryLangId");
			String status = request.getParameter("status");
			
			if(StringUtil.checkStringIsNull(categoryName)) {
				int companyId = activeUser.getCompany().getId();
				String langCode = localeSelector.getLocaleString();
				if(StringUtil.checkStringIsNotNullAndInteger(categoryLangId)) {
					CategoryLanguage cateLang = entityManager.find(CategoryLanguage.class, Integer.valueOf(categoryLangId.trim()));
					if(cateLang != null) {
						cateLang.setCategoryName(categoryName);
						cateLang.setDescription(categoryDesc);
						if(StringUtil.checkStringIsNull(categoryStartDate) && categoryStartDate.trim().length() == 10) {
							Date startDate = DateUtil.getStringToDateFormat(categoryStartDate, DateUtil.DATE_FORMAT_DE);
							cateLang.getCategory().setCategoryStartDate(startDate);
						}
						if(StringUtil.checkStringIsNull(categoryEndDate) && categoryEndDate.trim().length() == 10) {
							Date endDate = DateUtil.getStringToDateFormat(categoryEndDate, DateUtil.DATE_FORMAT_DE);
							cateLang.getCategory().setCategoryEndDate(endDate);
						}
						
						if(StringUtil.checkStringIsNull(status)) {
							if(status.trim().equalsIgnoreCase("true")) {
								cateLang.getCategory().setEnable(true);
							} else if(status.trim().equalsIgnoreCase("false")) {
								cateLang.getCategory().setEnable(false);
							}
						}
						entityManager.merge(cateLang);
						entityManager.flush();
						response.getWriter().write("true\n");
					} else {
						response.getWriter().write("false\n");
					}
				} else {
					List<CategoryLanguage> cateNameList = getCategoryLanguageByCategoryAndLangCompanyDeletedEnableCatName(companyId, false, langCode, true, categoryName);
					if(cateNameList != null && cateNameList.size() > 0) {
						response.getWriter().write("AlreadyExist\n");
					} else {
						Date startDate = null, endDate = null;
						if(StringUtil.checkStringIsNull(categoryStartDate) && NumberUtil.checkNumericWithDotSlashHyphen(categoryStartDate) && categoryStartDate.trim().length() == 10) {
							startDate = DateUtil.getStringToDateFormat(categoryStartDate, DateUtil.DATE_FORMAT_DE);
						}
						if(StringUtil.checkStringIsNull(categoryEndDate) && NumberUtil.checkNumericWithDotSlashHyphen(categoryEndDate) && categoryEndDate.trim().length() == 10) {
							endDate = DateUtil.getStringToDateFormat(categoryEndDate, DateUtil.DATE_FORMAT_DE);
						}
						Category category = persist(categoryName, categoryDesc, startDate, endDate);
						if(category != null) {
							response.getWriter().write("true\n");
						} else {
							response.getWriter().write("false\n");
						}
					}
				}
			} else {
				response.getWriter().write("false\n");
			}
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public Category persist(String categoryName, String description,
			Date startDate, Date endDate) {
		Category category = null;
		try {
			if(categoryName != null && !categoryName.isEmpty()) {
				/******************* Add New Data *******************/
				category = new Category();
				category.setCompany(activeUser.getCompany());
				category.setDescription(description);
				category.setCategoryName(categoryName);
				category.setCategoryStartDate(startDate);
				category.setCategoryEndDate(endDate);
				category.setEnable(true);
				category.setCreatedDate(new Date());
				category.setModifiedDate(new Date());
				entityManager.persist(category);
				entityManager.flush();
				createCategoryLang(category, true);
				/******************* Add New Data *******************/
				
				/*********** Add Other Category Language *************/
				
				JMSVO jmsVO = new JMSVO();
				JMSBean jmsBean = new JMSBean();
				jmsVO = jmsBean.resetJMSVO(jmsVO);
				jmsVO.setParam("Category");
				jmsVO.setLocaleString(localeSelector.getLocaleString());
				jmsVO.setPrimaryId(String.valueOf(category.getId()));
				jmsVO.setCategory(category);
				JMSClient jmsClient = new JMSClient();
				jmsClient.automaticJMS(jmsVO);
				
				/*********** Add Other Category Language *************/
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return category;
	}
	
	public void createCategoryLang(Category category, boolean currenLang) {
		try {
			if(category!= null) {
				String categoryName = category.getCategoryName();
				String description = category.getDescription();
				LanguageBean langBean = new LanguageBean();
				langBean.entityManager = entityManager;
				String sourceLang = localeSelector.getLocaleString();
				if(currenLang) {
					Languages lang = langBean.getLangFromLangCode(sourceLang);
					persistCategoryLang(sourceLang, category, lang, categoryName, description);
				}else {
					for(Languages lang : langBean.langList()) {
						if(!lang.getLangCode().trim().equalsIgnoreCase(sourceLang.trim()))
							persistCategoryLang(sourceLang, category, lang, categoryName, description);
					}					
				}
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void persistCategoryLang(String sourceLang, Category category, Languages lang, String categoryName, String description) {
		try {
			CategoryLanguage categoryLang = new CategoryLanguage();
			categoryLang.setCategoryName(Translator.translate(sourceLang, lang.getLangCode(), categoryName));
			categoryLang.setDescription(description != null ? Translator.translate(sourceLang, lang.getLangCode(), description) : null);
			categoryLang.setLanguages(lang);
			categoryLang.setCategory(category);
			categoryLang.setCreatedDate(new Date());
			categoryLang.setModifiedDate(new Date());
			entityManager.persist(categoryLang);
			entityManager.flush();
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void updateCategoryStatus() {
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			String categoryId = request.getParameter("categoryId");
			String status = request.getParameter("status");
			if(StringUtil.checkStringIsNotNullAndInteger(categoryId)) {
				Category category = entityManager.find(Category.class, Integer.valueOf(categoryId.trim()));
				if(category != null && category.getCompany().getId() == activeUser.getCompany().getId()) {
					if(StringUtil.checkStringIsNull(status) && status.equalsIgnoreCase("true"))
						category.setEnable(true);
					else if(StringUtil.checkStringIsNull(status) && status.equalsIgnoreCase("false"))
						category.setEnable(false);
					entityManager.merge(category);
					entityManager.flush();
				}
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void deleteCategory() {
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			String categoryId = request.getParameter("categoryId");
			if(StringUtil.checkStringIsNotNullAndInteger(categoryId)) {
				Category category = entityManager.find(Category.class, Integer.valueOf(categoryId.trim()));
				if(category != null && category.getCompany().getId() == activeUser.getCompany().getId()) {
					category.setEnable(false);
					category.setDeleted(true);
					category.setDeletedBy(activeUser);
					category.setDeletionDate(new Date());
					entityManager.merge(category);
					entityManager.flush();
				}
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void editCategory() {
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			String categoryLangId = request.getParameter("categoryLangId");
			if(StringUtil.checkStringIsNotNullAndInteger(categoryLangId)) {
				CategoryLanguage categoryLang = entityManager.find(CategoryLanguage.class, Integer.valueOf(categoryLangId.trim()));
				if(categoryLang != null) {
					Category category = categoryLang.getCategory();
					String categoryName = categoryLang.getCategoryName();
					String description = categoryLang.getDescription();
					String startDate = DateUtil.getDateToStringFormat(category.getCategoryStartDate(), activeUser.getCompany().getDatePattern());
					String endDate = DateUtil.getDateToStringFormat(category.getCategoryEndDate(), activeUser.getCompany().getDatePattern());
					boolean status = category.isEnable();
					JsonObject jsonContent = new JsonObject();
					jsonContent.addProperty("categoryName", categoryName);
					jsonContent.addProperty("description", description);
					jsonContent.addProperty("startDate", startDate);
					jsonContent.addProperty("endDate", endDate);
					jsonContent.addProperty("status", status);
					jsonContent.addProperty("categoryLangId", categoryLangId);
					response.setContentType("text/html; charset=UTF-8");
					String result = jsonContent.toString().replace("\",\"", "\",\n\"");
					response.getWriter().write(result+ "\nSplitData");
				}
			}
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public List<CategoryLanguage> getCategoryLanguageByCategoryAndLangCompanyDeleted(int companyId, boolean deleted, String langCode){
		try {
			return entityManager.createNamedQuery("findCategoryLanguageByCategoryAndLangCompanyDeleted")
					.setParameter("companyId", companyId)
					.setParameter("deleted", deleted)
					.setParameter("langCode", langCode.toUpperCase())
					.getResultList();
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			return new ArrayList<CategoryLanguage>();
		}
	}
	
	public List<CategoryLanguage> getCategoryLanguageByCategoryAndLangCompanyDeletedEnable(int companyId, boolean deleted, String langCode, boolean enable){
		try {
			return entityManager.createNamedQuery("findCategoryLanguageByCategoryAndLangCompanyDeletedEnable")
					.setParameter("companyId", companyId)
					.setParameter("deleted", deleted)
					.setParameter("langCode", langCode.toUpperCase())
					.setParameter("enable", enable)
					.getResultList();
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			return new ArrayList<CategoryLanguage>();
		}
	}
	
	public List<CategoryLanguage> getCategoryLanguageByCategoryAndLangCompanyDeletedEnableCatName(int companyId, boolean deleted, String langCode, boolean enable, String categoryName){
		try {
			return entityManager.createNamedQuery("findCategoryLanguageByCategoryAndLangCompanyDeletedEnableCatName")
					.setParameter("categoryName", categoryName)
					.setParameter("companyId", companyId)
					.setParameter("deleted", deleted)
					.setParameter("langCode", langCode.toUpperCase())
					.setParameter("enable", enable)
					.getResultList();
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			return new ArrayList<CategoryLanguage>();
		}
	}
	
}
