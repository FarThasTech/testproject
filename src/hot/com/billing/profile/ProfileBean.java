package com.billing.profile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
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

import com.billing.entity.Company;
import com.billing.entity.Currencies;
import com.billing.entity.FieldsValue;
import com.billing.entity.Languages;
import com.billing.entity.PaymentMethod;
import com.billing.entity.UserRole;
import com.billing.entity.UserRoleLanguage;
import com.billing.entity.Users;
import com.billing.entity.UsersAddress;
import com.billing.exceptions.ExceptionMsg;
import com.billing.fields.FieldsBean;
import com.billing.language.LanguageBean;
import com.billing.login.PasswordBean;
import com.billing.paymethod.PayMethodBean;
import com.billing.staticvalue.StaticValues;
import com.billing.util.MainUtil;
import com.billing.util.NumberUtil;
import com.billing.util.StringUtil;

@Scope(ScopeType.SESSION)
@Name("profileBean")
@SuppressWarnings("unchecked")
public class ProfileBean {

	@In
	public EntityManager entityManager;
	    
	@In
	public LocaleSelector localeSelector;
	
	@In(create=true,required=false) 
    public Users activeUser;
	
	@In(create=true)
	@Out(scope=ScopeType.SESSION)
	public ProfileVO profileVO;
	
	public void getProfileInformation() {
		try {
			reset();	
			if(activeUser != null) {
				Company company = activeUser.getCompany();
				int companyId = company.getId();
				if(company != null) {
					profileVO.setCompanyName(company.getCompanyName());
					List<Currencies> currencyList = entityManager.createNamedQuery("findCurrenciesByEnable")
							.setParameter("enable", true)
							.getResultList();
					profileVO.setCurrencyList(currencyList);
					profileVO.setCurrency(company.getCurrencies());
					profileVO.setCompanyAddress(company.getAddress());
					profileVO.setCompanyHouseNo(company.getHouseno());
					profileVO.setCompanyCity(company.getCity());
					profileVO.setCompanyState(company.getState());
					profileVO.setCompanyZip(company.getZip());
					profileVO.setCompanyCountry(company.getCountry());
					profileVO.setCompanyEmail(company.getEmail());
					profileVO.setCompanyMobile(company.getMobile());
					profileVO.setCompanyTelephone(company.getTelephone());
					profileVO.setCompanyWebsiteUrl(company.getWebsiteLink());
					profileVO.setCompanyLogoUrl(company.getLogoUrl() != null && !company.getLogoUrl().isEmpty() 
							? StringUtil.getImageToEncodeImage(company.getLogoUrl()) : StringUtil.getImageToEncodeImage("/images/img_not_found.png"));
				}
				Users users = activeUser;
				profileVO.setFirstName(users.getFirstName());
				profileVO.setLastName(users.getLastName());
				Languages lang = users.getLanguages();
				String localString = localeSelector.getLocaleString();
				FieldsBean fieldBean = new FieldsBean();
				fieldBean.entityManager = entityManager;
				List<FieldsValue> professionList = fieldBean.fieldsValueListDependsOnFiels(StaticValues.Profession, localString);
				profileVO.setProfessionList(professionList);
				profileVO.setProfession(users.getProfessionTitle());
				List<FieldsValue> titleList = fieldBean.fieldsValueListDependsOnFiels(StaticValues.Title, localString);
				profileVO.setTitleList(titleList);
				profileVO.setTitle(users.getTitle());
				LanguageBean langBean = new LanguageBean();
				langBean.entityManager = entityManager;
				List<Languages> langList = langBean.langList();
				profileVO.setLangList(langList);
				profileVO.setLangCode(lang!=null ? lang.getLangCode() : "EN");
//				profileVO.setDateOfBirth(DateUtil.getDateToStringFormat(users.getDateofbirth(), users.getCompany().getDatePattern()));
				if(activeUser.getUsersAddressList() != null && activeUser.getUsersAddressList().size() > 0) {
					UsersAddress userAddr = activeUser.getUsersAddressList().iterator().next();
					profileVO.setAddress(userAddr.getAddress1() != null ? userAddr.getAddress1() : "");
					profileVO.setHouseNo(userAddr.getHouseNo() != null ? userAddr.getHouseNo() : "");
					profileVO.setCity(userAddr.getCity() != null ? userAddr.getCity() : "");
					profileVO.setState(userAddr.getState() != null ? userAddr.getState() : "");
					profileVO.setZip(userAddr.getZip() != null ? userAddr.getZip() : "");
					profileVO.setCountry(userAddr.getCountry() != null ? userAddr.getCountry() : "");
				}
				profileVO.setEmail(users.getPrimaryEmail() != null ? users.getPrimaryEmail() : "");
				profileVO.setMobile(users.getMobile() != null ? users.getMobile() : "");
				profileVO.setTelephone(users.getTelephone() != null ? users.getTelephone() : "");
				UserRole userRole = users.getUserRole();
				if(userRole != null) {
					List<UserRoleLanguage> userRoleLangList = entityManager.createNamedQuery("findRoleLanguageByRoleAndLang")
																		.setParameter("roleId", userRole.getId())
																		.setParameter("langCode", localString)
																		.getResultList();
					for(UserRoleLanguage userRoleLang: userRoleLangList) {
						if(userRoleLang.getLanguages() != null && userRoleLang.getLanguages().getLangCode() != null 
								&& userRoleLang.getLanguages().getLangCode().equalsIgnoreCase(localString)) {
							profileVO.setRole(userRoleLang.getRole());
						}
					}
				}
				PaymentMethod stripePay = null;
				try{
					stripePay = (PaymentMethod) entityManager.createNamedQuery("findPaymentMethodByCompanyAndpaymentType")
																.setParameter("companyId", companyId)
																.setParameter("payTypeId", PayMethodBean.Stripe_Sepa_Id)
																.getSingleResult();
				}catch(Exception e){
					stripePay = null;
				}
				if(stripePay != null)
					profileVO.setStripeConnected(true);
				List<PaymentMethod> payMethodList = entityManager.createNamedQuery("findPaymentMethodByCompany")
													.setParameter("companyId", companyId)
													.getResultList();
				profileVO.setPayMethodList(payMethodList);
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void reset() {
		try {
			profileVO.setCompanyName("");
			profileVO.setCurrencyList(null);
			profileVO.setCurrency(null);
			profileVO.setCompanyAddress("");
			profileVO.setCompanyHouseNo("");
			profileVO.setCompanyCity("");
			profileVO.setCompanyState("");
			profileVO.setCompanyZip("");
			profileVO.setCompanyCountry("");
			profileVO.setCompanyEmail("");
			profileVO.setCompanyMobile("");
			profileVO.setCompanyTelephone("");
			profileVO.setCompanyWebsiteUrl("");
			profileVO.setCompanyLogoUrl("");
			profileVO.setFirstName("");
			profileVO.setLastName("");
			profileVO.setProfessionList(null);
			profileVO.setProfession(null);
			profileVO.setTitleList(null);
			profileVO.setTitle(null);
			profileVO.setLangList(null);
			profileVO.setLangCode("EN");
			profileVO.setDateOfBirth("");
			profileVO.setAddress("");
			profileVO.setHouseNo("");
			profileVO.setCity("");
			profileVO.setState("");
			profileVO.setZip("");
			profileVO.setCountry("");
			profileVO.setEmail("");
			profileVO.setMobile("");
			profileVO.setTelephone("");
			profileVO.setRole("");
			profileVO.setStripeConnected(false);
			profileVO.setEnable(false);
			profileVO.setPaymentType(null);
			profileVO.setPayMethodList(null);
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void persistUserData() {
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			String userFirstName = request.getParameter("userFirstName");
			String userLastName = request.getParameter("userLastName");
			String userLanguage = request.getParameter("userLanguage");
			String userAddress = request.getParameter("userAddress");
			String userHouseNo = request.getParameter("userHouseNo");
			String userCity = request.getParameter("userCity");
			String userCountry = request.getParameter("userCountry");
			String userPinCode = request.getParameter("userPinCode");
			String userEmail = request.getParameter("userEmail");
			String userTelephone = request.getParameter("userTelephone");
			
			Users users = activeUser;
			users.setFirstName(userFirstName);
			users.setLastName(userLastName);
			users.setPrimaryEmail(userEmail);
			users.setTelephone(userTelephone);
			if(users != null && users.getUsersAddressList() != null && users.getUsersAddressList().size() > 0) {
				UsersAddress userAddr = activeUser.getUsersAddressList().iterator().next();
				userAddr.setAddress1(userAddress);
				userAddr.setHouseNo(userHouseNo);
				userAddr.setCity(userCity);
				userAddr.setCountry(userCountry);
				userAddr.setZip(userPinCode);
				entityManager.merge(userAddr);
				entityManager.flush();
				users.getUsersAddressList().add(userAddr);
			}
			if(userLanguage != null && !userLanguage.isEmpty() && NumberUtil.checkNumeric(userLanguage.trim()) && Integer.valueOf(userLanguage.trim()) > 0) {
				Languages lang = entityManager.find(Languages.class, Integer.valueOf(userLanguage.trim()));
				users.setLanguages(lang);
				localeSelector.setLocaleString(lang.getLangCode().toLowerCase());
			}
			entityManager.merge(users);
			entityManager.flush();
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void persistCompanyData() {
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			String orgName = request.getParameter("orgName");
			String orgEmail = request.getParameter("orgEmail");
			String orgTelephone = request.getParameter("orgTelephone");
			String orgAddress = request.getParameter("orgAddress");
			String orgHouseNo = request.getParameter("orgHouseNo");
			String orgCity = request.getParameter("orgCity");
			String orgCountry = request.getParameter("orgCountry");
			String orgPinCode = request.getParameter("orgPinCode");
			String orgCurrency = request.getParameter("orgCurrency");
			String orgWebsiteUrl = request.getParameter("orgWebsiteUrl");
			String fileName = request.getParameter("orgLogoName");
			String source = request.getParameter("orgLogoSource");
			Company company = activeUser.getCompany();
			int companyId = company.getId();
			company.setCompanyName(orgName);
			company.setEmail(orgEmail);
			company.setTelephone(orgTelephone);
			company.setAddress(orgAddress);
			company.setHouseno(orgHouseNo);
			company.setCity(orgCity);
			company.setCountry(orgCountry);
			company.setZip(orgPinCode);
			company.setWebsiteLink(orgWebsiteUrl);
			if(orgCurrency != null && !orgCurrency.isEmpty() && NumberUtil.checkNumeric(orgCurrency.trim()) && Integer.valueOf(orgCurrency.trim()) > 0) {
				Currencies currencies = entityManager.find(Currencies.class, Integer.valueOf(orgCurrency.trim()));
				company.setCurrencies(currencies);
			}
			if(source != null && !source.isEmpty()) {
				String imagePath = MainUtil.getLogoImagePath(companyId);
				// String extension = FilenameUtils.getExtension(fileName); 
				fileName = companyId + ".png";
				String imageUrl = createimage(source, fileName, imagePath);
				imageUrl = MainUtil.getImageSourcePath(activeUser.getCompany().getId() , "Logo") + MainUtil.getFileSeparator() + fileName ;
				company.setLogoUrl(imageUrl);
			}
			entityManager.merge(company);
			entityManager.flush();
			activeUser.setCompany(company);
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public String createimage(String source, String fileName, String imagePath) throws Exception{
		try{
			if(source!=null && !source.isEmpty()){
				if(source.contains("/png;base64") || source.contains("/gif;base64"))
					source = source.substring(22, source.length());
				else
					source = source.substring(23, source.length());
				
				File files = new File(imagePath);
			    if(!files.exists()){
			    	files.mkdirs();
			    }
				byte[] encodedImage = Base64.getDecoder().decode(source);
				String path = imagePath + MainUtil.getFileSeparator() + fileName;
				if(encodedImage!=null){
			        FileOutputStream fos = new FileOutputStream(new File(path));
			        fos.write(encodedImage);
			        fos.flush();
			        fos.close();
				}
				return path;
			}
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return null;
	}
	
	public void validateOldPassword() {
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			response.setContentType("text/html; charset=UTF-8");
			String password = request.getParameter("password");
			if(activeUser != null && password != null && !password.isEmpty()) {
				String encryptPwd = PasswordBean.getInstance().encryptPassword(password);
				if(encryptPwd != null && !encryptPwd.isEmpty() && encryptPwd.equalsIgnoreCase(activeUser.getPassword())) {
					response.getWriter().write("true\n");
				}else {
					response.getWriter().write("error\n");
				}
			}else {
				response.getWriter().write("error\n");
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			try {
				response.getWriter().write("error\n");
			}catch(Exception e1) {
			}
		}
	}
	
	public void setNewPassword() {
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			response.setContentType("text/html; charset=UTF-8");
			String password = request.getParameter("password");
			if(activeUser != null && password != null && !password.isEmpty()) {
				String encryptPwd = PasswordBean.getInstance().encryptPassword(password);
				if(encryptPwd != null && !encryptPwd.isEmpty()) {
					activeUser.setPassword(encryptPwd);
					entityManager.merge(activeUser);
					entityManager.flush();
					response.getWriter().write("true\n");
				}else {
					response.getWriter().write("error\n");
				}
			}else {
				response.getWriter().write("error\n");
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			try {
				response.getWriter().write("error\n");
			}catch(Exception e1) {
				ExceptionMsg.ErrorMsg(e1, Thread.currentThread().getStackTrace()[1]);
			}
		}
	}
	
}
