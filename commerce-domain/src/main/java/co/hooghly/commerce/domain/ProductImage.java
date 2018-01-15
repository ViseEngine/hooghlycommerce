package co.hooghly.commerce.domain;

import javax.persistence.*;
import org.hibernate.validator.constraints.NotEmpty;

import lombok.*;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true, exclude={"product"})
@Entity
@Table(name = "PRODUCT_IMAGE")
public class ProductImage extends AbstractBaseEntity {

	
	@Column(name = "DEFAULT_IMAGE")
	private boolean defaultImage = true;
	
	/**
	 * default to 0 for images managed by the system
	 */
	@Column(name = "IMAGE_TYPE")
	private int imageType;
	
	/**
	 * Refers to images not accessible through the system. It may also be a video.
	 */
	@Column(name = "PRODUCT_IMAGE_URL")
	private String productImageUrl;
	
	

	@Column(name = "IMAGE_CROP")
	private boolean imageCrop;
	
	@ManyToOne(targetEntity = Product.class)
	@JoinColumn(name = "PRODUCT_ID", nullable = false)
	private Product product;
	
	
	@Column(name="ALT_TAG", length=100)
	private String altTag;
	
	@NotEmpty
	@Column(name="NAME", nullable = false, length=120)
	private String name;
	
	@Column(name="TITLE", length=100)
	private String title;
	
	@Column(name="SUB_TITLE", length=100)
	private String subtitle;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	
}
