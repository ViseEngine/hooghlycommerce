package co.hooghly.commerce.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Entity
@Table(name = "PRODUCT_PRICE")
@Data
@EqualsAndHashCode(callSuper=true, exclude={"productAvailability"})
public class ProductPrice extends AbstractBaseEntity {
	
	
	private final static String DEFAULT_PRICE_CODE="base";

	

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "productPrice", cascade = CascadeType.ALL)
	private Set<ProductPriceDescription> descriptions = new HashSet<>();

	@NotEmpty
	@Pattern(regexp="^[a-zA-Z0-9_]*$")
	@Column(name = "PRODUCT_PRICE_CODE", nullable=false)
	private String code = DEFAULT_PRICE_CODE;

	@Column(name = "PRODUCT_PRICE_AMOUNT", nullable=false)
	private BigDecimal productPriceAmount = new BigDecimal(0);

	@Column(name = "PRODUCT_PRICE_TYPE", length=20)
	@Enumerated(value = EnumType.STRING)
	private ProductPriceType productPriceType = ProductPriceType.ONE_TIME;

	@Column(name = "DEFAULT_PRICE")
	private boolean defaultPrice = false;

	@Temporal(TemporalType.DATE)
	@Column(name = "PRODUCT_PRICE_SPECIAL_ST_DATE")
	private Date productPriceSpecialStartDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "PRODUCT_PRICE_SPECIAL_END_DATE")
	private Date productPriceSpecialEndDate;

	@Column(name = "PRODUCT_PRICE_SPECIAL_AMOUNT")
	private BigDecimal productPriceSpecialAmount;
	

	@ManyToOne(targetEntity = ProductAvailability.class)
	@JoinColumn(name = "PRODUCT_AVAIL_ID", nullable = false)
	private ProductAvailability productAvailability;
	

}
