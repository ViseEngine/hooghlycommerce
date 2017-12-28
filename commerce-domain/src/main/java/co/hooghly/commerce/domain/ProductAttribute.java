package co.hooghly.commerce.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "PRODUCT_ATTRIBUTE", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "OPTION_ID", "OPTION_VALUE_ID", "PRODUCT_ID" }) })
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductAttribute extends AbstractBaseEntity {

	@Column(name = "PRODUCT_ATRIBUTE_PRICE")
	private BigDecimal productAttributePrice;

	@Column(name = "PRODUCT_ATTRIBUTE_SORT_ORD")
	private Integer productOptionSortOrder;

	@Column(name = "PRODUCT_ATTRIBUTE_FREE")
	private boolean productAttributeIsFree;

	@Column(name = "PRODUCT_ATTRIBUTE_WEIGHT")
	private BigDecimal productAttributeWeight;

	@Column(name = "PRODUCT_ATTRIBUTE_DEFAULT")
	private boolean attributeDefault = false;

	@Column(name = "PRODUCT_ATTRIBUTE_REQUIRED")
	private boolean attributeRequired = false;

	/**
	 * a read only attribute is considered as a core attribute addition
	 */
	@Column(name = "PRODUCT_ATTRIBUTE_FOR_DISP")
	private boolean attributeDisplayOnly = false;

	@Column(name = "PRODUCT_ATTRIBUTE_DISCOUNTED")
	private boolean attributeDiscounted = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OPTION_ID", nullable = false)
	private ProductOption productOption;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OPTION_VALUE_ID", nullable = false)
	private ProductOptionValue productOptionValue;

	/**
	 * This transient object property is a utility used only to submit from a
	 * free text
	 */
	@Transient
	private String attributePrice = "0";

	/**
	 * This transient object property is a utility used only to submit from a
	 * free text
	 */
	@Transient
	private String attributeSortOrder = "0";

	/**
	 * This transient object property is a utility used only to submit from a
	 * free text
	 */
	@Transient
	private String attributeAdditionalWeight = "0";

	

	@JsonManagedReference
	@ManyToOne(targetEntity = Product.class)
	@JoinColumn(name = "PRODUCT_ID", nullable = false)
	private Product product;



}
