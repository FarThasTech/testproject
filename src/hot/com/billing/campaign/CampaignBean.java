package com.billing.campaign;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.web.ServletContexts;

import com.billing.category.CategoryBean;
import com.billing.donor.EncryptDecrypt;
import com.billing.entity.Category;
import com.billing.entity.CategoryLanguage;
import com.billing.entity.Company;
import com.billing.entity.Product;
import com.billing.entity.ProductGroup;
import com.billing.entity.ProductLanguage;
import com.billing.entity.ProductSubType;
import com.billing.entity.ProductType;
import com.billing.entity.Users;
import com.billing.exceptions.ExceptionMsg;
import com.billing.language.LanguageBean;
import com.billing.login.PasswordBean;
import com.billing.product.ProductBean;
import com.billing.staticvalue.StaticValues;
import com.billing.util.MainUtil;
import com.billing.util.NumberUtil;
import com.billing.util.StringUtil;

@Scope(ScopeType.SESSION)
@Name("campaignBean")
@SuppressWarnings({"unchecked","static-access"})
public class CampaignBean {
	
	@In
	public EntityManager entityManager;
	    
	@In
	public LocaleSelector localeSelector;
	
	@In(create=true,required=false) 
    public Users activeUser;
	
	@In(create=true)
	@Out(scope=ScopeType.SESSION)
	public CampaignVO campaignVO;
	
	public void execute() {
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			String param = request.getParameter("param");
			if(param != null && !param.isEmpty()) {
				if(param.equalsIgnoreCase("save")) {
					persist();
				}else if(param.equalsIgnoreCase("update")) {
					update();
				}
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void updateProdGroupStatus() {
		try {
			reset();
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			String prodGroupId = request.getParameter("prodGroupId");
			String status = request.getParameter("status");
			if(prodGroupId != null && !prodGroupId.isEmpty() && NumberUtil.checkNumeric(prodGroupId.trim()) && Integer.valueOf(prodGroupId.trim()) > 0) {
				ProductGroup prodGroup = entityManager.find(ProductGroup.class, Integer.valueOf(prodGroupId.trim()));
				if(prodGroup != null && prodGroup.getCompany().getId() == activeUser.getCompany().getId()) {
					if(StringUtil.checkStringIsNull(status) && status.equalsIgnoreCase("true"))
						prodGroup.setEnable(true);
					else if(StringUtil.checkStringIsNull(status) && status.equalsIgnoreCase("false"))
						prodGroup.setEnable(false);
					entityManager.merge(prodGroup);
					entityManager.flush();
				}
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void deleteProdGroup() {
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			String prodGroupId = request.getParameter("prodGroupId");
			if(prodGroupId != null && !prodGroupId.isEmpty() && NumberUtil.checkNumeric(prodGroupId.trim()) && Integer.valueOf(prodGroupId.trim()) > 0) {
				ProductGroup prodGroup = entityManager.find(ProductGroup.class, Integer.valueOf(prodGroupId.trim()));
				if(prodGroup != null && prodGroup.getCompany().getId() == activeUser.getCompany().getId()) {
					prodGroup.setEnable(false);
					prodGroup.setDeleted(true);
					prodGroup.setDeletedBy(activeUser);
					prodGroup.setDeletionDate(new Date());
					entityManager.merge(prodGroup);
					entityManager.flush();
				}
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void showEditPage() {
		try {
			reset();
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			String prodGroupId = request.getParameter("prodGroupId");
			if(activeUser != null) {
				CategoryBean cBean = new CategoryBean();
				cBean.entityManager = entityManager;
				String langCode = localeSelector.getLocaleString();
				boolean deleted = false, enable = true;
				int companyId = activeUser.getCompany().getId();
				List<CategoryLanguage> categoryLangList = cBean.getCategoryLanguageByCategoryAndLangCompanyDeletedEnable(companyId, deleted, langCode, enable);
				campaignVO.setCategoryLangList(categoryLangList);
				ProductBean prodBean = new ProductBean();
				prodBean.entityManager = entityManager;
				prodBean.activeUser = activeUser;
				List<ProductSubType> recurringSubTypeList = prodBean.getActiveRecurringSubType(true);
				campaignVO.setRecurringSubTypeList(recurringSubTypeList);
				
				List<ProductType> prodOtherTypeList = prodBean.getActiveProductOtherSubType();
				if(prodOtherTypeList != null && prodOtherTypeList.size() > 0) {
					campaignVO.setProdOtherTypeList(prodOtherTypeList);
				} else {
					campaignVO.setDisableProdOtherType(true);
				}
				
				if(prodGroupId != null && !prodGroupId.isEmpty()) {
					prodGroupId = EncryptDecrypt.correctTheDecrypValue(prodGroupId.trim());
					prodGroupId = PasswordBean.getInstance().decryptWithMD5DES(prodGroupId);
					if(NumberUtil.checkNumeric(prodGroupId.trim()) && Integer.valueOf(prodGroupId.trim()) > 0) {
						ProductGroup prodGroup = entityManager.find(ProductGroup.class, Integer.valueOf(prodGroupId.trim()));
						if(prodGroup != null && prodGroup.getCompany().getId() == activeUser.getCompany().getId()) {
							BigInteger count = (BigInteger) entityManager.createNativeQuery("select count(*) from fund_details where id_productgroup = " + prodGroup.getId()).getSingleResult();
							if (count != null && count.intValue() > 0) {
								campaignVO.setDisableAll(true);
							} else {
								campaignVO.setDisableAll(false);
							}
							System.out.println("Campaign edit page is called....! prodGroupId is : "+ prodGroupId + " Company Id is: " + activeUser.getCompany().getId());
							String campaignName = "", description = "";
							int prodTypeId = 1, prodOtherTypeId = 0;
							String recurringTypeSubName = "";
							campaignVO.setCategoryId(prodGroup.getCategory() != null ? prodGroup.getCategory().getId() : 0);
							Product product = prodGroup.getProduct();
							String localeString = localeSelector.getLocaleString();
							if(product != null) {
								Set<ProductLanguage> prodLangList = product.getProductLanguage();
								if(prodLangList != null && prodLangList.size() > 0) {
									for(ProductLanguage prodLang : prodLangList) {
										if(prodLang != null && prodLang.getLanguages() != null 
												&& prodLang.getLanguages().getLangCode().equalsIgnoreCase(localeString)) {
											campaignName = prodLang.getProductName();
											description = prodLang.getDescription();
										}
									}
								}
								Set<ProductType> productTypeList = product.getProductAccess();
								if(productTypeList != null && productTypeList.size() > 0) {
									for(ProductType prodType : productTypeList) {
										if(prodType != null) {
											if(prodType.getTypeName().equalsIgnoreCase(StaticValues.Quantity) 
												||  prodType.getTypeName().equalsIgnoreCase(StaticValues.Amount)){
												prodTypeId = prodType.getId();
											} else if(prodType.getTypeName().equalsIgnoreCase(StaticValues.Recurring)) {
												recurringTypeSubName = prodBean.getProdSubTypeName(product);
											} else if(prodType.getTypeName().equalsIgnoreCase(StaticValues.ManageWaterWell)
													|| prodType.getTypeName().equalsIgnoreCase(StaticValues.ManageOrphanManagement)) {
												prodOtherTypeId = prodType.getId();
											}
										}
									}
								}
							}
							String imageUrl = prodGroup.getImageUrl();
							campaignVO.setProdGroupIdStr(prodGroupId);
							campaignVO.setCampaignName(campaignName);
							campaignVO.setProdTypeId(prodTypeId);
							campaignVO.setProdOtherTypeId(prodOtherTypeId);
							campaignVO.setRecurringTypeSubName(recurringTypeSubName);
							campaignVO.setDescription(description);
							campaignVO.setAmountStr(NumberUtil.doubleWith2Digit(prodGroup.getAmount()));
							campaignVO.setFirstAmountStr(NumberUtil.doubleWith2Digit(prodGroup.getFirstAmount()));
							campaignVO.setSecondAmountStr(NumberUtil.doubleWith2Digit(prodGroup.getSecondAmount()));
							campaignVO.setThirdAmountStr(NumberUtil.doubleWith2Digit(prodGroup.getThirdAmount()));
							campaignVO.setFourthAmountStr(NumberUtil.doubleWith2Digit(prodGroup.getFourthAmount()));
							campaignVO.setTargetAmountStr(NumberUtil.doubleWith2Digit(prodGroup.getTargetAmount()));
							campaignVO.setCollectedAmountStr(NumberUtil.doubleWith2Digit(prodGroup.getCollectedAmount()));
							campaignVO.setImageUrl(StringUtil.checkStringIsNull(imageUrl) ? StringUtil.getImageToEncodeImage(imageUrl) : "");
							campaignVO.setProdGroupStatus(prodGroup.isEnable());
							campaignVO.setParam("update");
						}else {
							campaignVO.setParam("save");
						}
					}else {
						campaignVO.setParam("save");
					}
				}else {
					campaignVO.setParam("save");
				}
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void reset() {
		try {
			campaignVO.setDurationTypeId(0);
			campaignVO.setProdGroupIdStr("");
			campaignVO.setCampaignName("");
			campaignVO.setImageUrl("");
			campaignVO.setDescription("");
			campaignVO.setAmountStr("");
			campaignVO.setFifthAmountStr("");
			campaignVO.setFirstAmountStr("");
			campaignVO.setSecondAmountStr("");
			campaignVO.setThirdAmountStr("");
			campaignVO.setFourthAmountStr("");
			campaignVO.setTargetAmountStr("");
			campaignVO.setCollectedAmountStr("");
			campaignVO.setProdGroupStatus(false);
			campaignVO.setParam("save");
			campaignVO.setCategoryName("");
			campaignVO.setCategoryId(0);
			campaignVO.setCategoryLangList(null);
			campaignVO.setProdTypeId(1);
			campaignVO.setProdOtherTypeId(0);
			campaignVO.setRecurringTypeId(null);
			campaignVO.setRecurringTypeSubName("");
			campaignVO.setRecurringSubTypeList(null);
			campaignVO.setProdOtherTypeList(null);
			campaignVO.setDisableProdOtherType(false);
			campaignVO.setDisableCampaignDetails(false);
			campaignVO.setDisableAll(false);
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void update() {
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();
			String prodGroupId = request.getParameter("prodGroupId");
			String campaignTitle = request.getParameter("campaignTitle");
			String prodTypeId = request.getParameter("prodTypeId");
			String prodOtherTypeId = request.getParameter("prodOtherTypeId");
			String recurringTypeId = request.getParameter("recurringTypeId");
			String description = request.getParameter("description");
			String source = request.getParameter("campaignImageSource");
			String fileName = request.getParameter("campaignImageName");
			String amount = request.getParameter("amount");
			String externalAmt = request.getParameter("externalAmt");
			String targetAmt = request.getParameter("targetAmt");
			String firstAmt = request.getParameter("firstAmt");
			String secondAmt = request.getParameter("secondAmt");
			String thirdAmt = request.getParameter("thirdAmt");
			String fourthAmt = request.getParameter("fourthAmt");
			String status = request.getParameter("status");
			String categoryId = request.getParameter("categoryId");
			
			if(campaignTitle != null && !campaignTitle.isEmpty() 
					&& prodGroupId != null && !prodGroupId.isEmpty()
					&& NumberUtil.checkNumeric(prodGroupId.trim()) && Integer.valueOf(prodGroupId.trim()) > 0) {
				Company company = activeUser.getCompany();
				int companyId = company.getId();
				String langCode = localeSelector.getLocaleString();
				ProductGroup prodGroup = entityManager.find(ProductGroup.class, Integer.valueOf(prodGroupId.trim()));
				
				if(StringUtil.checkStringIsNotNullAndInteger(categoryId)) {
					Category category = entityManager.find(Category.class, Integer.valueOf(categoryId.trim()));
					if(category != null) {
						prodGroup.setCategory(category);
					}
				} 
				
				Product product = prodGroup.getProduct();
				if(product != null) {
					List<ProductLanguage> prodLangList = entityManager.createNamedQuery("findProductLanguageByProductAndLang")
																.setParameter("productId", product.getId())
																.setParameter("langCode", langCode)
																.getResultList();
					if(prodLangList != null && prodLangList.size() > 0) {
						ProductLanguage prodLang = prodLangList.get(0);
						if(prodLang != null) {
							prodLang.setProductName(campaignTitle);
							prodLang.setDescription(description);
							entityManager.merge(prodLang);
							entityManager.flush();
						} 
					} else {
						LanguageBean langBean = new LanguageBean();
						langBean.entityManager = entityManager;
						ProductLanguage prodLang = new ProductLanguage();
						prodLang.setProductName(campaignTitle);
						prodLang.setLanguages(langBean.getLangFromLangCode(langCode));
						prodLang.setProduct(product);
						prodLang.setCreatedDate(new Date());
						prodLang.setModifiedDate(new Date());
						prodLang.setDescription(description);
						entityManager.persist(prodLang);
						entityManager.flush();
					}
				}
				ProductBean prodBean = new ProductBean();
				prodBean.entityManager = entityManager;
				String prodTypeIds = prodTypeId + "," + prodOtherTypeId;
				if(StringUtil.checkStringIsNull(recurringTypeId)) {
					prodTypeIds = prodTypeIds + "," + StaticValues.RecurringId;
				}
				prodBean.updateProductType(prodTypeIds, product);
				entityManager.merge(product);
				entityManager.flush();
				prodBean.updateProductSubType(product, recurringTypeId);
				
				prodGroup.setAmount(Double.valueOf(amount));
				double amt = prodGroup.getAmount();
				if(StringUtil.checkStringIsNull(firstAmt)) {
					prodGroup.setFirstAmount(Double.valueOf(firstAmt));
				}else {
					prodGroup.setFirstAmount(amt);
				}
				if(StringUtil.checkStringIsNull(secondAmt)) {
					prodGroup.setSecondAmount(Double.valueOf(secondAmt));
				}else {
					prodGroup.setSecondAmount(amt * 3);
				}
				if(StringUtil.checkStringIsNull(thirdAmt)) {
					prodGroup.setThirdAmount(Double.valueOf(thirdAmt));
				}else {
					prodGroup.setThirdAmount(amt * 5);
				}
				if(StringUtil.checkStringIsNull(fourthAmt)) {
					prodGroup.setFourthAmount(Double.valueOf(fourthAmt));
				}else {
					prodGroup.setFourthAmount(amt * 10);
				}
				if(StringUtil.checkStringIsNull(targetAmt))
					prodGroup.setTargetAmount(Double.valueOf(targetAmt));
				if(StringUtil.checkStringIsNull(externalAmt))
					prodGroup.setExternalDonation(Double.valueOf(externalAmt));
				if(StringUtil.checkStringIsNull(description))
					description = StringUtils.normalizeSpace( description );
				
				if(status != null && !status.isEmpty() && status.equalsIgnoreCase("true"))
					prodGroup.setEnable(true);
				else if(status != null && !status.isEmpty() && status.equalsIgnoreCase("false"))
					prodGroup.setEnable(false);
				
				prodGroup.setDescription(description);
				prodGroup.setCompany(company);
				prodGroup.setCreatedDate(new Date());
				prodGroup.setModifiedDate(new Date());
				if(source != null && !source.isEmpty()) {
					String imagePath = MainUtil.getProductGroupImagePath(companyId) + prodGroupId;
					// String extension = FilenameUtils.getExtension(fileName); 
					fileName = prodGroupId + ".png";
					String imageUrl = createimage(source, fileName, imagePath);
					imageUrl = MainUtil.getImageSourcePath(activeUser.getCompany().getId() , "ProductGroup") + prodGroupId + MainUtil.getFileSeparator() + fileName ;
					prodGroup.setImageUrl(imageUrl);
				}
				entityManager.merge(prodGroup);
				entityManager.flush();
				ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1], "Product group updated for this Id : " + prodGroupId + " and company id is: "+ company.getId());
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void persist() {
		try {
			HttpServletRequest request =ServletContexts.instance().getRequest();	
			String campaignTitle = request.getParameter("campaignTitle");
			String prodTypeId = request.getParameter("prodTypeId");
			String prodOtherTypeId = request.getParameter("prodOtherTypeId");
			String recurringTypeId = request.getParameter("recurringTypeId");
			String description = request.getParameter("description");
			String source = request.getParameter("campaignImageSource");
			String fileName = request.getParameter("campaignImageName");
			String amount = request.getParameter("amount");
			String externalAmt = request.getParameter("externalAmt");
			String targetAmt = request.getParameter("targetAmt");
			String firstAmt = request.getParameter("firstAmt");
			String secondAmt = request.getParameter("secondAmt");
			String thirdAmt = request.getParameter("thirdAmt");
			String fourthAmt = request.getParameter("fourthAmt");
			String categoryId = request.getParameter("categoryId");
			
			String catName = "Campaign";
			if(campaignTitle != null && !campaignTitle.isEmpty()) {
				Company company = activeUser.getCompany();
				int companyId = company.getId();
				ProductGroup prodGroup = new ProductGroup();
				boolean createCategory = true;
				if(StringUtil.checkStringIsNotNullAndInteger(categoryId)) {
					Category category = entityManager.find(Category.class, Integer.valueOf(categoryId.trim()));
					if(category != null) {
						prodGroup.setCategory(category);
						createCategory = false;
					}
				} 
				if(createCategory) {
					List<Category> categoryList = entityManager.createNamedQuery("findCategoryByCompanyAndDependsOnCategoryName")
							.setParameter("companyId", companyId)
							.setParameter("catName", catName)
							.getResultList();
					if(categoryList != null && categoryList.size() > 0) {
						prodGroup.setCategory(categoryList.get(0));
					}else{
						CategoryBean catBean = new CategoryBean();
						catBean.entityManager = entityManager;
						catBean.activeUser = activeUser;
						catBean.localeSelector = localeSelector;
						Category category = catBean.persist(catName, "", null, null);
						prodGroup.setCategory(category);
					}
				}
//				List<Product> prodList = entityManager.createNamedQuery("findProductByCompanyAndDependsOnProductName")
//						.setParameter("companyId", companyId)
//						.setParameter("prodName", campaignTitle.trim())
//						.getResultList();
//				
//				if(prodList != null && prodList.size() > 0) {
//					prodGroup.setProduct(prodList.get(0));
//				}else{
				ProductBean prodBean = new ProductBean();
				prodBean.entityManager = entityManager;
				prodBean.activeUser = activeUser;
				prodBean.localeSelector = localeSelector;
				String prodTypeIds = prodTypeId + "," + prodOtherTypeId;
				if(StringUtil.checkStringIsNull(recurringTypeId)) {
					prodTypeIds = prodTypeIds + "," + StaticValues.RecurringId;
				}
				
				Product product = prodBean.persist(campaignTitle, description, prodTypeIds);
				prodBean.updateProductSubType(product, recurringTypeId);
				prodGroup.setProduct(product);
//				}
				
				List<ProductGroup> prodGroupList = entityManager.createNamedQuery("findProductGroupByCompany")
						.setParameter("companyId", companyId)
						.getResultList();
				int sortCode = 1;
				if(prodGroupList != null && (sortCode = prodGroupList.size()) > 0) {
					sortCode = sortCode + 1;
				}
				prodGroup.setSortCode(sortCode);
				prodGroup.setAmount(Double.valueOf(amount));
				double amt = prodGroup.getAmount();
				if(StringUtil.checkStringIsNull(firstAmt)) {
					prodGroup.setFirstAmount(Double.valueOf(firstAmt));
				}else {
					prodGroup.setFirstAmount(amt);
				}
				if(StringUtil.checkStringIsNull(secondAmt)) {
					prodGroup.setSecondAmount(Double.valueOf(secondAmt));
				}else {
					prodGroup.setSecondAmount(amt * 3);
				}
				if(StringUtil.checkStringIsNull(thirdAmt)) {
					prodGroup.setThirdAmount(Double.valueOf(thirdAmt));
				}else {
					prodGroup.setThirdAmount(amt * 5);
				}
				if(StringUtil.checkStringIsNull(fourthAmt)) {
					prodGroup.setFourthAmount(Double.valueOf(fourthAmt));
				}else {
					prodGroup.setFourthAmount(amt * 10);
				}
				if(targetAmt != null && !targetAmt.isEmpty())
					prodGroup.setTargetAmount(Double.valueOf(targetAmt));
				if(StringUtil.checkStringIsNull(externalAmt))
					prodGroup.setExternalDonation(Double.valueOf(externalAmt));
				if(description != null && !description.isEmpty())
					description = StringUtils.normalizeSpace( description );
				prodGroup.setDescription(description);
				prodGroup.setCompany(company);
				prodGroup.setEnable(true);
				if(campaignTitle != null && !campaignTitle.isEmpty() && campaignTitle.trim().length() > 4) {
					prodGroup.setProductCode(campaignTitle.trim().substring(0, 4));
				}else {
					prodGroup.setProductCode(campaignTitle.trim());
				}
				prodGroup.setCreatedDate(new Date());
				prodGroup.setModifiedDate(new Date());
				entityManager.persist(prodGroup);
				entityManager.flush();
				int prodGroupId = prodGroup.getId();
				if(source != null && !source.isEmpty()) {
					String imagePath = MainUtil.getProductGroupImagePath(companyId) + prodGroupId;
					String extension = FilenameUtils.getExtension(fileName); 
					fileName = prodGroupId + "." + extension.toLowerCase();
					String imageUrl = createimage(source, fileName, imagePath);
					imageUrl = MainUtil.getImageSourcePath(activeUser.getCompany().getId() , "ProductGroup") + prodGroupId + MainUtil.getFileSeparator() + fileName ;
					prodGroup.setImageUrl(imageUrl);
					entityManager.merge(prodGroup);
					entityManager.flush();
				}
				ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1], "Product group created for this campaign : " + campaignTitle 
						+ " and company id is: "+ company.getId());
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public String createimage(String source, String fileName, String imagePath) throws Exception{
		try{
			if(source!=null && !source.isEmpty()){
				if(source.contains("/png;base64") || source.contains("/gif;base64"))
					source = source.substring(22, source.length());
				else
					source = source.substring(23, source.length());	
				
				File files = new File(imagePath);
			    if(!files.exists()){
			    	files.mkdirs();
			    }
				byte[] encodedImage = Base64.getDecoder().decode(source);
				String path = imagePath + MainUtil.getFileSeparator() + fileName;
				if(encodedImage!=null){
			        FileOutputStream fos = new FileOutputStream(new File(path));
			        fos.write(encodedImage);
			        fos.flush();
			        fos.close();
				}
				return path;
			}
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return null;
	}
	
	public List<CampaignVO> getCampaignList(String param){
		try {
			String langCode = localeSelector.getLocaleString().toUpperCase();
			entityManager.createNativeQuery("drop VIEW IF EXISTS fundlist").executeUpdate();
			StringBuilder queryString = new StringBuilder(); 
			queryString.append("create or replace view fundlist as ");
			queryString.append("select pg.id as prodGroupId, pg.image_url as imageUrl, pl.product_name as campaignName, "
					+ " pg.amount as amount, pg.target_amount as targetAmount, pl.description as description,"
					+ " pg.enable as prodGroupStatus, cl.category_name as categoryName"
					+ " from product_group pg "
					+ " LEFT JOIN product as p ON pg.id_product = p.id "
					+ " LEFT JOIN category as c ON pg.id_category = c.id "
					+ " LEFT JOIN product_language as pl ON p.id = pl.id_product "
					+ " LEFT JOIN category_language as cl ON c.id = cl.id_category "
					+ " LEFT JOIN languages as la ON pl.id_languages = la.id "
					+ " LEFT JOIN languages as lac ON cl.id_languages = lac.id "
					+ " where pg.deleted = false and pg.id_company = " + activeUser.getCompany().getId() 
					+ " and la.lang_code = '" + langCode +"' and lac.lang_code = '" + langCode +"'");
			entityManager.createNativeQuery(queryString.toString()).executeUpdate();
	
			String query = "select prodGroupId, imageUrl, campaignName, amount, targetAmount, description, prodGroupStatus, categoryName from fundlist ORDER BY prodGroupId desc";
			Session session = entityManager.unwrap(Session.class);
			SQLQuery sqlquery = session.createSQLQuery(query);
			sqlquery.setResultTransformer( Transformers.aliasToBean(CampaignVO.class) );
			List<CampaignVO> list = sqlquery.list();
			ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1],
					param + " list size is : "+ list.size() +". Company Id is : " + activeUser.getCompany().getId());
			return list;
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return null;
	}

}
