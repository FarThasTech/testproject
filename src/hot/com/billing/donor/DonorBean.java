package com.billing.donor;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.Messages;
import org.jboss.seam.web.ServletContexts;

import com.billing.entity.Company;
import com.billing.entity.ContactOptions;
import com.billing.entity.Currencies;
import com.billing.entity.FieldsValue;
import com.billing.entity.FundDetails;
import com.billing.entity.FundGroup;
import com.billing.entity.Languages;
import com.billing.entity.PaymentStatus;
import com.billing.entity.UserAccountDetails;
import com.billing.entity.UserRole;
import com.billing.entity.Users;
import com.billing.entity.UsersAddress;
import com.billing.entity.UsersContact;
import com.billing.exceptions.ExceptionMsg;
import com.billing.fields.FieldsBean;
import com.billing.language.LanguageBean;
import com.billing.paystatus.PaymentStatusBean;
import com.billing.staticvalue.StaticValues;
import com.billing.util.DateUtil;
import com.billing.util.NumberUtil;

@Scope(ScopeType.SESSION)
@Name("donorBean")
@SuppressWarnings("unchecked")
public class DonorBean {

	@In
	public EntityManager entityManager;
	    
	@In
	public LocaleSelector localeSelector;
	
	@In(create=true,required=false) 
    public Users activeUser;
	
	@In(create=true)
	@Out(scope=ScopeType.SESSION)
	public DonorVO donorVO;
	
	private List<DonorVO> donorVOFilterList;
	
	public void execute() {
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			String param = request.getParameter("param");
			if(param != null && !param.isEmpty()) {
				if(param.equalsIgnoreCase("save")) {
					persist();
				}else if(param.equalsIgnoreCase("update")) {
					update();
				}else if(param.equalsIgnoreCase("redirect")) {
					redirectToDetails();
				}
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void init() {
		try {
			String localString = localeSelector.getLocaleString();
			FieldsBean fieldBean = new FieldsBean();
			fieldBean.entityManager = entityManager;
			List<FieldsValue> genderList = fieldBean.fieldsValueListDependsOnFiels(StaticValues.Gender, localString);
			donorVO.setGenderList(genderList);
			if(genderList != null && genderList.size() > 0) {
				donorVO.setGenderId(genderList.get(0).getFields().getId());
			}
			LanguageBean langBean = new LanguageBean();
			langBean.entityManager = entityManager;
			List<Languages> langList = langBean.langList();
			donorVO.setLangList(langList);
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void resetDonorVal() {
		try {
			donorVO.setGenderList(null);
			donorVO.setGenderId(0);
			donorVO.setLangList(null);
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}

	public List<DonorVO> donorList(){
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			String param = request.getParameter("param");
			if(param != null && !param.isEmpty()) {
				donorVO.setDonorList(getList(StaticValues.Donor));
			}
			return donorVO.getDonorList();
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			return null;
		}
	}
	
	public List<DonorVO> getDonorList(){
		try {
			return getList(StaticValues.Donor);
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			return null;
		}
	}
	
	public List<DonorVO> getList(String param){
		try {
			int companyId = activeUser.getCompany().getId();
			List<PaymentStatus> paymentStatusList = entityManager.createNamedQuery("findPaymentStatusByCompanyAndStatusType")
					.setParameter("companyId", companyId)
					.setParameter("statusType", PaymentStatusBean.Success)
					.getResultList();
			PaymentStatus payStatus = null;
			if(paymentStatusList != null && paymentStatusList.size() > 0) {
				payStatus = paymentStatusList.get(0); 
			}
			String payStatQuery = "";
			if(payStatus != null) {
				payStatQuery = " and id_paymentstatus = " + payStatus.getId();
			}
			
			entityManager.createNativeQuery("drop VIEW IF EXISTS fundlist").executeUpdate();
			StringBuilder queryString = new StringBuilder(); 
			queryString.append("create or replace view fundlist as ");
			queryString.append("select fg.id as fundGroupId, u.id as userId, COALESCE(u.first_name,'') as firstName, COALESCE(u.last_name,'') as lastName, "
					+ " COALESCE(uAddr.address_1,'') as address, COALESCE(uAddr.city,'') as city, COALESCE(uAddr.country,'') as country ,"
					+ " COALESCE(uAddr.house_no,'') as houseNo, COALESCE(uAddr.zip,'') as zip, COALESCE(uAddr.state,'') as state,"
					+ " CONVERT(COALESCE(uAddr.id, ''), CHAR(50))  as uAddrId,"
					+ " COALESCE(la.lang_code, '') as lang, CONVERT(COALESCE(la.id, ''), CHAR(50))  as langid,"
					+ " COALESCE(DATE_FORMAT(u.date_of_birth,'%d.%m.%Y'),NULL) as dateofbirth , u.user_nr as userNr, "
					+ " u.primary_email as email, u.telephone as telephone, COALESCE(cu.first_name,'') as createdUser, "
					
					+ " CONVERT(ROUND(COALESCE((select sum(amount) from fund_details where installment IS NOT NULL and installment != ''"
					+ " and ((installment not LIKE '%/%' and CONVERT(installment , UNSIGNED integer) <= 1) or installment LIKE '%/%') and deleted=false " + payStatQuery
					+ " and id_fundGroup = fd.id_fundGroup),0),2), CHAR(50)) as amount, "
					
					+ " CONVERT(ROUND(COALESCE((select (sum(trans_fee) + sum(app_fee)) from fund_details where installment IS NOT NULL and installment != ''"
					+ " and ((installment not LIKE '%/%' and CONVERT(installment , UNSIGNED integer) <= 1) or installment LIKE '%/%') and deleted = false " + payStatQuery
					+ " and trans_taken_over = true and id_fundGroup = fd.id_fundGroup),0),2), CHAR(50)) as wholeFee "

					+ " from fund_group fg "
					+ " LEFT JOIN users as u ON fg.id_users = u.id "
					+ " LEFT JOIN users as cu ON fg.id_createduser = cu.id "
					+ " LEFT JOIN languages as la ON u.id_languages = la.id "
					+ " LEFT JOIN users_address as uAddr ON u.id = uAddr.id_users "
					+ " LEFT JOIN fund_details as fd ON fg.id = fd.id_fundGroup  "
					+ " where fg.deleted = false and fg.id_company = " + companyId);
			
			queryString.append(" group by fg.id order by fd.transaction_date desc");
			
			entityManager.createNativeQuery(queryString.toString()).executeUpdate();
	
			String query = "select fundGroupId, userId, firstName, lastName, address, houseNo, city, state, country," 
									+ " zip, uAddrId, lang, langid, dateofbirth, userNr, email, telephone,"
									+ " createdUser, amount, wholeFee from fundlist ORDER BY fundGroupId desc";
			Session session = entityManager.unwrap(Session.class);
			SQLQuery sqlquery = session.createSQLQuery(query);
			sqlquery.setResultTransformer( Transformers.aliasToBean(DonorVO.class) );
			List<DonorVO> list = sqlquery.list();
			ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1],
					( param + " list size is : " + list.size() + ". Company Id is : " + companyId));
			return list;
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			return null;
		}
	}
	
	public void persist() {
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			String genderId = request.getParameter("genderId");
			String firstName = request.getParameter("firstName");
			String lastName = request.getParameter("lastName");
			String email = request.getParameter("email");
			String telephone = request.getParameter("telephone");
			String dateOfBirth = request.getParameter("dateOfBirth");
			String langId = request.getParameter("langId");
			String street = request.getParameter("address");
			String houseNo = request.getParameter("houseNo");
			String city = request.getParameter("city");
			String state = request.getParameter("state");
			String country = request.getParameter("country");
			String zip = request.getParameter("zip");
			if(firstName != null && !firstName.isEmpty() 
					&& lastName != null && !lastName.isEmpty()) {
				Languages languages = null;
				if(langId != null && !langId.isEmpty()) {
					languages = entityManager.find(Languages.class, Integer.valueOf(langId));
				}else {
					languages = entityManager.find(Languages.class, 1); 	// 1 is english language
				}
				
				/********************* Persist Users Data *********************/
				Users users = persistOrUpateUser(new Users(), firstName, lastName, languages, dateOfBirth, email, telephone, genderId);
				/********************* Persist Users Data *********************/
				
				/******************* Persist FundGroup Data *******************/
				persistFundGroup(users);
				/******************* Persist FundGroup Data *******************/
				
				/****************** Persist User Address Data *****************/
				persistUserAddress(users, street, houseNo, city, state, country, zip);
				/****************** Persist User Address Data *****************/
				
				/****************** Persist User Contact Data *****************/
				persistUserContact(users, email, StaticValues.Email);
				
				persistUserContact(users, telephone, StaticValues.Telephone);
				/****************** Persist User Contact Data *****************/
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public Users persistOrUpateUser(Users users, String firstName, String lastName,
			Languages languages, String dateOfBirth, String email, String telephone,
			String genderId) {
		try {
			UserRole userRole = entityManager.find(UserRole.class, 1);
			users.setFirstName(firstName);
			users.setLastName(lastName);
			users.setPrimaryEmail(email);
			users.setTelephone(telephone);
			users.setWritePermission(false);
			users.setCompanyUser(false);
			users.setLoginAccess(false);
			users.setLanguages(languages);
			users.setDateofbirth(DateUtil.getStringToDateFormat(dateOfBirth, activeUser.getCompany().getDatePattern()));
			users.setCompany(activeUser.getCompany());
			users.setCreatedUser(activeUser);
			users.setUserRole(userRole);
			users.setCreatedDate(new Date());
			users.setModifiedDate(new Date());
			entityManager.persist(users);
			String userNr = activeUser.getCompany().getCode();
			users.setUserNr(userNr != null ? userNr.concat(String.valueOf(users.getId())) : String.valueOf(users.getId()));
			entityManager.merge(users);
			entityManager.flush();
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return users;
	}
	
	public FundGroup persistFundGroup(Users users) {
		FundGroup fundGroup = new FundGroup();
		try {
			fundGroup.setDonateUser(users);
			fundGroup.setCreatedUser(activeUser);
			fundGroup.setCompany(activeUser.getCompany());
			fundGroup.setCreatedDate(new Date());
			fundGroup.setModifiedDate(new Date());
			entityManager.persist(fundGroup);
			entityManager.flush();
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return fundGroup;
	}
	
	public UsersAddress persistUserAddress(Users users,String street, String houseNo,
									String city, String state, String country, String zip) {
		UsersAddress userAddress = null;
		try {
			if(users != null && ((street != null && !street.isEmpty()) || (houseNo != null && !houseNo.isEmpty()) ||
					(city != null && !city.isEmpty()) || (state != null && !state.isEmpty()) ||
					(country != null && !country.isEmpty()) || (zip != null && !zip.isEmpty()))) {
				userAddress = new UsersAddress();
				userAddress.setAddress1(street);
				userAddress.setHouseNo(houseNo);
				userAddress.setCity(city);
				userAddress.setState(state);
				userAddress.setCountry(country);
				userAddress.setZip(zip);
				userAddress.setUsers(users);
				userAddress.setCreatedDate(new Date());
				userAddress.setModifiedDate(new Date());
				entityManager.persist(userAddress);
				entityManager.flush();
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return userAddress;
	}
	
	public void persistUserContact(Users users, String contactDetails, String contactType) {
		try {
			List<ContactOptions> contactOptions = entityManager.createNamedQuery("findContactOptionsByCompanyAndContactType")
					.setParameter("companyId", activeUser.getCompany().getId())
					.setParameter("contactType", contactType)
					.getResultList();
			if(contactOptions != null && contactOptions.size() > 0
					&& contactDetails != null && !contactDetails.isEmpty()) {
				UsersContact usersContact = new UsersContact();
				usersContact.setCompany(activeUser.getCompany());
				usersContact.setUsers(users);
				usersContact.setContactDetails(contactDetails);
				usersContact.setContactOptions(contactOptions.get(0));
				usersContact.setCreatedDate(new Date());
				usersContact.setModifiedDate(new Date());
				entityManager.persist(usersContact);
				entityManager.flush();
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void update() {
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			String usersId = request.getParameter("usersId");
			String genderId = request.getParameter("genderId");
			String fundGroupId = request.getParameter("fundGroupId");
			String index = request.getParameter("index");
			String firstName = request.getParameter("firstName");
			String lastName = request.getParameter("lastName");
			String email = request.getParameter("email");
			String telephone = request.getParameter("telephone");
			String langId = request.getParameter("langId");
			String uAddrId = request.getParameter("uAddrId");
			String street = request.getParameter("address");
			String houseNo = request.getParameter("houseNo");
			String city = request.getParameter("city");
			String state = request.getParameter("state");
			String country = request.getParameter("country");
			String zip = request.getParameter("zip");
			if(usersId != null && !usersId.isEmpty()) {
				Users users = entityManager.find(Users.class, Integer.valueOf(usersId.trim()));
				if(users != null){
					/********************** Users Update **********************/
					String oldEmail = users.getPrimaryEmail();
					String oldTelephone = users.getTelephone();
					users.setFirstName(firstName);
					users.setLastName(lastName);
					users.setPrimaryEmail(email);
					users.setTelephone(telephone);
					Languages languages = null;
					if(langId != null && !langId.isEmpty()) {
						languages = entityManager.find(Languages.class, Integer.valueOf(langId));
					}else {
						languages = entityManager.find(Languages.class, 1); 	// 1 is english language
					}
					users.setLanguages(languages);
					entityManager.merge(users);
					entityManager.flush();
					/********************** Users Update **********************/
					/****************** UsersContact Update *******************/
					if(oldEmail != null && !oldEmail.isEmpty()) {
						List<UsersContact> usersContactList = entityManager.createNamedQuery("findUsersContactByUsersContactTypeAndContactDetails")
																	.setParameter("usersId", users.getId())
																	.setParameter("contactType", StaticValues.Email)
																	.setParameter("contactDetails", oldEmail.trim())
																	.getResultList();
						if(usersContactList != null && usersContactList.size() > 0) {
							updateUsersContact(usersContactList.get(0), email);
						}
					}else if(email != null && !email.isEmpty()){
						persistUserContact(users, email, StaticValues.Email);
					}
					
					if(oldTelephone != null && !oldTelephone.isEmpty()) {
						List<UsersContact> usersContactList = entityManager.createNamedQuery("findUsersContactByUsersContactTypeAndContactDetails")
								.setParameter("usersId", users.getId())
								.setParameter("contactType", StaticValues.Telephone)
								.setParameter("contactDetails", oldTelephone.trim())
								.getResultList();
						if(usersContactList != null && usersContactList.size() > 0) {
							updateUsersContact(usersContactList.get(0), telephone);
						}
					}else if(telephone != null && !telephone.isEmpty()){
						persistUserContact(users, telephone, StaticValues.Telephone);
					}

					/****************** UsersContact Update *******************/
					
					/****************** UsersAddress Update *******************/
					if(uAddrId != null && !uAddrId.isEmpty() 
							&& NumberUtil.checkNumeric(uAddrId.trim()) 
							&& Integer.valueOf(uAddrId.trim()) > 0) {
						UsersAddress userAddress = entityManager.find(UsersAddress.class, Integer.valueOf(uAddrId.trim()));
						userAddress.setAddress1(street);
						userAddress.setHouseNo(houseNo);
						userAddress.setCity(city);
						userAddress.setState(state);
						userAddress.setCountry(country);
						userAddress.setZip(zip);
						userAddress.setUsers(users);
						userAddress.setModifiedDate(new Date());
						entityManager.merge(userAddress);
						entityManager.flush();
					}else {
						UsersAddress userAddress = persistUserAddress(users, street, houseNo, city, state, country, zip);
						if(userAddress != null) {
							uAddrId = String.valueOf(userAddress.getId());
						}
					}
					/****************** UsersAddress Update *******************/
					String lastColumn = "<a href=\"javascript:void(0);\" title=\""+Messages.instance().get("Edit")+"\" " + 
							"onclick=\"editDonor('"+ usersId.trim() + "','" + (index) + "','" + (firstName) + "','" + (lastName) +
							"','" + (street) + "','" + (houseNo) +  "','" + (state) + "','" + (city) + "','" + (country) + "','" + (zip) + 
							"','" + (email) + "','" + (telephone) + "','" + (langId) + "','" + (genderId) + "','" + (uAddrId) +
							"','" + (uAddrId) +"' )\"" + 
							"data-toggle=\"modal\" data-target=\"#addNewDonor\" >" + 
							"<i data-feather=\"check-circle\" class=\"text-primary\" ></i>" +
							"</a>";
					String moreInfo = "<a href=\"details.jsf\" title=\""+Messages.instance().get("MoreInfo")+"\" " + 
							"onclick=\"donorDetails('"+ fundGroupId +"')\" >" + 
							"<i data-feather=\"x-circle\" class=\"text-danger\"></i>" +
							"</a>";
					response.setContentType("text/html; charset=UTF-8");
					response.getWriter().write(lastColumn +" "+ moreInfo +"\n");
				}else {
					response.getWriter().write("\n");
				}
			}else {
				response.getWriter().write("\n");
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			try {
				HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
				response.getWriter().write("\n");
			} catch (IOException e1) {
				ExceptionMsg.ErrorMsg(e1, Thread.currentThread().getStackTrace()[1]);
			}
		}
	}
	
	public void updateUsersContact(UsersContact usersContact, String contactDetails) {
		if(usersContact != null) {
			usersContact.setContactDetails(contactDetails);
			entityManager.merge(usersContact);
			entityManager.flush();
		}
	}
	
	public void redirectToDetails() {
		try {
			resetRedirectPageVal();
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			String fundGroupId = request.getParameter("fundGroupId");
			if(fundGroupId != null && !fundGroupId.isEmpty() 
					&& NumberUtil.checkNumeric(fundGroupId.trim())
					&& Integer.valueOf(fundGroupId.trim()) > 0) {
				int fundId = Integer.valueOf(fundGroupId.trim()); 
				FundGroup fundGroup = entityManager.find(FundGroup.class, fundId);
				if(fundGroup != null) {
					Users users = fundGroup.getDonateUser();
					Company company = users.getCompany();
					if(users != null) {
						donorVO.setFirstName(users.getFirstName());
						donorVO.setLastName(users.getLastName());
						
						LanguageBean langBean = new LanguageBean();
						langBean.entityManager = entityManager;
						List<Languages> langList = langBean.langList();
						donorVO.setLangList(langList);
						donorVO.setLangid(String.valueOf(users.getLanguages() != null ? users.getLanguages().getId() : 0));
						donorVO.setEmail(users.getPrimaryEmail());
						donorVO.setTelephone(users.getTelephone());
						List<UsersAddress> usersAddressList = entityManager.createNamedQuery("findUsersAddressByUsersAndNotDeleted")
																	.setParameter("usersId", users.getId())
																	.getResultList();
						if(usersAddressList != null && usersAddressList.size() > 0) {
							UsersAddress usersAddr = usersAddressList.get(0);
							donorVO.setUserAddrId(usersAddr.getId());
							donorVO.setAddress(usersAddr.getAddress1());
							donorVO.setHouseNo(usersAddr.getHouseNo());
							donorVO.setState(usersAddr.getState());
							donorVO.setZip(usersAddr.getZip());
							donorVO.setCity(usersAddr.getCity());
							donorVO.setCountry(usersAddr.getCountry());
						}
						List<UserAccountDetails> userAccDetailsList = entityManager.createNamedQuery("findUserAccountDetailsByUserWithPaymentType")
																							.setParameter("userId", users.getId())
																							.setParameter("payTypeId", 2)
																							.getResultList();
						if(userAccDetailsList != null && userAccDetailsList.size() > 0) {
							List<DonorVO> userAccVOList = new ArrayList<DonorVO>();
							EncryptDecrypt encryptDecrypt = new EncryptDecrypt();
							Cipher cipherEncrypt = encryptDecrypt.InitiateDecryptProcess();
							for(UserAccountDetails userAccDet : userAccDetailsList) {
								DonorVO userAccVO = new DonorVO();
								userAccVO.setUserAccId(userAccDet.getId());
								userAccVO.setAccName(userAccDet.getAccountName());
								String iban = userAccDet.getIbanCode();
								if(iban != null && !iban.trim().isEmpty()) {
									String decryptedIban = encryptDecrypt.decryptData(cipherEncrypt, iban.trim());
									if(decryptedIban != null && !decryptedIban.trim().isEmpty()) {
										userAccVO.setIban(ibanLastDigit(decryptedIban));
									}
								}
								userAccVOList.add(userAccVO);
							}
							donorVO.setUserAccountList(userAccVOList);
						}
						
						List<FundDetails> fundDetailsList = entityManager.createNamedQuery("findFundDetailsByFundGroupAndWithoutMainEntry")
																	.setParameter("fundGroupId", fundGroup.getId())
																	.getResultList();
						if(fundDetailsList != null && fundDetailsList.size() > 0) {
							Currencies currencies = activeUser.getCompany().getCurrencies();
							String beforeCurr = "", afterCurr = "";
							if(currencies != null && currencies.getCurrencyCode() != null && currencies.getCurrencyCode().trim().equalsIgnoreCase("EUR")) {
								afterCurr = currencies.getCurrencySymbol();
							} else {
								beforeCurr = currencies.getCurrencySymbol();
							}
							List<DonorVO> fundDetailsVOList = new ArrayList<DonorVO>();
							Locale locale = new Locale(localeSelector.getLocaleString());
							NumberFormat currencyFormatter = NumberFormat.getNumberInstance(locale);
							currencyFormatter.setMinimumFractionDigits(2);
							currencyFormatter.setMaximumFractionDigits(2);
							for(FundDetails fDetail : fundDetailsList) {
								DonorVO fDetailVO = new DonorVO();
								fDetailVO.setFundDetailId(fDetail.getId());
								fDetailVO.setQuantity(fDetail.getQuantity());
								fDetailVO.setAmount(beforeCurr + " " + currencyFormatter.format(fDetail.getAmount().doubleValue()) + " " +  afterCurr);
								fDetailVO.setTotalAmount(beforeCurr + " " + currencyFormatter.format(fDetail.getAmount().doubleValue()) + " " +  afterCurr);
								fDetailVO.setTransactionDate(DateUtil.getDateToStringFormat(fDetail.getTransactionDate(), company.getDatePattern()));
								fDetailVO.setTransactionCode(fDetail.getTransactionCode());
								fDetailVO.setPaymentMethodName(fDetail.getPaymentMethod().getPaymentName());
								fDetailVO.setPaymentStatusName(fDetail.getPaymentStatus().getStatusType());
								fDetailVO.setCampaignName(fDetail.getProductGroup().getProduct().getProductName());
								fundDetailsVOList.add(fDetailVO);
							}
							if(fundDetailsVOList != null && fundDetailsVOList.size() > 0) {
								Collections.sort(fundDetailsVOList, new Comparator<DonorVO>() {
									public int compare(DonorVO o1, DonorVO o2) {
										return o2.getTransactionDate().compareTo(o1.getTransactionDate());
									}
								});
							}
							donorVO.setFundDetailsList(fundDetailsVOList);
						}
					}
				}
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public String ibanLastDigit(String decryptedIban) {
		try {
			if(decryptedIban != null && !decryptedIban.trim().isEmpty()) {
				decryptedIban = decryptedIban.trim();
				int decryptedIbanLength = decryptedIban.length();
				if(decryptedIbanLength > 4) {
					String str = "";
					for(int i = 1 ; i < decryptedIbanLength - 5 ; i++) {
						str = str + "*";
						if(decryptedIbanLength > 20 && (i == 4 || i == 8 || i == 12 || i == 16 || i == 20)) {
							str = str + " - ";
						}
					}
					str = str + decryptedIban.substring(decryptedIbanLength - 4 , decryptedIbanLength);
					return str;
				}
			}
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return decryptedIban;
	}
	
	public void resetRedirectPageVal() {
		try {
			donorVO.setFirstName("");
			donorVO.setLastName("");
			donorVO.setLangList(null);
			donorVO.setLang(activeUser.getLanguages().getLangCode());
			donorVO.setEmail("");
			donorVO.setTelephone("");
			donorVO.setAddress("");
			donorVO.setHouseNo("");
			donorVO.setCity("");
			donorVO.setCountry("");
			donorVO.setZip("");
			donorVO.setUserAccountList(null);
			donorVO.setFundDetailsList(null);
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}

	public List<DonorVO> getDonorVOFilterList() {
		return donorVOFilterList;
	}

	public void setDonorVOFilterList(List<DonorVO> donorVOFilterList) {
		this.donorVOFilterList = donorVOFilterList;
	}

}
