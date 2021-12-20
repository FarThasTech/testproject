package com.billing.myteam;

import java.util.List;

import org.jboss.seam.annotations.Name;

import com.billing.entity.Languages;
import com.billing.entity.ModuleAccess;
import com.billing.entity.UserRoleLanguage;
import com.billing.entity.Users;

@Name("myTeamVO")
public class MyTeamVO {

	private List<UserRoleLanguage> userRoleLangList;
	
	private List<Languages> langList;
	
	private String langCode;
	
	private int userRoleId;
	
	private List<Users> teamList;
	
	private List<ModuleAccess> moduleAccessList;
	
	public List<UserRoleLanguage> getUserRoleLangList() {
		return userRoleLangList;
	}	

	public void setUserRoleLangList(List<UserRoleLanguage> userRoleLangList) {
		this.userRoleLangList = userRoleLangList;
	}

	public List<Languages> getLangList() {
		return langList;
	}

	public void setLangList(List<Languages> langList) {
		this.langList = langList;
	}

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	public int getUserRoleId() {
		return userRoleId;
	}

	public void setUserRoleId(int userRoleId) {
		this.userRoleId = userRoleId;
	}

	public List<Users> getTeamList() {
		return teamList;
	}

	public void setTeamList(List<Users> teamList) {
		this.teamList = teamList;
	}

	public List<ModuleAccess> getModuleAccessList() {
		return moduleAccessList;
	}

	public void setModuleAccessList(List<ModuleAccess> moduleAccessList) {
		this.moduleAccessList = moduleAccessList;
	}
		
}
