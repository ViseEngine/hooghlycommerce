package co.hooghly.commerce.domain.admin;

import co.hooghly.commerce.domain.ProductAvailability;
import co.hooghly.commerce.domain.ProductDescription;
import co.hooghly.commerce.domain.ProductImage;
import co.hooghly.commerce.domain.ProductPrice;
import lombok.Data;

import org.hibernate.validator.constraints.NotEmpty;
//import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class Product implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4531526676134574984L;

	/**
	 * 
	 */

	//provides wrapping to the main product entity
	@Valid
	private co.hooghly.commerce.domain.Product product;
	
	@Valid
	private List<ProductDescription> descriptions = new ArrayList<ProductDescription>();
	
	@Valid
	private ProductAvailability availability = null;
	
	@Valid
	private ProductPrice price = null;
	
	private MultipartFile image = null;
	
	private ProductImage productImage = null;
	
	@NotEmpty
	private String productPrice = "0";
	
	private String dateAvailable;

	private ProductDescription description = null;
	
	


}
