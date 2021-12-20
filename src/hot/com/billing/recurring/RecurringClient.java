package com.billing.recurring;


import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class RecurringClient {
	private QueueConnectionFactory qcf = null;

	private QueueConnection conn = null;

	private QueueSession session = null;

	private Queue que = null;

	private InitialContext iniCtx = null;

	private static String QUEUENAME = "queue/AutomaticTrigger";

	private static String FACTORY = "java:/ConnectionFactory";

	/**
	 * Constructor
	 * 
	 */
	public RecurringClient() {
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

	public void chargeRecurringPaymentJMS(RecurringVO rVO) throws JMSException, NamingException {
		try{
			setupPTP();
			QueueSender send = session.createSender(que);
			/*********************************************/
			/********* old *******************************/
			//ObjectMessage om = session.createObjectMessage(fVO);
			/*********************************************/
			/********* new *******************************/
			ObjectMessage om = session.createObjectMessage();
			om.setStringProperty("fDetailsIds", rVO.getFundDetailsId());
			om.setStringProperty("fundGroupId", String.valueOf(rVO.getFundGroupId()));
			om.setStringProperty("userAccId", String.valueOf(rVO.getUserAccId()));
			/*********************************************/
			send.send(om);
			send.close();	
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
