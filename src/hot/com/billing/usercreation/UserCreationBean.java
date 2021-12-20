package com.billing.usercreation;

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
import org.jboss.seam.web.ServletContexts;

import com.billing.entity.Fields;
import com.billing.entity.FieldsValue;
import com.billing.entity.Languages;
import com.billing.entity.ModuleAccess;
import com.billing.entity.UserRole;
import com.billing.entity.UserRoleLanguage;
import com.billing.entity.Users;
import com.billing.fields.FieldsBean;
import com.billing.language.LanguageBean;
import com.billing.login.PasswordBean;
import com.billing.modules.GrantAccess;
import com.billing.modules.Modules;
import com.billing.role.RoleBean;
import com.billing.staticvalue.StaticValues;

@Scope(ScopeType.SESSION)
@Name("userCreationBean")
@SuppressWarnings({"unchecked"})
public class UserCreationBean {

	@In
	public EntityManager entityManager;
	    
	@In
	public LocaleSelector localeSelector;
	
	@In(create=true,required=false) 
    public Users activeUser;
	
	@In(create=true)
	@Out(scope=ScopeType.SESSION)
	public UserCreationVO userCreationVO;
	
	@DataModel
	public List<GrantAccess> grants = new ArrayList<GrantAccess>();
	
	public void execute() {
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			String param = request.getParameter("param");
			if(param != null && !param.isEmpty()) {
				if(param.equalsIgnoreCase("save")) {
					persist();
				}else if(param.equalsIgnoreCase("update")) {
					update();
				}else if(param.equalsIgnoreCase("updateModule")) {
					String value = request.getParameter("moduleId");
					String usersId = request.getParameter("usersId");
					if(usersId != null && !usersId.isEmpty()) {
						Users users = entityManager.find(Users.class, Integer.valueOf(usersId.trim()));
						String parentIds = addParentId(value);
						value = parentIds.concat(value);
						grantModule(value);
						updateModules(users);
						entityManager.merge(users);
						entityManager.flush();
						
						/*************** Update Data ****************/
						
						if(userCreationVO.getUsersCreationList() != null) {
							for(UserCreationVO userCreationVO: userCreationVO.getUsersCreationList()) {
								if(users.getId() == userCreationVO.getId()) {
									userCreationVO = getUserCreationVal(users, userCreationVO);
								}
							}
						}
						/*************** Update Data ****************/
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void init() {
		try {
			List<Users> usersList = getUserCreationList();
			if(usersList != null && usersList.size() > 0) {
				List<UserCreationVO> usersCreationList = new ArrayList<UserCreationVO>();
				for(Users users : usersList) {
					UserCreationVO userCreationVO = new UserCreationVO();
					userCreationVO = getUserCreationVal(users, userCreationVO);
					usersCreationList.add(userCreationVO);
				}
				userCreationVO.setUsersCreationList(usersCreationList);
			}
			LanguageBean langBean = new LanguageBean();
			langBean.entityManager = entityManager;
			List<Languages> langList = langBean.langList();
			userCreationVO.setLangList(langList);
			FieldsBean fields = new FieldsBean();
			fields.entityManager = entityManager;
			List<FieldsValue> titleList = fields.fieldsValueListDependsOnFiels(StaticValues.Title, localeSelector.getLocaleString());
			userCreationVO.setTitleList(titleList);
			RoleBean roleBean = new RoleBean();
			roleBean.activeUser = activeUser;
			roleBean.entityManager = entityManager;
			roleBean.localeSelector = localeSelector;
			List<UserRoleLanguage> userRoleLangList = roleBean.userRoleLangList();
			userCreationVO.setUserRoleLangList(userRoleLangList);
			moduleAccessList();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<ModuleAccess> allModuleList(){
		return entityManager.createNamedQuery("findAllModuleAccessByCompany")
								.setParameter("companyId", activeUser.getCompany().getId())
								.getResultList();
	}
	
	public List<ModuleAccess> parentList(){
		return entityManager.createNamedQuery("findAllModuleAccessByCompanyWithParentIdNotNull")
								.setParameter("companyId", activeUser.getCompany().getId())
								.getResultList();
	}
	
	
	public List<ModuleAccess> childList(int childId){
		return entityManager.createNamedQuery("findAllModuleAccessByCompanyWithChildId")
								.setParameter("companyId", activeUser.getCompany().getId())
								.setParameter("childId", childId).getResultList();
	}
	
	public void moduleAccessList(){
		try {
			List<ModuleAccess> parentList = parentList();
			List<UserCreationVO> moduleList = new ArrayList<UserCreationVO>();
			if(parentList != null && parentList.size() > 0) {
				for(ModuleAccess parent : parentList){
					String moduleName = parent.getModules().name().toString();
					if(!parent.isEnable()){
						continue;
					}
					UserCreationVO creationV0 = new UserCreationVO();
					creationV0.setModuleName(moduleName);
					creationV0.setPrimaryKey(parent.getId());
					
					List<ModuleAccess> childList = childList(parent.getParentId());
					if(childList != null && childList.size() > 0) {
						List<UserCreationVO> childListVO = new ArrayList<UserCreationVO>();
						for(ModuleAccess child : childList){
							String childName = child.getModules().name().toString();
							if(!child.isEnable()){
								continue;
							}
							UserCreationVO childV0 = new UserCreationVO();
							childV0.setModuleName(childName);
							childV0.setPrimaryKey(child.getId());
							childListVO.add(childV0);
						}
						creationV0.setChildList(childListVO);
					}
					moduleList.add(creationV0);
				}
			}
			
			userCreationVO.setModuleList(moduleList);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public UserCreationVO getUserCreationVal(Users users, UserCreationVO userCreationVO) {
		try {
			userCreationVO.setId(users.getId());
			String localeString = localeSelector.getLocaleString();
			UserRole userRole = users.getUserRole();
			userCreationVO.setRoleId(userRole.getId());
			userCreationVO.setRole(new RoleBean().getUserRoleByLang(userRole, localeString));
			Fields title = users.getTitle();
			if(title != null) {
				List<FieldsValue> titleList = entityManager.createNamedQuery("findFieldsValueByFieldIdAndLang")
						.setParameter("fieldId", title.getId())
						.setParameter("langCode", localeString)
						.getResultList();
				userCreationVO.setTitle(titleList != null && titleList.size() > 0 ? titleList.get(0).getFieldName() : "");
				userCreationVO.setTitleId(title.getId());
			}else {
				userCreationVO.setTitle("");
				userCreationVO.setTitleId(0);
			}
			userCreationVO.setFirstName(users.getFirstName());
			userCreationVO.setLastName(users.getLastName());
			userCreationVO.setLang(users.getLanguages().getLangCode());
			userCreationVO.setLangId(users.getLanguages().getId());
			userCreationVO.setEmail(users.getPrimaryEmail());
			userCreationVO.setSalary(String.valueOf(users.getSalary()));
			userCreationVO.setCommission(users.getCommisson());
			userCreationVO.setUserName(users.getUserName());
			userCreationVO.setLoginAccess(users.isLoginAccess());
			Set<ModuleAccess> userAccess = users.getUsersAccess();
			String accessId = "";
			if(userAccess != null && userAccess.size()>0) {
				for(ModuleAccess module: userAccess) {
					accessId = accessId.concat(String.valueOf(module.getId())).concat(","); 
				}
				if(accessId != null && !accessId.isEmpty()) {
					accessId = accessId.substring(0, accessId.length() - 1);
				}
			}
			userCreationVO.setAccessIdList(accessId);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return userCreationVO;
	}
	
	public void reset() {
		userCreationVO.setUsersCreationList(null);
		userCreationVO.setUsersList(null);
		userCreationVO.setLangList(null);
		userCreationVO.setTitleList(null);
		userCreationVO.setUserRoleLangList(null);
		userCreationVO.setModuleList(null);
	}
	
	
	
	public List<Users> getUserCreationList() {
		try {
			return entityManager.createNamedQuery("findUsersByCompanyUser")
						.setParameter("companyId", activeUser.getCompany().getId())
						.getResultList();
		}catch(Exception e) {
			e.printStackTrace();
			return new ArrayList<Users>();
		}
	}
	
	public void persist() {
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();
			String titleId = request.getParameter("titleId");
			String roleId = request.getParameter("roleId");
			String firstName = request.getParameter("firstName");
			String lastName = request.getParameter("lastName");
			String langId = request.getParameter("langId");
			String email = request.getParameter("email");
			String salary = request.getParameter("salary");
			String commission = request.getParameter("commission");
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			
			if(firstName != null && !firstName.isEmpty() && lastName != null && !lastName.isEmpty()
					&& email != null && !email.isEmpty() && langId != null && !langId.isEmpty()) {
				Users users = new Users();
				users.setFirstName(firstName);
				users.setLastName(lastName);
				users.setCompany(activeUser.getCompany());
				Languages languages = entityManager.find(Languages.class, Integer.valueOf(langId));
				users.setLanguages(languages);
				if(titleId != null && !titleId.isEmpty()) {
					Fields fields = entityManager.find(Fields.class, Integer.valueOf(titleId.trim()));
					users.setTitle(fields);
				}
				if(roleId != null && !roleId.isEmpty()) {
					UserRole userRole = entityManager.find(UserRole	.class, Integer.valueOf(roleId.trim()));
					if(userRole != null) {
						users.setUserRole(userRole);
						Set<ModuleAccess> moduleAccessList = userRole.getUserRoleModule();
						if(moduleAccessList != null && moduleAccessList.size() > 0) {
							String value = "";
							for(ModuleAccess modules : moduleAccessList) {
								value = value.concat(String.valueOf(modules.getId())).concat(",");
								List<ModuleAccess> childList = childList(modules.getParentId());
								if(childList != null && childList.size() > 0) {
									for(ModuleAccess child : childList) {
										value = value.concat(String.valueOf(child.getId())).concat(",");
									}
								}
							}
							if(value != null && !value.isEmpty()) 
								value = value.substring(0, value.trim().length() - 1).trim();
							grantModule(value);
							updateModules(users);
						}
					}
				}
				users.setPrimaryEmail(email);
				users.setUserName(username);
				users.setPassword(PasswordBean.getInstance().encryptPassword(password));
				users.setCompanyUser(true);
				users.setLoginAccess(true);
				users.setWritePermission(true);
				users.setCreatedUser(activeUser);
				users.setCreatedDate(new Date());
				users.setModifiedDate(new Date());
				if(salary != null && !salary.isEmpty()) { 
					users.setSalary(Double.valueOf(salary.trim())); }
				if(commission != null && !commission.isEmpty()) {
					users.setSalary(Double.valueOf(commission.trim())); 
				}
				entityManager.persist(users);
				entityManager.flush();
						
				/*************** New Data ****************/
				UserCreationVO userVO = new UserCreationVO();
				userVO = getUserCreationVal(users, userVO);
				if(userCreationVO.getUsersCreationList() != null)
					userCreationVO.getUsersCreationList().add(userVO);
				else {
					userCreationVO.setUsersCreationList(new ArrayList<UserCreationVO>());
					userCreationVO.getUsersCreationList().add(userVO);
				}
				/*************** New Data ****************/
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public String addParentId(String value) {
		String parentId = "";
		try {
			List<ModuleAccess> moduleList = allModuleList();
			List<ModuleAccess> childList = new ArrayList<ModuleAccess>();
			List<Integer> parentList = new ArrayList<Integer>();
			if(moduleList != null && moduleList.size()>0){
				for(ModuleAccess module : moduleList){
					if(value!=null && !value.isEmpty() && value.trim().length()>0){
						if(value!=null && value.trim().length()>0){
							String[] valArr = value.split(",");
							if(checkModules(module, valArr)){
								childList.add(module);
							}
						}
					}
				}
				if(childList != null && childList.size()>0){
					for(ModuleAccess child : childList){
						if(!checkParentIdisExist(parentList, child.getChildId())) {
							parentList.add(child.getChildId());
						}
					}
				}
				if(parentList != null && parentList.size()>0) {
					for(Integer id: parentList) {
						List<ModuleAccess> parentModule = entityManager.createNamedQuery("findAllModuleAccessByCompanyWithParentId")
								.setParameter("companyId", activeUser.getCompany().getId())
								.setParameter("parentId", id)
								.getResultList();
						if(parentModule != null && parentModule.size() > 0) {
							ModuleAccess parent = parentModule.get(0);
							if(parent != null)
								parentId = parentId.concat(String.valueOf(parent.getId())).concat(",");
						}
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return parentId;
	}
	
	public boolean checkParentIdisExist(List<Integer> parentList, int parentId) {
		try {
			if(parentList != null && parentList.size()>0){
				for(Integer parent : parentList){
					if(parentId == parent.intValue())
						return true;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public List<GrantAccess> grantModule(String value) {
		try{
			if(grants!=null){
				grants.clear();
			}else{
				grants = new ArrayList<GrantAccess>();
			}
			List<ModuleAccess> moduleList = allModuleList();
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
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean checkModules(ModuleAccess moduleAccess, String[] valArr){
		try{
			for(int i =0; i < valArr.length; i++){
				String val = valArr[i];
				if(val != null && !val.isEmpty()) {
					if(moduleAccess.getId() == Integer.parseInt(val.trim())){
						return true;
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void updateModules(Users users) {
		try{
			if (grants != null) {
				for (GrantAccess grant : grants) {
					if (grant.isEnable()) {
						if(!checkUsersModule(grant.getModuleAccess().getModules(), users)) {
							users.getUsersAccess().add(grant.getModuleAccess());
						}
					} else {
						if (checkUsersModule(grant.getModuleAccess().getModules(), users)) {
							users.getUsersAccess().remove(grant.getModuleAccess());
						}
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Boolean checkUsersModule(Modules module, Users users) {
		for (ModuleAccess mod : users.getUsersAccess()) {
			if (mod.getModules().equals(module)) {
				return true;
			}
		}
		return false;
	}
	
	public void update() {
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();
			String userId = request.getParameter("usersId");
			String titleId = request.getParameter("titleId");
			String roleId = request.getParameter("roleId");
			String firstName = request.getParameter("firstName");
			String lastName = request.getParameter("lastName");
			String langId = request.getParameter("langId");
			String email = request.getParameter("email");
			String salary = request.getParameter("salary");
			String commission = request.getParameter("commission");
			String username = request.getParameter("username");
			String loginAccess = request.getParameter("loginAccessId");
			if(userId != null && !userId.isEmpty()) {
				Users users = entityManager.find(Users.class, Integer.valueOf(userId.trim()));
				users.setFirstName(firstName);
				users.setLastName(lastName);
				users.setCompany(activeUser.getCompany());
				Languages languages = entityManager.find(Languages.class, Integer.valueOf(langId));
				users.setLanguages(languages);
				if(titleId != null && !titleId.isEmpty()) {
					Fields fields = entityManager.find(Fields.class, Integer.valueOf(titleId.trim()));
					users.setTitle(fields);
				}
				if(roleId != null && !roleId.isEmpty()) {
					UserRole userRole = entityManager.find(UserRole	.class, Integer.valueOf(roleId.trim()));
					users.setUserRole(userRole);
				}
				users.setPrimaryEmail(email);
				users.setUserName(username);
				if(loginAccess != null && !loginAccess.isEmpty()) {
					if(loginAccess.equalsIgnoreCase("1"))
						users.setLoginAccess(true);
					else
						users.setLoginAccess(false);
				}
				
				users.setModifiedDate(new Date());
				if(salary != null && !salary.isEmpty()) { 
					users.setSalary(Double.valueOf(salary.trim())); }
				if(commission != null && !commission.isEmpty()) {
					users.setSalary(Double.valueOf(commission.trim())); 
				}
				entityManager.merge(users);
				entityManager.flush();
						
				/*************** Update Data ****************/
				
				if(userCreationVO.getUsersCreationList() != null) {
					for(UserCreationVO userCreationVO: userCreationVO.getUsersCreationList()) {
						if(users.getId() == userCreationVO.getId()) {
							userCreationVO = getUserCreationVal(users, userCreationVO);
						}
					}
				}
				/*************** Update Data ****************/
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
