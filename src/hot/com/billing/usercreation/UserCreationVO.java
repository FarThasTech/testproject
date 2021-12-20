package com.billing.usercreation;

import java.util.List;

import org.jboss.seam.annotations.Name;

import com.billing.entity.FieldsValue;
import com.billing.entity.Languages;
import com.billing.entity.UserRoleLanguage;
import com.billing.entity.Users;

@Name("userCreationVO")
public class UserCreationVO {

	private List<Users> usersList;
	
	private List<Languages> langList;
	
	private List<FieldsValue> titleList;
	
	private List<UserRoleLanguage> userRoleLangList;
	
	private List<UserCreationVO> usersCreationList;
	
	private Users users;
	
	private int id;
	
	private String title;
	
	private int titleId;
	
	private String role;
	
	private int roleId;
	
	private String firstName;
	
	private String lastName;
	
	private String lang;
	
	private int langId;
	
	private String email;
	
	private String salary;
	
	private double commission;
	
	private String userName;
	
	private boolean loginAccess;
	
	private String moduleName;
	
	private int primaryKey;
	
	private boolean active;
	
	private  List<UserCreationVO> moduleList;
	
	private  List<UserCreationVO> childList;
	
	private String accessIdList;
	
	public List<Users> getUsersList() {
		return usersList;
	}

	public void setUsersList(List<Users> usersList) {
		this.usersList = usersList;
	}

	public List<Languages> getLangList() {
		return langList;
	}

	public void setLangList(List<Languages> langList) {
		this.langList = langList;
	}

	public List<FieldsValue> getTitleList() {
		return titleList;
	}

	public void setTitleList(List<FieldsValue> titleList) {
		this.titleList = titleList;
	}

	public List<UserRoleLanguage> getUserRoleLangList() {
		return userRoleLangList;
	}

	public void setUserRoleLangList(List<UserRoleLanguage> userRoleLangList) {
		this.userRoleLangList = userRoleLangList;
	}

	public List<UserCreationVO> getUsersCreationList() {
		return usersCreationList;
	}

	public void setUsersCreationList(List<UserCreationVO> usersCreationList) {
		this.usersCreationList = usersCreationList;
	}

	public Users getUsers() {
		return users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getTitleId() {
		return titleId;
	}

	public void setTitleId(int titleId) {
		this.titleId = titleId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public int getLangId() {
		return langId;
	}

	public void setLangId(int langId) {
		this.langId = langId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public double getCommission() {
		return commission;
	}

	public void setCommission(double commission) {
		this.commission = commission;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isLoginAccess() {
		return loginAccess;
	}

	public void setLoginAccess(boolean loginAccess) {
		this.loginAccess = loginAccess;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public int getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(int primaryKey) {
		this.primaryKey = primaryKey;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<UserCreationVO> getModuleList() {
		return moduleList;
	}

	public void setModuleList(List<UserCreationVO> moduleList) {
		this.moduleList = moduleList;
	}

	public List<UserCreationVO> getChildList() {
		return childList;
	}

	public void setChildList(List<UserCreationVO> childList) {
		this.childList = childList;
	}

	public String getAccessIdList() {
		return accessIdList;
	}

	public void setAccessIdList(String accessIdList) {
		this.accessIdList = accessIdList;
	}

}
