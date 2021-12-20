package com.billing.product;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.LocaleSelector;

import com.billing.commonFile.Translator;
import com.billing.entity.Languages;
import com.billing.entity.ModuleAccess;
import com.billing.entity.Product;
import com.billing.entity.ProductAccess;
import com.billing.entity.ProductLanguage;
import com.billing.entity.ProductSubType;
import com.billing.entity.ProductType;
import com.billing.entity.Users;
import com.billing.exceptions.ExceptionMsg;
import com.billing.jms.JMSBean;
import com.billing.jms.JMSClient;
import com.billing.jms.JMSVO;
import com.billing.language.LanguageBean;
import com.billing.staticvalue.StaticValues;
import com.billing.util.StringUtil;

@Scope(ScopeType.SESSION)
@Name("productBean")
@SuppressWarnings("unchecked")
public class ProductBean {

	@In
	public EntityManager entityManager;
	    
	@In
	public LocaleSelector localeSelector;
	
	@In(create=true,required=false) 
    public Users activeUser;
	
	public Product persist(String productName, String description, String prodType) {
		Product product = null;
		try {
			if(productName != null && !productName.isEmpty()) {
				/******************* Add New Data *******************/
				product = new Product();
				product.setCompany(activeUser.getCompany());
				product.setDescription(description);
				product.setProductName(productName);
				product.setEnable(true);
				product.setCreatedDate(new Date());
				product.setModifiedDate(new Date());
				updateProductType(prodType, product);
				entityManager.persist(product);
				entityManager.flush();
				createProductLang(product, true);
				/******************* Add New Data *******************/
				
				/************* Add Other Product Language ***********/
				
				JMSVO jmsVO = new JMSVO();
				JMSBean jmsBean = new JMSBean();
				jmsVO = jmsBean.resetJMSVO(jmsVO);
				jmsVO.setParam("Product");
				jmsVO.setLocaleString(localeSelector.getLocaleString());
				jmsVO.setPrimaryId(String.valueOf(product.getId()));
				jmsVO.setProduct(product);
				JMSClient jmsClient = new JMSClient();
				jmsClient.automaticJMS(jmsVO);
				
				/************* Add Other Product Language ***********/
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return product;
	}
	
	public void updateProductSubType(Product product, String recurringTypeId) {
		try {
			Set<ProductType> productTypeList = product.getProductAccess();
			if(productTypeList != null && productTypeList.size() > 0) {
				for(ProductType prodType : productTypeList) {
					if(prodType != null) {
						if(prodType.getTypeName().equalsIgnoreCase(StaticValues.Recurring)) {
							entityManager.createNativeQuery("update product_access set product_type_sub_id =  '"+ recurringTypeId + "' "
									+ "where productAccess_id = "+ prodType.getId() +" and product_id = " +product.getId()).executeUpdate();
						} 
					}
				}
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void createProductLang(Product product, boolean currenLang) {
		try {
			if(product!= null) {
				String productName = product.getProductName();
				String description = product.getDescription();
				LanguageBean langBean = new LanguageBean();
				langBean.entityManager = entityManager;
				String sourceLang = localeSelector.getLocaleString();
				if(currenLang) {
					Languages lang = langBean.getLangFromLangCode(sourceLang);
					persistProductLang(sourceLang, product, lang, productName, description, currenLang);
				}else {
					for(Languages lang : langBean.langList()) {
						if(!lang.getLangCode().trim().equalsIgnoreCase(sourceLang.trim()))
							persistProductLang(sourceLang, product, lang, productName, description, currenLang);
					}					
				}
			}
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void persistProductLang(String sourceLang, Product product, Languages lang, String productName, String description, boolean currentLang) {
		try {
			ProductLanguage productLang = new ProductLanguage();
			if(currentLang) {
				productLang.setProductName(productName);
				productLang.setDescription(description);
			} else {
				productLang.setProductName(Translator.translate(sourceLang, lang.getLangCode(), productName));
				productLang.setDescription(description != null ? htmlToTextAndTextToHtml(sourceLang, lang.getLangCode(), description) : null);
			}
			productLang.setLanguages(lang);
			productLang.setProduct(product);
			productLang.setCreatedDate(new Date());
			productLang.setModifiedDate(new Date());
			entityManager.persist(productLang);
			entityManager.flush();
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public String htmlToTextAndTextToHtml(String sourceLang, String destinationLang, String description) {
		StringBuffer str = new StringBuffer();
		try {
		    if (description == null || !StringUtil.checkStringIsNull(description)) {
		        return description;
		    }
		    Pattern REMOVE_TAGS = Pattern.compile("<.+?>");
		    Matcher matcher = REMOVE_TAGS.matcher(description);
		    int start = 0, end = 0;
		    boolean appendData = false;
		    while (matcher.find()) {    
	            start = matcher.start();
	            String result = "";
	            if(appendData && end != start) {
	            	result = description.substring(end,start);
	            	result = Translator.translate(sourceLang, destinationLang, result);
	            	str.append(result).append(matcher.group());
	            } else {
	            	str.append(matcher.group());
	            }
	            appendData = true;
	            end = matcher.end();
	        }   
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	    return str.toString();
	}
	
	public static Boolean validateProdType(ProductType productType, Product product) {
		try {
			for(ProductType prodType: product.getProductAccess()){
				if (productType.equals(prodType)) {
					return true;
				}
			}
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return false;
	}
	
	public void updateProductType(String prodType, Product product) {
		try {
			if(prodType!=null && prodType.trim().length() >0){
				String[] prodTypeArr = prodType.split(",");
				List<ProductType> productTypelist = entityManager.createNamedQuery("findAllProductType").getResultList();
				if(productTypelist !=null && productTypelist.size() >0) {
					for(ProductType pType:productTypelist){
						if(checkProductType(pType, prodTypeArr)){
							if(!validateProdType(pType, product))
								product.getProductAccess().add(pType);
						} else{
							if(validateProdType(pType, product))
								product.getProductAccess().remove(pType);
						}
					}
				}
			}
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public boolean checkProductType(ProductType dont, String[] prodTypeArr){
		try {
			for(int i =0; i < prodTypeArr.length;i++){
				if(dont.getId() == Integer.parseInt(prodTypeArr[i].trim())){
					return true;
				}
			}
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return false;
	}
	
	public String getProductNameLang(Product product, String localeString) {
		try {
			if(product != null) {
				Set<ProductLanguage> productLangList = product.getProductLanguage();
				if(productLangList != null && productLangList.size() > 0) {
					for(ProductLanguage prodLang: productLangList) {
						if(prodLang != null && prodLang.getLanguages() != null && prodLang.getLanguages().getLangCode().equalsIgnoreCase(localeString)) {
							return prodLang.getProductName();
						}
					}
				}
				
			}
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return "";
	}
	
	public List<ProductSubType> getActiveRecurringSubType(boolean enable){
		try {
			return entityManager.createNamedQuery("findAllProductSubTypeAndEnableWithOrderBySortCodeAsc")
					.setParameter("enable", enable)
					.getResultList();
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return new ArrayList<ProductSubType>();
	}
	
	public List<ProductType> getActiveProductOtherSubType(){
		List<ProductType> prodOtherSubList = new ArrayList<ProductType>();
		try {
			if(activeUser != null) {
				List<ModuleAccess> moduleAccessList = entityManager.createNamedQuery("findAllModuleAccessByCompanyWithParentIdNotNull")
																	.setParameter("companyId", activeUser.getCompany().getId())
																	.getResultList();
				if(moduleAccessList != null && moduleAccessList.size() > 0) {
					for(ModuleAccess module : moduleAccessList) {
						if(module.getModules().toString().equalsIgnoreCase(StaticValues.ManageWaterWell)) {
							ProductType prodType = entityManager.find(ProductType.class, StaticValues.ManageWaterWellId);
							if(prodType != null) {
								prodOtherSubList.add(prodType);
							}
						}
					}
				}
			}
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return prodOtherSubList;
	}
	
	public List<ProductSubType> getProdSubTypeList(Product product) {
		List<ProductSubType> prodSubTypeList = new ArrayList<ProductSubType>();
		try {
			if(product != null) {
				List<ProductAccess> productAccessList = getProductAccessDependsOnProdAndProductType(product, StaticValues.RecurringId);
				if(productAccessList != null && productAccessList.size() > 0) {
					ProductAccess prodAccess = productAccessList.get(0);
					if(prodAccess != null) {
						String prodTypeSubId = prodAccess.getId().getProductTypeSubId();
						if(StringUtil.checkStringIsNull(prodTypeSubId)) {
							String[] prodTypeSubIds = prodTypeSubId.split(",");
							if(prodTypeSubIds != null && prodTypeSubIds.length > 0) {
								for(String pTSId: prodTypeSubIds) {
									if(StringUtil.checkStringIsNotNullAndInteger(pTSId)) {
										ProductSubType pSubType = entityManager.find(ProductSubType.class, Integer.valueOf(pTSId.trim()));
										if(pSubType != null) {
											prodSubTypeList.add(pSubType);
										}
									}
								}
							}
						}
					}
				}
			}
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			prodSubTypeList = new ArrayList<ProductSubType>();
		}
		return prodSubTypeList;
	}
	
	public String getProdSubTypeIdsList(Product product) {
		String prodTypeSubIds = "";
		try {
			if(product != null) {
				List<ProductAccess> productAccessList = getProductAccessDependsOnProdAndProductType(product, StaticValues.RecurringId);
				if(productAccessList != null && productAccessList.size() > 0) {
					ProductAccess prodAccess = productAccessList.get(0);
					if(prodAccess != null) {
						String prodTypeSubId = prodAccess.getId().getProductTypeSubId();
						if(StringUtil.checkStringIsNull(prodTypeSubId)) {
							prodTypeSubIds = prodTypeSubId;
						}
					}
				}
			}
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return prodTypeSubIds;
	}
	
	public String getProdSubTypeName(Product product) {
		StringBuffer str = new StringBuffer();
		String prodSubTypeName = "";
		try {
			if(product != null) {
				List<ProductAccess> productAccessList = getProductAccessDependsOnProdAndProductType(product, StaticValues.RecurringId);
				if(productAccessList != null && productAccessList.size() > 0) {
					ProductAccess prodAccess = productAccessList.get(0);
					if(prodAccess != null) {
						String prodTypeSubId = prodAccess.getId().getProductTypeSubId();
						if(StringUtil.checkStringIsNull(prodTypeSubId)) {
							String[] prodTypeSubIds = prodTypeSubId.split(",");
							if(prodTypeSubIds != null && prodTypeSubIds.length > 0) {
								for(String pTSId: prodTypeSubIds) {
									if(StringUtil.checkStringIsNotNullAndInteger(pTSId)) {
										ProductSubType pSubType = entityManager.find(ProductSubType.class, Integer.valueOf(pTSId.trim()));
										if(pSubType != null) {
											str.append(pSubType.getSubTypeName()).append(",");
										}
									}
								}
							}
						}
					}
				}
			}
			if(str != null) {
				String result = str.toString();
				if(StringUtil.checkStringIsNull(result) && result.trim().length() > 2) {
					prodSubTypeName = result.trim().substring(0, result.trim().length() - 1);
				}
			}
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return prodSubTypeName;
	}
	
	public List<ProductLanguage> getProductLanguageByProductNameAndLangCompanyDeleted(String productName, String langCode, int companyId, boolean deleted){
		try {
			return entityManager.createNamedQuery("findProductLanguageByProductNameAndLangCompanyDeleted")
										.setParameter("companyId", companyId)
										.setParameter("productName", productName)
										.setParameter("langCode", langCode)
										.setParameter("deleted", deleted)
										.getResultList();
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return new ArrayList<ProductLanguage>();
	}
	
	public List<ProductAccess> getProductAccessDependsOnProdAndProductType(Product product, int productTypeId){
		return entityManager.createQuery("select prod from ProductAccess "
					+ " prod where prod.id.productId =" + product.getId()
					+ " and prod.id.productAccessId = "+ productTypeId).getResultList();
	}
	
}
