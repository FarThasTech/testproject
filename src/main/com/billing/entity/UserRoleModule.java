package com.billing.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "userrole_module")
public class UserRoleModule implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private UserRoleModuleId id;

	public UserRoleModule() {
	}

	public UserRoleModule(UserRoleModuleId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "userroleId", column = @Column(name = "user_role_id")),
			@AttributeOverride(name = "userrolemoduleId", column = @Column(name = "userrolemodule_id"))})
	public UserRoleModuleId getId() {
		return this.id;
	}

	public void setId(UserRoleModuleId id) {
		this.id = id;
	}

}
