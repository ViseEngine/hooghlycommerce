package co.hooghly.commerce.domain.admin;

import co.hooghly.commerce.domain.ProductAvailability;
import co.hooghly.commerce.domain.ProductPriceDescription;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

public class ProductPrice {
	
	@Valid
	private co.hooghly.commerce.domain.ProductPrice price = null;
	@Valid
	private List <ProductPriceDescription> descriptions = new ArrayList<ProductPriceDescription>();
	private String priceText;
	private String specialPriceText;
	private ProductAvailability productAvailability;
	
	
	//cannot convert in this object to date ??? needs to use a string, parse, bla bla
	private String productPriceSpecialStartDate;
	private String productPriceSpecialEndDate;
	
	private co.hooghly.commerce.domain.Product product;
	
	
	
	
	
	public List <ProductPriceDescription> getDescriptions() {
		return descriptions;
	}
	public void setDescriptions(List <ProductPriceDescription> descriptions) {
		this.descriptions = descriptions;
	}
	public ProductAvailability getProductAvailability() {
		return productAvailability;
	}
	public void setProductAvailability(ProductAvailability productAvailability) {
		this.productAvailability = productAvailability;
	}
	public String getPriceText() {
		return priceText;
	}
	public void setPriceText(String priceText) {
		this.priceText = priceText;
	}
	public co.hooghly.commerce.domain.ProductPrice getPrice() {
		return price;
	}
	public void setPrice(co.hooghly.commerce.domain.ProductPrice price) {
		this.price = price;
	}
	public String getSpecialPriceText() {
		return specialPriceText;
	}
	public void setSpecialPriceText(String specialPriceText) {
		this.specialPriceText = specialPriceText;
	}

	public co.hooghly.commerce.domain.Product getProduct() {
		return product;
	}
	public void setProduct(co.hooghly.commerce.domain.Product product) {
		this.product = product;
	}
	public String getProductPriceSpecialStartDate() {
		return productPriceSpecialStartDate;
	}
	public void setProductPriceSpecialStartDate(
			String productPriceSpecialStartDate) {
		this.productPriceSpecialStartDate = productPriceSpecialStartDate;
	}
	public String getProductPriceSpecialEndDate() {
		return productPriceSpecialEndDate;
	}
	public void setProductPriceSpecialEndDate(String productPriceSpecialEndDate) {
		this.productPriceSpecialEndDate = productPriceSpecialEndDate;
	}

}
