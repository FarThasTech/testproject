package com.billing.modules;

import com.billing.entity.ModuleAccess;

public class GrantAccess {

	private ModuleAccess moduleAccess;
	private boolean enable;
	
	public GrantAccess(){
		
	}

	public GrantAccess(ModuleAccess moduleAccess,boolean enable){
		this.moduleAccess=moduleAccess;
		this.enable=enable;
	}
	
	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public boolean isEnable() {
		return enable;
	}

	public ModuleAccess getModuleAccess() {
		return moduleAccess;
	}

	public void setModuleAccess(ModuleAccess moduleAccess) {
		this.moduleAccess = moduleAccess;
	}
}
