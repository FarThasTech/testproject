package com.billing.company;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jboss.seam.Seam;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.Messages;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.web.ServletContexts;

import com.billing.commonFile.MailClient;
import com.billing.commonFile.MailContent;
import com.billing.entity.Company;
import com.billing.entity.Currencies;
import com.billing.entity.Plan;
import com.billing.entity.SignUp;
import com.billing.entity.Users;
import com.billing.exceptions.ExceptionMsg;
import com.billing.language.LanguageBean;
import com.billing.login.AuthenticatorBean;
import com.billing.login.CustomIdentity;
import com.billing.login.PasswordBean;
import com.billing.staticvalue.StaticValues;
import com.billing.util.MainUtil;
import com.billing.util.NumberUtil;
import com.itextpdf.text.pdf.qrcode.Encoder;

@SuppressWarnings({"unchecked", "unused", "static-access"})
@Name("signUpBean")
public class SignUpBean {

	@In
	public EntityManager entityManager;
	    
	@In
	public LocaleSelector localeSelector;

	public void sendSignUpLink() {
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();
			response.setContentType("text/html; charset=UTF-8");
			String name = request.getParameter("name");
			String email = request.getParameter("email");
			String orgName = request.getParameter("orgName");
			String currenyId = request.getParameter("currenyId");
			if(name != null && !name.isEmpty() 
					&& email != null && !email.isEmpty() 
					&& orgName != null && !orgName.isEmpty()) {
				List<SignUp> signUpList = entityManager.createNamedQuery("findSignUpByEmail")
												.setParameter("email", email.trim())
												.getResultList();
				if(signUpList != null && signUpList.size() > 0) {
					response.getWriter().write("exist\n");
				}else {
					SignUp signUp = new SignUp();
					signUp.setName(name);
					signUp.setEmail(email);
					Plan plan = entityManager.find(Plan.class, 1);	// Default plan is Trial
					signUp.setPlan(plan);
					signUp.setOrganizationName(orgName);
					if(currenyId != null && !currenyId.isEmpty() 
							&& NumberUtil.checkNumeric(currenyId.trim()) 
							&& Integer.valueOf(currenyId.trim()) > 0) {
						Currencies currencies = entityManager.find(Currencies.class, Integer.valueOf(currenyId.trim()));
						signUp.setCurrencies(currencies);
					}else {
						Currencies currencies = entityManager.find(Currencies.class, 1);	// Default currency is EURO
						signUp.setCurrencies(currencies);
					}
					signUp.setSubscribe(false);
					signUp.setCreatedDate(new Date());
					signUp.setModifiedDate(new Date());
					entityManager.persist(signUp);
					entityManager.flush();
					String encryptedSignUpId = PasswordBean.getInstance().encryptWithMD5DES(String.valueOf(signUp.getId()));
					String url = new MainUtil().getInfoFromProperty("Domain_Name")+"/activation.jsf?signUpId="+encryptedSignUpId;
					
					System.out.println("Activation URL is : " + url);
					MailContent mailContent = new MailContent();
					Map<String, String> messages = Messages.instance();
					try {
						if(localeSelector != null) {
							messages = new LanguageBean().getMessagesPropertiesFromLangCode(localeSelector.getLocaleString().toLowerCase());
						}
					}catch(Exception e) {
						
					}
					mailContent.setMessages(messages);
					Company company = entityManager.find(Company.class, StaticValues.Company_Id);
					String street = company.getAddress();
					String houseNo = company.getHouseno();
					String city = company.getCity();
					String country = company.getCountry();
					String zip = company.getZip();
					
					street = street != null && !street.isEmpty() ? street.trim() : "";
					houseNo = houseNo != null && !houseNo.isEmpty() ? houseNo.trim() : "";
					city = city != null && !city.isEmpty() ? city.trim() : "";
					country = country != null && !country.isEmpty() ? country.trim() : "";
					zip = zip != null && !zip.isEmpty() ? ("- " + zip.trim()) : "";
					
					String message = mailContent.getemailcontentforsignup(name, url, new MainUtil().getInfoFromProperty("App_Name"), street, houseNo, city, country, zip);
					String subject = messages.get("Activate_Signup");
					
					String logo = MainUtil.getLogoImagePath(StaticValues.Company_Id) + StaticValues.Company_Id+".png";
					boolean local = new MainUtil().checkLocal();
					if(local) {
						email = new MainUtil().getInfoFromProperty("Email");
					}
					
					MailClient mailClient = new MailClient();
					if(!local) {
						mailClient.sendMail("info@softitservice.de", email, "", "farthastech1994@gmail.com", subject, message, logo, "");	
					}
					response.getWriter().write("valid\n");
				}
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			try {
				response.getWriter().write("Invalid\n");
			}catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void validateActivation() {
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			response.setContentType("text/html; charset=UTF-8");
			String signUpId = request.getParameter("signUpId");
			if(signUpId != null && !signUpId.isEmpty()) {
				signUpId = PasswordBean.getInstance().decryptWithMD5DES(signUpId.trim());
				if(NumberUtil.checkNumeric(signUpId.trim())) {
					SignUp signUp = entityManager.find(SignUp.class, Integer.valueOf(signUpId.trim()));
					if(signUp != null && !signUp.isSubscribe()) {
						System.out.println("Sign up is valid - " + signUpId);
					}else {
			    		FacesContext.getCurrentInstance().getExternalContext().redirect("activationresult.jsf?msg=register");
					}
				}else {
					FacesContext.getCurrentInstance().getExternalContext().redirect("activationresult.jsf?msg=register");
				}
			}else {
				FacesContext.getCurrentInstance().getExternalContext().redirect("activationresult.jsf?msg=error");
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect("activationresult.jsf?msg=register");
			}catch(Exception e1) {
			}
		}
	}
	
	public void activateSignUpLink() {
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			response.setContentType("text/html; charset=UTF-8");
			String signUpId = request.getParameter("signUpId");
			String password = request.getParameter("password");
			if(signUpId != null && !signUpId.isEmpty()
					&& password != null && !password.isEmpty()) {
				signUpId = PasswordBean.getInstance().decryptWithMD5DES(signUpId.trim());
				if(NumberUtil.checkNumeric(signUpId.trim())) {
					SignUp signUp = entityManager.find(SignUp.class, Integer.valueOf(signUpId.trim()));
					if(signUp != null && !signUp.isSubscribe()) {
						List<Company> companyList = entityManager.createNamedQuery("findAllCompanyWithAssignedOrderByAsc")
																	.setParameter("assigned", false)
																	.getResultList();
						if(companyList != null && companyList.size() > 0) {
							boolean local = new MainUtil().checkLocal();
							Company company = companyList.get(0);
							String name = signUp.getName().trim();	
			    			String[] nameArr = name.split("\\s+");
							String firstName = "", lastName = "", email = signUp.getEmail();
							if(name != null && !name.isEmpty()) {
								if(nameArr.length != 0){
									firstName = nameArr[0].trim();
								}
								if(nameArr.length >= 2){
									lastName = "";
									for(int i=1; i < nameArr.length; i++){
										lastName = lastName.concat(" "+nameArr[i].trim());
									}
								}
							}
							String organizationName = signUp.getOrganizationName();
							company.setFirstName(firstName);
							company.setLastName(lastName);
							company.setEmail(email);
							company.setAssigned(true);
							company.setEnable(true);
							company.setCompanyName(organizationName);
							company.setLiveAccount(local ? false : true);
							company.setSelfStripeAccount(true);
							company.setSendDboxEmail(true);
							company.setSendCCMail(true);
							company.setEnableTransCheckBox(true);
							if(organizationName != null && !organizationName.isEmpty()) {
								if(organizationName.trim().length() > 4) {
									company.setCode(organizationName.substring(0, 4));
								}else {
									company.setCode(organizationName);
								}
							}
							company.setPlan(signUp.getPlan());
							Currencies currencies = signUp.getCurrencies(); 
							if(currencies != null) {
								company.setCurrencies(currencies);
							}else {
								currencies = entityManager.find(Currencies.class, 1);
								company.setCurrencies(currencies);
							}
							company.setCreatedDate(new Date());
							company.setModifiedDate(new Date());
							entityManager.merge(company);
							entityManager.flush();
							
							Users users = company.getUsers();
							users.setFirstName(firstName);
							users.setLastName(lastName);
							users.setPrimaryEmail(email);
							users.setUserName(email);
							users.setPassword(PasswordBean.getInstance().encryptPassword(password));
							users.setCompanyUser(true);
							users.setWritePermission(true);
							users.setCreatedDate(new Date());
							users.setModifiedDate(new Date());
							entityManager.merge(users);
							entityManager.flush();
							
							signUp.setSubscribe(true);
							entityManager.merge(signUp);
							entityManager.flush();
							
							/****** Auto Authenticate ******/
//							autoAuthentication(users);
							/****** Auto Authenticate ******/
							response.getWriter().write("valid\n");
						}else {
							response.getWriter().write("false\n");
						}
					}else {
						response.getWriter().write("false\n");
					}
				}else {
					response.getWriter().write("false\n");
				}
			}else {
				response.getWriter().write("false\n");
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			try {
				response.getWriter().write("false\n");
			}catch(Exception e1) {
			}
		}
	}
	
	public void resetPassword() {
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		try {
			System.out.println("ResetPassword called...!");
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			String email = request.getParameter("email");
			if(email != null && !email.isEmpty()) {
				List<Users> userList = entityManager.createNamedQuery("findUsersByUserNameAndCompanyUser")
														.setParameter("userName", email.trim())
														.getResultList();
				if(userList != null && userList.size() > 0) {
					if(userList.size() == 1) {
						Users users = userList.get(0);
						users.setResetPassword(true);
						entityManager.merge(users);
						entityManager.flush();
						
						String encryptedSignUpId = PasswordBean.getInstance().encryptWithMD5DES(String.valueOf(users.getId()));
						String url = new MainUtil().getInfoFromProperty("Domain_Name")+"/forgotpasswordset.jsf?userId="+encryptedSignUpId;
						MailContent mailContent = new MailContent();
						Map<String, String> messages = Messages.instance();
						try {
							if(localeSelector != null) {
								messages = new LanguageBean().getMessagesPropertiesFromLangCode(localeSelector.getLocaleString().toLowerCase());
							}
						}catch(Exception e) {
							
						}
						mailContent.setMessages(messages);
						
						Company company = users.getCompany();
						String street = company.getAddress();
						String houseNo = company.getHouseno();
						String city = company.getCity();
						String country = company.getCountry();
						String zip = company.getZip();
						
						street = street != null && !street.isEmpty() ? street.trim() : "";
						houseNo = houseNo != null && !houseNo.isEmpty() ? houseNo.trim() : "";
						city = city != null && !city.isEmpty() ? city.trim() : "";
						country = country != null && !country.isEmpty() ? country.trim() : "";
						zip = zip != null && !zip.isEmpty() ? ("- " + zip.trim()) : "";
						
						String message = mailContent.getemailcontentforResetPwd(users.getFirstName(), url,
								company.getCompanyName() , street, houseNo, city, country, zip);
						
						String subject = messages.get("Reset_Password");
						
						String logo = MainUtil.getLogoImagePath(company.getId()) + company.getId() + ".png";
						
						if(new MainUtil().checkLocal()) {
							email = new MainUtil().getInfoFromProperty("Email");
						}
						System.out.println("Reset email is send to : " + email);
						MailClient mailClient = new MailClient();
						mailClient.sendMail("info@softitservice.de", email, "", "farthastech1994@gmail.com", subject, message, logo, "");	
						
						response.getWriter().write("valid\n");
					}else {
						response.getWriter().write("false\n");
					}
				}else {
					response.getWriter().write("false\n");
				}
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			try {
				response.getWriter().write("false\n");
			}catch(Exception e1) {
			}
		}		
	}
	
	public void validateResetPassword() {
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			response.setContentType("text/html; charset=UTF-8");
			String userId = request.getParameter("userId");
			if(userId != null && !userId.isEmpty()) {
				userId = PasswordBean.getInstance().decryptWithMD5DES(userId.trim());
				if(NumberUtil.checkNumeric(userId.trim())) {
					Users users = entityManager.find(Users.class, Integer.valueOf(userId.trim()));
					if(users != null && users.isResetPassword()) {
						System.out.println("User id is valid - " + userId);
					}else {
			    		FacesContext.getCurrentInstance().getExternalContext().redirect("forgotpasswordresult.jsf?msg=expire");
					}
				}else {
		    		FacesContext.getCurrentInstance().getExternalContext().redirect("forgotpasswordresult.jsf?msg=expire");
				}
			}else {
				FacesContext.getCurrentInstance().getExternalContext().redirect("forgotpasswordresult.jsf?msg=expire");
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect("forgotpasswordresult.jsf?msg=expire");
			}catch(Exception e1) {
			}
		}
	}
	
	public void setNewPassword() {
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			response.setContentType("text/html; charset=UTF-8");
			String userId = request.getParameter("userId");
			String password = request.getParameter("password");
			if(userId != null && !userId.isEmpty()) {
				userId = PasswordBean.getInstance().decryptWithMD5DES(userId.trim());
				if(NumberUtil.checkNumeric(userId.trim())) {
					Users users = entityManager.find(Users.class, Integer.valueOf(userId.trim()));
					if(users != null && users.isResetPassword()) {
						System.out.println("User id is valid - " + userId);
						users.setPassword(PasswordBean.getInstance().encryptPassword(password));
						users.setResetPassword(false);
						entityManager.merge(users);
						entityManager.flush();
						response.getWriter().write("reset\n");
					}else if(users != null && !users.isResetPassword()){
						response.getWriter().write("expire\n");
					}else {
						response.getWriter().write("error\n");
					}
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
	
	public void autoAuthentication(Users users) {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			HttpSession session = request.getSession(false);
			if(session != null){
				Identity identity = (Identity) session.getAttribute(Seam.getComponentName(Identity.class));
			    Credentials credentials = (Credentials) session.getAttribute(Seam.getComponentName(Credentials.class));	      
			    credentials.setInitialized(true);
			    credentials.setUsername(users.getUserName());
			    credentials.setPassword(users.getPassword());
			    if(users != null) {
				    AuthenticatorBean aBean = new AuthenticatorBean();
				    aBean.identity = (CustomIdentity) identity;
				    aBean.entityManager = entityManager;
				    aBean.credentials = credentials;	
				    context.getExternalContext().redirect("/login.jsf?actionMethod=login.xhtml:identity.login");
				}else {
					context.getExternalContext().redirect("/login.jsf");  
				}
			}else{
				context.getExternalContext().redirect("/login.jsf");  
			} 
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
}
