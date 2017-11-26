package co.hooghly.commerce.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import co.hooghly.commerce.constants.MeasureUnit;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "MERCHANT_STORE_VIEW" , uniqueConstraints={
	    @UniqueConstraint(columnNames = {"MERCHANT_STORE_ID", "LANGUAGE_ID", "CURRENCY_ID"})
	})

@Data
@EqualsAndHashCode(callSuper=false)
public class MerchantStoreView extends SalesManagerEntity<Integer, MerchantStoreView> {
	private static final long serialVersionUID = 7671103335743647655L;
	
	
	public final static String DEFAULT_STORE = "DEFAULT";
	
	@Id
	@Column(name = "MERCHANT_STORE_VIEW_ID", unique=true, nullable=false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
		
	@Column(name = "WEIGHTUNITCODE", length=5)
	private String weightunitcode = MeasureUnit.LB.name();

	@Column(name = "SEIZEUNITCODE", length=5)
	private String seizeunitcode = MeasureUnit.IN.name();

	
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Language.class)
	@JoinColumn(name = "LANGUAGE_ID", nullable=false)
	private Language language;

	
	@Column(name="CONTINUESHOPPINGURL", length=150)
	private String continueshoppingurl;
	
	
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Currency.class)
	@JoinColumn(name = "CURRENCY_ID", nullable=false)
	private Currency currency;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="MERCHANT_STORE_ID")
	private MerchantStore merchantStore;
	
	@Column(name = "THEME", length=20)
	private String theme ;
	
	@Column(name = "IS_DEFAULT_STORE_VIEW")
	private boolean defaultView = false;
	

}
