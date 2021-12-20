package com.billing.campaign;

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

import com.billing.entity.Users;
import com.billing.login.PasswordBean;
import com.billing.staticvalue.StaticValues;
import com.billing.util.StringUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@SuppressWarnings({"unchecked","static-access"})
public class CampaignListServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	
	 @Resource
	 private UserTransaction userTransaction;

	public CampaignListServlet() {
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
			CampaignVOListDTO dataTableObject = new CampaignVOListDTO();
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
			localeSelector.setLocaleString(StringUtil.checkStringIsNull(localeId) ? localeId : "en");
			Locale locale = new Locale(localeSelector.getLocaleString());
			NumberFormat currencyFormatter = NumberFormat.getNumberInstance(locale);
			currencyFormatter.setMinimumFractionDigits(2);
			currencyFormatter.setMaximumFractionDigits(2);
			CampaignBean campaignBean = new CampaignBean();
			campaignBean.campaignVO = new CampaignVO();
			campaignBean.activeUser = activeUser;
			campaignBean.localeSelector = localeSelector;
			campaignBean.entityManager = entityManager;
			List<CampaignVO> campaignVOList = campaignBean.getCampaignList("Campaign");
			List<CampaignVO> campaignListVO = new ArrayList<CampaignVO>();
			CampaignVO campaignVO = new CampaignVO();
			int index = 0;
			String prodGroupIdStr = StaticValues.ProductGroup;
			String companyIdStr = String.valueOf(activeUser.getCompany().getId());
			for(CampaignVO campVO: campaignVOList) {
				campaignVO = new CampaignVO();
				campaignVO.setCategoryName(campVO.getCategoryName());
				int prodGroupId = campVO.getProdGroupId();
				String encryptProdGroupUrl = PasswordBean.getInstance().encryptWithMD5DES(companyIdStr+prodGroupIdStr+String.valueOf(prodGroupId));
				String encryptProdGroupId = PasswordBean.getInstance().encryptWithMD5DES(String.valueOf(prodGroupId));
				String cBox = "<a target=\"_DBOX\" href=\"dbox.jsf?"+encryptProdGroupUrl+"\"><i class=\"fa fa-external-link\"></i></a>";
				String campaignName = campVO.getCampaignName();
				campaignVO.setCampaignName(campaignName != null && !campaignName.isEmpty() ? (campaignName + " " + cBox) : cBox);
				String imageUrl = campVO.getImageUrl();
				campaignVO.setImageUrl(StringUtil.checkStringIsNull(imageUrl) ? "<img src=\""+StringUtil.getImageToEncodeImage(imageUrl)+"\" style=\"height: 75px;\" />" : "");
				campaignVO.setAmountStr(currencyFormatter.format(campVO.getAmount()));
				String description = campVO.getDescription() != null ? campVO.getDescription() : "";
				String descMsg = "";
				if(description != null && !description.isEmpty()) {
					description =  description.replaceAll("\\<.*?\\>", "");
					if(description.trim().length() > 25)
						descMsg = description.substring(0, 25) + "... <a href=\"#\" onclick=\"showDescription('"+description+"');\"><i class=\"fa fa-info\" /> </a>";
					else
						descMsg = description;
				}
				campaignVO.setDescription(descMsg); 
				
				campaignVO.setTargetAmountStr(currencyFormatter.format(campVO.getTargetAmount()));
//						"<div class=\"progress-border-style\">"+
//						 	"<div class=\"progress\">"+
//								"<div class=\"progress-bar progress-bar-danger progress-bar-striped progress-bar-animated\" role=\"progressbar\" "+
//									"aria-valuenow=\"#{donationCart.progressValue}\" aria-valuemin=\"0\" aria-valuemax=\"100\" style=\"width: #{donationCart.progressValue}%\">"+
//								  	"<span></span>"+
//								"</div>"+
//						  	"</div>"+
//						  	"<div style=\"text-align: center;margin-top: -15px;font-size: 1.5rem !important;font-weight: 600 !important;\">"+
//					  			"<span>#{donationCart.progressValue}% #{messages.Collected}</span>"+
//					  		"</div>"+
//				  		"</div>" + 
						
				campaignVO.setProdGroupStatusStr(campVO.isProdGroupStatus() 
									? "<a href=\"#\" onclick=\"updateStatus('Enabled','" + (index++) + "','" + prodGroupId + "');\" ><span class=\"badge badge-success\">Enabled</span></a>" 
									: "<a href=\"#\" onclick=\"updateStatus('Disabled','" + (index++) + "','" + prodGroupId + "');\" ><span class=\"badge badge-danger\">Disabled</span></a>");
				campaignVO.setLastColumn("<a href=\"campaign.jsf?prodGroupId="+encryptProdGroupId+"\"><i class=\"fa fa-edit\" ></i></a> "
						+ " <a href=\"#\" onclick=\"deleteProductGroup('" + prodGroupId + "');\" ><i class=\"fa fa-trash-o\" style=\"color:red;\"></i></a>");
				campaignListVO.add(campaignVO);
			}
			dataTableObject.setAaData(campaignListVO);
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

