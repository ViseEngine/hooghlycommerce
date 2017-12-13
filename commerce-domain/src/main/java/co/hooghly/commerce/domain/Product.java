package co.hooghly.commerce.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Entity
@EntityListeners(value = AuditListener.class)
@Table(name = "PRODUCT",  uniqueConstraints=
@UniqueConstraint(columnNames = {"MERCHANT_ID", "SKU"}))
@Data
@EqualsAndHashCode(callSuper=false)
public class Product extends SalesManagerEntity<Long, Product> implements Auditable {
	private static final long serialVersionUID = -6228066416290007047L;
	
	
	@Id
	@Column(name = "PRODUCT_ID", unique=true, nullable=false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	private AuditSection auditSection = new AuditSection();
	
	
	@JsonManagedReference
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "product")
	private Set<ProductDescription> descriptions = new HashSet<ProductDescription>();
	
	
	@JsonBackReference
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="product")
	private Set<ProductAvailability> availabilities = new HashSet<ProductAvailability>();
	
	
	@JsonBackReference
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "product")
	private Set<ProductAttribute> attributes = new HashSet<ProductAttribute>();
	
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "product")//cascade is set to remove because product save requires logic to create physical image first and then save the image id in the database, cannot be done in cascade
	@JsonBackReference
	private Set<ProductImage> images = new HashSet<ProductImage>();

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "product")
	@JsonBackReference
	private Set<ProductRelationship> relationships = new HashSet<ProductRelationship>();

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="MERCHANT_ID", nullable=false)
	private MerchantStore merchantStore;
	
	@JsonBackReference
	@ManyToMany(fetch=FetchType.LAZY, cascade = {CascadeType.REFRESH})
	@JoinTable(name = "PRODUCT_CATEGORY", joinColumns = { 
			@JoinColumn(name = "PRODUCT_ID", nullable = false, updatable = false) }
			, 
			inverseJoinColumns = { @JoinColumn(name = "CATEGORY_ID", 
					nullable = false, updatable = false) }
	)
	@Cascade({
		org.hibernate.annotations.CascadeType.DETACH,
		org.hibernate.annotations.CascadeType.LOCK,
		org.hibernate.annotations.CascadeType.REFRESH,
		org.hibernate.annotations.CascadeType.REPLICATE
		
	})
	private Set<Category> categories = new HashSet<Category>();
	
	@Column(name="DATE_AVAILABLE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateAvailable = new Date();
	
	
	@Column(name="AVAILABLE")
	private boolean available = true;
	
	@Column(name="PREORDER")
	private boolean preOrder = false;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
	@JoinColumn(name="MANUFACTURER_ID", nullable=true)
	private Manufacturer manufacturer;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
	@JoinColumn(name="PRODUCT_TYPE_ID", nullable=true)
	private ProductType type;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
	@JoinColumn(name="TAX_CLASS_ID", nullable=true)
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
	@Pattern(regexp="^[a-zA-Z0-9_]*$")
	@Column(name = "SKU")
	private String sku;
	
	/**
	 * External system reference SKU/ID
	 */
	@Column(name = "REF_SKU")
	private String refSku;

	
	public ProductDescription getProductDescription() {
		if(this.getDescriptions()!=null && this.getDescriptions().size()>0) {
			return this.getDescriptions().iterator().next();
		}
		return null;
	}
	
	public ProductImage getProductImage() {
		ProductImage productImage = null;
		if(this.getImages()!=null && this.getImages().size()>0) {
			for(ProductImage image : this.getImages()) {
				productImage = image;
				if(productImage.isDefaultImage()) {
					break;
				}
			}
		}
		return productImage;
	}
	
}
