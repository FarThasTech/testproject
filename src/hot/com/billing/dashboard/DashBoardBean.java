package com.billing.dashboard;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Synchronized;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.web.ServletContexts;
import org.json.simple.JSONObject;

import com.billing.entity.FundDetails;
import com.billing.entity.PaymentMethod;
import com.billing.entity.PaymentStatus;
import com.billing.entity.Users;
import com.billing.exceptions.ExceptionMsg;
import com.billing.paymethod.PayMethodBean;
import com.billing.paystatus.PaymentStatusBean;
import com.billing.util.DateUtil;
import com.billing.util.NumberUtil;

@Synchronized(timeout=15000)
@Scope(ScopeType.SESSION)
@Name("dashBoardBean")
@SuppressWarnings("unchecked")
public class DashBoardBean {

	@In
	public EntityManager entityManager;
	    
	@In
	public LocaleSelector localeSelector;
	
	@In(create=true,required=false) 
    public Users activeUser;
	
	@In(create = true)
	@Out(scope = ScopeType.SESSION)
	public DashBoardVO dashBoardVO;
	
	public void resetData() {
		try {
			dashBoardVO.setCampaignCount(0);
			dashBoardVO.setDonorCount(0);
			dashBoardVO.setCurrentMonthDonation("");
			dashBoardVO.setTotalDonation("");
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void dashboardValue() {
		try {
			resetData();
			/*********************** Total Donor Count ***********************/
			dashBoardVO.setDonorCount(donorCount());
			/*********************** Total Donor Count ***********************/
			/********************** Total Campaign Count *********************/
			dashBoardVO.setCampaignCount(campaignCount());
			/********************** Total Campaign Count *********************/
			/********************** Total Year Donation **********************/
			dashBoardVO.setTotalDonation(calculateDonationAmt(null, null));
			/********************** Total Year Donation **********************/
			/********************** Current Month Donation *******************/
			String startDate = DateUtil.getStartDateofMonthWithStartTime();
			String endDate = DateUtil.getEndDateofMonthWithEndTime();
			dashBoardVO.setCurrentMonthDonation(calculateDonationAmt(startDate, endDate));
			/********************** Current Month Donation *******************/
			
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public int donorCount() {
		try {
			BigInteger count = BigInteger.ZERO;
			if (activeUser != null) {
				String donorCountQuery = "select count(*) from fund_group fg where fg.id_company = " + activeUser.getCompany().getId() 
						       					+ " and fg.deleted = false";
				count = (BigInteger) entityManager.createNativeQuery(donorCountQuery).getSingleResult();
			}
			if (count != null)
				return count.intValue();
			else
				return 0;
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return 0;
	}
	
	public int campaignCount() {
		try {
			BigInteger count = BigInteger.ZERO;
			if (activeUser != null) {
				String campaignCountQuery = "select count(*) from product_group pg "
						+ " where pg.id_company = " + activeUser.getCompany().getId() 
						+ " and pg.enable = true and pg.deleted = false";
				count = (BigInteger) entityManager.createNativeQuery(campaignCountQuery).getSingleResult();
			}
			if (count != null)
				return count.intValue();
			else
				return 0;
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return 0;
	}
	
	public String calculateDonationAmt(String startDate, String endDate) {
		String totalAmt = " ";
		try {
			if (activeUser != null) {
				List<PaymentStatus> paymentStatusList = entityManager.createNamedQuery("findPaymentStatusByCompanyAndStatusType")
						.setParameter("companyId", activeUser.getCompany().getId())
						.setParameter("statusType", PaymentStatusBean.Success)
						.getResultList();
				PaymentStatus payStatus = null;
				if(paymentStatusList != null && paymentStatusList.size() > 0) {
					payStatus = paymentStatusList.get(0); 
				}

				
				String appendQuery = "";
				if(startDate != null && endDate != null) {
					appendQuery = " fd.transaction_date  >= '" + startDate + "' and fd.transaction_date  <= '" + endDate + "' and ";
				}
				
				String query = "select sum(fd.amount) from fund_details fd "
						+ " left join fund_group fg on fd.id_fundgroup = fg.id where "
						+ appendQuery
						+ " fd.id_paymentstatus = " + payStatus.getId() 
						+ " and (fd.installment IS NOT NULL and fd.installment != '' "
						+ " and ((CONVERT(fd.installment , UNSIGNED integer) <= 1 and fd.installment not LIKE '%/%') or fd.installment LIKE '%/%'))"
						+ " and fg.id_company = " + activeUser.getCompany().getId();
				BigDecimal amount = (BigDecimal) entityManager.createNativeQuery(query).getSingleResult();

				query = "select (sum(trans_fee) + sum(app_fee)) from fund_details fd "
						+ " left join fund_group fg on fd.id_fundgroup = fg.id where "
						+ appendQuery
						+ " fd.id_paymentstatus = " + payStatus.getId() 
						+ " and (fd.installment IS NOT NULL and fd.installment != '' "
						+ " and ((CONVERT(fd.installment , UNSIGNED integer) <= 1 and fd.installment not LIKE '%/%') or fd.installment LIKE '%/%')) "
						+ " and fd.trans_taken_over = true "
						+ " and fg.id_company = " + activeUser.getCompany().getId();
				
				BigDecimal wholeFee = (BigDecimal) entityManager.createNativeQuery(query).getSingleResult();


				double total = 0.0;
				if (amount != null) {
					total = amount.doubleValue();
				}
				if (wholeFee != null) {
					total = total + wholeFee.doubleValue();
				}
				
				Locale locale = new Locale(activeUser.getLanguages().getLangCode());
				NumberFormat currencyFormatter = NumberFormat.getNumberInstance(locale);
				currencyFormatter.setMinimumFractionDigits(2);
				currencyFormatter.setMaximumFractionDigits(2);
				totalAmt = currencyFormatter.format(total);
			}
		} catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return totalAmt;
	}
	
	public void payMethodStatistics() {
		try {
			String result = "", start = "", end = "";
			if (activeUser != null) {
				HttpServletRequest request = ServletContexts.instance().getRequest();
				HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
				response.setContentType("text/html; charset=UTF-8");
				start = request.getParameter("startDate");
				end = request.getParameter("endDate");
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date startDate = dateFormat.parse(DateUtil.getStartDateofMonthWithStartTime());
				Date endDate = dateFormat.parse(DateUtil.getEndDateofMonthWithEndTime());
				if (start != null && !start.isEmpty() && end != null && !end.isEmpty()) {
					SimpleDateFormat formatFrom = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
					SimpleDateFormat formatTo = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
					start = formatFrom.format(DateUtil.getStringToDateFormat(start, DateUtil.DATE_FORMAT));
					end = formatTo.format(DateUtil.getStringToDateFormat(end, DateUtil.DATE_FORMAT));
					startDate = DateUtil.convertFormattedFullDate(start);
					endDate = DateUtil.convertFormattedFullDate(end);
				} else {
					start = DateUtil.getStartDateofMonthWithStartTime();
					end = DateUtil.getEndDateofMonthWithEndTime();
				}
				if (activeUser != null) {	
					int companyId = activeUser.getCompany().getId();
					List<FundDetails> fundDetailsList = entityManager.createNamedQuery("findFundDetailsByParticularTransDateAndGroupByPayMethod")
																			.setParameter("companyId", companyId)
																			.setParameter("startDate", startDate)
																			.setParameter("endDate", endDate)
																			.getResultList();
					if (fundDetailsList != null && fundDetailsList.size() > 0) {
						PaymentStatus payStatus = (PaymentStatus) entityManager .createNamedQuery("findPaymentStatusByCompanyAndStatusType")
																			.setParameter("companyId", companyId)
																			.setParameter("statusType", PaymentStatusBean.Success)
																			.getSingleResult();

						JSONObject obj = new JSONObject();
						StringBuffer str = new StringBuffer();
						PayMethodBean payBean = new PayMethodBean();
						String localeString = localeSelector.getLocaleString();
						for (FundDetails fundDet : fundDetailsList) {
							PaymentMethod payMethod = fundDet.getPaymentMethod();
							String query = "select sum(CONVERT(COALESCE(fd.amount,0), CHAR(50))) from fund_details fd "
									+ " left join fund_group fg on fd.id_fundGroup = fg.id where "
									+ " fd.transaction_date  >= '" + start + "' and fd.transaction_date <= '" + end
									+ "' and fd.id_paymentstatus = " + payStatus.getId()
									+ " and (fd.installment IS NOT NULL and fd.installment != ''"
									+ " and ((CONVERT(fd.installment , UNSIGNED integer) <= 1 and fd.installment not LIKE '%/%') or fd.installment LIKE '%/%'))"
									+ " and fg.id_company = " + activeUser.getCompany().getId()
									+ " and fd.id_paymentmethod = " + payMethod.getId();
							Double amount = (Double) entityManager.createNativeQuery(query).getSingleResult();
							
							query = "select sum(CONVERT(COALESCE((fd.trans_fee + fd.app_fee),0), CHAR(50))) from fund_details fd "
									+ " left join fund_group fg on fd.id_fundGroup = fg.id where "
									+ " fd.transaction_date  >= '" + start + "' and fd.transaction_date <= '" + end
									+ "' and fd.id_paymentstatus = " + payStatus.getId()
									+ " and (fd.installment IS NOT NULL and fd.installment != ''"
									+ " and ((CONVERT(fd.installment , UNSIGNED integer) <= 1 and fd.installment not LIKE '%/%') or fd.installment LIKE '%/%'))"
									+ " and fg.id_company = " + activeUser.getCompany().getId()
									+ " and fd.id_paymentmethod = " + payMethod.getId()
									+ " and fd.trans_taken_over = true ";
							Double wholeFee = (Double) entityManager.createNativeQuery(query).getSingleResult();

							double total = 0.0;
							if (amount != null) {
								total = amount;
							}
							if(wholeFee != null) {
								total = total + wholeFee;
							}
							if (total > 0) {
								String payType1 = payBean.getPayMethodLangName(payMethod, localeString);
								obj.put("name", payType1);
								obj.put("value", total);
								str = str.append(obj.toJSONString());
								str = str.append(",");
							}
						}
						if (str.toString().length() > 0) {
							result = str.toString().substring(0, str.toString().length() - 1);
						}
					}
					response.getWriter().write("\npaymentMethodStatStart\n" + result + "\npaymentMethodStatEnd\n");
				}
			}
		} catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void payStatusStatistics() {
		try {
			String result = "", start = "", end = "";
			if (activeUser != null) {
				HttpServletRequest request = ServletContexts.instance().getRequest();
				HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
				response.setContentType("text/html; charset=UTF-8");
				start = request.getParameter("startDate");
				end = request.getParameter("endDate");
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date startDate = dateFormat.parse(DateUtil.getStartDateofMonthWithStartTime());
				Date endDate = dateFormat.parse(DateUtil.getEndDateofMonthWithEndTime());
				if (start != null && !start.isEmpty() && end != null && !end.isEmpty()) {
					SimpleDateFormat formatFrom = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
					SimpleDateFormat formatTo = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
					start = formatFrom.format(DateUtil.getStringToDateFormat(start, DateUtil.DATE_FORMAT));
					end = formatTo.format(DateUtil.getStringToDateFormat(end, DateUtil.DATE_FORMAT));
					startDate = DateUtil.convertFormattedFullDate(start);
					endDate = DateUtil.convertFormattedFullDate(end);
				} else {
					start = DateUtil.getStartDateofMonthWithStartTime();
					end = DateUtil.getEndDateofMonthWithEndTime();
				}
				if (activeUser != null) {	
					int companyId = activeUser.getCompany().getId();
					List<FundDetails> fundDetailsList = entityManager.createNamedQuery("findFundDetailsByParticularTransDateAndGroupByPayStatus")
																			.setParameter("companyId", companyId)
																			.setParameter("startDate", startDate)
																			.setParameter("endDate", endDate)
																			.getResultList();
					if (fundDetailsList != null && fundDetailsList.size() > 0) {
						JSONObject obj = new JSONObject();
						StringBuffer str = new StringBuffer();
						PaymentStatusBean payStatusBean = new PaymentStatusBean();
						String localeString = localeSelector.getLocaleString();
						for (FundDetails fundDet : fundDetailsList) {
							PaymentStatus payStatus = fundDet.getPaymentStatus();
							String query = "select sum(CONVERT(COALESCE(fd.amount,0), CHAR(50))) from fund_details fd "
									+ " left join fund_group fg on fd.id_fundGroup = fg.id where "
									+ " fd.transaction_date  >= '" + start + "' and fd.transaction_date <= '" + end
									+ "' and fd.id_paymentstatus = " + payStatus.getId()
									+ " and (fd.installment IS NOT NULL and fd.installment != ''"
									+ " and ((CONVERT(fd.installment , UNSIGNED integer) <= 1 and fd.installment not LIKE '%/%') or fd.installment LIKE '%/%'))"
									+ " and fg.id_company = " + activeUser.getCompany().getId();
							Double amount = (Double) entityManager.createNativeQuery(query).getSingleResult();
							
							query = "select sum(CONVERT(COALESCE((fd.trans_fee + fd.app_fee),0), CHAR(50))) from fund_details fd "
									+ " left join fund_group fg on fd.id_fundGroup = fg.id where "
									+ " fd.transaction_date  >= '" + start + "' and fd.transaction_date <= '" + end
									+ "' and fd.id_paymentstatus = " + payStatus.getId()
									+ " and (fd.installment IS NOT NULL and fd.installment != ''"
									+ " and ((CONVERT(fd.installment , UNSIGNED integer) <= 1 and fd.installment not LIKE '%/%') or fd.installment LIKE '%/%'))"
									+ " and fg.id_company = " + activeUser.getCompany().getId()
									+ " and fd.trans_taken_over = true ";
							Double wholeFee = (Double) entityManager.createNativeQuery(query).getSingleResult();

							double total = 0.0;
							if (amount != null) {
								total = amount;
							}
							if(wholeFee != null) {
								total = total + wholeFee;
							}
							if (total > 0) {
								String payType1 = payStatusBean.getPayStatusLangName(payStatus, localeString);
								obj.put("name", payType1);
								obj.put("value", total);
								str = str.append(obj.toJSONString());
								str = str.append(",");
							}
						}
						if (str.toString().length() > 0) {
							result = str.toString().substring(0, str.toString().length() - 1);
						}
					}
					response.getWriter().write("\npaymentStatusStatStart\n" + result + "\npaymentStatusStatEnd\n");
				}
			}
		} catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void donorStatistics() {
		try {
			String dataResult = "", monthResult = "";
			if (activeUser != null) {
				HttpServletRequest request = ServletContexts.instance().getRequest();
				JSONObject data = new JSONObject();
				JSONObject monthLabel = new JSONObject();
				String yearValue = request.getParameter("year");
				HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance()
						.getExternalContext().getResponse();
				
				String subscriptionCountQuery = "select count(*) from fund_group fg "
	    				+ " where fg.id_company = " + activeUser.getCompany().getId();
				
				StringBuffer dataStr = new StringBuffer();
				StringBuffer monthLabelStr = new StringBuffer();
				int year = Integer.valueOf(DateUtil.getCurrentYear());
				//if(statisticsVO.getYearList()!=null && statisticsVO.getYearList().size() > 0) {
				//	year = statisticsVO.getYearList().get(0);
				//}
				Locale locale = new Locale(localeSelector.getLocaleString());
				if (yearValue != null && !yearValue.isEmpty() && NumberUtil.checkNumeric(yearValue.trim())
						&& Integer.valueOf(yearValue.trim()) > 0) {
					if(year >= Integer.valueOf(yearValue.trim())) {
						year = Integer.valueOf(yearValue.trim());						
					}
				}
				for (Integer month : DateUtil.getAllMonthInInteger()) {
					String query = subscriptionCountQuery + " and (date(fg.created_date) >= '"
							+ DateUtil.getStartDateOfMonth(month, year) + "' and date(fg.created_date) <=  '"
							+ DateUtil.getEndDateOfMonth(month, year) + "')";
					BigInteger donorCount = (BigInteger) entityManager.createNativeQuery(query).getSingleResult();
					String[] months = new DateFormatSymbols(locale).getShortMonths();
					monthLabel.put("monthLabel", months[month]);
					data.put("value", donorCount);
					dataStr = dataStr.append(data.toJSONString());
					dataStr = dataStr.append(",");
					monthLabelStr = monthLabelStr.append(monthLabel.toJSONString());
					monthLabelStr = monthLabelStr.append(",");
				}
				if (dataStr.toString().length() > 0 && monthLabelStr.toString().length() > 0) {
					dataResult = dataStr.toString().substring(0, dataStr.toString().length() - 1);
					monthResult = monthLabelStr.toString().substring(0, monthLabelStr.toString().length() - 1);
				}
				response.getWriter().write("\ndataStatisticsStart\n" + dataResult + "\ndataStatisticsEnd\n");
				response.getWriter().write("\nmonthStatisticsStart\n" + monthResult + "\nmonthStatisticsEnd\n");
			}
		} catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
}
