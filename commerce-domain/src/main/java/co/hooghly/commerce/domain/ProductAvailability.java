package co.hooghly.commerce.domain;

import java.util.*;

import javax.persistence.*;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.*;



@Entity
@Table(name="PRODUCT_AVAILABILITY")
@Data
@EqualsAndHashCode(callSuper = true, exclude={"product"})
public class ProductAvailability extends AbstractBaseEntity {
	

	
	@JsonManagedReference
	@ManyToOne(targetEntity = Product.class)
	@JoinColumn(name = "PRODUCT_ID", nullable = false)
	private Product product;
	
	@NotNull
	@Column(name="QUANTITY")
	private Integer productQuantity = 0;
	
	@Temporal(TemporalType.DATE)
	@Column(name="DATE_AVAILABLE")
	private Date productDateAvailable;
	
	@Column(name="REGION")
	private String region = "*";
	
	@Column(name="REGION_VARIANT")
	private String regionVariant;
	
	@Column(name="STATUS")
	private boolean productStatus = true;
	
	@Column(name="FREE_SHIPPING")
	private boolean productIsAlwaysFreeShipping;
	
	@Column(name="QUANTITY_ORD_MIN")
	private Integer productQuantityOrderMin = 0;
	
	@Column(name="QUANTITY_ORD_MAX")
	private Integer productQuantityOrderMax = 0;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy="productAvailability", cascade = CascadeType.ALL)
	private Set<ProductPrice> prices = new HashSet<>();
	
	@Transient
	public ProductPrice defaultPrice() {
		
		for(ProductPrice price : prices) {
			if(price.isDefaultPrice()) {
				return price;
			}
		}
		return new ProductPrice();
	}
	
		
}
