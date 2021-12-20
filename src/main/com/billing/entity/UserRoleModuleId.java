package com.billing.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class UserRoleModuleId implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int userroleId;
	private int userrolemoduleId;

	public UserRoleModuleId() {
	}

	public UserRoleModuleId(int userroleId, int userrolemoduleId) {
		this.userroleId = userroleId;
		this.userrolemoduleId = userrolemoduleId;
	}

	@Column(name = "user_role_id")
	public int getUserroleId() {
		return this.userroleId;
	}

	public void setUserroleId(int userroleId) {
		this.userroleId = userroleId;
	}

	@Column(name = "userrolemodule_id")
	public int getUserrolemoduleId() {
		return this.userrolemoduleId;
	}

	public void setUserrolemoduleId(int userrolemoduleId) {
		this.userrolemoduleId = userrolemoduleId;
	}

}
