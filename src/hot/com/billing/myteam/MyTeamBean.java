package com.billing.myteam;

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

import com.billing.entity.Company;
import com.billing.entity.Languages;
import com.billing.entity.ModuleAccess;
import com.billing.entity.UserRole;
import com.billing.entity.UserRoleLanguage;
import com.billing.entity.Users;
import com.billing.exceptions.ExceptionMsg;
import com.billing.language.LanguageBean;
import com.billing.login.PasswordBean;
import com.billing.modules.GrantAccess;
import com.billing.role.RoleBean;
import com.billing.usercreation.UserCreationBean;
import com.billing.util.NumberUtil;
import com.billing.util.StringUtil;

@Scope(ScopeType.SESSION)
@Name("myTeamBean")
@SuppressWarnings("unchecked")
public class MyTeamBean {

	@In
	public EntityManager entityManager;

	@In
	public LocaleSelector localeSelector;

	@In(create = true, required = false)
	public Users activeUser;
	
	@In(create=true)
	@Out(scope=ScopeType.SESSION)
	public MyTeamVO myTeamVO;
	
	public void showTeam() {
		List<Users> teamList = new ArrayList<Users>();
		try {
			teamList = entityManager.createNamedQuery("findUsersByCompanyUser")
														.setParameter("companyId", activeUser.getCompany().getId())
														.getResultList();
			myTeamVO.setTeamList(teamList);
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void resetValue() {
		try {
			myTeamVO.setLangList(null);
			myTeamVO.setUserRoleLangList(null);
			myTeamVO.setLangCode("");
			myTeamVO.setUserRoleId(0);
			myTeamVO.setTeamList(null);
			myTeamVO.setModuleAccessList(null);
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void showValues() {
		try {
			resetValue();
			showTeam();
			LanguageBean langBean = new LanguageBean();
			langBean.entityManager = entityManager;
			List<Languages> langList = langBean.langList();
			myTeamVO.setLangList(langList);
			RoleBean roleBean = new RoleBean();
			roleBean.entityManager = entityManager;
			roleBean.activeUser = activeUser;
			roleBean.localeSelector = localeSelector;
			List<UserRoleLanguage> userRoleLang = roleBean.userRoleLangList();
			myTeamVO.setUserRoleLangList(userRoleLang);
			if(userRoleLang != null && userRoleLang.size() > 0) {
				myTeamVO.setUserRoleId(userRoleLang.get(0).getUserRole().getId());
			}
			myTeamVO.setLangCode(localeSelector != null ? localeSelector.getLocaleString().toUpperCase() : "EN");
			HttpServletRequest request =ServletContexts.instance().getRequest();
			String roleId = request.getParameter("roleId");
			if(roleId != null && !roleId.trim().isEmpty() && NumberUtil.checkNumeric(roleId.trim()) && Integer.valueOf(roleId.trim()) > 0) {
				myTeamVO.setUserRoleId(Integer.valueOf(roleId.trim()));
			}
			List<ModuleAccess> moduleAccessList = entityManager.createNamedQuery("findAllModuleAccessByCompanyWithParentIdNotNull")
																	.setParameter("companyId", activeUser.getCompany().getId())
																	.getResultList();
			myTeamVO.setModuleAccessList(moduleAccessList);
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void persistUserData() {
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			response.setContentType("text/html; charset=UTF-8");
			String userFirstName = request.getParameter("userFirstName");
			String userLastName = request.getParameter("userLastName");
			String userRole = request.getParameter("userRole");
			String userLanguage = request.getParameter("userLanguage");
			String userEmail = request.getParameter("userEmail");
			String userTelephone = request.getParameter("userTelephone");
			String userPassword = request.getParameter("userPassword");
			String moduleId = request.getParameter("moduleId"); 
			if(userFirstName != null && !userFirstName.trim().isEmpty() 
					&& userLastName != null && !userLastName.trim().isEmpty()
					&& ((userRole != null && !userRole.trim().isEmpty()
						&& NumberUtil.checkNumeric(userRole.trim()) && Integer.valueOf(userRole.trim()) > 0)
						|| myTeamVO.getUserRoleId() > 0)
					&& ((userLanguage != null && !userLanguage.trim().isEmpty() 
						&& NumberUtil.checkNumeric(userLanguage.trim()) && Integer.valueOf(userLanguage.trim()) > 0)
						|| (myTeamVO.getLangCode() != null && !myTeamVO.getLangCode().isEmpty()))
					&& userEmail != null && !userEmail.trim().isEmpty()
					&& userPassword != null && !userPassword.trim().isEmpty()) {
				int companyId = activeUser.getCompany().getId();
				List<Users> usersList = entityManager.createNamedQuery("findUsersByCompanyAndEmail")
																.setParameter("companyId", companyId)
																.setParameter("email", userEmail)
																.getResultList();
				StringBuffer childIdBuffer = new StringBuffer();
				if(StringUtil.checkStringIsNull(moduleId)) {
					if(moduleId.trim().contains(",")) {
						String[] moduleIds = moduleId.split(",");
						for(String mId: moduleIds) {
							if(StringUtil.checkStringIsNull(mId) && NumberUtil.checkNumeric(mId) && Integer.valueOf(mId) > 0) {
								ModuleAccess moduleAccess = entityManager.find(ModuleAccess.class, Integer.valueOf(mId));
								if(moduleAccess != null && moduleAccess.getParentId() != null) {
									List<ModuleAccess> moduleAccessList = entityManager.createNamedQuery("findAllModuleAccessByCompanyWithChildId")
																					.setParameter("companyId", companyId)
																					.setParameter("childId", moduleAccess.getParentId())
																					.getResultList();
									if(moduleAccessList != null && moduleAccessList.size() > 0) {
										for(ModuleAccess mAcc : moduleAccessList) {
											childIdBuffer.append(mAcc.getId()).append(",");
										}
									}
								}
							}
						}
					}
				} else {
					moduleId = "";
				}
				
				if(childIdBuffer != null) {
					String childId = childIdBuffer.toString();
					if(StringUtil.checkStringIsNull(childId)) {
						childId = childId.substring(0, (childId.length()-1));
						moduleId = moduleId + "," + childId;
					}
				}
				
				System.out.println(moduleId);
				
				Users users = null;
				boolean createUser = true;
				if(usersList != null && usersList.size() > 0) {
					users = usersList.get(0);
					createUser = false;
					if(users != null && !users.isCompanyUser()) {
						users.setPassword(PasswordBean.getInstance().encryptPassword(userPassword.trim()));
						users.setCompanyUser(true);
						updateModulePermission(users, moduleId);
						entityManager.merge(users);
						entityManager.flush();
						response.getWriter().write("true\n");
					}else {
						response.getWriter().write("AlreadyExist\n");
					}
				}
				if(createUser) {
					users = new Users();
					Company company = activeUser.getCompany();
					users.setFirstName(userFirstName);
					users.setLastName(userLastName);
					users.setPrimaryEmail(userEmail);
					users.setTelephone(userTelephone);
					users.setUserName(userEmail);
					users.setPassword(PasswordBean.getInstance().encryptPassword(userPassword.trim()));
					if(userLanguage != null && !userLanguage.isEmpty() && NumberUtil.checkNumeric(userLanguage.trim()) && Integer.valueOf(userLanguage.trim()) > 0) {
						Languages lang = entityManager.find(Languages.class, Integer.valueOf(userLanguage.trim()));
						users.setLanguages(lang);
					} else if(myTeamVO.getLangCode() != null && !myTeamVO.getLangCode().isEmpty()){
						LanguageBean langBean = new LanguageBean();
						langBean.entityManager = entityManager;
						Languages lang = langBean.getLangFromLangCode(myTeamVO.getLangCode().trim());
						users.setLanguages(lang);
					} else {
						users.setLanguages(activeUser.getLanguages());
					}
					
					if(userRole != null && !userRole.trim().isEmpty() && NumberUtil.checkNumeric(userRole.trim()) && Integer.valueOf(userRole.trim()) > 0) {
						UserRole uRole = entityManager.find(UserRole.class, Integer.valueOf(userRole.trim()));
						users.setUserRole(uRole);
					} else {
						UserRole uRole = entityManager.find(UserRole.class, myTeamVO.getUserRoleId());
						users.setUserRole(uRole);
					}
					users.setCompanyUser(true);
					users.setCompany(company);
					users.setCreatedDate(new Date());
					users.setModifiedDate(new Date());
					users.setLoginAccess(true);
					users.setWritePermission(true);
					entityManager.persist(users);
					entityManager.flush();
					updateModulePermission(users, moduleId);
					String code = company != null && company.getCode() != null && !company.getCode().trim().isEmpty() ? company.getCode().trim() : "";
					users.setUserNr(code+users.getId());
					entityManager.merge(users);
					entityManager.flush();
					response.getWriter().write("true\n");
				}
			}else {
				response.getWriter().write("false\n");
			}
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void updateModulePermission(Users users, String moduleId) {
		try {
			UserCreationBean usersBean = new UserCreationBean();
			usersBean.entityManager =entityManager;
			usersBean.localeSelector = localeSelector;
			usersBean.activeUser = activeUser;
			usersBean.grants = new ArrayList<GrantAccess>();
			usersBean.grantModule(moduleId);
			usersBean.updateModules(users);
			entityManager.merge(users);
			entityManager.flush();
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	
	public void persistNewRole() {
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			response.setContentType("text/html; charset=UTF-8");
			String roleName = request.getParameter("roleName");
			if(roleName != null && !roleName.trim().isEmpty()) {
				List<UserRole> userRoleList = entityManager.createNamedQuery("findUserRoleByCompanyAndRole")
																.setParameter("companyId", activeUser.getCompany().getId())
																.setParameter("role", roleName.trim().toUpperCase())
																.getResultList();
				if(userRoleList != null && userRoleList.size() > 0) {
					response.getWriter().write("AlreadyExist\n");
				}else {
					RoleBean roleBean = new RoleBean();
					roleBean.entityManager = entityManager;
					roleBean.activeUser = activeUser;
					roleBean.localeSelector = localeSelector;
					UserRole userRole = roleBean.persist(false);
					if(userRole != null) {
						response.getWriter().write(userRole.getId()+"\n");
					}else {
						response.getWriter().write("false\n");						
					}
				}
			} else {
				response.getWriter().write("false\n");
			}
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
}
