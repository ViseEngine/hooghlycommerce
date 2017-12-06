package co.hooghly.commerce.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Entity
@EntityListeners(value = AuditListener.class)
@Table(name = "LANGUAGE")
@Cacheable
@Data
@EqualsAndHashCode(callSuper=false)
public class Language extends SalesManagerEntity<Integer, Language> implements Auditable {
	private static final long serialVersionUID = -7676627812941330669L;
	

	
	@Id
	@Column(name="LANGUAGE_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Embedded
	private AuditSection auditSection = new AuditSection();

	@Column(name="CODE", nullable = false)
	private String code;
	
	@Column(name="SORT_ORDER")
	private Integer sortOrder;
	
	
	@OneToMany(mappedBy = "defaultLanguage", targetEntity = MerchantStore.class)
	private List<MerchantStore> storesDefaultLanguage;
	
	
	@ManyToMany(mappedBy = "languages", targetEntity = MerchantStore.class)
	private List<MerchantStore> stores = new ArrayList<MerchantStore>();
	
	
}
