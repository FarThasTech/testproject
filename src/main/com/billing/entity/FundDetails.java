package com.billing.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@NamedQueries({
	@NamedQuery(
		name = "findFundDetailsByFundGroup",
		query = "from FundDetails fundDetails where fundDetails.fundGroup.id = :fundGroupId",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findFundDetailsByFundGroupAndWithoutMainEntry",
		query = "from FundDetails fundDetails where fundDetails.fundGroup.id = :fundGroupId"
				+ " and fundDetails.deleted  = false and (fundDetails.installment Like '%/%' "
				+ " or (fundDetails.installment not Like '%/%' and fundDetails.installment <= 1))"
				+ " order by fundDetails.groupId asc",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findFundDetailsByGroupIdAndMainInstallment",
		query = "from FundDetails fundDetails where fundDetails.groupId = :groupId "
				+ " and fundDetails.deleted  = false"
				+ " and fundDetails.installment not Like '%/%'"
				+ " order by fundDetails.groupId asc",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findFundDetailsByParticularTransDateAndGroupByPayMethod",
		query = " from FundDetails fundDetails where fundDetails.fundGroup.company.id = :companyId "
				+ " and fundDetails.transactionDate between :startDate and :endDate "
				+ " and fundDetails.paymentMethod is not null group by fundDetails.paymentMethod.id",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findFundDetailsByParticularTransDateAndGroupByPayStatus",
		query = " from FundDetails fundDetails where fundDetails.fundGroup.company.id = :companyId "
				+ " and fundDetails.transactionDate between :startDate and :endDate "
				+ " and fundDetails.paymentStatus is not null group by fundDetails.paymentStatus.id",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)
})

@Entity
@Table(name = "fund_details")
public class FundDetails implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	private int id;
	private Company company;
	private FundGroup fundGroup;
	private Users createdUser;
	private int quantity;
	private BigDecimal amount;
	private BigDecimal intPrice;
	private BigDecimal extPrice;
	private BigDecimal transactionFee;
	private BigDecimal applicationFee;
	private boolean transTakenOver;
	private ProductGroup productGroup;
	private PaymentMethod paymentMethod;
	private PaymentStatus paymentStatus;
	private FundDetailsGroup fundGroupDetails;
	private int userAccId;
	private String invoiceNo;
	private String notes;
	private Date transactionDate;
	private Date bookingDate;
	private Date startDate;
	private Date endDate;
	private Date cancelDate;
	private Date refundDate;
	private Date disputeDate;
	private String cancelReason;
	private Users cancelBy;
	private String transactionCode;
	private int groupId;
	private Users deletedBy;
	private boolean deleted;
	private Date deletionDate;
	private String productType;
	private String installment;
	private String stripeSrc;
	private String langCode;
	private boolean loggedIn;
	private boolean mainEntry;
	private BigDecimal disputeAmount;
	private String disputeTransCode;
	private String disputeJson;
	private int disputeCount;
	private boolean recurringGenerated;
	private Date createdDate;
	private Date modifiedDate;
	
	public FundDetails() {
		
	}

	public FundDetails(int id) {
		this.id = id;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_company")
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_fundGroup")
	public FundGroup getFundGroup() {
		return fundGroup;
	}

	public void setFundGroup(FundGroup fundGroup) {
		this.fundGroup = fundGroup;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_createduser")
	public Users getCreatedUser() {
		return createdUser;
	}

	public void setCreatedUser(Users createdUser) {
		this.createdUser = createdUser;
	}

	@Column(name = "quantity")
	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Column(name="amount", precision = 20, scale = 4)
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(name="int_price", precision = 20, scale = 4)	
	public BigDecimal getIntPrice() {
		return intPrice;
	}

	public void setIntPrice(BigDecimal intPrice) {
		this.intPrice = intPrice;
	}

	@Column(name="ext_price", precision = 20, scale = 4)
	public BigDecimal getExtPrice() {
		return extPrice;
	}

	public void setExtPrice(BigDecimal extPrice) {
		this.extPrice = extPrice;
	}
	
	@Column(name="trans_fee", precision = 20, scale = 4)
	public BigDecimal getTransactionFee() {
		return transactionFee;
	}

	public void setTransactionFee(BigDecimal transactionFee) {
		this.transactionFee = transactionFee;
	}

	@Column(name="app_fee", precision = 20, scale = 4)
	public BigDecimal getApplicationFee() {
		return applicationFee;
	}

	public void setApplicationFee(BigDecimal applicationFee) {
		this.applicationFee = applicationFee;
	}
	
	@Column(name = "trans_taken_over", columnDefinition="tinyint(1) default 0")
	public boolean isTransTakenOver() {
		return transTakenOver;
	}

	public void setTransTakenOver(boolean transTakenOver) {
		this.transTakenOver = transTakenOver;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_productgroup")	
	public ProductGroup getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(ProductGroup productGroup) {
		this.productGroup = productGroup;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_paymentmethod")
	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_paymentstatus")
	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_fundgroupdetails")
	public FundDetailsGroup getFundGroupDetails() {
		return fundGroupDetails;
	}

	public void setFundGroupDetails(FundDetailsGroup fundGroupDetails) {
		this.fundGroupDetails = fundGroupDetails;
	}

	@Column(name="user_acc_id")
	public int getUserAccId() {
		return userAccId;
	}

	public void setUserAccId(int userAccId) {
		this.userAccId = userAccId;
	}

	@Column(name = "invoice_no")
	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	@Column(name="notes")
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "transaction_date", length = 29)
	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "booking_date", length = 29)
	public Date getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_date", length = 29)
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_date", length = 29)
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "cancel_date", length = 29)
	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "refund_date", length = 29)
	public Date getRefundDate() {
		return refundDate;
	}

	public void setRefundDate(Date refundDate) {
		this.refundDate = refundDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dispute_date", length = 29)
	public Date getDisputeDate() {
		return disputeDate;
	}

	public void setDisputeDate(Date disputeDate) {
		this.disputeDate = disputeDate;
	}

	@Column(name="cancel_reason")
	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cancel_by")
	public Users getCancelBy() {
		return cancelBy;
	}

	public void setCancelBy(Users cancelBy) {
		this.cancelBy = cancelBy;
	}

	@Column(name = "transaction_code")
	public String getTransactionCode() {
		return transactionCode;
	}

	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}

	@Column(name="group_id")
	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "deleted_by")
	public Users getDeletedBy() {
		return deletedBy;
	}

	public void setDeletedBy(Users deletedBy) {
		this.deletedBy = deletedBy;
	}

	@Column(name = "deleted", columnDefinition="tinyint(1) default 0")
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "deletion_date", length = 29)
	public Date getDeletionDate() {
		return deletionDate;
	}

	public void setDeletionDate(Date deletionDate) {
		this.deletionDate = deletionDate;
	}
	
	@Column(name = "product_type")
	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	@Column(name = "installment")
	public String getInstallment() {
		return installment;
	}

	public void setInstallment(String installment) {
		this.installment = installment;
	}

	@Column(name = "stripe_src")
	public String getStripeSrc() {
		return stripeSrc;
	}

	public void setStripeSrc(String stripeSrc) {
		this.stripeSrc = stripeSrc;
	}

	@Column(name = "lang_code")
	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	@Column(name = "logged_in", columnDefinition="tinyint(1) default 0")
	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	@Column(name = "main_entry", columnDefinition="tinyint(1) default 0")
	public boolean isMainEntry() {
		return mainEntry;
	}

	public void setMainEntry(boolean mainEntry) {
		this.mainEntry = mainEntry;
	}

	@Column(name="dispute_amount", precision = 20, scale = 4)
	public BigDecimal getDisputeAmount() {
		return disputeAmount;
	}

	public void setDisputeAmount(BigDecimal disputeAmount) {
		this.disputeAmount = disputeAmount;
	}

	@Column(name = "dispute_trans_code")
	public String getDisputeTransCode() {
		return disputeTransCode;
	}

	public void setDisputeTransCode(String disputeTransCode) {
		this.disputeTransCode = disputeTransCode;
	}

	@Column(name = "dispute_json")
	public String getDisputeJson() {
		return disputeJson;
	}

	public void setDisputeJson(String disputeJson) {
		this.disputeJson = disputeJson;
	}

	@Column(name = "dispute_count")
	public int getDisputeCount() {
		return disputeCount;
	}

	public void setDisputeCount(int disputeCount) {
		this.disputeCount = disputeCount;
	}

	@Column(name = "recurring_generated", columnDefinition="tinyint(1) default 0")
	public boolean isRecurringGenerated() {
		return recurringGenerated;
	}

	public void setRecurringGenerated(boolean recurringGenerated) {
		this.recurringGenerated = recurringGenerated;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", length = 29)
	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modified_date", length = 29)
	public Date getModifiedDate() {
		return this.modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
}
