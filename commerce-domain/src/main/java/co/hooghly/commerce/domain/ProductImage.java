package co.hooghly.commerce.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "PRODUCT_IMAGE")
public class ProductImage extends SalesManagerEntity<Long, ProductImage> {
	private static final long serialVersionUID = 247514890386076337L;
	
	@Id
	@Column(name = "PRODUCT_IMAGE_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "productImage", cascade = CascadeType.ALL)
	private List<ProductImageDescription> descriptions = new ArrayList<ProductImageDescription>();

	
	@Column(name = "PRODUCT_IMAGE")
	private String productImage;
	
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
	
	/**
	 * Refers to images through the system. It may also be a video.
	 */
	@Column(name = "PRODUCT_IMAGE_URL_LOCAL", unique = true)
	private String productImageLocalUrl;
	

	@Column(name = "IMAGE_CROP")
	private boolean imageCrop;
	
	@ManyToOne(targetEntity = Product.class)
	@JoinColumn(name = "PRODUCT_ID", nullable = false)
	private Product product;
	
	
	@Lob
    @Column(name="PRODUCT_IMAGE_DATA", nullable=true, columnDefinition="mediumblob")
    private byte[] image;
	
	
	public String getProductImageLocalUrl() {
		if(StringUtils.isEmpty(productImageLocalUrl)) {
			setProductImageLocalUrl("/shop/product/image/download/"+id);
		}
		
		return productImageLocalUrl;
	}
}
