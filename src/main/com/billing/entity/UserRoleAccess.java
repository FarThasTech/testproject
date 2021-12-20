package com.billing.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "userrole_access")
public class UserRoleAccess implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private UserRoleAccessId id;

	public UserRoleAccess() {
	}

	public UserRoleAccess(UserRoleAccessId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "userroleId", column = @Column(name = "user_role_id")),
			@AttributeOverride(name = "userroleaccessId", column = @Column(name = "userroleaccess_id"))})
	public UserRoleAccessId getId() {
		return this.id;
	}

	public void setId(UserRoleAccessId id) {
		this.id = id;
	}

}
