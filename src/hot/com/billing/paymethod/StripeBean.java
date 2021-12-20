package com.billing.paymethod;

import java.lang.reflect.Type;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.web.ServletContexts;

import com.billing.donor.DonationCart;
import com.billing.entity.Company;
import com.billing.entity.FundDetails;
import com.billing.entity.PaymentKeys;
import com.billing.entity.PaymentMethod;
import com.billing.entity.PaymentType;
import com.billing.entity.Users;
import com.billing.exceptions.ExceptionMsg;
import com.billing.staticvalue.StaticValues;
import com.billing.util.MainUtil;
import com.billing.util.StringUtil;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.web.ServletContexts;
import org.json.JSONObject;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.BalanceTransaction;
import com.stripe.model.Card;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentSource;
import com.stripe.model.Source;
import com.stripe.model.Token;
import com.stripe.net.ApiResource;

@SuppressWarnings({"unchecked","unused","deprecation"})	
@Scope(ScopeType.SESSION)
@Name("stripeBean")
public class StripeBean {

	@In
	public EntityManager entityManager;
	
	@In
    public LocaleSelector localeSelector;
	
	@In(create=true,required=false) 
	public Users activeUser;
	
	public static String TEST_SECRET_KEY = new MainUtil().getInfoFromProperty("STRIPE_TEST_SECRET_KEY");
	public static String TEST_PUBLISHABLE_KEY = new MainUtil().getInfoFromProperty("STRIPE_TEST_PUBLISHABLE_KEY");
	public static String TEST_CLIENT_ID = new MainUtil().getInfoFromProperty("STRIPE_TEST_CLIENT_ID");
	
	public static String LIVE_SECRET_KEY = new MainUtil().getInfoFromProperty("STRIPE_LIVE_SECRET_KEY");
	public static String LIVE_PUBLISHABLE_KEY = new MainUtil().getInfoFromProperty("STRIPE_LIVE_PUBLISHABLE_KEY");
	public static String LIVE_CLIENT_ID = new MainUtil().getInfoFromProperty("STRIPE_LIVE_CLIENT_ID");
	
	public static String API_KEY_SECRET(Company company) {
		if(new MainUtil().checkLocal()) {
			return TEST_SECRET_KEY;
		}else {
			return  company.isLiveAccount() ? LIVE_SECRET_KEY : TEST_SECRET_KEY;	
		}
	}
																				
	public static String API_KEY_PUBLISHABLE(Company company) {
		if(new MainUtil().checkLocal()) {
			return TEST_PUBLISHABLE_KEY;
		}else {
			return company.isLiveAccount() ? LIVE_PUBLISHABLE_KEY : TEST_PUBLISHABLE_KEY;
		}
	}
																				
	public static String CLIENT_ID(Company company) {
		if(new MainUtil().checkLocal()) {
			return TEST_CLIENT_ID;
		}else {
			return company.isLiveAccount() ? LIVE_CLIENT_ID : TEST_CLIENT_ID;
		}
	}
																				
	private String AUTHORIZE_URI= "https://connect.stripe.com/oauth/authorize";
	
	private String TOKEN_URI= "https://connect.stripe.com/oauth/token";
	
	public static String NonEUCardFee = "2.9 % + 0.25";
	
	public static String[] EU_COUNTRY_CODES = {"AT", "BE", "BG", "CY", "CZ", "DE", "DK", "EE", "ES", "FI", "FR", "GB", "GR", "HR", 
												"HU", "IE", "IT", "LT", "LU", "LV", "MT", "NL", "PL", "PT", "RO", "SE", "SI", "SK"};
	
	public static String Stripe_Account_Prefix = "acct_";
	
	public static String Stripe_Customer_Prefix = "cus_";
	
	public static String Stripe_SecretKey_Prefix = "sk_";
	
	public static String Stripe_PublishableKey_Prefix = "pk_";

	public static String Stripe_Token_Prefix = "tok_";
	
	public static String Stripe_Source_Prefix = "src_";
	
	public static String Stripe_NonCard_Trans_Prefix = "py_";
	
	public static String Stripe_Card_Trans_Prefix = "ch_";
	
	public static String Stripe_Payment_Intent_Trans_Prefix = "pi_";
	
	public boolean error;
	
	private Event customEvent; 
	
	public static String STRIPE_CreditCard = "CreditCard";
	public static String STRIPE_SOFORT = "Sofort";
	public static String STRIPE_GIROPAY = "Giropay";
	public static String STRIPE_IDEAL= "Ideal"; 
	public static String STRIPE_EPS= "EPS"; 
	public static String STRIPE_BANCONTACT= "Bancontact"; 
	public static String STRIPE_MULTIBANCO= "Multibanco"; 
	public static String STRIPE_P24= "P24"; 
	public static String STRIPE_APPLEPAY= "ApplePay"; 
	public static String STRIPE_GOOGLEPAY= "GooglePay"; 
	public static String STRIPE_SEPAPAY= "Sepa"; 
	
	public static String STRIPE_FUNDRAISE= "FRAISE";
	
	/******************** Stripe Events ********************/
	public static String account_updated = "account.updated";
	public static String account_application_authorized = "account.application.authorized";
	public static String account_application_deauthorized = "account.application.deauthorized";
	public static String account_external_account_created = "account.external_account.created";
	public static String account_external_account_deleted = "account.external_account.deleted";
	public static String account_external_account_updated = "account.external_account.updated";
	public static String application_fee_created = "application_fee.created";
	public static String application_fee_refunded = "application_fee.refunded";
	public static String application_fee_refund_updated = "application_fee.refund.updated";
	public static String balance_available = "balance.available";
	public static String charge_captured = "charge.captured";
	public static String charge_expired = "charge.expired";
	public static String charge_failed = "charge.failed";
	public static String charge_pending = "charge.pending";
	public static String charge_refunded = "charge.refunded";
	public static String charge_succeeded = "charge.succeeded";
	public static String charge_updated = "charge.updated";
	public static String charge_dispute_closed = "charge.dispute.closed";
	public static String charge_dispute_created = "charge.dispute.created";
	public static String charge_dispute_funds_reinstated = "charge.dispute.funds_reinstated";
	public static String charge_dispute_funds_withdrawn = "charge.dispute.funds_withdrawn";
	public static String charge_dispute_updated = "charge.dispute.updated";
	public static String charge_refund_updated = "charge.refund.updated";
	public static String coupon_created = "coupon.created";
	public static String coupon_deleted = "coupon.deleted";
	public static String coupon_updated = "coupon.updated";
	public static String customer_created = "customer.created";
	public static String customer_deleted = "customer.deleted";
	public static String customer_updated = "customer.updated";
	public static String customer_bank_account_deleted = "customer.bank_account.deleted";
	public static String customer_discount_created = "customer.discount.created";
	public static String customer_discount_deleted = "customer.discount.deleted";
	public static String customer_discount_updated = "customer.discount.updated";
	public static String customer_source_created = "customer.source.created";
	public static String customer_source_deleted = "customer.source.deleted";
	public static String customer_source_expiring = "customer.source.expiring";
	public static String customer_source_updated = "customer.source.updated";
	public static String customer_subscription_created = "customer.subscription.created";
	public static String customer_subscription_deleted = "customer.subscription.deleted";
	public static String customer_subscription_trial_will_end = "customer.subscription.trial_will_end";
	public static String customer_subscription_updated = "customer.subscription.updated";
	public static String file_created = "file.created";
	public static String invoice_created = "invoice.created";
	public static String invoice_deleted = "invoice.deleted";
	public static String invoice_marked_uncollectible = "invoice.marked_uncollectible";
	public static String invoice_payment_failed = "invoice.payment_failed";
	public static String invoice_payment_succeeded = "invoice.payment_succeeded";
	public static String invoice_sent = "invoice.sent";
	public static String invoice_upcoming = "invoice.upcoming";
	public static String invoice_updated = "invoice.updated";
	public static String invoice_voided = "invoice.voided";
	public static String invoiceitem_created = "invoiceitem.created";
	public static String invoiceitem_deleted = "invoiceitem.deleted";
	public static String invoiceitem_updated = "invoiceitem.updated";
	public static String issuer_fraud_record_created = "issuer_fraud_record.created";
	public static String issuing_authorization_created = "issuing_authorization.created";
	public static String issuing_authorization_updated = "issuing_authorization.updated";
	public static String issuing_card_created = "issuing_card.created";
	public static String issuing_card_updated = "issuing_card.updated";
	public static String issuing_cardholder_created = "issuing_cardholder.created";
	public static String issuing_cardholder_updated = "issuing_cardholder.updated";
	public static String issuing_dispute_created = "issuing_dispute.created";
	public static String issuing_dispute_updated = "issuing_dispute.updated";
	public static String issuing_transaction_created = "issuing_transaction.created";
	public static String issuing_transaction_updated = "issuing_transaction.updated";
	public static String order_created = "order.created";
	public static String order_payment_failed = "order.payment_failed";
	public static String order_payment_succeeded = "order.payment_succeeded";
	public static String order_updated = "order.updated";
	public static String order_return_created = "order_return.created";
	public static String payment_intent_amount_capturable_updated = "payment_intent.amount_capturable_updated";
	public static String payment_intent_created = "payment_intent.created";
	public static String payment_intent_payment_failed = "payment_intent.payment_failed";
	public static String payment_intent_requires_capture = "payment_intent.requires_capture";
	public static String payment_intent_succeeded = "payment_intent.succeeded";
	public static String payout_canceled = "payout.canceled";
	public static String payout_created = "payout.created";
	public static String payout_failed = "payout.failed";
	public static String payout_paid = "payout.paid";
	public static String payout_updated = "payout.updated";
	public static String plan_created = "plan.created";
	public static String plan_deleted = "plan.deleted";
	public static String plan_updated = "plan.updated";
	public static String product_created = "product.created";
	public static String product_deleted = "product.deleted";
	public static String product_updated = "product.updated";
	public static String recipient_created = "recipient.created";
	public static String recipient_deleted = "recipient.deleted";
	public static String recipient_updated = "recipient.updated";
	public static String reporting_report_run_failed = "reporting.report_run.failed";
	public static String reporting_report_run_succeeded = "reporting.report_run.succeeded";
	public static String reporting_report_type_updated = "reporting.report_type.updated";
	public static String review_closed = "review.closed";
	public static String review_opened = "review.opened";
	public static String sigma_scheduled_query_run_created = "sigma.scheduled_query_run.created";
	public static String sku_created = "sku.created";
	public static String sku_deleted = "sku.deleted";
	public static String sku_updated = "sku.updated";
	public static String source_canceled = "source.canceled";
	public static String source_chargeable = "source.chargeable";
	public static String source_failed = "source.failed";
	public static String source_mandate_notification = "source.mandate_notification";
	public static String source_refund_attributes_required = "source.refund_attributes_required";
	public static String source_transaction_created = "source.transaction.created";
	public static String source_transaction_updated = "source.transaction.updated";
	public static String topup_canceled = "topup.canceled";
	public static String topup_created = "topup.created";
	public static String topup_failed = "topup.failed";
	public static String topup_reversed = "topup.reversed";
	public static String topup_succeeded = "topup.succeeded";
	public static String transfer_created = "transfer.created";
	public static String transfer_reversed = "transfer.reversed";
	public static String transfer_updated = "transfer.updated";
	/******************** Stripe Events ********************/
	
	/******************** Stripe Source Status ********************/
	public static String sourceStatus_chargeable = "chargeable"; 
	public static String sourceStatus_consumed = "consumed";
	public static String sourceStatus_canceled = "canceled"; 
	public static String sourceStatus_failed = "failed";
	public static String sourceStatus_pending = "pending";

	/******************** Stripe payment types ********************/
	/******************** Source Re-Usable Payment types ********************/ 
	public static String payType_ach_credit_transfer = "ach_credit_transfer";
	public static String payType_ach_debit = "ach_debit";
	public static String payType_card = "card";
	public static String payType_card_present = "card_present";
	public static String payType_paper_check = "paper_check";
	public static String payType_sepa_credit_transfer = "sepa_credit_transfer";
	public static String payType_sepa_debit = "sepa_debit";
	public static String payType_three_d_secure = "three_d_secure";
	/******************** Source Re-Usable Payment types ********************/
	
	/******************** Source Single Use Payment types ********************/ 
	public static String payType_alipay = "alipay"; 
	public static String payType_bancontact = "bancontact";
	public static String payType_eps = "eps";
	public static String payType_giropay = "giropay";
	public static String payType_ideal = "ideal";
	public static String payType_multibanco = "multibanco";
	public static String payType_p24 = "p24";
	public static String payType_sofort = "sofort";
	/******************** Source Single Use Payment types ********************/
	
	public String[] singleUsagePayType(){
		String[] status = {payType_alipay, payType_bancontact, payType_eps, payType_giropay, payType_ideal, payType_multibanco, payType_p24, payType_sofort};
		return status;
	}
	
	public boolean checkSingleUsagePayType(String payType){
		try{
		for(String str : singleUsagePayType()){
			if(str.equalsIgnoreCase(payType)){
				return true;
			}
		}
		} catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return false;
	}
	
	public void stripeAccountCreationUrl() {
		try {
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			response.setContentType("text/html; charset=UTF-8");
			response.getWriter().write(stripeAuthorizationURL()+"\n");
		} catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public String stripeAuthorizationURL(){
		try{
			URI uri = new URIBuilder(AUTHORIZE_URI)
					.setParameter("response_type", "code")
					.setParameter("scope", "read_write")
					.setParameter("client_id", CLIENT_ID(activeUser.getCompany()))     
					.setParameter("redirect_uri", new MainUtil().getInfoFromProperty("Domain_Name")+"/StripeAuthResponse.jsf")
					.setParameter("state", String.valueOf(activeUser.getCompany().getId()))
					.build();
			return uri.toString();
		}catch(Exception e){
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			return "";
		}
	}
	
	public void stripeAuthResponse(){
		HttpServletRequest request = null;
		try{
			request = ServletContexts.instance().getRequest();
			String code = request.getParameter("code");
			String error = request.getParameter("error");
			System.out.println("Received Response from stripe");
			System.out.println("Received Auth Code :: " + code);
			if(code != null){
				String state = request.getParameter("state");
				Integer companyId = Integer.valueOf(state);
				Company company = entityManager.find(Company.class, companyId);
				PaymentMethod stripePay = null;
				try{
					stripePay = (PaymentMethod) entityManager.createNamedQuery("findPaymentMethodByCompanyAndpaymentType")
																.setParameter("companyId", companyId)
																.setParameter("paytypeId", PayMethodBean.Stripe_Sepa_Id)
																.getSingleResult();
				}catch(Exception e){
					stripePay = null;
				}
				
				if(stripePay == null){
					String accountId = "";
					String secrectApiKey = "";
					String publishablekey = "";
					String country = "";
					String currency = "";
					if(!code.equals("customconnect")){
						CloseableHttpClient httpClient = HttpClients.createDefault();
						URI uri = new URIBuilder( TOKEN_URI)
			                .setParameter("client_secret", API_KEY_SECRET(company)) 
			                .setParameter("grant_type", "authorization_code")
			                .setParameter("client_id", CLIENT_ID(company))
			                .setParameter("code", code)
			                .build();
			
						HttpPost httpPost = new HttpPost(uri);
						CloseableHttpResponse resp = httpClient.execute(httpPost);
						
						String bodyAsString = EntityUtils.toString(resp.getEntity());
						Type t = new TypeToken<Map<String, String>>() { }.getType();
						Map<String, String> map = new GsonBuilder().create().fromJson(bodyAsString, t);
						
						System.out.println("Body As String : " + bodyAsString);
						
						accountId = map.get("stripe_user_id");
						secrectApiKey = map.get("access_token");
						publishablekey = map.get("stripe_publishable_key");
						country = getStripeCountry(accountId, secrectApiKey);
						currency = getStripeCurrency(accountId, secrectApiKey);
						System.out.println("Received Account Id :: " + accountId);
						System.out.println("Secret Key :: " + secrectApiKey);
						System.out.println("Publishable Key :: " + publishablekey);
						System.out.println("Stripe account is created for this company id : " + companyId);
					}else if(code.equals("customconnect")) {
						accountId = "update keys";
						secrectApiKey = "update keys";
						publishablekey = "update keys";
						System.out.println("Need to update keys in Payment keys (Stripe) table for this company : "+ companyId);
					}
					PaymentKeys paymentKeys = new PaymentKeys();
					paymentKeys.setAccountId(accountId);
					paymentKeys.setPayParam1(secrectApiKey);
					paymentKeys.setPayParam2(publishablekey);
					paymentKeys.setCompany(company);
					paymentKeys.setAccountConnected(true);
					paymentKeys.setPaymentType(PayMethodBean.Stripe);
					paymentKeys.setAccountCountry(country);
					paymentKeys.setAccountCurrencyCode(currency != null ? currency.toUpperCase() : currency);
					paymentKeys.setCreatedDate(new Date());
					paymentKeys.setModifiedDate(new Date());
					entityManager.persist(paymentKeys);
					entityManager.flush();
					List<PaymentType> stripePaymentType = entityManager.createNamedQuery("findPaymentTypeByPaymentType")
																.setParameter("paymentType", "Stripe")
																.getResultList();
					for(PaymentType paymentType: stripePaymentType){
						PayMethodBean payMethodBean = new PayMethodBean();
						payMethodBean.localeSelector = localeSelector;
						payMethodBean.entityManager = entityManager;
						payMethodBean.createPaymentMethods(paymentType, company, paymentKeys);
					}
				} else {
					CloseableHttpClient httpClient = HttpClients.createDefault();
					URI uri = new URIBuilder( TOKEN_URI)
		                .setParameter("client_secret", API_KEY_SECRET(company)) 
		                .setParameter("grant_type", "authorization_code")
		                .setParameter("client_id", CLIENT_ID(company))
		                .setParameter("code", code)
		                .build();
		
					HttpPost httpPost = new HttpPost(uri);
					CloseableHttpResponse resp = httpClient.execute(httpPost);
					
					String bodyAsString = EntityUtils.toString(resp.getEntity());
					Type t = new TypeToken<Map<String, String>>() { }.getType();
					Map<String, String> map = new GsonBuilder().create().fromJson(bodyAsString, t);
					
					System.out.println("Body As String : " + bodyAsString);
					
					String accountId = map.get("stripe_user_id");
					String secrectApiKey = map.get("access_token");
					String publishablekey = map.get("stripe_publishable_key");
					String country = getStripeCountry(accountId, secrectApiKey);
					String currency = getStripeCurrency(accountId, secrectApiKey);
					System.out.println("Received Account Id :: " + accountId);
					System.out.println("Secret Key :: " + secrectApiKey);
					System.out.println("Publishable Key :: " + publishablekey);
					System.out.println("Stripe key is updated for this company id : "+ companyId);
					PaymentKeys paymentKeys = stripePay.getPaymentKeys();
					paymentKeys.setAccountId(accountId);
					paymentKeys.setPayParam1(secrectApiKey);
					paymentKeys.setPayParam2(publishablekey);
					paymentKeys.setCompany(company);
					paymentKeys.setAccountConnected(true);
					paymentKeys.setPaymentType(PayMethodBean.Stripe);
					paymentKeys.setAccountCountry(country);
					paymentKeys.setAccountCurrencyCode(currency != null ? currency.toUpperCase() : currency);
					paymentKeys.setCreatedDate(new Date());
					paymentKeys.setModifiedDate(new Date());
					entityManager.persist(paymentKeys);
					entityManager.flush();
				}
			}
		}catch(Exception e){
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public String getStripeCurrency(String accountId, String secrectApiKey) {
		String currency = null;
		try {
			if(secrectApiKey != null && !secrectApiKey.isEmpty() && accountId != null && !accountId.isEmpty()) {
		    	com.stripe.Stripe.apiKey = secrectApiKey.trim();
		    	try {
		    		Account account = Account.retrieve(accountId.trim());
		    		currency = account != null ? account.getDefaultCurrency() : null;
		    	}catch(Exception e) {
		    		currency = null;
		    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		    	}
			}
		}catch(Exception e){
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return currency;
	}

	public String getStripeCountry(String accountId, String secrectApiKey) {
		String country = null;
		try {
			if(secrectApiKey != null && !secrectApiKey.isEmpty() && accountId != null && !accountId.isEmpty()) {
		    	com.stripe.Stripe.apiKey = secrectApiKey.trim();
		    	try {
		    		Account account = Account.retrieve(accountId.trim());
		    		country = account != null ? account.getCountry() : StaticValues.DefaultStripeCountry;
		    	}catch(Exception e) {
		    		country = null;
		    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		    	}
			}
		}catch(Exception e){
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return country;
	}
	
	public static Integer convertToCents(double amount){
		try{
			amount = Double.valueOf(roundOffDouble(amount)).doubleValue();
	    	Integer returnValue = (int) (amount * 100);
	    	return returnValue;
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return 0;
    }
	
	public static String roundOffDouble(double d) { 
    	try{
	        DecimalFormat fmt = new DecimalFormat("0.00");
	        String string = fmt.format(d);   
	        return string.replace(",", ".");
    	}catch (Exception e) {
    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
    	return null;
    }
	
	public String createCustomerForSepaRecurring(String sepa_src, String customerEmail, String apiKey){
    	try {
    		com.stripe.Stripe.apiKey = apiKey;
    		Map<String, Object> customerParams = new HashMap<String, Object>();
    		customerParams.put("email", customerEmail);
    		customerParams.put("source", sepa_src);

    		Customer customer = Customer.create(customerParams);
			return customer.getId();
    	}catch (Exception e) {
    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return null;
    }
	
	public static String stripeSourceSplit(String source) {
		try {
			if(source != null && !source.isEmpty()) {
				String[] strSplitH = source.split(StaticValues.ManualSeperator);
				String[] strSplitC = source.split(StaticValues.Comma);
				
				if(strSplitH != null && strSplitH.length > 1 && source.startsWith(Stripe_Source_Prefix)) {
					for(String str: strSplitH) {
						if(str.trim().startsWith(Stripe_Source_Prefix)) {
							return str.trim();
						}
					}
				}else if(strSplitC != null && strSplitC.length > 1 && source.startsWith(Stripe_Source_Prefix)) {
					for(String str: strSplitC) {
						if(str.trim().startsWith(Stripe_Source_Prefix)) {
							return str.trim();
						}
					}
				}else {
					return source.trim();
				}
			}
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return null;
	}
	
	public String convertTokenToSource(String token, String customerEmail, String apiKey){
    	try {
    		
    		com.stripe.Stripe.apiKey = apiKey;
    		
    		Map<String, Object> sourceParams = new HashMap<String, Object>();
    		sourceParams.put("type", "card");
    		sourceParams.put("token", token);

    		Source convertedSource = Source.create(sourceParams); 
			return convertedSource.getId();
    	}catch (Exception e) {
    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return null;
    }
	
	public Map<String, String> SecretPublishableKey(PaymentMethod paymentMethod) {
		try{
			boolean local = new MainUtil().checkLocal();
			Map<String, String> spkeyMap = new HashMap<String, String>();
			PaymentType payType = paymentMethod.getPaymentType();
			PaymentKeys payKeys = paymentMethod.getPaymentKeys();
			Company company = paymentMethod.getCompany();
			
			String stripeType = payType!=null && payType.getPaymentType() != null ? payType.getPaymentType() : null;
			
			if(paymentMethod!=null && payType!=null && payKeys!=null && stripeType != null && stripeType.equalsIgnoreCase(PayMethodBean.Stripe)){
				String acct = payKeys.getAccountId();
				acct = acct != null && !acct.trim().isEmpty() && acct.trim().startsWith(Stripe_Account_Prefix) ? acct : null;
				String skey = payKeys.getPayParam1();
				skey = skey != null && !skey.trim().isEmpty() && skey.trim().startsWith(Stripe_SecretKey_Prefix) ? skey : null;
				String pkey = payKeys.getPayParam2();
				pkey = pkey != null && !pkey.trim().isEmpty() && pkey.trim().startsWith(Stripe_PublishableKey_Prefix) ? pkey : "";
				
				if(!company.isSelfStripeAccount()) {
					skey = API_KEY_SECRET(company);
					pkey = API_KEY_PUBLISHABLE(company);
				}
				
				if(local || !company.isLiveAccount()){	// Test Keys
					skey = payKeys.getTestPayParam1();
					skey = skey != null && !skey.trim().isEmpty() && skey.trim().startsWith(Stripe_SecretKey_Prefix) ? skey : null;
					
					pkey = payKeys.getTestPayParam2();
					pkey = pkey != null && !pkey.trim().isEmpty() && pkey.trim().startsWith(Stripe_PublishableKey_Prefix) ? pkey : "";
				}
				
				spkeyMap.put("accountId", acct);
				spkeyMap.put("secretKey", skey);
				spkeyMap.put("publishableKey", pkey);
				return spkeyMap;
			}
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return null;
	}
	
	public static Integer totalamountChargable(double amount){
		try{
			amount = Double.valueOf(roundOffDouble(amount)).doubleValue();
	    	Integer returnValue = (int) (amount * 100);
	    	return returnValue;
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return 0;
    }
	
	public String changeNormalSourceToSepaRecurring(String pay, String src, String currency, String customerEmail){
		try {
    		Map<String, Object> sourceParams = new HashMap<String, Object>();
			sourceParams.put("type", "sepa_debit");
			sourceParams.put("currency", currency);
			
			Map<String, Object> sepaParams = new HashMap<String, Object>();
			sepaParams.put(pay, src);
			sourceParams.put("sepa_debit", sepaParams);
			
			String owner = StringUtil.fixed3DotLengthString(customerEmail);
			if(owner != null && !owner.trim().isEmpty()) {
				Map<String, Object> ownerParams = new HashMap<String, Object>();
				ownerParams.put("name", owner.trim());
				sourceParams.put("owner", ownerParams);
			}
			Source source = Source.create(sourceParams);
			return source.getId();
    	}catch (Exception e) {
    		ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return null;
	}
	
	public void responseFromWebhooks() {
		HttpServletRequest request = null;
		try {
			request = ServletContexts.instance().getRequest();
			String orgid = request.getParameter("orgid");
			String connect = request.getParameter("connect");
			
			if(orgid != null && Integer.valueOf(orgid) > 0){
				
			}else if(connect != null && connect.equals("true")){
				
			}else{
				
			}
			
			String rawJson = IOUtils.toString(request.getInputStream());
			Event event = ApiResource.GSON.fromJson(rawJson, Event.class);
			if(rawJson != null && !rawJson.trim().isEmpty()) {
				 event = ApiResource.GSON.fromJson(rawJson, Event.class);
			}
			 
			if(event != null){
				if(event.getType().equals(transfer_created)){
					System.out.println("Transfer created : "+ event.getData().getObject());
					JSONObject jsonObj = new JSONObject(event.getData().getObject());
					String srcTrans = (String) jsonObj.getString("sourceTransaction");
					String desTrans = (String) jsonObj.getString("destinationPayment");
					String desAcct = (String) jsonObj.getString("destination");
					PaymentKeys pparam = null;
					try {
						pparam = (PaymentKeys) entityManager.createNamedQuery("findPaymentKeysByCompanyByAccountId")
																.setParameter("accountId", desAcct.trim())
																.getSingleResult();
					}catch (Exception e) {
						 pparam = null;
					}
					if(pparam != null && pparam.getPayParam1() != null && !pparam.getPayParam1().isEmpty()){
						Charge srcTransCharge = Charge.retrieve(srcTrans);
						String descriptionUpdate = srcTransCharge.getDescription();
						Stripe.apiKey = pparam.getPayParam1();	
						Charge descTransCharge = Charge.retrieve(desTrans);
						Map<String, Object> updateParams = new HashMap<String, Object>();
						updateParams.put("description", descriptionUpdate);
						descTransCharge.update(updateParams);
					}
				}
				if(event.getType().equals(charge_succeeded)){
					 
				}
				if(event.getType().equals(charge_dispute_created)){
					System.out.println("dispute created : "+ event.getData().getObject());
				}
				if(event.getType().equals(charge_dispute_funds_reinstated)){
					System.out.println("dispute fund reinstated created : "+ event.getData().getObject());
				}
				if(event.getType().equals(charge_dispute_funds_withdrawn)){
					System.out.println("charge dispute funds withdrawn created : "+ event.getData().getObject());
				}
				if(event.getType().equals(charge_dispute_closed)){
					System.out.println("charge dispute closed created : "+ event.getData().getObject());
				}
				if(event.getType().equals(source_chargeable)){
					System.out.println("source chargeable created : "+ event.getData().getObject());
					JSONObject jsonObj = new JSONObject(event.getData().getObject());
					String stripeSource = (String) jsonObj.getString("id");
					String payType = (String) jsonObj.getString("type");
					String usage = (String) jsonObj.getString("usage");
					if(checkSingleUsagePayType(payType) && usage.equalsIgnoreCase("single_use")){
						System.out.println(stripeSource);
						chargeFromWebhook(payType, stripeSource);
					}	
				}
			}
		} catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void chargeFromWebhook(String payType, String stripeSource) {
		DonationCart donationCart = new DonationCart();
		donationCart.entityManager = entityManager;
		try {
			if(stripeSource != null) {
				List<FundDetails> fcdSrcEntries = null;
				try{
					String query = "select fcd from FundDetails fcd where fcd.stripeSrc LIKE '%"+stripeSource+"%'";
					fcdSrcEntries = entityManager.createQuery(query).getResultList();
				}catch (Exception e) {
					fcdSrcEntries = null;
				}
				if(fcdSrcEntries != null && fcdSrcEntries.size() > 0){
					donationCart.setStripeSource(stripeSource);
					donationCart.executeStripePayment(fcdSrcEntries);
				}
			}
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public String getOtherCardFee() {
		return NonEUCardFee;
	}
	
	public String getEuropeCountryCodes() {
		List<String> list = new ArrayList<String>();
		for(String eu : EU_COUNTRY_CODES) {
			list.add(eu);
		}
		return list.toString();
	}
	
	public boolean EUCountryCodeExists(String countyCode) {
		try{
			if(countyCode != null && !countyCode.trim().isEmpty()) {
				 List<String> list = Arrays.asList(EU_COUNTRY_CODES);
				 if(list.contains(countyCode.trim())) {
					 return true;
				 }
			}
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return false;
	}
	
}
