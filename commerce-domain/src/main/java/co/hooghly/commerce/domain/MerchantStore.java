package co.hooghly.commerce.domain;

import java.util.*;

import javax.persistence.*;

import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

import co.hooghly.commerce.constants.MeasureUnit;
import lombok.*;

@Entity
@Table(name = "MERCHANT_STORE")
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantStore extends AbstractBaseEntity {

	public static final String DEFAULT_STORE = "DEFAULT";

	@NotEmpty
	@Column(name = "STORE_NAME", nullable = false, length = 100)
	private String storename;

	@NotEmpty
	@Pattern(regexp = "^[a-zA-Z0-9_]*$")
	@Column(name = "STORE_CODE", nullable = false, unique = true, length = 100)
	private String code;

	@NotEmpty
	@Column(name = "STORE_PHONE", length = 50)
	private String storephone;

	@Email
	@NotEmpty
	@Column(name = "STORE_EMAIL", length = 60, nullable = false)
	private String storeEmailAddress;

	@Column(name = "STORE_LOGO", length = 100)
	private String storeLogo;

	@ManyToOne(fetch = FetchType.EAGER, targetEntity = Currency.class)
	@JoinColumn(name = "CURRENCY_ID", nullable = false)
	private Currency currency;

	@Embedded
	private Address address;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER, targetEntity = Country.class)
	@JoinColumn(name = "COUNTRY_ID", nullable = false, updatable = true)
	private Country country;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER, targetEntity = Zone.class)
	@JoinColumn(name = "ZONE_ID", nullable = true, updatable = true)
	private Zone zone;

	@Enumerated(EnumType.STRING)
	@Column(name = "WEIGHTUNITCODE", length = 5)
	private MeasureUnit weightunitcode = MeasureUnit.LB;

	@Enumerated(EnumType.STRING)
	@Column(name = "SEIZEUNITCODE", length = 5)
	private MeasureUnit seizeunitcode = MeasureUnit.IN;

	@Temporal(TemporalType.DATE)
	@Column(name = "IN_BUSINESS_SINCE")
	private Date inBusinessSince = new Date();

	@Transient
	private String dateBusinessSince;

	@ManyToOne(fetch = FetchType.EAGER, targetEntity = Language.class)
	@JoinColumn(name = "LANGUAGE_ID", nullable = false)
	private Language defaultLanguage;

	@NotEmpty
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "MERCHANT_LANGUAGE")
	private List<Language> languages = new ArrayList<>();

	@Column(name = "USE_CACHE")
	private boolean useCache = false;

	@Column(name = "STORE_TEMPLATE", length = 25)
	private String storeTemplate;

	@Column(name = "INVOICE_TEMPLATE", length = 25)
	private String invoiceTemplate;

	@Column(name = "DOMAIN_NAME", length = 80)
	private String domainName;

	@Column(name = "CONTINUESHOPPINGURL", length = 150)
	private String continueshoppingurl;

	@Column(name = "CURRENCY_FORMAT_NATIONAL")
	private boolean currencyFormatNational;
	
	@JoinColumn(name = "THEME", nullable = false)
	private String theme;
	

}
