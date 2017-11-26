package co.hooghly.commerce.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Pattern;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonBackReference;

import co.hooghly.commerce.constants.MeasureUnit;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "MERCHANT_STORE" )
@Data
@EqualsAndHashCode(callSuper=false)
public class MerchantStore extends SalesManagerEntity<Integer, MerchantStore> {
	private static final long serialVersionUID = 7671103335743647655L;
	
	
	public final static String DEFAULT_STORE = "DEFAULT";
	
	@Id
	@Column(name = "MERCHANT_ID", unique=true, nullable=false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotEmpty
	@Column(name = "STORE_NAME", nullable=false, length=100)
	private String storename;
	
	@NotEmpty
	@Pattern(regexp="^[a-zA-Z0-9_]*$")
	@Column(name = "STORE_CODE", nullable=false, unique=true, length=100)
	private String code;
	
	@NotEmpty
	@Column(name = "STORE_PHONE", length=50)
	private String storephone;

	@Column(name = "STORE_ADDRESS")
	private String storeaddress;

	@NotEmpty
	@Column(name = "STORE_CITY", length=100)
	private String storecity;

	@NotEmpty
	@Column(name = "STORE_POSTAL_CODE", length=15)
	private String storepostalcode;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Country.class)
	@JoinColumn(name="COUNTRY_ID", nullable=false, updatable=true)
	private Country country;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Zone.class)
	@JoinColumn(name="ZONE_ID", nullable=true, updatable=true)
	private Zone zone;

	@Column(name = "STORE_STATE_PROV", length=100)
	private String storestateprovince;
	
	@Column(name = "WEIGHTUNITCODE", length=5)
	private String weightunitcode = MeasureUnit.LB.name();

	@Column(name = "SEIZEUNITCODE", length=5)
	private String seizeunitcode = MeasureUnit.IN.name();

	@Temporal(TemporalType.DATE)
	@Column(name = "IN_BUSINESS_SINCE")
	private Date inBusinessSince = new Date();
	
	@Transient
	private String dateBusinessSince;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Language.class)
	@JoinColumn(name = "LANGUAGE_ID", nullable=false)
	private Language defaultLanguage;
	
	@Deprecated
	@NotEmpty
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "MERCHANT_LANGUAGE")
	private List<Language> languages = new ArrayList<Language>();
	
	@Column(name = "USE_CACHE")
	private boolean useCache = false;
	
	@Column(name="STORE_TEMPLATE", length=25)
	private String storeTemplate;
	
	@Column(name="INVOICE_TEMPLATE", length=25)
	private String invoiceTemplate;
	
	@Column(name="DOMAIN_NAME", length=80)
	private String domainName;
	
	@Column(name="CONTINUESHOPPINGURL", length=150)
	private String continueshoppingurl;
	
	@Email
	@NotEmpty
	@Column(name = "STORE_EMAIL", length=60, nullable=false)
	private String storeEmailAddress;
	
	@Column(name="STORE_LOGO", length=100)
	private String storeLogo;
	
	@Deprecated
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Currency.class)
	@JoinColumn(name = "CURRENCY_ID", nullable=false)
	private Currency currency;
	
	@Column(name = "CURRENCY_FORMAT_NATIONAL")
	private boolean currencyFormatNational;
	

	@OneToMany(mappedBy="merchantStore",fetch=FetchType.EAGER, cascade = CascadeType.PERSIST)
	@Fetch(FetchMode.SUBSELECT)
	private List<MerchantStoreView> storeViews = new ArrayList<>();




}
