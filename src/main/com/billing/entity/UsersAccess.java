package com.billing.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name = "users_access")
public class UsersAccess implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private UsersAccessId id;

	public UsersAccess() {
	}

	public UsersAccess(UsersAccessId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "usersId", column = @Column(name = "users_id")),
			@AttributeOverride(name = "usersAccessId", column = @Column(name = "usersAccess_id"))})
	public UsersAccessId getId() {
		return this.id;
	}

	public void setId(UsersAccessId id) {
		this.id = id;
	}

}
