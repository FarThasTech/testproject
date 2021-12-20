package com.billing.role;

import java.util.List;

import org.jboss.seam.annotations.Name;

import com.billing.entity.ModuleAccess;
import com.billing.entity.UserRole;

@Name("roleVO")
public class RoleVO {

	private  int id;
	
	private UserRole userRole;
	
	private String roleName;
	
	private boolean enable;
	
	private String access;
	
	private boolean rowfreeze;
	
	private List<RoleVO> roleVOList;
	
	private List<ModuleAccess> moduleList;
	
	private String accessId;
	
	private List<ModuleAccess> parentList;
	
	private String moduleName;
	
	private String moduleId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public boolean getEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getAccess() {
		return access;
	}

	public void setAccess(String access) {
		this.access = access;
	}

	public boolean isRowfreeze() {
		return rowfreeze;
	}

	public void setRowfreeze(boolean rowfreeze) {
		this.rowfreeze = rowfreeze;
	}

	public List<RoleVO> getRoleVOList() {
		return roleVOList;
	}

	public void setRoleVOList(List<RoleVO> roleVOList) {
		this.roleVOList = roleVOList;
	}

	public List<ModuleAccess> getModuleList() {
		return moduleList;
	}

	public void setModuleList(List<ModuleAccess> moduleList) {
		this.moduleList = moduleList;
	}

	public String getAccessId() {
		return accessId;
	}

	public void setAccessId(String accessId) {
		this.accessId = accessId;
	}

	public List<ModuleAccess> getParentList() {
		return parentList;
	}

	public void setParentList(List<ModuleAccess> parentList) {
		this.parentList = parentList;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
	
}
