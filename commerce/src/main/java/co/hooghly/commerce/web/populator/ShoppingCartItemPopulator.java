package co.hooghly.commerce.web.populator;

import org.apache.commons.lang3.Validate;

import co.hooghly.commerce.business.ConversionException;
import co.hooghly.commerce.business.ProductAttributeService;
import co.hooghly.commerce.business.ProductService;
import co.hooghly.commerce.business.ServiceException;
import co.hooghly.commerce.business.ShoppingCartService;
import co.hooghly.commerce.business.utils.AbstractDataPopulator;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.domain.ProductAttribute;
import co.hooghly.commerce.domain.ShoppingCartItem;
import co.hooghly.commerce.web.ui.PersistableOrderProduct;

public class ShoppingCartItemPopulator extends
		AbstractDataPopulator<PersistableOrderProduct, ShoppingCartItem> {
	

	private ProductService productService;
	private ProductAttributeService productAttributeService;
	private ShoppingCartService shoppingCartService;

	@Override
	public ShoppingCartItem populate(PersistableOrderProduct source,
			ShoppingCartItem target, MerchantStore store, Language language)
			throws ConversionException {
		Validate.notNull(productService, "Requires to set productService");
		Validate.notNull(productAttributeService, "Requires to set productAttributeService");
		Validate.notNull(shoppingCartService, "Requires to set shoppingCartService");
		
		/*Product product = productService.findOne(source.getProduct().getId());
		if(source.getAttributes()!=null) {

			for(co.hooghly.commerce.web.ui.ProductAttribute attr : source.getAttributes()) {
				ProductAttribute attribute = productAttributeService.getById(attr.getId());
				if(attribute==null) {
					throw new ConversionException("ProductAttribute with id " + attr.getId() + " is null");
				}
				if(attribute.getProduct().getId().longValue()!=source.getProduct().getId().longValue()) {
					throw new ConversionException("ProductAttribute with id " + attr.getId() + " is not assigned to Product id " + source.getProduct().getId());
				}
				product.getAttributes().add(attribute);
			}
		}
		
		try {
			return shoppingCartService.populateShoppingCartItem(product);
		} catch (ServiceException e) {
			throw new ConversionException(e);
		}*/
		
		return null;
		
	}

	@Override
	protected ShoppingCartItem createTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setProductAttributeService(ProductAttributeService productAttributeService) {
		this.productAttributeService = productAttributeService;
	}

	public ProductAttributeService getProductAttributeService() {
		return productAttributeService;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public ProductService getProductService() {
		return productService;
	}

	public void setShoppingCartService(ShoppingCartService shoppingCartService) {
		this.shoppingCartService = shoppingCartService;
	}

	public ShoppingCartService getShoppingCartService() {
		return shoppingCartService;
	}

}
