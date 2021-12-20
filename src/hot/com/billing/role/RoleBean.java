package com.billing.role;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.Messages;
import org.jboss.seam.web.ServletContexts;

import com.billing.commonFile.Translator;
import com.billing.entity.Languages;
import com.billing.entity.ModuleAccess;
import com.billing.entity.UserRole;
import com.billing.entity.UserRoleLanguage;
import com.billing.entity.Users;
import com.billing.exceptions.ExceptionMsg;
import com.billing.jms.JMSBean;
import com.billing.jms.JMSClient;
import com.billing.jms.JMSVO;
import com.billing.language.LanguageBean;
import com.billing.modules.GrantAccess;
import com.billing.modules.Modules;
import com.billing.usercreation.UserCreationBean;

@Scope(ScopeType.SESSION)
@Name("roleBean")
@SuppressWarnings({"unchecked", "static-access"})
public class RoleBean {

	@In
	public EntityManager entityManager;
	    
	@In
	public LocaleSelector localeSelector;
	
	@In(create=true,required=false) 
    public Users activeUser;
	
	@In(create=true)
	@Out(scope=ScopeType.SESSION)
	public RoleVO roleVO;
	
	@DataModel
	public List<GrantAccess> grants = new ArrayList<GrantAccess>();
	
	public void execute() {
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			String param = request.getParameter("param");
			if(param != null && !param.isEmpty()) {
				if(param.equalsIgnoreCase("save")) {
					persist(true);
				}else if(param.equalsIgnoreCase("update")) {
					update();
				}
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void init() {
		try {
			resetRoleDetails();
			List<UserRole> userRoleList = entityManager.createNamedQuery("findUserRoleByCompany")
					.setParameter("companyId", activeUser.getCompany().getId())
					.getResultList();
			String localeString = localeSelector.getLocaleString();
			if(userRoleList != null && userRoleList.size() > 0) {
				List<RoleVO> roleVOList = new ArrayList<RoleVO>();
				for(UserRole userRole: userRoleList) {
					if(userRole != null) {
						RoleVO roleVal = new RoleVO();
						String role = getUserRoleByLang(userRole, localeString);
						roleVal = updateDetailsVal(roleVal, userRole, role);
						roleVOList.add(roleVal);
					}
				}
				roleVO.setRoleVOList(roleVOList);
			}
			List<ModuleAccess> moduleList = entityManager.createNamedQuery("findAllModuleAccessByCompanyAndDefaultAccess")
					.setParameter("companyId", activeUser.getCompany().getId())
					.getResultList();
			roleVO.setModuleList(moduleList);
			UserCreationBean userCreationBean = new UserCreationBean();
			userCreationBean.activeUser = activeUser;
			userCreationBean.entityManager = entityManager;
			roleVO.setParentList(userCreationBean.parentList());
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public String getUserRoleByLang(UserRole userRole, String localeString) {
		String role = "";
		try{
			if(userRole != null) {
				Set<UserRoleLanguage> userRoleLang = userRole.getUserRoleLanguage();
				if(userRoleLang != null && userRoleLang.size() > 0) {
					for(UserRoleLanguage roleLang: userRoleLang) {
						if(roleLang!= null && roleLang.getLanguages() != null && roleLang.getLanguages().getLangCode() != null
								&& roleLang.getLanguages().getLangCode().equalsIgnoreCase(localeString)) {
							role =  roleLang.getRole();
						}
					}
				}
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return role;
	}
	
	public List<UserRoleLanguage> userRoleLangList(){
		try{
			return entityManager.createNamedQuery("findRoleLanguageByCompanyAndLang")
									.setParameter("companyId", activeUser.getCompany().getId())
									.setParameter("langCode", localeSelector.getLocaleString())
									.getResultList();
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return null;
	}
	
	public void resetRoleDetails() {
		roleVO.setRoleVOList(null);
		roleVO.setModuleList(null);
		roleVO.setParentList(null);
	}
	
	public UserRole persist(boolean updateValue) {
		UserRole userRole = null;
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			String role = request.getParameter("roleName");
			String moduleId = request.getParameter("moduleId");
			String parentId = request.getParameter("parentId");
			if(role != null && !role.isEmpty()) {
				/******************* Add New Data *******************/
				userRole = new UserRole();
				userRole.setCompany(activeUser.getCompany());
				userRole.setRole(role);
				userRole.setEnable(true);
				userRole.setRowfreeze(false);
				userRole.setSortCode(1);
				userRole.setCreatedDate(new Date());
				userRole.setModifiedDate(new Date());
				entityManager.persist(userRole);
				entityManager.flush();
				createUserRoleLang(userRole, true);
				if(moduleId != null && !moduleId.trim().isEmpty()
					&& parentId != null && !parentId.trim().isEmpty()) {
					grantAccess(moduleId);
					updateAccess(userRole);
					grantModule(parentId);
					updateModules(userRole);
				}
				/******************* Add New Data *******************/
				/******************* Add New Data TO Exist List *******************/  
				if(updateValue) {
					RoleVO roleVal = new RoleVO();
					roleVal = updateDetailsVal(roleVal, userRole, role);
					
					if(roleVO.getRoleVOList() != null)
						roleVO.getRoleVOList().add(roleVal);
					else {
						roleVO.setRoleVOList(new ArrayList<RoleVO>());
						roleVO.getRoleVOList().add(roleVal);
					}
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
				
				/****************** Add Other User Role Language ******************/
				
				/******************* Add New Data TO Exist List *******************/
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return userRole;
	}
	

	public void update() {
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();
			String roleId = request.getParameter("roleId");
			String role = request.getParameter("roleName");
			String moduleId = request.getParameter("moduleId");
			String parentId = request.getParameter("parentId");
			String roleEnableId  = request.getParameter("roleEnableId");
			String localeString = localeSelector.getLocaleString();
			if(roleId!=null && !roleId.isEmpty()) {
				UserRole userRole = entityManager.find(UserRole.class, Integer.valueOf(roleId.trim()));
				if(roleEnableId != null && !roleEnableId.isEmpty()) {
					if(roleEnableId.equalsIgnoreCase("1"))
						userRole.setEnable(true);
					else
						userRole.setEnable(false);
				}
				grantAccess(moduleId);
				updateAccess(userRole);
				grantModule(parentId);
				updateModules(userRole);
				List<UserRoleLanguage> userRoleLang = entityManager.createNamedQuery("findRoleLanguageByRoleAndLang")
						.setParameter("roleId", userRole.getId())
						.setParameter("langCode", localeString)
						.getResultList();
				if(userRoleLang != null && userRoleLang.size() > 0) {
					for(UserRoleLanguage roleLang: userRoleLang) {
						if(roleLang!= null && roleLang.getLanguages() != null && roleLang.getLanguages().getLangCode() != null
								&& roleLang.getLanguages().getLangCode().equalsIgnoreCase(localeString)) {
							roleLang.setRole(role);
							entityManager.merge(roleLang);
							entityManager.flush();
						}
					}
				}else {
					String sourceLang = localeSelector.getLocaleString();
					Languages lang = (Languages) entityManager.createNamedQuery("findAllLanguagesByLangCode")
							.setParameter("langCode", sourceLang)
							.getSingleResult();
					persistUserRoleLang(sourceLang, userRole, lang, role);
				}
				entityManager.merge(userRole);
				entityManager.flush();
				
				if(roleVO.getRoleVOList() != null && roleVO.getRoleVOList().size() > 0) {
					for(RoleVO roleVal: roleVO.getRoleVOList()) {
						if(roleVal.getId() == userRole.getId()) {
							updateDetailsVal(roleVal, userRole, role);
						}
					}
				}
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public RoleVO updateDetailsVal(RoleVO roleVal, UserRole userRole, String role) {
		roleVal.setRoleName(role);
		roleVal.setId(userRole.getId());
		roleVal.setEnable(userRole.isEnable());
		roleVal.setRowfreeze(userRole.isRowfreeze());
		roleVal.setUserRole(userRole);
		Set<ModuleAccess> accessList = userRole.getUserRoleAccess();
		String access = "";
		String accessId = "";
		if(accessList != null && accessList.size() > 0) {
			for(ModuleAccess modules : accessList) {
				String module = Messages.instance().get(modules.getModules().toString()) + " , "; 
				String modId = String.valueOf(modules.getId()).concat(",");
				access = access + module ;
				accessId = accessId + modId;
			}
			if(access != null && !access.isEmpty()) 
				access = access.substring(0, access.trim().length() - 1).trim();
			if(accessId != null && !accessId.isEmpty()) 
				accessId = accessId.substring(0, accessId.trim().length() - 1).trim();
		}
		roleVal.setAccess(access);
		roleVal.setAccessId(accessId);
		
		Set<ModuleAccess> moduleAccessList = userRole.getUserRoleModule();
		String moduleList = "";
		String moduleId = "";
		if(moduleAccessList != null && moduleAccessList.size() > 0) {
			for(ModuleAccess modules : moduleAccessList) {
				String module = Messages.instance().get(modules.getModules().toString()) + " , "; 
				String modId = String.valueOf(modules.getId()).concat(",");
				moduleList = moduleList + module ;
				moduleId = moduleId + modId;
			}
			if(moduleList != null && !moduleList.isEmpty()) 
				moduleList = moduleList.substring(0, moduleList.trim().length() - 1).trim();
			if(moduleId != null && !moduleId.isEmpty()) 
				moduleId = moduleId.substring(0, moduleId.trim().length() - 1).trim();
		}
		roleVal.setModuleName(moduleList);
		roleVal.setModuleId(moduleId);
		return roleVal;
	}
	
	
	public void createUserRoleLang(UserRole userRole, boolean currenLang) {
		try {
			if(userRole!= null) {
				String role = userRole.getRole();
				LanguageBean langBean = new LanguageBean();
				langBean.entityManager = entityManager;
				String sourceLang = localeSelector.getLocaleString();
				if(currenLang) {
					Languages lang = langBean.getLangFromLangCode(sourceLang);
					persistUserRoleLang(sourceLang, userRole, lang, role);
				}else {
					for(Languages lang : langBean.langList()) {
						if(!lang.getLangCode().trim().equalsIgnoreCase(sourceLang.trim()))
							persistUserRoleLang(sourceLang, userRole, lang, role);
					}					
				}
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void persistUserRoleLang(String sourceLang, UserRole userRole, Languages lang, String role) {
		UserRoleLanguage userRoleLang = new UserRoleLanguage();
		userRoleLang.setRole(Translator.translate(sourceLang, lang.getLangCode(), role));
		userRoleLang.setLanguages(lang);
		userRoleLang.setUserRole(userRole);
		userRoleLang.setCreatedDate(new Date());
		userRoleLang.setModifiedDate(new Date());
		entityManager.persist(userRoleLang);
		entityManager.flush();
	}
	
	public void updateAccess(UserRole userRole) {
		try{
			if (grants != null) {
				for (GrantAccess grant : grants) {
					if (grant.isEnable()) {
						if(!this.checkUserRoleAccess(grant.getModuleAccess().getModules(), userRole)) {
							userRole.getUserRoleAccess().add(grant.getModuleAccess());
						}
					} else {
						if (this.checkUserRoleAccess(grant.getModuleAccess().getModules(), userRole)) {
							userRole.getUserRoleAccess().remove(grant.getModuleAccess());
						}
					}
				}
			}
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void updateModules(UserRole userRole) {
		try{
			if (grants != null) {
				for (GrantAccess grant : grants) {
					if (grant.isEnable()) {
						if(!this.checkUserRoleModule(grant.getModuleAccess().getModules(), userRole)) {
							userRole.getUserRoleModule().add(grant.getModuleAccess());
						}
					} else {
						if (this.checkUserRoleModule(grant.getModuleAccess().getModules(), userRole)) {
							userRole.getUserRoleModule().remove(grant.getModuleAccess());
						}
					}
				}
			}
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public List<GrantAccess> grantModule(String value) {
		try{
			if(grants!=null){
				grants.clear();
			}else{
				grants = new ArrayList<GrantAccess>();
			}
			UserCreationBean userCreationBean = new UserCreationBean();
			userCreationBean.activeUser = activeUser;
			userCreationBean.entityManager = entityManager;
			List<ModuleAccess> moduleList = userCreationBean.parentList();
			if(moduleList != null && moduleList.size()>0){
				for(ModuleAccess module : moduleList){
					if(value!=null && !value.isEmpty() && value.trim().length()>0){
						if(value!=null && value.trim().length()>0){
							String[] valArr = value.split(",");
							if(checkModules(module, valArr)){
								GrantAccess grant = new GrantAccess(module,true);
								grants.add(grant);
							}else{
								GrantAccess grant = new GrantAccess(module,false);
								grants.add(grant);
							}
						}
					}else{
						GrantAccess grant = new GrantAccess(module,false);
						grants.add(grant);
					}
				}
			}
			return grants;
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			return null;
		}
	}
	
	public List<GrantAccess> grantAccess(String value) {
		try{
			if(grants!=null){
				grants.clear();
			}else{
				grants = new ArrayList<GrantAccess>();
			}
			List<ModuleAccess> moduleList = entityManager.createNamedQuery("findAllModuleAccessByCompanyAndDefaultAccess")
												.setParameter("companyId", activeUser.getCompany().getId())
												.getResultList();
			if(moduleList != null && moduleList.size()>0){
				for(ModuleAccess module : moduleList){
					if(value!=null && !value.isEmpty() && value.trim().length()>0){
						if(value!=null && value.trim().length()>0){
							String[] valArr = value.split(",");
							if(checkModules(module, valArr)){
								GrantAccess grant = new GrantAccess(module,true);
								grants.add(grant);
							}else{
								GrantAccess grant = new GrantAccess(module,false);
								grants.add(grant);
							}
						}
					}else{
						GrantAccess grant = new GrantAccess(module,false);
						grants.add(grant);
					}
				}
			}
			return grants;
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			return null;
		}
	}
	
	public boolean checkModules(ModuleAccess modulePermission, String[] valArr){
		try{
			for(int i =0; i < valArr.length; i++){
				String val = valArr[i];
				if(val != null && !val.isEmpty()) {
					if(modulePermission.getId() == Integer.parseInt(val.trim())){
						return true;
					}
				}
			}
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return false;
	}
	
	public static Boolean checkUserRoleAccess(Modules module, UserRole userRole) {
		for (ModuleAccess mod : userRole.getUserRoleAccess()) {
			if (mod.getModules().equals(module)) {
				return true;
			}
		}
		return false;
	}
	
	public static Boolean checkUserRoleModule(Modules module, UserRole userRole) {
		for (ModuleAccess mod : userRole.getUserRoleModule()) {
			if (mod.getModules().equals(module)) {
				return true;
			}
		}
		return false;
	}
}
