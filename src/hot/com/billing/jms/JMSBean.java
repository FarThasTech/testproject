package com.billing.jms;

import org.jboss.seam.annotations.Name;

@Name("jmsBean")
public class JMSBean {

	public JMSVO resetJMSVO(JMSVO jmsVO) {
		jmsVO.setParam("");
		jmsVO.setLocaleString("");
		jmsVO.setPrimaryId("");
		jmsVO.setCategory(null);
		jmsVO.setProduct(null);
		jmsVO.setUserRole(null);
		return jmsVO;
	}
}
