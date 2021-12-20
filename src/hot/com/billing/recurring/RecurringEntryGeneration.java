package com.billing.recurring;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import com.billing.entity.Company;
import com.billing.entity.FundDetails;
import com.billing.entity.PaymentStatus;
import com.billing.exceptions.ExceptionMsg;
import com.billing.paymethod.StripeBean;
import com.billing.paystatus.PaymentStatusBean;
import com.billing.staticvalue.StaticValues;
import com.billing.util.DateUtil;

@SuppressWarnings("unchecked")
@Name("recurringEntryGeneration")
public class RecurringEntryGeneration {
	
	@In
	public EntityManager entityManager;

	public static Integer Future_Entry_Generation_Count = 250;
	
	public void generateNextDurationOnlyForAllOrganization(){
		try {
			List<Company> companyList = entityManager.createNamedQuery("findAllCompanyWithAssignedOrderByAsc")
											.setParameter("assigned", true)
											.getResultList();
			if(companyList != null && companyList.size()>0){
				String info = "";
				for(Company company : companyList){
					int companyId = company.getId();
					if(company.isCreateFutureEntry()) {
						String startDate = DateUtil.getDateToStringFormat(DateUtil.Yesterday(), DateUtil.DATE_FORMAT_US)+" 00:00:00";
						String endDate = DateUtil.getDateToStringFormat(new Date(), DateUtil.DATE_FORMAT_US)+" 23:59:59";
						consolidateRecurringEntriesForCreation(companyId, startDate, endDate);
						info = "Future entry generation is done for this company id : " + companyId;
					} else {
						info = "Future entry generation is block for this company id : " + companyId;
					}
					ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1], info);
				}
			}
		}catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
	
	public void consolidateRecurringEntriesForCreation(int companyId, String startDate, String endDate) {
		try {
			PaymentStatus paymentStatus = (PaymentStatus) entityManager.createNamedQuery("findPaymentStatusByCompanyAndStatusType")
												.setParameter("companyId", companyId)							
												.setParameter("statusType", PaymentStatusBean.Pending)
												.getSingleResult();
			String queryForRecurringCreation = "select fd.group_id from fund_details fd "
					+ " left join fund_group fg ON  fd.id_fundgroup = fg.id " 
					+ " left join payment_status ps on ps.id = fd.id_paymentstatus"
					+ " left join payment_method pm on pm.id = fd.id_paymentmethod"
					+ " left join payment_type pt on pt.id = pm.id_paymenttype"
					+ " left join user_account_details uad on uad.id = fd.user_acc_id"
					+ " where fg.id_company = "+companyId+" and "
					+ " (pt.payment_type != 'Stripe' or pt.payment_type is null  or pt.payment_type = '' or "
					+ " (pt.payment_type = 'Stripe' and fd.user_acc_id is not null and (((pt.id = 3 or pt.id = 4 or pt.id = 5 or pt.id = 6)"
					+ " and uad.customer_id LIKE '"+StripeBean.Stripe_Customer_Prefix+"%') or (pt.id = 2 and uad.iban_code is not null )))) "
					+ " and ps.status_type != '" + PaymentStatusBean.Cancel + "' and ps.status_type != '" + PaymentStatusBean.Failure + "' "
					+ " and fd.group_id IS NOT NULL and fd.group_id != '' "
					+ " and fd.installment like '%/%' and (fd.transaction_date between '"+startDate+"' and '"+endDate+"') "
					+ " and fd.recurring_generated = false and fd.deleted = false"
					+ " group by fd.group_id having count(*) > 0 order by fd.group_id asc";
			
			List<Integer> groupIdList = entityManager.createNativeQuery(queryForRecurringCreation).getResultList();
			
			String info = "Total Group Id for this company (" + companyId +") is : "+ groupIdList.size(); 
			ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1], info);
			
			if(groupIdList != null && groupIdList.size() > 0) {
				if(groupIdList.size() > Future_Entry_Generation_Count) {
					groupIdList = groupIdList.subList(0, Future_Entry_Generation_Count);
				}
				
				info = "Generating entry for this company (" + companyId +") is : "+ groupIdList.size(); 
				ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1], info);
				
				for(Integer groupId : groupIdList) {
					try {
						FundDetails mainFundDetails = (FundDetails) entityManager.createNamedQuery("findFundDetailsByGroupIdAndMainInstallment")
																	.setParameter("groupId", groupId)
																	.getSingleResult();
						if(mainFundDetails != null) {
							Date eDate = mainFundDetails.getEndDate();
							String subquery = "select fd from FundDetails fd where fd.groupId = "+ mainFundDetails.getGroupId()
												+ " and fd.installment LIKE '%/%' order by fd.id desc";
							List<FundDetails> recurringDetailsList = entityManager.createQuery(subquery).getResultList();
							if(eDate != null) {
								/****** End date is greater than current date ******/
								if(new Date().compareTo(eDate) < 0) {	
									if(recurringDetailsList!=null && recurringDetailsList.size() > 0){
										FundDetails recurringDetails = recurringDetailsList.get(0);
										if(recurringDetails != null) {
											Date transDate = findTransDate(recurringDetails, mainFundDetails, recurringDetails.getProductType());
											/****** End date is less than future entry transaction date ******/
											if(transDate.compareTo(eDate) < 0) {									
												persistFundDetailsSub(recurringDetails, paymentStatus, transDate);
											}else {																	
												for(FundDetails fDetails : recurringDetailsList) {
													fDetails.setRecurringGenerated(true);
													entityManager.merge(fDetails);
													entityManager.flush();
												}
											}
										}
									}
								}else {
									/****** End date is less than current date ******/
									if(recurringDetailsList!=null && recurringDetailsList.size() > 0){
										for(FundDetails fDetails : recurringDetailsList) {
											fDetails.setRecurringGenerated(true);
											entityManager.merge(fDetails);
											entityManager.flush();
										}
									}
								}
							}else {
								if(recurringDetailsList!=null && recurringDetailsList.size() > 0){
									FundDetails recurringDetails = recurringDetailsList.get(0);
									Date transDate = findTransDate(recurringDetails, mainFundDetails, recurringDetails.getProductType());
									persistFundDetailsSub(recurringDetails, paymentStatus, transDate);
								}
							}
						}
					}catch(Exception e) {
						ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
					}
				}
			}
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}

	
	public Date findTransDate(FundDetails recurringDetails, FundDetails mainFundDetails, String recurringType) {
		try {
			Date transactionDate = recurringDetails.getTransactionDate();
			Calendar mainfundCal = Calendar.getInstance();
			mainfundCal.setTime(mainFundDetails.getTransactionDate());
			int mainFDay = mainfundCal.get(Calendar.DATE);
			
			Calendar fundCal = Calendar.getInstance();
			fundCal.setTime(transactionDate);
			Calendar cal = fundCal;
			if(recurringType.equalsIgnoreCase(StaticValues.Monthly)) {
				cal.add(Calendar.MONTH, 1);
			} else if(recurringType.equalsIgnoreCase(StaticValues.Annually)) {
				cal.add(Calendar.YEAR, 1);
			}
			int calday = DateUtil.getDay(cal.getTime());
			int calMaxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			if(calMaxDays >= mainFDay && mainFDay > calday) {
				int daydiff = mainFDay - calday;
				cal.add(Calendar.DATE, daydiff);
			}
			return cal.getTime();
		}catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
		return null;
	}
	
	public void persistFundDetailsSub(FundDetails recurringDetails, PaymentStatus paymentStatus, Date transDate) {
		try {
			FundDetails fundDetails = new FundDetails();
			fundDetails.setCompany(recurringDetails.getCompany());
			fundDetails.setCreatedUser(recurringDetails.getCreatedUser());
			fundDetails.setFundGroup(recurringDetails.getFundGroup());
			fundDetails.setAmount(recurringDetails.getAmount());
			fundDetails.setExtPrice(recurringDetails.getExtPrice());
			fundDetails.setIntPrice(recurringDetails.getIntPrice());
			fundDetails.setProductGroup(recurringDetails.getProductGroup());
			fundDetails.setPaymentMethod(recurringDetails.getPaymentMethod());
			fundDetails.setPaymentStatus(paymentStatus);
			fundDetails.setProductType(recurringDetails.getProductType());
			fundDetails.setQuantity(recurringDetails.getQuantity());
			fundDetails.setTransTakenOver(recurringDetails.isTransTakenOver());
			fundDetails.setApplicationFee(BigDecimal.ZERO);
			fundDetails.setTransactionFee(BigDecimal.ZERO);
			fundDetails.setInstallment(recurringDetails.getInstallment());
			fundDetails.setStartDate(transDate);				
			fundDetails.setBookingDate(transDate);			
			fundDetails.setTransactionDate(transDate);	
			fundDetails.setUserAccId(recurringDetails.getUserAccId());
			fundDetails.setFundGroupDetails(recurringDetails.getFundGroupDetails());
			fundDetails.setGroupId(recurringDetails.getGroupId());
			fundDetails.setRecurringGenerated(false);
			fundDetails.setMainEntry(false);
			fundDetails.setDisputeCount(0);
			fundDetails.setDisputeAmount(BigDecimal.ZERO);
			fundDetails.setDisputeJson(null);
			fundDetails.setDisputeTransCode(null);
			fundDetails.setTransactionCode(null);
			fundDetails.setDeleted(false);
			fundDetails.setDeletedBy(null);
			fundDetails.setCreatedDate(new Date());
			fundDetails.setModifiedDate(new Date());
			entityManager.persist(fundDetails);
			
			recurringDetails.setRecurringGenerated(true);
			entityManager.merge(recurringDetails);
			entityManager.flush();
		} catch(Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
		}
	}
}
