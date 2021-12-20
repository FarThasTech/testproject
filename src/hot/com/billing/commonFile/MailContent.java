package com.billing.commonFile;

import java.util.Map;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.international.Messages;

import com.billing.exceptions.ExceptionMsg;
import com.billing.util.DateUtil;
import com.billing.util.MainUtil;


@Name("mailContent")
public class MailContent {
	
	public Map<String, String> messages = Messages.instance();
	
	public String getemailcontentforsignup(String name, String url, String companyName, String street, String houseNo,
			String city, String country, String zip) {
		try {
			return  "<!DOCTYPE html>"+
				"<html>"+
				"<head>"+
				"<title></title>"+
				"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"+
				"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"+
				"<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">"+
				"<style type=\"text/css\">"+
				"    /* FONTS */"+
				"    @import url('https://fonts.googleapis.com/css?family=Poppins:100,100i,200,200i,300,300i,400,400i,500,500i,600,600i,700,700i,800,800i,900,900i');"+
				"    /* CLIENT-SPECIFIC STYLES */"+
				"    body, table, td, a { -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }"+
				"    table, td { mso-table-lspace: 0pt; mso-table-rspace: 0pt; }"+
				"    img { -ms-interpolation-mode: bicubic; }"+
				"    /* RESET STYLES */"+
				"    img { border: 0; height: auto; line-height: 100%; outline: none; text-decoration: none; }"+
				"    table { border-collapse: collapse !important; }"+
				"    body { height: 100% !important; margin: 0 !important; padding: 0 !important; width: 100% !important; }"+
				"    /* iOS BLUE LINKS */"+
				"    a[x-apple-data-detectors] {"+
				"        color: inherit !important;"+
				"        text-decoration: none !important;"+
				"        font-size: inherit !important;"+
				"        font-family: inherit !important;"+
				"        font-weight: inherit !important;"+
				"        line-height: inherit !important;"+
				"    }"+
				"    /* MOBILE STYLES */"+
				"    @media screen and (max-width:600px){"+
				"        h1 {"+
				"            font-size: 32px !important;"+
				"            line-height: 32px !important;"+
				"        }"+
				"    }"+
				"    /* ANDROID CENTER FIX */"+
				"    div[style*=\"margin: 16px 0;\"] { margin: 0 !important; }"+
				"</style>"+
				"</head>"+
				"<body style=\"background-color: #f3f5f7; margin: 0 !important; padding: 0 !important;\">"+
				"<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">"+
				"    <tr>"+
				"        <td align=\"center\">"+
				"            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">"+
				"                <tr>"+
				"                    <td align=\"center\" valign=\"top\" style=\"padding: 40px 10px 10px 10px;\">"+
				"                        <a href=\"#\" target=\"_blank\" style=\"text-decoration: none;\">"+
				"							<img src=\"cid:image\" class=\"img-fluid max-w-100\" width=\"100\" alt=\"\" style=\"max-width: 100px;\" />"+
				"							<span style=\"display: block; font-family: 'Poppins', sans-serif; color: #3e8ef7; font-size: 36px;\" border=\"0\">" +
				"								<b>"+companyName+"</b>" +
				"							</span>"+
				"                        </a>"+
				"                    </td>"+
				"                </tr>"+
				"            </table>"+
				"        </td>"+
				"    </tr>"+
				"    <tr>"+
				"        <td align=\"center\" style=\"padding: 0px 10px 0px 10px;\">"+
				"            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">"+
				"                <tr>"+
				"                    <td bgcolor=\"#ffffff\" align=\"center\" valign=\"top\" style=\"padding: 40px 20px 20px 20px; border-radius: 4px 4px 0px 0px; color: #111111; font-family: 'Poppins', sans-serif; font-size: 48px; font-weight: 400; letter-spacing: 2px; line-height: 48px;\">"+
				"                      <h1 style=\"font-size: 36px; font-weight: 600; margin: 0;\">"+messages.get("Hi")+" "+name+"</h1>"+
				"                    </td>"+
				"                </tr>"+
				"            </table>"+
				"           "+
				"        </td>"+
				"    </tr>"+
				"    <tr>"+
				"        <td align=\"center\" style=\"padding: 0px 10px 0px 10px;\">"+
				"            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">"+
				"              <tr>"+
				"                <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 20px 30px 20px 30px; color: #666666; font-family: 'Poppins', sans-serif; font-size: 16px; font-weight: 400; line-height: 25px;\">"+
				"                  <p style=\"margin: 0;\">"+messages.get("We_Are_Excited")+"</p>"+
				"                </td>"+
				"              </tr>"+
				"              <tr>"+
				"                <td bgcolor=\"#ffffff\" align=\"left\">"+
				"                  <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">"+
				"                    <tr>"+
				"                      <td bgcolor=\"#ffffff\" align=\"center\" style=\"padding: 20px 30px 30px 30px;\">"+
				"                        <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">"+
				"                          <tr>"+
				"                              <td align=\"center\" style=\"border-radius: 3px;\" bgcolor=\"#17b3a3\">" +
				"									<a href=\""+url+"\" target=\"_blank\" style=\"font-size: 18px; font-family: Helvetica, Arial, sans-serif; color: #ffffff; text-decoration: none;" +
				" 										color: #ffffff; text-decoration: none; padding: 12px 50px; border-radius: 2px; border: 1px solid #17b3a3; display: inline-block;\">" +
				"										"+messages.get("Confirm_Account")+"</a>" +
				"								</td>" +
				"                          </tr>"+
				"                        </table>"+
				"                      </td>"+
				"                    </tr>"+
				"                  </table>"+
				"                </td>"+
				"              </tr>"+
				"              <tr>"+
				"                <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 0px 30px 0px 30px; color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 400; line-height: 25px;\">"+
				"                  <p style=\"margin: 0;\">"+messages.get("If_Link_Does_Not_Work")+":</p>"+
				"                </td>"+
				"              </tr>"+
				"                <tr>"+
				"                  <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 20px 30px 20px 30px; color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 12px; font-weight: 400; line-height: 25px;\">"+
				"                    <p style=\"margin: 0;\"><a href=\""+url+"\" target=\"_blank\" style=\"color: #17b3a3;\">" +
	 			"						"+url+"</a>" +
	 			"					</p>"+
				"                  </td>"+
				"                </tr>"+
				"              <tr>"+
				"                <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 0px 30px 20px 30px; color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 400; line-height: 25px;\">"+
				"                  <p style=\"margin: 0;\">"+messages.get("Any_Question_Ask")+"</p>"+
				"                </td>"+
				"              </tr>"+
				"              <tr>"+
				"                <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 0px 30px 40px 30px; border-radius: 0px 0px 0px 0px; color: #666666; font-family: 'Poppins', sans-serif; font-size: 14px; font-weight: 400; line-height: 25px;\">"+
				"                  <p style=\"margin: 0;\">"+messages.get("Cheers")+",<br>"+messages.get("Team")+"</p>"+
				"                </td>"+
				"              </tr>"+
				"            </table>"+
				"            "+
				"        </td>"+
				"    </tr>"+
				"	<tr>"+
				"        <td align=\"center\" style=\"padding: 10px 10px 50px 10px;\">"+
				"            "+
				"            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">"+
				"              <tr>"+
				"                <td align=\"center\" style=\"padding: 30px 30px 30px 30px; color: #333333; font-family: 'Poppins', sans-serif; font-size: 12px; font-weight: 400; line-height: 18px;\">"+
				"					<p style=\"margin: 0;\">"+street+" " +houseNo+" " +city+" " +country+" " +zip+"</p>                 "+
				"					<p style=\"margin: 0;\">Copyright © " + DateUtil.getCurrentYear() + " " + new MainUtil().getInfoFromProperty("App_Name") + ". "+messages.get("All_Rights_Reserved")+".</p>"+
				"                </td>"+
				"              </tr>"+
				"            </table>"+
				"          "+
				"        </td>"+
				"    </tr>"+
				"</table>"+
				"</body>"+
				"</html>";
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return "";
	}
	
	public String getemailcontentforResetPwd(String name, String url, String companyName, String street, String houseNo,
			String city, String country, String zip) {
		try {
			return "<!DOCTYPE html>"+
				"<html>"+
				"<head>"+
				"<title></title>"+
				"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"+
				"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"+
				"<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">"+
				"<style type=\"text/css\">"+
				"    /* FONTS */"+
				"    @import url('https://fonts.googleapis.com/css?family=Poppins:100,100i,200,200i,300,300i,400,400i,500,500i,600,600i,700,700i,800,800i,900,900i');"+
				"    /* CLIENT-SPECIFIC STYLES */"+
				"    body, table, td, a { -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }"+
				"    table, td { mso-table-lspace: 0pt; mso-table-rspace: 0pt; }"+
				"    img { -ms-interpolation-mode: bicubic; }"+
				"    /* RESET STYLES */"+
				"    img { border: 0; height: auto; line-height: 100%; outline: none; text-decoration: none; }"+
				"    table { border-collapse: collapse !important; }"+
				"    body { height: 100% !important; margin: 0 !important; padding: 0 !important; width: 100% !important; }"+
				"    /* iOS BLUE LINKS */"+
				"    a[x-apple-data-detectors] {"+
				"        color: inherit !important;"+
				"        text-decoration: none !important;"+
				"        font-size: inherit !important;"+
				"        font-family: inherit !important;"+
				"        font-weight: inherit !important;"+
				"        line-height: inherit !important;"+
				"    }"+
				"    /* MOBILE STYLES */"+
				"    @media screen and (max-width:600px){"+
				"        h1 {"+
				"            font-size: 32px !important;"+
				"            line-height: 32px !important;"+
				"        }"+
				"    }"+
				"    /* ANDROID CENTER FIX */"+
				"    div[style*=\"margin: 16px 0;\"] { margin: 0 !important; }"+
				"</style>"+
				"</head>"+
				"<body style=\"background-color: #f3f5f7; margin: 0 !important; padding: 0 !important;\">"+
				"<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">"+
				"    <tr>"+
				"        <td align=\"center\">"+
				"            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">"+
				"                <tr>"+
				"                    <td align=\"center\" valign=\"top\" style=\"padding: 40px 10px 10px 10px;\">"+
				"                        <a href=\"#\" target=\"_blank\" style=\"text-decoration: none;\">"+
				"							<img src=\"cid:image\" class=\"img-fluid max-w-100\" width=\"100\" alt=\"\" style=\"max-width: 100px;\" />"+
				"							<span style=\"display: block; font-family: 'Poppins', sans-serif; color: #3e8ef7; font-size: 36px;\" border=\"0\">" +
				"								<b>"+companyName+"</b>" +
				"							</span>"+
				"                        </a>"+
				"                    </td>"+
				"                </tr>"+
				"            </table>            "+
				"        </td>"+
				"    </tr>"+
				"    <tr>"+
				"        <td align=\"center\" style=\"padding: 0px 10px 0px 10px;\">"+
				"            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">"+
				"                <tr>"+
				"                    <td bgcolor=\"#ffffff\" align=\"center\" valign=\"top\" style=\"padding: 40px 20px 20px 20px; border-radius: 4px 4px 0px 0px; color: #111111; font-family: 'Poppins', sans-serif; font-size: 48px; font-weight: 400; letter-spacing: 2px; line-height: 48px;\">"+
				"                      <h1 style=\"font-size: 36px; font-weight: 600; margin: 0;\">"+messages.get("Hi")+" "+name+"</h1>"+
				"                    </td>"+
				"                </tr>"+
				"            </table>"+
				"           "+
				"        </td>"+
				"    </tr>"+
				"    <tr>"+
				"        <td align=\"center\" style=\"padding: 0px 10px 0px 10px;\">"+
				"            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">"+
				"                <tr>"+
				"                    <td bgcolor=\"#ffffff\" align=\"center\" valign=\"top\" style=\"padding: 40px 20px 20px 20px; border-radius: 4px 4px 0px 0px; color: #ff4c52; font-family: 'Poppins', sans-serif; font-size: 48px; font-weight: 400; letter-spacing: 2px; line-height: 48px;\">"+
				"                      <h1 style=\"font-size: 36px; font-weight: 600; margin: 0;\">"+messages.get("Trouble_Signing")+"</h1>"+
				"                    </td>"+
				"                </tr>"+
				"            </table>"+
				"        </td>"+
				"    </tr>"+
				"    <tr>"+
				"        <td align=\"center\" style=\"padding: 0px 10px 0px 10px;\">"+
				"            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">"+
				"              <tr>"+
				"                <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 20px 30px 40px 30px; color: #666666; font-family: 'Poppins', sans-serif; font-size: 16px; font-weight: 400; line-height: 25px;\">"+
				"                  <p style=\"margin: 0;\">"+messages.get("Request_Password_Change")+"</p>"+
				"                </td>"+
				"              </tr>"+
				"              <tr>"+
				"                <td bgcolor=\"#ffffff\" align=\"left\">"+
				"                  <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">"+
				"                    <tr>"+
				"                      <td bgcolor=\"#ffffff\" align=\"center\" style=\"padding: 20px 30px 60px 30px;\">"+
				"                        <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">"+
				"                          <tr>"+
				"                              <td align=\"center\" style=\"border-radius: 3px;\" bgcolor=\"#ff4c52\">" +
				"									<a href=\""+url+"\" target=\"_blank\" style=\"font-size: 18px; font-family: Helvetica, Arial, sans-serif; color: #ffffff;" +
				"								 		text-decoration: none; color: #ffffff; text-decoration: none; padding: 12px 50px; border-radius: 2px;" +
				"									 	border: 1px solid #ff4c52; display: inline-block;\">" +
														messages.get("Reset_Password")+
				"									</a>" +
				"								</td>"+
				"                          </tr>"+
				"                        </table>"+
				"                      </td>"+
				"                    </tr>"+
				"                  </table>"+
				"                </td>"+
				"              </tr>"+
				"              <tr>"+
				"                <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 0px 30px 20px 30px; color: #aaaaaa; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 13px; font-weight: 400; line-height: 25px;\">"+
				"                  <p style=\"margin: 0; text-align: center;\">"+messages.get("Request_Password_Not_Initiated")+"</p>"+
				"                </td>"+
				"              </tr>"+
				"              <tr>"+
				"                <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 0px 30px 40px 30px; border-radius: 0px 0px 0px 0px; color: #666666; font-family: 'Poppins', sans-serif; font-size: 14px; font-weight: 400; line-height: 25px;\">"+
				"                  <p style=\"margin: 0;\">"+messages.get("Cheers")+",<br>"+messages.get("Team")+"</p>"+
				"                </td>"+
				"              </tr>"+
				"            </table>"+
				"		</td>"+
				"    </tr>"+
				"    <tr>"+
				"        <td align=\"center\" style=\"padding: 10px 10px 50px 10px;\">"+
				"		      <tr>"+
				"                <td align=\"center\" style=\"padding: 30px 30px 30px 30px; color: #333333; font-family: 'Poppins', sans-serif; font-size: 12px; font-weight: 400; line-height: 18px;\">"+
				"					<p style=\"margin: 0;\">"+street+" " +houseNo+" " +city+" " +country+" " +zip+"</p>                 "+
				"					<p style=\"margin: 0;\">Copyright © " + DateUtil.getCurrentYear() + " "+ new MainUtil().getInfoFromProperty("App_Name") + ". "+messages.get("All_Rights_Reserved")+".</p>"+
				"                </td>"+
				"              </tr>"+
				"            </table>"+
				"        </td>"+
				"    </tr>"+
				"</table>"+
				"</body>"+
				"</html>";
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return "";
	}
	
	public String getEmailContentForDonationReceipt(String name, String companyName, String street, String houseNo,
			String city, String country, String zip, String campaignDetails, String transDate, String receiptNo, String transCode) {
		try {
			return "<!DOCTYPE html>"+
				"<html>"+
				"	<head>"+
				"		<title></title>"+
				"		<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"+
				"		<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"+
				"		<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">"+
				"		<style type=\"text/css\">"+
				"		    /* FONTS */"+
				"		    @import url('https://fonts.googleapis.com/css?family=Poppins:100,100i,200,200i,300,300i,400,400i,500,500i,600,600i,700,700i,800,800i,900,900i');"+
				"		    /* CLIENT-SPECIFIC STYLES */"+
				"		    body, table, td, a { -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }"+
				"		    table, td { mso-table-lspace: 0pt; mso-table-rspace: 0pt; }"+
				"		    img { -ms-interpolation-mode: bicubic; }"+
				"		    /* RESET STYLES */"+
				"		    img { border: 0; height: auto; line-height: 100%; outline: none; text-decoration: none; }"+
				"		    table { border-collapse: collapse !important; }"+
				"		    body { height: 100% !important; margin: 0 !important; padding: 0 !important; width: 100% !important; }"+
				"		    /* iOS BLUE LINKS */"+
				"		    a[x-apple-data-detectors] {"+
				"		        color: inherit !important;"+
				"		        text-decoration: none !important;"+
				"		        font-size: inherit !important;"+
				"		        font-family: inherit !important;"+
				"		        font-weight: inherit !important;"+
				"		        line-height: inherit !important;"+
				"		    }"+
				"		    /* MOBILE STYLES */"+
				"		    @media screen and (max-width:600px){"+
				"		        h1 {"+
				"		            font-size: 32px !important;"+
				"		            line-height: 32px !important;"+
				"		        }"+
				"		    }"+
				"		    /* ANDROID CENTER FIX */"+
				"	    	div[style*=\"margin: 16px 0;\"] { margin: 0 !important; }"+
				"		</style>"+
				"	</head>"+
				"	<body style=\"background-color: #f3f5f7; margin: 0 !important; padding: 0 !important;\">"+
				"		<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">"+
				"	    	<tr>"+
				"	        	<td align=\"center\" style=\"padding: 0px 10px 0px 10px;\">"+
				"	            	<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 1200px;\">"+
				"	                	<tr>"+
				"	                    	<td bgcolor=\"\" align=\"center\" valign=\"top\" style=\"padding: 20px 20px 20px 20px; border-radius: 4px 4px 0px 0px; color: #111111; font-family: 'Poppins', sans-serif; "+
				"                    		  	font-size: 48px; font-weight: 400; letter-spacing: 2px; line-height: 48px;\">"+
				"                      			<h1 style=\"font-size: 36px; font-weight: 600; margin: 0;\">"+
													messages.get("Donation_Receipt")+
				"								</h1>"+
				"	                    	</td>"+
				"	                	</tr>"+
				"	            	</table>"+
				"	        	</td>"+
				"	    	</tr>"+
				"	    	<tr>"+
				"	        	<td align=\"center\">"+
				"	           		<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 1200px;\">"+
				"	                	<tr>"+
				"	                    	<td align=\"center\" valign=\"top\" bgcolor=\"#d0dffd\" style=\"padding: 10px;\">"+
				"	                        	<a href=\"#\" target=\"_blank\" style=\"text-decoration: none;float:left;\">"+
				"	                        		<img src=\"cid:image\" class=\"img-fluid max-w-100\" alt=\"\" style=\"max-width: 100px;\" />"+
				"	                        	</a>"+
				"	                    	</td>"+
				"	                     	<td align=\"center\" valign=\"top\" style=\"padding: 20px 10px 10px 10px;\"  bgcolor=\"#d0dffd\">"+
				"	                        	<a href=\"#\" target=\"_blank\" style=\"text-decoration: none;float:right;\">"+
				"									<span style=\"display: block; font-family: 'Poppins', sans-serif; color: #3e8ef7; font-size: 18px;\" border=\"0\">"+
				"										<table>"+
				"											<tr>"+
				"												<td>"+
				"													<b style=\"float: left;\">"+messages.get("TransactionDate")+" : </b>"+
				"												</td>"+
				"												<td>"+
																	transDate +
				"												</td>"+
				"											</tr>"+
				"											<tr>"+
				"												<td>"+
				"													<b style=\"float: left;\">"+messages.get("Receipt_Number")+" : </b>"+
				"												</td>"+
				"												<td>"+
																	receiptNo +
				"												</td>"+
				"											</tr>"+
				"											<tr>"+
				"												<td>"+
				"													<b style=\"float: left;\">"+messages.get("TransactionCode")+" : </b>"+
				"												</td>"+
				"												<td>"+
																	transCode +
				"												</td>"+
				"											</tr>"+
				"										</table>"+
				"									</span>"+
				"	                        	</a>"+
				"	                    	</td>"+
				"	                	</tr>"+
				"	            	</table>"+
				"	        	</td>"+
				"	    	</tr>"+
				"	     	<tr>"+
				"		    	<td align=\"center\" style=\"padding: 0px 10px 0px 10px;\">"+
				"		        	<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 1200px;\">"+
				"		            	<tr>"+
				"		                	<td bgcolor=\"#ffffff\" align=\"center\" valign=\"top\" style=\"padding: 40px 20px 20px 20px; border-radius: 4px 4px 0px 0px; color: #111111; font-family: 'Poppins', sans-serif; "+
				"		                		font-size: 48px; font-weight: 400; letter-spacing: 2px; line-height: 48px;\">"+
				"		                  		<h1 style=\"font-size: 24px; font-weight: 600; margin: 0;float:left;\">"+messages.get("Hi")+" "+name+"</h1>"+
				"		                	</td>"+
				"		            	</tr>"+
				"		        	</table>"+
				"		    	</td>"+
				"			</tr>"+
				"	    	<tr>"+
				"	        	<td align=\"center\" style=\"padding: 0px 10px 0px 10px;\">"+
				"	            	<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 1200px;\">"+
				"	              		<tr>"+
				"	                		<td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 0px 0px 30px 100px; color: #666666; font-family: 'Poppins', sans-serif; font-size: 16px; font-weight: 400; line-height: 25px;\">"+
				"	                  			<p style=\"margin: 0;\">"+
													messages.get("Thank_You_For_Your_Donation")	+
				"								</p>"+
				"	                		</td>"+
				"	              		</tr>"+
				"	              		<tr>"+
				"	                		<td bgcolor=\"#ffffff\" align=\"left\">"+
				"	                  			<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">"+
				"	                    			<tr>"+
				"	                      				<td bgcolor=\"#ffffff\" align=\"center\">"+
				"	                        				<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">"+
				"	                          					<tr>"+
				"	                              					<td align=\"center\" style=\"border-radius: 3px;\" >"+
				"	                              						<a href=\"#\" style=\"font-size: 18px; font-family: Helvetica, Arial, sans-serif; color: #ffffff; text-decoration: none; color: black;"+
				"	                              						 	text-decoration: none; padding: 12px 50px; display: inline-block;\">"+
																			messages.get("Donation_Consisted_Following_Items")	+
				"	                              						</a>"+
				"	                              					</td>"+
				"	                          					</tr>"+
				"	                       					</table>"+
				"	                      				</td>"+
				"	                    			</tr>	"+
				"	                  			</table>"+
				"	                		</td>"+
				"	              		</tr>"+
				"	           		</table>"+
				"	       		</td>"+
				"	    	</tr>"+
				"	    	<tr>"+
				"	        	<td align=\"center\" style=\"padding: 0px 10px 0px 10px;\">"+
				"	             	<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 1200px;\">"+
				"	                	<tr>"+
				"	                  		<td bgcolor=\"#ffffff\" align=\"center\" style=\"padding: 15px;\">"+
				"	                  			<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 800px;\" align=\"center\">"+
				"			                		<tr>"+
				"			                  			<td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 10px 10px 10px 10px; color: black; font-family: 'Poppins', sans-serif;"+
				"			                  	 			font-size: 18px; font-weight: 400; line-height: 25px;  border: 1px solid #0bb2d4;\">"+
				"		                    				<h2 style=\"font-size: 20px; font-weight: 400; margin: 0;\">"+
																messages.get("CampaignName") + 
				"											</h2>"+
				"			                  			</td>"+
				"			                  			<td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 10px 10px 10px 10px; color: black; font-family: 'Poppins', sans-serif; "+
				"		                  					font-size: 18px; font-weight: 400; line-height: 25px;  border: 1px solid #0bb2d4;\">"+
				"			                    			<h2 style=\"font-size: 20px; font-weight: 400; margin: 0;\">"+
																messages.get("Amount") +
				"											</h2>"+
				"			                  			</td>"+
				"			                		</tr>"+
													campaignDetails + 
				"			            		</table>"+
				"	                  		</td>"+
				"	                	</tr>"+
				"	            	</table>"+
				"				</td>"+
				"	    	</tr>"+
				"	    	<tr>"+
				"	        	<td align=\"center\" style=\"padding: 10px 10px 50px 10px;\">"+
				"	            	<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 1200px;\">"+
				"	              		<tr>"+
				"	                		<td align=\"center\" style=\"padding: 30px 30px 30px 30px; color: #333333; font-family: 'Poppins', sans-serif; font-size: 12px; font-weight: 400; line-height: 18px;\">"+
				"								<p style=\"margin: 0;\">"+companyName+"</p>" +
				"								<p style=\"margin: 0;\">"+street+" " +houseNo+" " +city+" " +country+" " +zip+"</p>" +     
				"	                  			<p style=\"margin: 0;\">Copyright © " + DateUtil.getCurrentYear() + " " + new MainUtil().getInfoFromProperty("App_Name") + ". All rights reserved.</p>"+
				"	                		</td>"+
				"	              		</tr>"+
				"	            	</table>"+
				"	           	</td>"+
				"	    	</tr>"+
				"		</table>"+
				"	</body>"+
				"</html>";
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return "";
	}

	public Map<String, String> getMessages() {
		return messages;
	}

	public void setMessages(Map<String, String> messages) {
		this.messages = messages;
	}
	

}
