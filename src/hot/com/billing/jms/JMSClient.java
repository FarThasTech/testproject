package com.billing.jms;


import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.billing.util.StringUtil;

public class JMSClient {
	private QueueConnectionFactory qcf = null;

	private QueueConnection conn = null;

	private QueueSession session = null;

	private Queue que = null;

	private InitialContext iniCtx = null;

	private static String QUEUENAME = "queue/JMSTrigger";

	private static String FACTORY = "java:/ConnectionFactory";

	/**
	 * Constructor
	 * 
	 */
	public JMSClient() {
		super();
	}

	/**
	 * Methode initialisiert die Verbindung zur Queue
	 */
	public boolean initClient() throws Exception {
		this.setupPTP();
		return true;
	}

	/**
	 * Methode initialisiert
	 * 
	 */
	private void setupPTP() throws JMSException, NamingException {

		iniCtx = new InitialContext();
		qcf = (QueueConnectionFactory) iniCtx.lookup(FACTORY);
		conn = qcf.createQueueConnection();
		que = (Queue) iniCtx.lookup(QUEUENAME);
		session = conn.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
		conn.start();
	}

	public void automaticJMS(JMSVO jmsVO) throws JMSException, NamingException {
		try{
			if(jmsVO != null) {
				setupPTP();
				QueueSender send = session.createSender(que);
				
				String param = jmsVO.getParam();
				
				ObjectMessage om = session.createObjectMessage();
				if(StringUtil.checkStringIsNull(param)) {
					if(param.equalsIgnoreCase("Role")) {
						om = session.createObjectMessage(jmsVO.getUserRole());
					} else if(param.equalsIgnoreCase("Category")) {
						om = session.createObjectMessage(jmsVO.getCategory());
					} else if(param.equalsIgnoreCase("Product")) {
						om = session.createObjectMessage(jmsVO.getProduct());
					} 
				}
				
				om.setStringProperty("param", param);
				om.setStringProperty("localeString", jmsVO.getLocaleString());
				om.setStringProperty("primaryId", jmsVO.getPrimaryId());
				
				send.send(om);
				send.close();	
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			stop();
		}
	
	}

	public void stop() throws JMSException {
		conn.stop();
		session.close();
		conn.close();
	}
}
