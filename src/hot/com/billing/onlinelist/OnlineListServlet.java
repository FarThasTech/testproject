package com.billing.onlinelist;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;

import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.Messages;

import com.billing.entity.Users;
import com.billing.exceptions.ExceptionMsg;
import com.billing.util.DateUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@SuppressWarnings("unchecked")
public class OnlineListServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	
	 @Resource
	 private UserTransaction userTransaction;

	public OnlineListServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		try{
			userTransaction.begin();
			ServletLifecycle.beginRequest(request);
			EntityManagerFactory emf = (EntityManagerFactory)new InitialContext().lookup("java:/BillingEntityManagerFactory");  
			EntityManager entityManager = emf.createEntityManager();
			response.setContentType("application/json; charset=UTF-8");
			PrintWriter out = response.getWriter();
			OnlineVOListDTO dataTableObject = new OnlineVOListDTO();
			String usersId = request.getParameter("activeUsersId");
			String companyId = request.getParameter("activeCompanyId");
			String localeId = request.getParameter("activeLocale");
			String startDate = request.getParameter("startDate");
			String endDate = request.getParameter("endDate");
			if(startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
				Date sDate = DateUtil.getStringToDateFormat(startDate, DateUtil.DATE_FORMAT);
				Date eDate = DateUtil.getStringToDateFormat(endDate, DateUtil.DATE_FORMAT);
				if(sDate != null && sDate != null) {
					startDate = DateUtil.getDateToStringFormat(sDate, DateUtil.DATE_FORMAT_US);
					endDate = DateUtil.getDateToStringFormat(eDate, DateUtil.DATE_FORMAT_US);
				}
			}
			Users activeUser = null;
			if(usersId != null && !usersId.trim().isEmpty()){
				int userId = Integer.parseInt(usersId.trim());
				activeUser = entityManager.find(Users.class, userId);
			}
			if(activeUser == null && companyId != null && !companyId.trim().isEmpty()){
				int orgId = Integer.parseInt(companyId.trim());
				List<Users> usersList = entityManager.createQuery("select users from Users users "
										+ "where users.company.id = "+ orgId + " order by id asc")
										.setMaxResults(1)
										.getResultList();
				if(usersList!=null && usersList.size()>0){
					activeUser = usersList.get(0);
				}
		 	}
			LocaleSelector localeSelector = new LocaleSelector();
			localeSelector.setLocaleString(localeId != null && !localeId.isEmpty() ? localeId : "en");
			OnlineListBean financeBean = new OnlineListBean();
			financeBean.onlineListVO = new OnlineListVO();
			financeBean.activeUser = activeUser;
			financeBean.localeSelector = localeSelector;
			financeBean.entityManager = entityManager;
			List<OnlineListVO> financeList = financeBean.getList("Online List", startDate, endDate);
			List<OnlineListVO> financeListVO = new ArrayList<OnlineListVO>();
			OnlineListVO financeVO = new OnlineListVO();
			Locale locale = new Locale(localeSelector.getLocaleString());
			NumberFormat currencyFormatter = NumberFormat.getNumberInstance(locale);
			currencyFormatter.setMinimumFractionDigits(2);
			currencyFormatter.setMaximumFractionDigits(2);
			
			for(OnlineListVO finVO: financeList) {
				String email = finVO.getEmail();
				String telephone = finVO.getTelephone();
				String userNr = finVO.getUserNr();
				String firstName = finVO.getFirstName();
				String lastName = finVO.getLastName();
				firstName = firstName != null ? firstName.trim() : "";
				lastName = lastName != null ? lastName.trim() : "";
				String fullName = firstName + " " + lastName;
				String address = finVO.getAddress();
				String houseNo = finVO.getHouseNo();
				String zip = finVO.getZip();
				String city = finVO.getCity();
				String country = finVO.getCountry();
				address = address != null ? address.trim() : "";
				houseNo = houseNo != null ? houseNo.trim() : "";
				zip = zip != null ? zip.trim() : "";
				city = city != null ? city.trim() : "";
				country = country != null ? country.trim() : "";
				address = houseNo + " " + address + " " + city;
				String combineAddress = country + " " + zip;
				combineAddress = combineAddress != null && !combineAddress.trim().isEmpty() ? ("<br /> " + combineAddress) : "";
				String lang = finVO.getLang();
				int fundGroupId = finVO.getFundGroupId();
				financeVO = new OnlineListVO();
				financeVO.setUserNr(userNr != null ? userNr : "");
				financeVO.setUserNr("<a href=\"#\" onclick=\"redirectToDetails('"+fundGroupId+"')\">" + userNr +"</a>");
				financeVO.setFullName(fullName);
				financeVO.setFullAddress(address + combineAddress);
				financeVO.setLang(lang != null ? lang.trim() : "");
				financeVO.setEmail(email != null ? email.trim() : "");
				financeVO.setTelephone(telephone != null ? telephone.trim() : "");
				String campaignName = finVO.getCampaignName();
				campaignName = campaignName!= null && !campaignName.isEmpty() ? campaignName.trim() : "";
				financeVO.setCampaignName(campaignName);
				double amount = finVO.getAmount() != null && !finVO.getAmount().isEmpty() ? Double.valueOf(finVO.getAmount()) : 0.0;
				double wholeFee = finVO.getWholeFee() != null && !finVO.getWholeFee().isEmpty() ? Double.valueOf(finVO.getWholeFee()) : 0.0;
				double appFee = finVO.getAppFee() != null && !finVO.getAppFee().isEmpty() ? Double.valueOf(finVO.getAppFee()) : 0.0;
				double transFee = finVO.getTransFee() != null && !finVO.getTransFee().isEmpty() ? Double.valueOf(finVO.getTransFee()) : 0.0;
				boolean transTakenOver = finVO.isTransTakenOver();
				double totalAmount = amount, orgAmount = amount;
				if(transTakenOver) {
					totalAmount = amount + wholeFee;
				}else {
					orgAmount = amount - wholeFee;
				}
				financeVO.setAmount(currencyFormatter.format(totalAmount));
				financeVO.setAppFee(currencyFormatter.format(appFee));
				financeVO.setTransFee(currencyFormatter.format(transFee));
				financeVO.setOrgAmt(currencyFormatter.format(orgAmount));

				String paymentMethodName = finVO.getPaymentMethodName();
				paymentMethodName = paymentMethodName != null && !paymentMethodName.isEmpty() ? paymentMethodName.trim() : "";
				financeVO.setPaymentMethodName(paymentMethodName);
				
				String paymentStatusName = finVO.getPaymentStatusName();
				paymentStatusName = paymentStatusName != null && !paymentStatusName.isEmpty() ? paymentStatusName.trim() : "";
				financeVO.setPaymentStatusName(paymentStatusName);
				
				String transCode = finVO.getTransCode();
				transCode = transCode != null && !transCode.isEmpty() ? transCode.trim() : "";
				financeVO.setTransCode(transCode);
				
				String moreInfo = 	"<a href=\"#\" title=\""+Messages.instance().get("MoreInfo")+"\" " + 
										"onclick=\"redirectToDetails('"+ fundGroupId +"')\" >" + 
										"<i class=\"fa fa-align-justify\" style=\"color:#888ea8;\"></i>" +
									"</a>";
				financeVO.setLastColumn(moreInfo);
				financeListVO.add(financeVO);
			}
			dataTableObject.setAaData(financeListVO);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String json = gson.toJson(dataTableObject);
			out.print(json);
			ServletLifecycle.endRequest(request);
			userTransaction.commit();
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}

	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

