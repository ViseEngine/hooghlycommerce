package co.hooghly.commerce.domain;

import java.math.BigDecimal;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.*;

import lombok.*;

@Entity
@EntityListeners(value = AuditListener.class)
@Table(name = "PRODUCT", uniqueConstraints = @UniqueConstraint(columnNames = { "MERCHANT_ID", "SKU" }))
@Data
@EqualsAndHashCode(callSuper = true)
public class Product extends AbstractBaseEntity {

	@JsonBackReference
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "product")
	private Set<ProductAvailability> availabilities = new HashSet<>();

	@JsonBackReference
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "product")
	private Set<ProductAttribute> attributes = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "product")
	@JsonBackReference
	private Set<ProductImage> images = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "product")
	@JsonBackReference
	private Set<ProductRelationship> relationships = new HashSet<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MERCHANT_ID", nullable = false)
	private MerchantStore merchantStore;

	@JsonBackReference
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PRODUCT_CATEGORY", joinColumns = {
			@JoinColumn(name = "PRODUCT_ID", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "CATEGORY_ID", nullable = false, updatable = false) })
	@Cascade({ org.hibernate.annotations.CascadeType.DETACH, org.hibernate.annotations.CascadeType.LOCK,
			org.hibernate.annotations.CascadeType.REFRESH, org.hibernate.annotations.CascadeType.REPLICATE

	})
	private Set<Category> categories = new HashSet<>();

	@Column(name = "DATE_AVAILABLE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateAvailable = new Date();

	@Column(name = "AVAILABLE")
	private boolean available = true;

	@Column(name = "PREORDER")
	private boolean preOrder = false;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "MANUFACTURER_ID", nullable = true)
	private Manufacturer manufacturer;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PRODUCT_TYPE_ID", nullable = true)
	private ProductType type;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "TAX_CLASS_ID", nullable = true)
	private TaxClass taxClass;

	@Column(name = "PRODUCT_VIRTUAL")
	private boolean productVirtual = false;

	@Column(name = "PRODUCT_SHIP")
	private boolean productShipeable = false;

	@Column(name = "PRODUCT_FREE")
	private boolean productIsFree;

	@Column(name = "PRODUCT_LENGTH")
	private BigDecimal productLength;

	@Column(name = "PRODUCT_WIDTH")
	private BigDecimal productWidth;

	@Column(name = "PRODUCT_HEIGHT")
	private BigDecimal productHeight;

	@Column(name = "PRODUCT_WEIGHT")
	private BigDecimal productWeight;

	@Column(name = "REVIEW_AVG")
	private BigDecimal productReviewAvg;

	@Column(name = "REVIEW_COUNT")
	private Integer productReviewCount;

	@Column(name = "QUANTITY_ORDERED")
	private Integer productOrdered;

	@Column(name = "SORT_ORDER")
	private Integer sortOrder = new Integer(0);

	@NotEmpty
	@Pattern(regexp = "^[a-zA-Z0-9_]*$")
	@Column(name = "SKU")
	private String sku;

	/**
	 * External system reference SKU/ID
	 */
	@Column(name = "REF_SKU")
	private String refSku;

	// product desc
	@Column(name = "PRODUCT_HIGHLIGHT")
	private String productHighlight;

	@Column(name = "DOWNLOAD_LNK")
	private String productExternalDl;

	@Column(name = "SEF_URL")
	private String seUrl;

	@Column(name = "META_TITLE")
	private String metatagTitle;

	@Column(name = "META_KEYWORDS")
	private String metatagKeywords;

	@Column(name = "META_DESCRIPTION")
	private String metatagDescription;

	@NotEmpty
	@Column(name = "NAME", nullable = false, length = 120)
	private String name;

	@Column(name = "TITLE", length = 100)
	private String title;

	@Column(name = "SUB_TITLE", length = 100)
	private String subtitle;

	@Column(name = "DESCRIPTION")
	private String description;
	


	public ProductImage getProductImage() {
		return getImages().stream().filter(i -> i.isDefaultImage()).findFirst().get();
	}

	public Double getProductRating() {
		double rating = 0.0d;
		if (getProductReviewAvg() != null) {
			double avg = getProductReviewAvg().doubleValue();
			rating = Math.round(avg * 2) / 2.0f;

		}
		return rating;
	}
	
	public int getRatingCount() {
		int count = 0;
		
		if(getProductReviewCount()!=null) {
			count = getProductReviewCount().intValue();
		}
		
		return count;
	}
	
	@Transient
	private FinalPrice finalPrice;
}
