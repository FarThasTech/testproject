package com.billing.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class UserRoleAccessId implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int userroleId;
	private int userroleaccessId;

	public UserRoleAccessId() {
	}

	public UserRoleAccessId(int userroleId, int userroleaccessId) {
		this.userroleId = userroleId;
		this.userroleaccessId = userroleaccessId;
	}

	@Column(name = "user_role_id")
	public int getUserroleId() {
		return this.userroleId;
	}

	public void setUserroleId(int userroleId) {
		this.userroleId = userroleId;
	}

	@Column(name = "userroleaccess_id")
	public int getUserroleaccessId() {
		return this.userroleaccessId;
	}

	public void setUserroleaccessId(int userroleaccessId) {
		this.userroleaccessId = userroleaccessId;
	}

}
