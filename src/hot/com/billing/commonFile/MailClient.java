package com.billing.commonFile;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.jboss.seam.annotations.Name;

import com.billing.exceptions.ExceptionMsg;


@Name("mailClient")
public class MailClient {

//	public static final String SMTP_HOST_NAME = "smtp.sendgrid.net";
//	public static final String SMTP_PORT = "587";
//	public static final String FROM_ADDRESS = "";
//	public static final String UserName = "apikey";
//	public static final String PASSWORD = "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";
	
	public static final String SMTP_HOST_NAME = "smtp.1und1.de";
	public static final String SMTP_PORT = "587";
	public static final String UserName = "syed.farook@softitservice.de";
	public static final String PASSWORD = "f@r00kSyed!";
	
	public void sendMail(String fromEmail, String toMail, String ccToMail, String bccToMail,
			String subject, String message, String logo, String attachmentPath) {
		try {
			
			boolean debug = false;	// while sending email disabled the system out
			MimeMultipart multipart = new MimeMultipart("related");
			BodyPart messageBodyPart = new MimeBodyPart();	
			messageBodyPart.setContent(message, "text/html;charset=utf-8");	
			
			multipart.addBodyPart(messageBodyPart);
			
			if(attachmentPath!=null && !attachmentPath.isEmpty()){
				String imgPaths[] = attachmentPath.split(",");
				int count = 1;
				for (String str: imgPaths) {
					if(str!=null && !str.isEmpty() && str.trim().length() > 0){
						String pathext[] = str.split(":extention:");
						String fileName = "file" + count + "." + pathext[1].trim();
						messageBodyPart = new MimeBodyPart();
						File imgFile =  new File(pathext[0].trim());
						if(imgFile != null && imgFile.exists()){
							DataSource source = new FileDataSource(pathext[0].trim());
					        messageBodyPart.setDataHandler(new DataHandler(source));
					        messageBodyPart.setFileName(fileName);
							multipart.addBodyPart(messageBodyPart);
							++count;
						}
					}
				}
			}
			
			if(logo!=null && !logo.isEmpty()){
				File imgFile =  new File(logo);
				if(imgFile != null && imgFile.exists()){
					messageBodyPart = new MimeBodyPart();
					DataSource fds = new FileDataSource(logo);
					messageBodyPart.setDataHandler(new DataHandler(fds));
					messageBodyPart.setHeader("Content-ID", "<image>");
					multipart.addBodyPart(messageBodyPart);
				}
			}
			
			Properties props = new Properties();
			props.put("mail.smtp.host", SMTP_HOST_NAME);
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			//props.put("mail.smtp.ssl.enable", "true");
			props.put("mail.smtp.port", SMTP_PORT);
	
			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					try {
						return new PasswordAuthentication(UserName,
								PASSWORD);
					} catch (RuntimeException e) {
						ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
					}
					return null;
				}
			});
	
			/*************** Disable the log while sending email **************/
			session.setDebug(debug);
			/******************************************************************/
			
			Message msg = new MimeMessage(session);
			
			/*************************** Add From ****************************/
			InternetAddress addressFrom = new InternetAddress(fromEmail);
			msg.setFrom(addressFrom);
			/******************************************************************/
			
			/*************************** Add To ******************************/
			if(toMail!=null && !toMail.isEmpty()){
				String recipients[] = toMail.split(",");
				InternetAddress[] addressTo = new InternetAddress[recipients.length];
				for (int i = 0; i < recipients.length; i++) {
					addressTo[i] = new InternetAddress(recipients[i]);
				}
				msg.setRecipients(Message.RecipientType.TO, addressTo);
			}
			/******************************************************************/
			
			
			/*************************** Add Bcc ******************************/
			if(bccToMail!=null && !bccToMail.isEmpty()){
				String bccAddress[] = bccToMail.split(",");
				InternetAddress[] bccTo = new InternetAddress[bccAddress.length];
				for (int i = 0; i < bccAddress.length; i++) {
					bccTo[i] = new InternetAddress(bccAddress[i]);
				}
				msg.setRecipients(Message.RecipientType.BCC, bccTo);
			}
			/******************************************************************/
			
			/*************************** Add CC *******************************/
			if(ccToMail!=null && !ccToMail.isEmpty()){
				String ccAddress[] = ccToMail.split(",");
				InternetAddress[] ccTo = new InternetAddress[ccAddress.length];
				for (int i = 0; i < ccAddress.length; i++) {
					ccTo[i] = new InternetAddress(ccAddress[i]);
				}
				msg.setRecipients(Message.RecipientType.CC, ccTo);
			}
			/******************************************************************/
			
			/************************* Add Subject ****************************/
			msg.setSubject(MimeUtility.encodeText(subject, "utf-8", "B"));
			/******************************************************************/
			
			// put everything together
	        msg.setContent(multipart);
	        
	        if(msg.getRecipients(Message.RecipientType.TO) != null && msg.getRecipients(Message.RecipientType.TO).length > 0){
	        	Transport.send(msg);
			}
	        System.out.println("Email send successfully");
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
}
