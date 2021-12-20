package com.billing.finance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.LocaleSelector;

import com.billing.entity.Users;
import com.billing.exceptions.ExceptionMsg;
import com.billing.paystatus.PaymentStatusBean;
import com.billing.util.DateUtil;

@Scope(ScopeType.SESSION)
@Name("financeBean")
@SuppressWarnings("unchecked")
public class FinanceBean {

	@In
	public EntityManager entityManager;

	@In
	public LocaleSelector localeSelector;

	@In(create = true, required = false)
	public Users activeUser;

	@In(create = true)
	@Out(scope = ScopeType.SESSION)
	public FinanceVO financeVO;

	public List<FinanceVO> getList(String param, String startDate, String endDate) {
		List<FinanceVO> financeList = new ArrayList<FinanceVO>();
		try {
			boolean withTransCode = true;
			List<FinanceVO> list = getFinanceList(withTransCode, startDate, endDate);
			if(list != null && list.size() > 0) {
				financeList.addAll(list);
			}
			withTransCode = false;
			list = getFinanceList(withTransCode, startDate, endDate);
			if(list != null && list.size() > 0) {
				financeList.addAll(list);
			}
			ExceptionMsg.InfoMsg(Thread.currentThread().getStackTrace()[1],
					( param + " list size is : " + (financeList != null ?  financeList.size() : 0) + ". Company Id is : " + activeUser.getCompany().getId()));
			return financeList;
		} catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			return financeList;
		}
	}
	
	public List<FinanceVO>  getFinanceList(boolean withTransCode, String startDate, String endDate) {
		try {
			String localeString = localeSelector.getLocaleString().toUpperCase();
			entityManager.createNativeQuery("drop VIEW IF EXISTS fundlist").executeUpdate();
			StringBuilder queryString = new StringBuilder();
			queryString.append("create or replace view fundlist as ");
			queryString.append( "select fg.id as fundGroupId, u.id as userId, "
							+ " COALESCE(u.first_name,'') as firstName, COALESCE(u.last_name,'') as lastName, "
							+ " COALESCE(uAddr.address_1,'') as address, COALESCE(uAddr.city,'') as city, COALESCE(uAddr.country,'') as country ,"
							+ " COALESCE(uAddr.house_no,'') as houseNo, COALESCE(uAddr.zip,'') as zip, COALESCE(uAddr.state,'') as state,"
							+ " CONVERT(COALESCE(uAddr.id, ''), CHAR(50))  as uAddrId,"
							+ " COALESCE(la.lang_code, '') as lang, CONVERT(COALESCE(la.id, ''), CHAR(50))  as langid,"
							+ " u.user_nr as userNr, u.primary_email as email, u.telephone as telephone, "
							
							+ "CONVERT(COALESCE(sum(fd.amount),0), CHAR(50)) as amount, "
							
							+ "CONVERT(COALESCE((sum(trans_fee) + sum(app_fee)),0), CHAR(50)) as wholeFee, "
							
							+ "CONVERT(COALESCE(sum(fd.app_fee),0), CHAR(50)) as appFee, "
							
							+ "CONVERT(COALESCE(sum(fd.trans_fee),0), CHAR(50)) as transFee, "
							
							+ " fd.transaction_code as transCode, "
							
							+ " fd.trans_taken_over as transTakenOver, "
							
							+ " group_concat(COALESCE(prodlang.product_name,NULL),'') as campaignName, "
							
							+ " COALESCE((SELECT pml.method_name FROM payment_method_language pml "+ 
									"left join languages lan ON  pml.id_language = lan.id "+
									"where pml.id_paymentmethod = fd.id_paymentmethod and " +
									"lan.lang_code like '%"+localeString+"%' LIMIT 1) , '') as paymentMethodName, "
									
							+ " COALESCE((SELECT psl.status FROM payment_status_language psl "+ 
									"left join languages lan ON  psl.id_language = lan.id "+
									"where psl.id_paymentstatus = fd.id_paymentstatus and " +
									"lan.lang_code like '%"+localeString+"%' LIMIT 1) , '') as paymentStatusName, "
									
							+ " fd.transaction_date as transactionDate "
							
							+ " from fund_details fd "
							+ " LEFT JOIN fund_group as fg ON fd.id_fundGroup = fg.id "
							+ " LEFT JOIN users as u ON fg.id_users = u.id "
							+ " LEFT JOIN languages as la ON u.id_languages = la.id "
							+ " LEFT JOIN users_address as uAddr ON u.id = uAddr.id_users "
							+ " LEFT JOIN product_group as pg on fd.id_productgroup = pg.id "
							+ " LEFT JOIN product as prod on pg.id_product = prod.id "
							+ " LEFT JOIN product_language as prodlang on prod.id = prodlang.id_product "
							+ " LEFT JOIN languages as lang ON prodlang.id_languages = lang.id "
							+ " LEFT JOIN payment_status as payStat on fd.id_paymentstatus = payStat.id "
							+ " where fg.deleted = false and fg.id_company = " + activeUser.getCompany().getId()
							+ " and fd.installment IS NOT NULL and fd.installment != '' "
							+ " and ((fd.installment not LIKE '%/%' and CONVERT(fd.installment , UNSIGNED integer) <= 1)  or fd.installment LIKE '%/%')"
							+ " and payStat.status_type = '"+PaymentStatusBean.Success+"' "
							+ " and lang.lang_code = '"+localeString+"' ");
			
			if(startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
				queryString.append(" and date(fd.transaction_date) >= '" + startDate +"' and date(fd.transaction_date) <= '" + endDate +"' ");
			}

			if(withTransCode) {
				queryString.append(" and fd.transaction_code is not null and fd.transaction_code != '' "
									+ " group by fd.transaction_code order by fd.transaction_date desc");
			}else {
				queryString.append(" and (fd.transaction_code is null or fd.transaction_code = '') "
						+ " group by fd.id order by fd.transaction_date desc");
			}
			
			entityManager.createNativeQuery(queryString.toString()).executeUpdate();

			String query = "select fundGroupId, userId, firstName, lastName,"
					+ " address, houseNo, city, state, country, zip, "
					+ " uAddrId, lang, langid, userNr, email, telephone,"
					+ " amount, wholeFee, appFee, transFee, transCode, "
					+ " transTakenOver, campaignName, paymentMethodName,"
					+ " paymentStatusName, transactionDate from fundlist ORDER BY fundGroupId desc";
			Session session = entityManager.unwrap(Session.class);
			SQLQuery sqlquery = session.createSQLQuery(query);
			sqlquery.setResultTransformer(Transformers.aliasToBean(FinanceVO.class));
			List<FinanceVO> list = sqlquery.list();
			return list;
		} catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			return null;
		}
	}
	
	public String getCurrentDate() {
		try {
			return DateUtil.getDateToStringFormat(new Date(), DateUtil.DATE_FORMAT);
		} catch (Exception e) {
			ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
			return null;
		}
	}

}
