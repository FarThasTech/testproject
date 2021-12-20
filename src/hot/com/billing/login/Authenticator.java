package com.billing.login;

import javax.ejb.Local;

@Local
public interface Authenticator {

	boolean authenticate();

}
