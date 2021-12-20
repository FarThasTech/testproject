package com.billing.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class UsersAccessId implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int usersId;
	private int usersAccessId;

	public UsersAccessId() {
	}

	public UsersAccessId(int usersId, int usersAccessId) {
		this.usersId = usersId;
		this.usersAccessId = usersAccessId;
	}

	@Column(name = "users_id")
	public int getUsersId() {
		return this.usersId;
	}

	public void setUsersId(int usersId) {
		this.usersId = usersId;
	}

	@Column(name = "usersAccess_id")
	public int getUsersAccessId() {
		return this.usersAccessId;
	}

	public void setUsersAccessId(int usersAccessId) {
		this.usersAccessId = usersAccessId;
	}

}
