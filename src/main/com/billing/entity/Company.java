package com.billing.entity;



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
import javax.validation.constraints.Size;

@NamedQueries({
	@NamedQuery(
		name = "findAllCompany",
		query = "from Company company",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	),@NamedQuery(
		name = "findAllCompanyWithAssignedOrderByAsc",
		query = "from Company company where company.assigned = :assigned order by company.id asc",
		hints={@QueryHint(name="org.hibernate.cacheable",value="true")}
	)
})
@Entity
@Table(name = "company")
public class Company implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private Users users;
	private Currencies currencies;
	private Plan plan;
	private String firstName;
	private String lastName;
	private String email;
	private String telephone;
	private String mobile;
	private String address;
	private String houseno;
	private String city;
	private String country;
	private String state;
	private String zip;
	private String companyName;
	private String websiteLink;
	private String description;
	private String datePattern;
	private byte[] logo;
	private String logoUrl;
	private boolean enable;
	private boolean assigned;
	private String code;
	private boolean selfStripeAccount;
	private boolean liveAccount;
	private boolean addressShow;
	private boolean addressRequired;
	private boolean phoneShow;
	private boolean phoneRequired;
	private boolean sendDboxEmail;
	private boolean sendCCMail;
	private String fromEmail;
	private String ccMail;
	private boolean enableTransCheckBox;
	private boolean createFutureEntry;
	private boolean executeRecurring;
	private Date createdDate;
	private Date modifiedDate;
	
	public Company() {
	}

	public Company(int id) {
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
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_users")
	public Users getUsers() {
		return users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_currencies")
	public Currencies getCurrencies() {
		return currencies;
	}

	public void setCurrencies(Currencies currencies) {
		this.currencies = currencies;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_plan")
	public Plan getPlan() {
		return plan;
	}

	public void setPlan(Plan plan) {
		this.plan = plan;
	}

	@Column(name = "first_name", length = 255)
	@Size(max = 255)
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(name = "last_name", length = 255)
	@Size(max = 255)
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(name = "email", length = 255)
	@Size(max = 255)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "telephone", length = 255)
	@Size(max = 255)
	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	@Column(name = "mobile", length = 255)
	@Size(max = 255)
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Column(name = "address", length = 255)
	@Size(max = 255)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "house_no", length = 255)
	@Size(max = 255)
	public String getHouseno() {
		return houseno;
	}

	public void setHouseno(String houseno) {
		this.houseno = houseno;
	}

	@Column(name = "city", length = 255)
	@Size(max = 255)
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(name = "country", length = 255)
	@Size(max = 255)
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Column(name = "state", length = 255)
	@Size(max = 255)
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Column(name = "zip", length = 255)
	@Size(max = 255)
	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	@Column(name = "company_name", length = 255)
	@Size(max = 255)
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	@Column(name = "website_link", length = 255)
	@Size(max = 255)
	public String getWebsiteLink() {
		return websiteLink;
	}

	public void setWebsiteLink(String websiteLink) {
		this.websiteLink = websiteLink;
	}

	@Column(name = "description", length = 255)
	@Size(max = 255)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "date_pattern", length = 25)
	@Size(max = 25)
	public String getDatePattern() {
		return datePattern;
	}

	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
	}

	@Column(name = "logo")
	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	@Column(name="logo_url")
	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	@Column(name = "enable", columnDefinition="tinyint(1) default 1")
	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	@Column(name = "assigned", columnDefinition="tinyint(1) default 0")
	public boolean isAssigned() {
		return assigned;
	}

	public void setAssigned(boolean assigned) {
		this.assigned = assigned;
	}

	@Column(name="code")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "self_stripe_account", columnDefinition="tinyint(1) default 0")
	public boolean isSelfStripeAccount() {
		return selfStripeAccount;
	}

	public void setSelfStripeAccount(boolean selfStripeAccount) {
		this.selfStripeAccount = selfStripeAccount;
	}

	@Column(name = "live_account", columnDefinition="tinyint(1) default 0")
	public boolean isLiveAccount() {
		return liveAccount;
	}

	public void setLiveAccount(boolean liveAccount) {
		this.liveAccount = liveAccount;
	}

	@Column(name = "address_show", columnDefinition="tinyint(1) default 1")
	public boolean isAddressShow() {
		return addressShow;
	}

	public void setAddressShow(boolean addressShow) {
		this.addressShow = addressShow;
	}

	@Column(name = "address_required", columnDefinition="tinyint(1) default 0")
	public boolean isAddressRequired() {
		return addressRequired;
	}

	public void setAddressRequired(boolean addressRequired) {
		this.addressRequired = addressRequired;
	}

	@Column(name = "phone_show", columnDefinition="tinyint(1) default 1")
	public boolean isPhoneShow() {
		return phoneShow;
	}

	public void setPhoneShow(boolean phoneShow) {
		this.phoneShow = phoneShow;
	}

	@Column(name = "phone_required", columnDefinition="tinyint(1) default 0")
	public boolean isPhoneRequired() {
		return phoneRequired;
	}

	public void setPhoneRequired(boolean phoneRequired) {
		this.phoneRequired = phoneRequired;
	}
	
	@Column(name = "send_dbox_email", columnDefinition="tinyint(1) default 1")
	public boolean isSendDboxEmail() {
		return sendDboxEmail;
	}

	public void setSendDboxEmail(boolean sendDboxEmail) {
		this.sendDboxEmail = sendDboxEmail;
	}

	@Column(name = "send_cc_mail", columnDefinition="tinyint(1) default 1")
	public boolean isSendCCMail() {
		return sendCCMail;
	}

	public void setSendCCMail(boolean sendCCMail) {
		this.sendCCMail = sendCCMail;
	}

	@Column(name = "from_email")
	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	@Column(name = "cc_email")
	public String getCcMail() {
		return ccMail;
	}

	public void setCcMail(String ccMail) {
		this.ccMail = ccMail;
	}

	@Column(name = "enable_trans_check_box", columnDefinition="tinyint(1) default 1")
	public boolean isEnableTransCheckBox() {
		return enableTransCheckBox;
	}

	public void setEnableTransCheckBox(boolean enableTransCheckBox) {
		this.enableTransCheckBox = enableTransCheckBox;
	}

	@Column(name = "create_future_entry", columnDefinition="tinyint(1) default 1")
	public boolean isCreateFutureEntry() {
		return createFutureEntry;
	}

	public void setCreateFutureEntry(boolean createFutureEntry) {
		this.createFutureEntry = createFutureEntry;
	}

	@Column(name = "execute_recurring", columnDefinition="tinyint(1) default 1")
	public boolean isExecuteRecurring() {
		return executeRecurring;
	}

	public void setExecuteRecurring(boolean executeRecurring) {
		this.executeRecurring = executeRecurring;
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
