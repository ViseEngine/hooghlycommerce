package co.hooghly.commerce.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


/**
 * Merchant configuration information

 *
 */
@Entity
@EntityListeners(value = AuditListener.class)
@Table(name = "MERCHANT_CONFIGURATION",  uniqueConstraints=
	@UniqueConstraint(columnNames = {"MERCHANT_ID", "CONFIG_KEY"}))
public class MerchantConfiguration extends SalesManagerEntity<Long, MerchantConfiguration> implements Serializable, Auditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4246917986731953459L;

	@Id
	@Column(name = "MERCHANT_CONFIG_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="MERCHANT_ID", nullable=true)
	private MerchantStore merchantStore;
	
	@Embedded
	private AuditSection auditSection = new AuditSection();
	
	@Column(name="CONFIG_KEY")
	private String key;

	
	@Column(name="VALUE")
	@Lob
	private String value;
	
	@Column(name="TYPE")
	@Enumerated(value = EnumType.STRING)
	private MerchantConfigurationType merchantConfigurationType = MerchantConfigurationType.INTEGRATION;

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public AuditSection getAuditSection() {
		return auditSection;
	}

	public void setAuditSection(AuditSection auditSection) {
		this.auditSection = auditSection;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}



	public MerchantStore getMerchantStore() {
		return merchantStore;
	}

	public void setMerchantStore(MerchantStore merchantStore) {
		this.merchantStore = merchantStore;
	}

	public void setMerchantConfigurationType(MerchantConfigurationType merchantConfigurationType) {
		this.merchantConfigurationType = merchantConfigurationType;
	}

	public MerchantConfigurationType getMerchantConfigurationType() {
		return merchantConfigurationType;
	}


}
