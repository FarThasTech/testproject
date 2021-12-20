package com.billing.donor;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@SuppressWarnings({"unchecked", "unused"})
public class DonorListServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	
	 @Resource
	 private UserTransaction userTransaction;

	public DonorListServlet() {
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
			DonorVOListDTO dataTableObject = new DonorVOListDTO();
			String usersId = request.getParameter("activeUsersId");
			String companyId = request.getParameter("activeCompanyId");
			String localeId = request.getParameter("activeLocale");
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
			DonorBean donorBean = new DonorBean();
			donorBean.donorVO = new DonorVO();
			donorBean.activeUser = activeUser;
			donorBean.localeSelector = localeSelector;
			donorBean.entityManager = entityManager;
			List<DonorVO> donorVOList = donorBean.getList("Donor")/* getDonorList() */;
			List<DonorVO> donorListVO = new ArrayList<DonorVO>();
			DonorVO donVO = new DonorVO();
			int index = 0;
			
			Locale locale = new Locale(localeSelector.getLocaleString());
			NumberFormat currencyFormatter = NumberFormat.getNumberInstance(locale);
			currencyFormatter.setMinimumFractionDigits(2);
			currencyFormatter.setMaximumFractionDigits(2);
			
			for(DonorVO donorVO: donorVOList) {
				int genderId = donorVO.getGenderId();
				String langId = donorVO.getLangid();
				String email = donorVO.getEmail();
				String telephone = donorVO.getTelephone();
				String createdUser = donorVO.getCreatedUser();
				String userNr = donorVO.getUserNr();
				String firstName = donorVO.getFirstName();
				String lastName = donorVO.getLastName();
				firstName = firstName != null ? firstName.trim() : "";
				lastName = lastName != null ? lastName.trim() : "";
				String fullName = firstName + " " + lastName;
				String address = donorVO.getAddress();
				String houseNo = donorVO.getHouseNo();
				String zip = donorVO.getZip();
				String state = donorVO.getState();
				String city = donorVO.getCity();
				String country = donorVO.getCountry();
				address = address != null ? address.trim() : "";
				houseNo = houseNo != null ? houseNo.trim() : "";
				zip = zip != null ? zip.trim() : "";
				city = city != null ? city.trim() : "";
				country = country != null ? country.trim() : "";
				address = houseNo + " " + address + " " + city;
				String combineAddress = country + " " + zip;
				combineAddress = combineAddress != null && !combineAddress.trim().isEmpty() ? ("<br /> " + combineAddress) : "";
				String lang = donorVO.getLang();
				String uAddrId = donorVO.getuAddrId();
				int fundGroupId = donorVO.getFundGroupId();
				donVO = new DonorVO();
				donVO.setUserNr(userNr != null ? userNr : "");
				donVO.setUserNr("<a href=\"#\" onclick=\"redirectToDetails('"+fundGroupId+"')\">" + userNr +"</a>");
				donVO.setFullName(fullName);
				donVO.setFullAddress(address + combineAddress);
				donVO.setLang(lang != null ? lang.trim() : "");
				donVO.setEmail(email != null ? email.trim() : "");
				donVO.setTelephone(telephone != null ? telephone.trim() : "");
				double amount = donorVO.getAmount() != null && !donorVO.getAmount().isEmpty() ? Double.valueOf(donorVO.getAmount()) : 0.0;
				double wholeFee = donorVO.getWholeFee() != null && !donorVO.getWholeFee().isEmpty() ? Double.valueOf(donorVO.getWholeFee()) : 0.0;
				double totalAmount = amount + wholeFee;
				donVO.setAmount(currencyFormatter.format(totalAmount));
				donVO.setCreatedUser(createdUser != null && !createdUser.isEmpty() ? createdUser : Messages.instance().get("Online"));
				donVO.setLastDate("01.01.2020");
//				String lastColumn = "<a href=\"javascript:void(0);\" title=\""+Messages.instance().get("Edit")+"\" " + 
//										"onclick=\"editDonor('"+donorVO.getUserId() + "','" + (index++) + "','" + (firstName) + "','" + (lastName) +
//										"','" + (address) + "','" + (houseNo) +  "','" + (state) + "','" + (city) + "','" + (country) + "','" + (zip) + 
//										"','" + (email) + "','" + (telephone) + "','" + (langId) + "','" + (genderId) + "','" + (uAddrId) +
//										"','" + (fundGroupId) +"' )\"" + 
//										"data-toggle=\"modal\" data-target=\"#addNewDonor\" >" + 
//										"<i class=\"fa fa-edit\" class=\"text-primary\" ></i>" +
//									"</a>";
				String moreInfo = 	"<a href=\"#\" title=\""+Messages.instance().get("MoreInfo")+"\" " + 
										"onclick=\"redirectToDetails('"+ fundGroupId +"')\" >" + 
										"<i class=\"fa fa-align-justify\" style=\"color:#888ea8;\"></i>" +
									"</a>";
				donVO.setLastColumn(/* lastColumn + " " + */moreInfo);
				donorListVO.add(donVO);
			}
			dataTableObject.setAaData(donorListVO);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String json = gson.toJson(dataTableObject);
			out.print(json);
			ServletLifecycle.endRequest(request);
			userTransaction.commit();
		}catch (Exception e) {
		
		}
	}

	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

