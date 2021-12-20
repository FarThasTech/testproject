package com.billing.company;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.international.LocaleSelector;

import com.billing.category.CategoryBean;
import com.billing.entity.Company;
import com.billing.entity.ContactOptions;
import com.billing.entity.Currencies;
import com.billing.entity.Fields;
import com.billing.entity.Languages;
import com.billing.entity.ModuleAccess;
import com.billing.entity.PaymentFee;
import com.billing.entity.PaymentType;
import com.billing.entity.PaymentTypeSub;
import com.billing.entity.ProductSubType;
import com.billing.entity.ProductType;
import com.billing.entity.UserRole;
import com.billing.entity.Users;
import com.billing.entity.UsersAddress;
import com.billing.exceptions.ExceptionMsg;
import com.billing.fields.FieldsBean;
import com.billing.jms.JMSBean;
import com.billing.jms.JMSClient;
import com.billing.jms.JMSVO;
import com.billing.login.PasswordBean;
import com.billing.modules.GrantAccess;
import com.billing.modules.Modules;
import com.billing.paymethod.PayMethodBean;
import com.billing.paystatus.PaymentStatusBean;
import com.billing.role.RoleBean;
import com.billing.staticvalue.StaticValues;
import com.billing.usercreation.UserCreationBean;
import com.billing.util.DateUtil;
import com.billing.util.MainUtil;

@Name("companyCreation")
@SuppressWarnings("unchecked")
public class CompanyCreation {

	@In
	public EntityManager entityManager;
	    
	@In
	public LocaleSelector localeSelector;

	private String moduleId;
	
	private String userModuleId;
	
	public void createDefaultCompany() {
		System.out.println("New Commpany Creation Started");
		
		localeSelector.setLocaleString("EN");
		
		this.setModuleId(null);
		
		this.setUserModuleId(null);
		
		Company company = createCompany();
		
		createModuleAccess(company);
		
		createUser(company);
		
		createRole(company);
		
		createContactOption(company);
		
		createFields(company);
		
		createPaymentMethods(company);
		
		createPaymentStatus(company);
		
		createPaymentFee(company);
		
		createDefaultCategory(company);
		
		System.out.println("New Commpany Creation Done");

	}
	
	public Company createCompany() {
		boolean local = new MainUtil().checkLocal();
		Company company = new Company();
		company.setFirstName("First");
		company.setLastName("Last");
		company.setCompanyName("");
		company.setCode("");
		company.setAddress("");
		company.setHouseno("");
		company.setCity("");
		company.setState("");
		company.setZip("");
		company.setCountry("");
		company.setEmail("");
		company.setTelephone("");
		company.setMobile("");
		company.setCurrencies(entityManager.find(Currencies.class, 1));	// Euro is default currency
		company.setLiveAccount(local ? false : true);
		company.setSelfStripeAccount(true);
		company.setAssigned(false);
		company.setEnable(false);
		company.setAddressShow(true);
		company.setAddressRequired(false);
		company.setPhoneShow(true);
		company.setPhoneRequired(false);
		company.setSendDboxEmail(local ? false : true);
		company.setSendCCMail(local ? false : true);
		company.setEnableTransCheckBox(false);
		company.setCreateFutureEntry(true);
		company.setExecuteRecurring(local ? false : true);
		company.setDatePattern(DateUtil.DATE_FORMAT_DE);
		company.setCreatedDate(new Date());
		company.setModifiedDate(new Date());
		entityManager.persist(company);
		entityManager.flush();
		return company;
	}
	
	public List<String> defaultRole(){
		List<String> role = new ArrayList<String>();
		role.add(StaticValues.Admin);
		role.add(StaticValues.Donor);
		return role;
	}
	
	public void createRole(Company company) {
		try {
			RoleBean roleBean = new RoleBean();
			roleBean.entityManager =entityManager;
			roleBean.localeSelector = localeSelector;
			roleBean.activeUser = company.getUsers();
			roleBean.grants = new ArrayList<GrantAccess>();
			for(String role: defaultRole()) {
				UserRole userRole = new UserRole();
				userRole.setCompany(company);
				userRole.setRole(role);
				userRole.setEnable(true);
				userRole.setRowfreeze(false);
				userRole.setSortCode(1);
				if(role != StaticValues.Admin) {
					userRole.setHideRole(true);
				}
				userRole.setCreatedDate(new Date());
				userRole.setModifiedDate(new Date());
				entityManager.persist(userRole);
				entityManager.flush();
				roleBean.createUserRoleLang(userRole, true);
				roleBean.grantAccess(this.getModuleId());
				roleBean.updateAccess(userRole);
				if(role.equalsIgnoreCase(StaticValues.Admin)) {
					company.getUsers().setUserRole(userRole);
					entityManager.merge(company);
					entityManager.flush();
				}
				/****************** Add Other User Role Language ******************/
				JMSVO jmsVO = new JMSVO();
				JMSBean jmsBean = new JMSBean();
				jmsVO = jmsBean.resetJMSVO(jmsVO);
				jmsVO.setParam("Role");
				jmsVO.setLocaleString(localeSelector.getLocaleString());
				jmsVO.setPrimaryId(String.valueOf(userRole.getId()));
				jmsVO.setUserRole(userRole);
				JMSClient jmsClient = new JMSClient();
				jmsClient.automaticJMS(jmsVO);
//				roleBean.createUserRoleLang(userRole, false);
				/****************** Add Other User Role Language ******************/
			}
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void createCurrencies() {
		try {
			Currencies currencies = new Currencies();
			currencies.setCurrencyCode("EUR");
			currencies.setCurrencyName("EURO");
			currencies.setCurrencySymbol("€");
			currencies.setEnable(true);
			currencies.setSortCode(1);
			currencies.setCreatedDate(new Date());
			currencies.setModifiedDate(new Date());
			entityManager.persist(currencies);
			currencies = new Currencies();
			currencies.setCurrencyCode("USD");
			currencies.setCurrencyName("DOLLAR");
			currencies.setCurrencySymbol("$");
			currencies.setEnable(true);
			currencies.setSortCode(1);
			currencies.setCreatedDate(new Date());
			currencies.setModifiedDate(new Date());
			entityManager.persist(currencies);
			currencies = new Currencies();
			currencies.setCurrencyCode("GBP");
			currencies.setCurrencyName("POUND");
			currencies.setCurrencySymbol("£");
			currencies.setEnable(true);
			currencies.setSortCode(1);
			currencies.setCreatedDate(new Date());
			currencies.setModifiedDate(new Date());
			entityManager.persist(currencies);
			currencies = new Currencies();
			currencies.setCurrencyCode("INR");
			currencies.setCurrencyName("RUPEES");
			currencies.setCurrencySymbol("₹");
			currencies.setEnable(true);
			currencies.setSortCode(1);
			currencies.setCreatedDate(new Date());
			currencies.setModifiedDate(new Date());
			entityManager.persist(currencies);
			entityManager.flush();
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void createUser(Company company) {
		try {
			Users users = new Users();
			users.setFirstName("First");
			users.setLastName("Last");
			users.setPrimaryEmail("first@last.com");
			users.setUserName("admin");
			users.setPassword(PasswordBean.getInstance().encryptPassword("hotdemo123"));
			users.setLanguages(entityManager.find(Languages.class, 2));	// German language is default
			users.setCompany(company);
			users.setLoginAccess(true);
			users.setCompanyUser(true);
			users.setWritePermission(true);
			users.setCreatedUser(users);
			users.setTelephone("");
			users.setMobile("");
			users.setCreatedDate(new Date());
			users.setModifiedDate(new Date());
			users.setUsersType(StaticValues.Company_User);
			entityManager.persist(users);
			entityManager.flush();
			UsersAddress usersAddress = new UsersAddress();
			usersAddress.setUsers(users);
			usersAddress.setAddress1("");
			usersAddress.setHouseNo("");
			usersAddress.setCity("");
			usersAddress.setState("");
			usersAddress.setZip("");
			usersAddress.setCountry("");
			entityManager.persist(usersAddress);
			entityManager.flush();
			entityManager.merge(users);
			entityManager.flush();
			company.setUsers(users);
			entityManager.merge(company);
			entityManager.flush();
			
			UserCreationBean usersBean = new UserCreationBean();
			usersBean.entityManager =entityManager;
			usersBean.localeSelector = localeSelector;
			usersBean.activeUser = company.getUsers();
			usersBean.grants = new ArrayList<GrantAccess>();
			usersBean.grantModule(this.getUserModuleId());
			usersBean.updateModules(users);
			entityManager.merge(users);
			entityManager.flush();
			
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public List<String> defaultContacts(){
		List<String> contacts = new ArrayList<String>();
		try {
			contacts.add(StaticValues.Telephone);
			contacts.add(StaticValues.Mobile);
			contacts.add(StaticValues.Email);
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return contacts;
	}
	
	public void createContactOption(Company company) {
		try {
			int sortCode = 0;
			for(String str: defaultContacts()) {
				ContactOptions contOpt = new ContactOptions();
				contOpt.setCompany(company);
				contOpt.setContactType(str.trim());
				contOpt.setEnable(true);
				contOpt.setRowfreeze(true);
				contOpt.setSortCode(++sortCode);
				contOpt.setCreatedDate(new Date());
				contOpt.setModifiedDate(new Date());
				entityManager.persist(contOpt);
				entityManager.flush();
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public List<String> defaultFields(){
		List<String> contacts = new ArrayList<String>();
		try {
			contacts.add(StaticValues.Gender);
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return contacts;
	}
	
	public List<String> defaultGender(){
		List<String> gender = new ArrayList<String>();
		try {
			gender.add("Male");
			gender.add("Female");
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return gender;
	}
	
	public void createFields(Company company) {
		try {
			FieldsBean fieldsBean = new FieldsBean();
			fieldsBean.entityManager = entityManager;
			fieldsBean.localeSelector = localeSelector;
			fieldsBean.activeUser = company.getUsers();
			for(String str: defaultFields()) {
				if(str.trim().equalsIgnoreCase(StaticValues.Gender)) {
					for(String profession: defaultGender()) {
						Fields fields = new Fields();
						fields.setCompany(company);
						fields.setFieldName(profession.trim());
						fields.setStandardType(str.trim());
						fields.setEnable(true);
						fields.setCreatedDate(new Date());
						fields.setModifiedDate(new Date());
						entityManager.persist(fields);
						entityManager.flush();
						fieldsBean.createFieldsValueLang(fields, profession.trim());
					}
				}
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void createPaymentMethods(Company company) {
		try {
			PayMethodBean payBean = new PayMethodBean();
			payBean.entityManager = entityManager;
			payBean.localeSelector = localeSelector;
			PaymentType payType = entityManager.find(PaymentType.class, 1); 	// Cash Payment
			payBean.createPaymentMethods(payType, company, null);
			/* List<PaymentType> paymentTypeList = payBean.getAllPaymentType();
			if(paymentTypeList != null && paymentTypeList.size() > 0) {
				for(PaymentType payType: paymentTypeList) {
					payBean.createPaymentMethods(payType, company);
				}				
			} */
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void createPaymentStatus(Company company) {
		try {
			PaymentStatusBean payBean = new PaymentStatusBean();
			payBean.entityManager = entityManager;
			payBean.localeSelector = localeSelector;
			for(String str: defaultPaymentStatus()) {
				payBean.createPaymentStatus(str.trim(), company);
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public List<String> defaultPaymentStatus(){
		List<String> payStatus = new ArrayList<String>();
		try {
			payStatus.add(PaymentStatusBean.Success);
			payStatus.add(PaymentStatusBean.Pending);
			payStatus.add(PaymentStatusBean.Failure);
			payStatus.add(PaymentStatusBean.Cancel);
			payStatus.add(PaymentStatusBean.Refund);
			payStatus.add(PaymentStatusBean.RefundCancel);
			payStatus.add(PaymentStatusBean.Dispute);
			payStatus.add(PaymentStatusBean.DisputeResolved);
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return payStatus;
	}
	
	public String defaultModules() {
		try {
			return  Modules.Add + "," + Modules.Edit + "," + Modules.View + "," + Modules.Delete;
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			return null;
		}
	}
	
	private String Administration_Module = Modules.MyTeam.toString() + " , " + Modules.PaymentGateway.toString();
	
	private String Manage_Campaign_Module = Modules.AddCampaign.toString() + " , " + Modules.CampaignList.toString();
	
	private String Manage_Donor_Module = Modules.DonorList.toString();
	
	private String Manage_Finance_Module = Modules.Finance.toString() + " , " + Modules.OnlineDonorList.toString() + " , " + Modules.RecurringList.toString();
	
	private String Manage_WaterWell_Module = Modules.WaterWellList.toString() + "," + Modules.WaterWellCustomerList.toString();
	
	private int DashboardId = 1;
	
	private int AdministrationId = 2;
	
	private int ManageCampaignId = 3;
	
	private int ManageDonorId = 4;
	
	private int ManageFinanceId = 5;
	
	private int ManageWaterWellId = 6;
 	
	
	public void createModuleAccess(Company company) {
		try {
			int sortCode = 0;
			String moduleId = "";
			String userModuleId = "";
			for(Modules module : Modules.values()) {
				ModuleAccess moduleAccess = new ModuleAccess();
				moduleAccess.setCompany(company);
				moduleAccess.setSortCode(++sortCode);
				moduleAccess.setEnable(true);
				moduleAccess.setCreatedDate(new Date());
				moduleAccess.setModifiedDate(new Date());
				moduleAccess.setModules(module);
				
				/********************* Parent Id *********************/ 
				if(defaultModules().contains(module.name())) { 
					moduleAccess.setDefaultAccess(true);
				}else if(module.name().equalsIgnoreCase(Modules.Dashboard.name())) {
					moduleAccess.setParentId(DashboardId);
				}else if(module.name().equalsIgnoreCase(Modules.Administration.name())) {
					moduleAccess.setParentId(AdministrationId);
				}else if(module.name().equalsIgnoreCase(Modules.ManageCampaign.name())) {
					moduleAccess.setParentId(ManageCampaignId);
				}else if(module.name().equalsIgnoreCase(Modules.ManageDonor.name())) {
					moduleAccess.setParentId(ManageDonorId);
				}else if(module.name().equalsIgnoreCase(Modules.ManageFinance.name())) {
					moduleAccess.setParentId(ManageFinanceId);
				}else if(module.name().equalsIgnoreCase(Modules.ManageWaterWell.name())) {
					moduleAccess.setParentId(ManageWaterWellId);
				}
				/********************* Parent Id *********************/
				/********************* Child Id **********************/
				else if(Administration_Module.contains(module.name())) {
					moduleAccess.setChildId(AdministrationId);
				}else if(Manage_Campaign_Module.contains(module.name())) {
					moduleAccess.setChildId(ManageCampaignId);
				}else if(Manage_Donor_Module.contains(module.name())) {
					moduleAccess.setChildId(ManageDonorId);
				}else if(Manage_Finance_Module.contains(module.name())) {
					moduleAccess.setChildId(ManageFinanceId);
				}else if(Manage_WaterWell_Module.contains(module.name())) {
					moduleAccess.setChildId(ManageWaterWellId);
				}
				/********************* Child Id **********************/
				entityManager.persist(moduleAccess);
				entityManager.flush();
				if(moduleAccess.isDefaultAccess())
					moduleId = moduleId + moduleAccess.getId() + ",";
				else
					userModuleId = userModuleId + moduleAccess.getId() + ",";
	        } 
			if(moduleId != null && !moduleId.isEmpty()) {
				moduleId = moduleId.substring(0, moduleId.trim().length() - 1);
				this.setModuleId(moduleId);
			}
			if(userModuleId != null && !userModuleId.isEmpty()) {
				userModuleId = userModuleId.substring(0, userModuleId.trim().length() - 1);
				this.setUserModuleId(userModuleId);
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void createLanguage() {
		try {
			
			System.out.println("Language creation started.");
			
			Languages lang = new Languages();
			lang.setLangName("ENGLISH");
			lang.setLangCode("EN");
			lang.setEnable(true);
			lang.setSortCode(1);
			lang.setCreatedDate(new Date());
			entityManager.persist(lang);
			entityManager.flush();
			
			lang = new Languages();
			lang.setLangName("GERMAN");
			lang.setLangCode("DE");
			lang.setEnable(true);
			lang.setSortCode(2);
			lang.setCreatedDate(new Date());
			entityManager.persist(lang);
			entityManager.flush();
			
			lang = new Languages();
			lang.setLangName("FRENCH");
			lang.setLangCode("FR");
			lang.setEnable(true);
			lang.setSortCode(3);
			lang.setCreatedDate(new Date());
			entityManager.persist(lang);
			entityManager.flush();
			
			lang = new Languages();
			lang.setLangName("TURKISH");
			lang.setLangCode("TR");
			lang.setEnable(true);
			lang.setSortCode(4);
			lang.setCreatedDate(new Date());
			entityManager.persist(lang);
			entityManager.flush();
			
			lang = new Languages();
			lang.setLangName("ARABIC");
			lang.setLangCode("AR");
			lang.setEnable(true);
			lang.setSortCode(5);
			lang.setCreatedDate(new Date());
			entityManager.persist(lang);
			entityManager.flush();
			
			lang = new Languages();
			lang.setLangName("SPANISH");
			lang.setLangCode("ES");
			lang.setEnable(true);
			lang.setSortCode(6);
			lang.setCreatedDate(new Date());
			entityManager.persist(lang);
			entityManager.flush();
			
			System.out.println("Language creation done.");
			
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public Map<String, String> paymentTypeFeeAT() {
		Map<String, String> paymentTypeFeeAT = new HashMap<String, String>();
		paymentTypeFeeAT.put("PayUmoney", "");
		paymentTypeFeeAT.put("PayPal", "1.5 % + 0.35");	
		paymentTypeFeeAT.put("Sofortuberweisung", "");
		paymentTypeFeeAT.put("Bank", "");	
		paymentTypeFeeAT.put("None", "");
		paymentTypeFeeAT.put("Citrus", "");
		paymentTypeFeeAT.put("Micropayment", "");
		paymentTypeFeeAT.put("Cash", "");
		paymentTypeFeeAT.put("Mixed", "");
		paymentTypeFeeAT.put("InstaMojo", "");
		paymentTypeFeeAT.put("BankTransfer", "");
		paymentTypeFeeAT.put("CreditCard", "1.4 % + 0.25");
		paymentTypeFeeAT.put("Giropay", "1.4 % + 0.25");
		paymentTypeFeeAT.put("Ideal", "0.0 % + 0.45");	
		paymentTypeFeeAT.put("Sofort", "1.4 % + 0.25");
		paymentTypeFeeAT.put("CreditCardOffline", "");
		paymentTypeFeeAT.put("Quitung Auf Konto", "");
		paymentTypeFeeAT.put("Per SMS", "");
		paymentTypeFeeAT.put("GiroKrt Einzug", "");
		paymentTypeFeeAT.put("Nicht Definiert", "");
		paymentTypeFeeAT.put("EPS", "1.6 % + 0.25");
		paymentTypeFeeAT.put("Bancontact", "1.4 % + 0.25");
		paymentTypeFeeAT.put("P24", "2.2 % + 0.25");
		paymentTypeFeeAT.put("ApplePay", "1.4 % + 0.25");
		paymentTypeFeeAT.put("GooglePay", "1.4 % + 0.25");
		paymentTypeFeeAT.put("AmazonPay", "2.0 % + 0.35");
		paymentTypeFeeAT.put("Sepa", "0.0 % + 0.35");
		paymentTypeFeeAT.put("GOCARDLESS", "1.0 % + 0.00");
		paymentTypeFeeAT.put("M_directdebit", "0.0 % + 0.25");
		paymentTypeFeeAT.put("M_bancontact", "0.0 % + 0.39");
		paymentTypeFeeAT.put("M_eps", "1.5 % + 0.25");
		paymentTypeFeeAT.put("M_giropay", "1.5 % + 0.25");
		paymentTypeFeeAT.put("M_ideal", "0.0 % + 0.29");
		paymentTypeFeeAT.put("M_sofort", "0.9 % + 0.25");
		paymentTypeFeeAT.put("M_przelewy24", "2.2 % + 0.25");
		paymentTypeFeeAT.put("M_kbc", "0.9 % + 0.25");
		paymentTypeFeeAT.put("M_inghomepay", "0.9 % + 0.25");
		paymentTypeFeeAT.put("M_belfius", "0.9 % + 0.25");
		return paymentTypeFeeAT;
	}
	
	public Map<String, String> paymentTypeFeeBE() {
		Map<String, String> paymentTypeFeeBE = new HashMap<String, String>();
		paymentTypeFeeBE.put("PayUmoney", "");
		paymentTypeFeeBE.put("PayPal", "1.5 % + 0.35");	
		paymentTypeFeeBE.put("Sofortuberweisung", "");
		paymentTypeFeeBE.put("Bank", "");	
		paymentTypeFeeBE.put("None", "");
		paymentTypeFeeBE.put("Citrus", "");
		paymentTypeFeeBE.put("Micropayment", "");
		paymentTypeFeeBE.put("Cash", "");
		paymentTypeFeeBE.put("Mixed", "");
		paymentTypeFeeBE.put("InstaMojo", "");
		paymentTypeFeeBE.put("BankTransfer", "");
		paymentTypeFeeBE.put("CreditCard", "1.4 % + 0.25");
		paymentTypeFeeBE.put("Giropay", "1.4 % + 0.25");
		paymentTypeFeeBE.put("Ideal", "0.0 % + 0.45");	
		paymentTypeFeeBE.put("Sofort", "1.4 % + 0.25");
		paymentTypeFeeBE.put("CreditCardOffline", "");
		paymentTypeFeeBE.put("Quitung Auf Konto", "");
		paymentTypeFeeBE.put("Per SMS", "");
		paymentTypeFeeBE.put("GiroKrt Einzug", "");
		paymentTypeFeeBE.put("Nicht Definiert", "");
		paymentTypeFeeBE.put("EPS", "1.6 % + 0.25");
		paymentTypeFeeBE.put("Bancontact", "1.4 % + 0.25");
		paymentTypeFeeBE.put("P24", "2.2 % + 0.25");
		paymentTypeFeeBE.put("ApplePay", "1.4 % + 0.25");
		paymentTypeFeeBE.put("GooglePay", "1.4 % + 0.25");
		paymentTypeFeeBE.put("AmazonPay", "2.0 % + 0.35");
		paymentTypeFeeBE.put("Sepa", "0.0 % + 0.35");
		paymentTypeFeeBE.put("GOCARDLESS", "1.0 % + 0.00");
		paymentTypeFeeBE.put("M_directdebit", "0.0 % + 0.25");
		paymentTypeFeeBE.put("M_bancontact", "0.0 % + 0.39");
		paymentTypeFeeBE.put("M_eps", "1.5 % + 0.25");
		paymentTypeFeeBE.put("M_giropay", "1.5 % + 0.25");
		paymentTypeFeeBE.put("M_ideal", "0.0 % + 0.29");
		paymentTypeFeeBE.put("M_sofort", "0.9 % + 0.25");
		paymentTypeFeeBE.put("M_przelewy24", "2.2 % + 0.25");
		paymentTypeFeeBE.put("M_kbc", "0.9 % + 0.25");
		paymentTypeFeeBE.put("M_inghomepay", "0.9 % + 0.25");
		paymentTypeFeeBE.put("M_belfius", "0.9 % + 0.25");
		return paymentTypeFeeBE;
	}
	
	public Map<String, String> paymentTypeFeeCA() {
		Map<String, String> paymentTypeFeeCA = new HashMap<String, String>();
		paymentTypeFeeCA.put("PayUmoney", "");
		paymentTypeFeeCA.put("PayPal", "1.5 % + 0.35");	
		paymentTypeFeeCA.put("Sofortuberweisung", "");
		paymentTypeFeeCA.put("Bank", "");	
		paymentTypeFeeCA.put("None", "");
		paymentTypeFeeCA.put("Citrus", "");
		paymentTypeFeeCA.put("Micropayment", "");
		paymentTypeFeeCA.put("Cash", "");
		paymentTypeFeeCA.put("Mixed", "");
		paymentTypeFeeCA.put("InstaMojo", "");
		paymentTypeFeeCA.put("BankTransfer", "");
		paymentTypeFeeCA.put("CreditCard", "2.9 % + 0.30");
		paymentTypeFeeCA.put("Giropay", "1.4 % + 0.30");
		paymentTypeFeeCA.put("Ideal", "0.0 % + 0.80");	
		paymentTypeFeeCA.put("Sofort", "1.4 % + 0.30");
		paymentTypeFeeCA.put("CreditCardOffline", "");
		paymentTypeFeeCA.put("Quitung Auf Konto", "");
		paymentTypeFeeCA.put("Per SMS", "");
		paymentTypeFeeCA.put("GiroKrt Einzug", "");
		paymentTypeFeeCA.put("Nicht Definiert", "");
		paymentTypeFeeCA.put("EPS", "1.6 % + 0.30");
		paymentTypeFeeCA.put("Bancontact", "1.4 % + 0.30");
		paymentTypeFeeCA.put("P24", "2.2 % + 0.30");
		paymentTypeFeeCA.put("ApplePay", "2.9 % + 0.30");
		paymentTypeFeeCA.put("GooglePay", "2.9 % + 0.30");
		paymentTypeFeeCA.put("AmazonPay", "2.0 % + 0.35");
		paymentTypeFeeCA.put("Sepa", "0.8 % + 0.30");
		paymentTypeFeeCA.put("GOCARDLESS", "1.0 % + 0.00");
		paymentTypeFeeCA.put("M_directdebit", "0.0 % + 0.25");
		paymentTypeFeeCA.put("M_bancontact", "0.0 % + 0.39");
		paymentTypeFeeCA.put("M_eps", "1.5 % + 0.25");
		paymentTypeFeeCA.put("M_giropay", "1.5 % + 0.25");
		paymentTypeFeeCA.put("M_ideal", "0.0 % + 0.29");
		paymentTypeFeeCA.put("M_sofort", "0.9 % + 0.25");
		paymentTypeFeeCA.put("M_przelewy24", "2.2 % + 0.25");
		paymentTypeFeeCA.put("M_kbc", "0.9 % + 0.25");
		paymentTypeFeeCA.put("M_inghomepay", "0.9 % + 0.25");
		paymentTypeFeeCA.put("M_belfius", "0.9 % + 0.25");
		return paymentTypeFeeCA;
	}
	
	public Map<String, String> paymentTypeFeeDE() {
		Map<String, String> paymentTypeFeeDE = new HashMap<String, String>();
		paymentTypeFeeDE.put("PayUmoney", "");
		paymentTypeFeeDE.put("PayPal", "1.5 % + 0.35");	
		paymentTypeFeeDE.put("Sofortuberweisung", "");
		paymentTypeFeeDE.put("Bank", "");	
		paymentTypeFeeDE.put("None", "");
		paymentTypeFeeDE.put("Citrus", "");
		paymentTypeFeeDE.put("Micropayment", "");
		paymentTypeFeeDE.put("Cash", "");
		paymentTypeFeeDE.put("Mixed", "");
		paymentTypeFeeDE.put("InstaMojo", "");
		paymentTypeFeeDE.put("BankTransfer", "");
		paymentTypeFeeDE.put("CreditCard", "1.4 % + 0.25");
		paymentTypeFeeDE.put("Giropay", "1.4 % + 0.25");
		paymentTypeFeeDE.put("Ideal", "0.0 % + 0.29");	
		paymentTypeFeeDE.put("Sofort", "1.4 % + 0.25");
		paymentTypeFeeDE.put("CreditCardOffline", "");
		paymentTypeFeeDE.put("Quitung Auf Konto", "");
		paymentTypeFeeDE.put("Per SMS", "");
		paymentTypeFeeDE.put("GiroKrt Einzug", "");
		paymentTypeFeeDE.put("Nicht Definiert", "");
		paymentTypeFeeDE.put("EPS", "1.6 % + 0.25");
		paymentTypeFeeDE.put("Bancontact", "1.4 % + 0.25");
		paymentTypeFeeDE.put("P24", "2.2 % + 0.25");
		paymentTypeFeeDE.put("ApplePay", "1.4 % + 0.25");
		paymentTypeFeeDE.put("GooglePay", "1.4 % + 0.25");
		paymentTypeFeeDE.put("AmazonPay", "2.0 % + 0.35");
		paymentTypeFeeDE.put("Sepa", "0.0 % + 0.35");
		paymentTypeFeeDE.put("GOCARDLESS", "1.0 % + 0.00");
		paymentTypeFeeDE.put("M_directdebit", "0.0 % + 0.25");
		paymentTypeFeeDE.put("M_bancontact", "0.0 % + 0.39");
		paymentTypeFeeDE.put("M_eps", "1.5 % + 0.25");
		paymentTypeFeeDE.put("M_giropay", "1.5 % + 0.25");
		paymentTypeFeeDE.put("M_ideal", "0.0 % + 0.29");
		paymentTypeFeeDE.put("M_sofort", "0.9 % + 0.25");
		paymentTypeFeeDE.put("M_przelewy24", "2.2 % + 0.25");
		paymentTypeFeeDE.put("M_kbc", "0.9 % + 0.25");
		paymentTypeFeeDE.put("M_inghomepay", "0.9 % + 0.25");
		paymentTypeFeeDE.put("M_belfius", "0.9 % + 0.25");
		return paymentTypeFeeDE;
	}
	
	public Map<String, String> paymentTypeFeeGB() {
		Map<String, String> paymentTypeFeeGB = new HashMap<String, String>();
		paymentTypeFeeGB.put("PayUmoney", "");
		paymentTypeFeeGB.put("PayPal", "1.5 % + 0.35");	
		paymentTypeFeeGB.put("Sofortuberweisung", "");
		paymentTypeFeeGB.put("Bank", "");	
		paymentTypeFeeGB.put("None", "");
		paymentTypeFeeGB.put("Citrus", "");
		paymentTypeFeeGB.put("Micropayment", "");
		paymentTypeFeeGB.put("Cash", "");
		paymentTypeFeeGB.put("Mixed", "");
		paymentTypeFeeGB.put("InstaMojo", "");
		paymentTypeFeeGB.put("BankTransfer", "");
		paymentTypeFeeGB.put("CreditCard", "1.4 % + 0.20");
		paymentTypeFeeGB.put("Giropay", "1.4 % + 0.20");
		paymentTypeFeeGB.put("Ideal", "0.0 % + 0.40");	
		paymentTypeFeeGB.put("Sofort", "1.4 % + 0.20");
		paymentTypeFeeGB.put("CreditCardOffline", "");
		paymentTypeFeeGB.put("Quitung Auf Konto", "");
		paymentTypeFeeGB.put("Per SMS", "");
		paymentTypeFeeGB.put("GiroKrt Einzug", "");
		paymentTypeFeeGB.put("Nicht Definiert", "");
		paymentTypeFeeGB.put("EPS", "1.6 % + 0.20");
		paymentTypeFeeGB.put("Bancontact", "1.4 % + 0.20");
		paymentTypeFeeGB.put("P24", "2.2 % + 0.20");
		paymentTypeFeeGB.put("ApplePay", "1.4 % + 0.20");
		paymentTypeFeeGB.put("GooglePay", "1.4 % + 0.20");
		paymentTypeFeeGB.put("AmazonPay", "2.0 % + 0.35");
		paymentTypeFeeGB.put("Sepa", "1.0 % + 0.20");
		paymentTypeFeeGB.put("GOCARDLESS", "1.0 % + 0.00");
		paymentTypeFeeGB.put("M_directdebit", "0.0 % + 0.25");
		paymentTypeFeeGB.put("M_bancontact", "0.0 % + 0.39");
		paymentTypeFeeGB.put("M_eps", "1.5 % + 0.25");
		paymentTypeFeeGB.put("M_giropay", "1.5 % + 0.25");
		paymentTypeFeeGB.put("M_ideal", "0.0 % + 0.29");
		paymentTypeFeeGB.put("M_sofort", "0.9 % + 0.25");
		paymentTypeFeeGB.put("M_przelewy24", "2.2 % + 0.25");
		paymentTypeFeeGB.put("M_kbc", "0.9 % + 0.25");
		paymentTypeFeeGB.put("M_inghomepay", "0.9 % + 0.25");
		paymentTypeFeeGB.put("M_belfius", "0.9 % + 0.25");
		return paymentTypeFeeGB;
	}
	
	public Map<String, String> paymentTypeFeeIN() {
		Map<String, String> paymentTypeFeeIN = new HashMap<String, String>();
		paymentTypeFeeIN.put("PayUmoney", "");
		paymentTypeFeeIN.put("PayPal", "1.5 % + 0.35");	
		paymentTypeFeeIN.put("Sofortuberweisung", "");
		paymentTypeFeeIN.put("Bank", "");	
		paymentTypeFeeIN.put("None", "");
		paymentTypeFeeIN.put("Citrus", "");
		paymentTypeFeeIN.put("Micropayment", "");
		paymentTypeFeeIN.put("Cash", "");
		paymentTypeFeeIN.put("Mixed", "");
		paymentTypeFeeIN.put("InstaMojo", "");
		paymentTypeFeeIN.put("BankTransfer", "");
		paymentTypeFeeIN.put("CreditCard", "2.0 % + 0.00");
		paymentTypeFeeIN.put("Giropay", "");
		paymentTypeFeeIN.put("Ideal", "");	
		paymentTypeFeeIN.put("Sofort", "");
		paymentTypeFeeIN.put("CreditCardOffline", "");
		paymentTypeFeeIN.put("Quitung Auf Konto", "");
		paymentTypeFeeIN.put("Per SMS", "");
		paymentTypeFeeIN.put("GiroKrt Einzug", "");
		paymentTypeFeeIN.put("Nicht Definiert", "");
		paymentTypeFeeIN.put("EPS", "");
		paymentTypeFeeIN.put("Bancontact", "");
		paymentTypeFeeIN.put("P24", "");
		paymentTypeFeeIN.put("ApplePay", "2.0 % + 0.00");
		paymentTypeFeeIN.put("GooglePay", "2.0 % + 0.00");
		paymentTypeFeeIN.put("AmazonPay", "2.0 % + 0.35");
		paymentTypeFeeIN.put("Sepa", "");
		paymentTypeFeeIN.put("GOCARDLESS", "1.0 % + 0.00");
		paymentTypeFeeIN.put("M_directdebit", "0.0 % + 0.25");
		paymentTypeFeeIN.put("M_bancontact", "0.0 % + 0.39");
		paymentTypeFeeIN.put("M_eps", "1.5 % + 0.25");
		paymentTypeFeeIN.put("M_giropay", "1.5 % + 0.25");
		paymentTypeFeeIN.put("M_ideal", "0.0 % + 0.29");
		paymentTypeFeeIN.put("M_sofort", "0.9 % + 0.25");
		paymentTypeFeeIN.put("M_przelewy24", "2.2 % + 0.25");
		paymentTypeFeeIN.put("M_kbc", "0.9 % + 0.25");
		paymentTypeFeeIN.put("M_inghomepay", "0.9 % + 0.25");
		paymentTypeFeeIN.put("M_belfius", "0.9 % + 0.25");
		return paymentTypeFeeIN;
	}
	
	public Map<String, String> paymentTypeFeeNL() {
		Map<String, String> paymentTypeFeeNL = new HashMap<String, String>();
		paymentTypeFeeNL.put("PayUmoney", "");
		paymentTypeFeeNL.put("PayPal", "1.5 % + 0.35");
		paymentTypeFeeNL.put("Sofortuberweisung", "");
		paymentTypeFeeNL.put("Bank", "");	
		paymentTypeFeeNL.put("None", "");
		paymentTypeFeeNL.put("Citrus", "");
		paymentTypeFeeNL.put("Micropayment", "");
		paymentTypeFeeNL.put("Cash", "");
		paymentTypeFeeNL.put("Mixed", "");
		paymentTypeFeeNL.put("InstaMojo", "");
		paymentTypeFeeNL.put("BankTransfer", "");
		paymentTypeFeeNL.put("CreditCard", "1.4 % + 0.25");
		paymentTypeFeeNL.put("Giropay", "1.4 % + 0.25");
		paymentTypeFeeNL.put("Ideal", "0.0 % + 0.29");	
		paymentTypeFeeNL.put("Sofort", "1.4 % + 0.25");
		paymentTypeFeeNL.put("CreditCardOffline", "");
		paymentTypeFeeNL.put("Quitung Auf Konto", "");
		paymentTypeFeeNL.put("Per SMS", "");
		paymentTypeFeeNL.put("GiroKrt Einzug", "");
		paymentTypeFeeNL.put("Nicht Definiert", "");
		paymentTypeFeeNL.put("EPS", "1.6 % + 0.25");
		paymentTypeFeeNL.put("Bancontact", "1.4 % + 0.25");
		paymentTypeFeeNL.put("P24", "2.2 % + 0.25");
		paymentTypeFeeNL.put("ApplePay", "1.4 % + 0.25");
		paymentTypeFeeNL.put("GooglePay", "1.4 % + 0.25");
		paymentTypeFeeNL.put("AmazonPay", "2.0 % + 0.35");
		paymentTypeFeeNL.put("Sepa", "0.0 % + 0.35");
		paymentTypeFeeNL.put("GOCARDLESS", "1.0 % + 0.00");
		paymentTypeFeeNL.put("M_directdebit", "0.0 % + 0.25");
		paymentTypeFeeNL.put("M_bancontact", "0.0 % + 0.39");
		paymentTypeFeeNL.put("M_eps", "1.5 % + 0.25");
		paymentTypeFeeNL.put("M_giropay", "1.5 % + 0.25");
		paymentTypeFeeNL.put("M_ideal", "0.0 % + 0.29");
		paymentTypeFeeNL.put("M_sofort", "0.9 % + 0.25");
		paymentTypeFeeNL.put("M_przelewy24", "2.2 % + 0.25");
		paymentTypeFeeNL.put("M_kbc", "0.9 % + 0.25");
		paymentTypeFeeNL.put("M_inghomepay", "0.9 % + 0.25");
		paymentTypeFeeNL.put("M_belfius", "0.9 % + 0.25");
		return paymentTypeFeeNL;
	}
	
	public Map<String, String> paymentTypeFeeUS() {
		Map<String, String> paymentTypeFeeUS = new HashMap<String, String>();
		paymentTypeFeeUS.put("PayUmoney", "");
		paymentTypeFeeUS.put("PayPal", "1.5 % + 0.35");	
		paymentTypeFeeUS.put("Sofortuberweisung", "");
		paymentTypeFeeUS.put("Bank", "");	
		paymentTypeFeeUS.put("None", "");
		paymentTypeFeeUS.put("Citrus", "");
		paymentTypeFeeUS.put("Micropayment", "");
		paymentTypeFeeUS.put("Cash", "");
		paymentTypeFeeUS.put("Mixed", "");
		paymentTypeFeeUS.put("InstaMojo", "");
		paymentTypeFeeUS.put("BankTransfer", "");
		paymentTypeFeeUS.put("CreditCard", "2.9 % + 0.30");
		paymentTypeFeeUS.put("Giropay", "1.4 % + 0.30");
		paymentTypeFeeUS.put("Ideal", "0.0 % + 0.80");	
		paymentTypeFeeUS.put("Sofort", "1.4 % + 0.30");
		paymentTypeFeeUS.put("CreditCardOffline", "");
		paymentTypeFeeUS.put("Quitung Auf Konto", "");
		paymentTypeFeeUS.put("Per SMS", "");
		paymentTypeFeeUS.put("GiroKrt Einzug", "");
		paymentTypeFeeUS.put("Nicht Definiert", "");
		paymentTypeFeeUS.put("EPS", "1.6 % + 0.30");
		paymentTypeFeeUS.put("Bancontact", "1.4 % + 0.30");
		paymentTypeFeeUS.put("P24", "2.2 % + 0.30");
		paymentTypeFeeUS.put("ApplePay", "2.9 % + 0.30");
		paymentTypeFeeUS.put("GooglePay", "2.9 % + 0.30");
		paymentTypeFeeUS.put("AmazonPay", "2.0 % + 0.35");
		paymentTypeFeeUS.put("Sepa", "0.8 % + 0.30");
		paymentTypeFeeUS.put("GOCARDLESS", "1.0 % + 0.00");
		paymentTypeFeeUS.put("M_directdebit", "0.0 % + 0.25");
		paymentTypeFeeUS.put("M_bancontact", "0.0 % + 0.39");
		paymentTypeFeeUS.put("M_eps", "1.5 % + 0.25");
		paymentTypeFeeUS.put("M_giropay", "1.5 % + 0.25");
		paymentTypeFeeUS.put("M_ideal", "0.0 % + 0.29");
		paymentTypeFeeUS.put("M_sofort", "0.9 % + 0.25");
		paymentTypeFeeUS.put("M_przelewy24", "2.2 % + 0.25");
		paymentTypeFeeUS.put("M_kbc", "0.9 % + 0.25");
		paymentTypeFeeUS.put("M_inghomepay", "0.9 % + 0.25");
		paymentTypeFeeUS.put("M_belfius", "0.9 % + 0.25");
		return paymentTypeFeeUS;
	}
	
	public void defaultDatabaseDetails() {
		try {
			createLanguage();
			createCurrencies();
			createPaymentType();
			createPaymentTypeSub();
			createProductType();
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void createPaymentTypeSub() {
		try {
			String[] stripeCountryList = {"AT","BE","CA","DE","GB","IN","NL","US"};
			List<PaymentType> paymentTypeList = entityManager.createNamedQuery("findAllPaymentType").getResultList();
			System.out.println("Payment Type Sub creation started.");
			for(String country: stripeCountryList) {
				String stripeConvertionFee = "";
				Map<String, String> paymentTypeFee = new HashMap<String, String>();
				if(country.equalsIgnoreCase("AT")) {
					paymentTypeFee = paymentTypeFeeAT();
				}else if(country.equalsIgnoreCase("BE")) {
					paymentTypeFee = paymentTypeFeeBE();
				}else if(country.equalsIgnoreCase("CA")) {
					paymentTypeFee = paymentTypeFeeCA();
					stripeConvertionFee = "1.5 % + 0.00";
				}else if(country.equalsIgnoreCase("DE")) {
					paymentTypeFee = paymentTypeFeeDE();
				}else if(country.equalsIgnoreCase("GB")) {
					paymentTypeFee = paymentTypeFeeGB();
				}else if(country.equalsIgnoreCase("IN")) {
					paymentTypeFee = paymentTypeFeeIN();
				}else if(country.equalsIgnoreCase("NL")) {
					paymentTypeFee = paymentTypeFeeNL();
				}else if(country.equalsIgnoreCase("US")) {
					paymentTypeFee = paymentTypeFeeUS();
					stripeConvertionFee = "1.5 % + 0.00";
				}
					
				for(PaymentType payType: paymentTypeList) {
					List<PaymentTypeSub> payTypeSubList = entityManager.createNamedQuery("findAllPaymentTypeSubByPaymentTypeAndCountry")
																	.setParameter("payTypeId", payType.getId())
																	.setParameter("country", country)
																	.getResultList();
					if(payTypeSubList == null || (payTypeSubList != null && payTypeSubList.size() == 0)) {
						PaymentTypeSub paySub = new PaymentTypeSub();
						paySub.setCountry(country);
						paySub.setPaymentType(payType);
						paySub.setPaymentFee(paymentTypeFee.get(payType.getPaymentName()));
						if(payType.getPaymentType() != null && payType.getPaymentType().equalsIgnoreCase("STRIPE"))
							paySub.setStripeConvertionFee(stripeConvertionFee);
						else
							paySub.setStripeConvertionFee("");
						paySub.setCreatedDate(new Date());
						paySub.setModifiedDate(new Date());
						entityManager.persist(paySub);
						entityManager.flush();
					}else {
						System.out.println("Payment Type Sub already created for this payment type : " + payType.getPaymentType() + ". country is : "+country);
					}
				}
			}
			System.out.println("Payment Type Sub creation done.");
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void createPaymentFee() {
		Company company = entityManager.find(Company.class, 1);
		createPaymentFee(company);
	}

	public void createPaymentFee(Company company) {
		List<PaymentType> paymentTypeList = entityManager.createNamedQuery("findAllPaymentType").getResultList();
		for(PaymentType payType: paymentTypeList) {
			PaymentFee pFee = new PaymentFee();
			pFee.setCompany(company);
			pFee.setFeeNotTakenOver("");
			pFee.setFeeTakenOver("");
			pFee.setPaymentType(payType);
			entityManager.persist(pFee);
			entityManager.flush();
		}
	}
	
	public void createDefaultCategory(Company company) {
		try {
			CategoryBean catBean = new CategoryBean();
			catBean.entityManager = entityManager;
			catBean.activeUser = company.getUsers();
			catBean.localeSelector = localeSelector;
			catBean.persist("General", "General Category", null, null);
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);	
		}
	}
	
	public void createProductType() {
		try {
			System.out.println("Product Type creation started.");
			
			ProductType prodType = new ProductType();
			prodType.setTypeName("Amount");
			prodType.setTypeDescription("Can give unlimited amount");
			prodType.setEnable(true);
			prodType.setCreatedDate(new Date());
			prodType.setModifiedDate(new Date());
			prodType.setSortCode(1);
			entityManager.persist(prodType);
			
			prodType = new ProductType();
			prodType.setTypeName("Quantity");
			prodType.setTypeDescription("Incremental by quantity");
			prodType.setEnable(true);
			prodType.setCreatedDate(new Date());
			prodType.setModifiedDate(new Date());
			prodType.setSortCode(2);
			entityManager.persist(prodType);
			
			prodType = new ProductType();
			prodType.setTypeName("Recurring");
			prodType.setTypeDescription("Can give unlimited amount");
			prodType.setEnable(true);
			prodType.setCreatedDate(new Date());
			prodType.setModifiedDate(new Date());
			prodType.setSortCode(3);
			entityManager.persist(prodType);
			entityManager.flush();
			
			ProductSubType prodSubType = new ProductSubType();
			prodSubType.setProductType(prodType);
			prodSubType.setSubTypeName("OneTime");
			prodSubType.setSubTypeDescription("OneTime Transaction");
			prodSubType.setEnable(true);
			prodSubType.setCreatedDate(new Date());
			prodSubType.setModifiedDate(new Date());
			prodSubType.setSortCode(1);
			entityManager.persist(prodSubType);
			
			prodSubType = new ProductSubType();
			prodSubType.setProductType(prodType);
			prodSubType.setSubTypeName("Monthly");
			prodSubType.setSubTypeDescription("Monthly Transaction");
			prodSubType.setEnable(true);
			prodSubType.setCreatedDate(new Date());
			prodSubType.setModifiedDate(new Date());
			prodSubType.setSortCode(2);
			entityManager.persist(prodSubType);
			
			prodSubType = new ProductSubType();
			prodSubType.setProductType(prodType);
			prodSubType.setSubTypeName("Annually");
			prodSubType.setSubTypeDescription("Annually Transaction");
			prodSubType.setEnable(false);
			prodSubType.setCreatedDate(new Date());
			prodSubType.setModifiedDate(new Date());
			prodSubType.setSortCode(3);
			entityManager.persist(prodSubType);
			entityManager.flush();
			
			prodSubType = new ProductSubType();
			prodSubType.setProductType(prodType);
			prodSubType.setSubTypeName("Quarterly");
			prodSubType.setSubTypeDescription("Quarterly Transaction");
			prodSubType.setEnable(false);
			prodSubType.setCreatedDate(new Date());
			prodSubType.setModifiedDate(new Date());
			prodSubType.setSortCode(4);
			entityManager.persist(prodSubType);
			entityManager.flush();
			
			prodSubType = new ProductSubType();
			prodSubType.setProductType(prodType);
			prodSubType.setSubTypeName("Halfyearly");
			prodSubType.setSubTypeDescription("Halfyearly Transaction");
			prodSubType.setEnable(false);
			prodSubType.setCreatedDate(new Date());
			prodSubType.setModifiedDate(new Date());
			prodSubType.setSortCode(5);
			entityManager.persist(prodSubType);
			entityManager.flush();
			
			prodSubType = new ProductSubType();
			prodSubType.setProductType(prodType);
			prodSubType.setSubTypeName("Weekly");
			prodSubType.setSubTypeDescription("Weekly Transaction");
			prodSubType.setEnable(false);
			prodSubType.setCreatedDate(new Date());
			prodSubType.setModifiedDate(new Date());
			prodSubType.setSortCode(6);
			entityManager.persist(prodSubType);
			entityManager.flush();
			
			prodType = new ProductType();
			prodType.setTypeName("ManageWaterWell");
			prodType.setTypeDescription("WaterWell Management Type");
			prodType.setEnable(true);
			prodType.setCreatedDate(new Date());
			prodType.setModifiedDate(new Date());
			prodType.setSortCode(4);
			entityManager.persist(prodType);
			entityManager.flush();
			
			prodType = new ProductType();
			prodType.setTypeName("OrphanManagement");
			prodType.setTypeDescription("Orphan Management Type");
			prodType.setEnable(true);
			prodType.setCreatedDate(new Date());
			prodType.setModifiedDate(new Date());
			prodType.setSortCode(5);
			entityManager.persist(prodType);
			entityManager.flush();
			
			System.out.println("Product Type creation done.");
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);	
		}
	}
	
	public void createPaymentType() {
		try {
			System.out.println("Payment Type creation started.");
			
			PaymentType paymentType = new PaymentType();
			paymentType.setPaymentName("Cash");
			paymentType.setPaymentType("Cash");
			paymentType.setSupportCurrency("EUR,USD,GBP,INR");
			paymentType.setSupportRecurring(true);
			paymentType.setOtherReuccring(false);
			paymentType.setCreatedDate(new Date());
			paymentType.setModifiedDate(new Date());
			entityManager.persist(paymentType);
			
			paymentType = new PaymentType();
			paymentType.setPaymentName("Sepa");
			paymentType.setPaymentType("Stripe");
			paymentType.setSupportCurrency("EUR");
			paymentType.setSupportRecurring(true);
			paymentType.setOtherReuccring(false);
			paymentType.setCreatedDate(new Date());
			paymentType.setModifiedDate(new Date());
			entityManager.persist(paymentType);
			
			paymentType = new PaymentType();
			paymentType.setPaymentName("CreditCard");
			paymentType.setPaymentType("Stripe");
			paymentType.setSupportCurrency("EUR,USD,GBP,INR");
			paymentType.setSupportRecurring(true);
			paymentType.setOtherReuccring(true);
			paymentType.setCreatedDate(new Date());
			paymentType.setModifiedDate(new Date());
			entityManager.persist(paymentType);
			
			paymentType = new PaymentType();
			paymentType.setPaymentName("Sofort");
			paymentType.setPaymentType("Stripe");
			paymentType.setSupportCurrency("EUR");
			paymentType.setSupportRecurring(true);
			paymentType.setOtherReuccring(true);
			paymentType.setCreatedDate(new Date());
			paymentType.setModifiedDate(new Date());
			entityManager.persist(paymentType);
			
			paymentType = new PaymentType();
			paymentType.setPaymentName("Ideal");
			paymentType.setPaymentType("Stripe");
			paymentType.setSupportCurrency("EUR");
			paymentType.setSupportRecurring(true);
			paymentType.setOtherReuccring(true);
			paymentType.setCreatedDate(new Date());
			paymentType.setModifiedDate(new Date());
			entityManager.persist(paymentType);
			
			paymentType = new PaymentType();
			paymentType.setPaymentName("Bancontact");
			paymentType.setPaymentType("Stripe");
			paymentType.setSupportCurrency("EUR");
			paymentType.setSupportRecurring(true);
			paymentType.setOtherReuccring(true);
			paymentType.setCreatedDate(new Date());
			paymentType.setModifiedDate(new Date());
			entityManager.persist(paymentType);
			
			paymentType = new PaymentType();
			paymentType.setPaymentName("ApplePay");
			paymentType.setPaymentType("Stripe");
			paymentType.setSupportCurrency("EUR");
			paymentType.setSupportRecurring(true);
			paymentType.setOtherReuccring(true);
			paymentType.setCreatedDate(new Date());
			paymentType.setModifiedDate(new Date());
			entityManager.persist(paymentType);
			
			paymentType = new PaymentType();
			paymentType.setPaymentName("GooglePay");
			paymentType.setPaymentType("Stripe");
			paymentType.setSupportCurrency("EUR");
			paymentType.setSupportRecurring(true);
			paymentType.setOtherReuccring(true);
			paymentType.setCreatedDate(new Date());
			paymentType.setModifiedDate(new Date());
			entityManager.persist(paymentType);
			
			paymentType = new PaymentType();
			paymentType.setPaymentName("Giropay");
			paymentType.setPaymentType("Stripe");
			paymentType.setSupportCurrency("EUR");
			paymentType.setSupportRecurring(false);
			paymentType.setOtherReuccring(false);
			paymentType.setCreatedDate(new Date());
			paymentType.setModifiedDate(new Date());
			entityManager.persist(paymentType);
			
			paymentType = new PaymentType();
			paymentType.setPaymentName("EPS");
			paymentType.setPaymentType("Stripe");
			paymentType.setSupportCurrency("EUR");
			paymentType.setSupportRecurring(false);
			paymentType.setOtherReuccring(false);
			paymentType.setCreatedDate(new Date());
			paymentType.setModifiedDate(new Date());
			entityManager.persist(paymentType);
			
			paymentType = new PaymentType();
			paymentType.setPaymentName("P24");
			paymentType.setPaymentType("Stripe");
			paymentType.setSupportCurrency("EUR");
			paymentType.setSupportRecurring(false);
			paymentType.setOtherReuccring(false);
			paymentType.setCreatedDate(new Date());
			paymentType.setModifiedDate(new Date());
			entityManager.persist(paymentType);
			entityManager.flush();
			
			System.out.println("Payment Type creation done.");	
			
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public String getUserModuleId() {
		return userModuleId;
	}

	public void setUserModuleId(String userModuleId) {
		this.userModuleId = userModuleId;
	}
	    
}
