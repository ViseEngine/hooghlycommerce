package co.hooghly.commerce.business;

import java.util.List;

import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.domain.ProductAttribute;
import co.hooghly.commerce.repository.ProductAttributeRepository;

@Service
public class ProductAttributeService extends AbstractBaseBusinessDelegate<ProductAttribute, Long> {

	private ProductAttributeRepository productAttributeRepository;

	public ProductAttributeService(ProductAttributeRepository productAttributeRepository) {
		super(productAttributeRepository);
		this.productAttributeRepository = productAttributeRepository;
	}

	public ProductAttribute getById(Long id) {

		return productAttributeRepository.findOne(id);

	}

	public List<ProductAttribute> getByOptionId(MerchantStore store, Long id)  {

		return productAttributeRepository.findByOptionId(store.getId(), id);

	}

	public List<ProductAttribute> getByAttributeIds(MerchantStore store, Product product, List<Long> ids)
			throws ServiceException {

		return productAttributeRepository.findByAttributeIds(store.getId(), product.getId(), ids);

	}

	public List<ProductAttribute> getByOptionValueId(MerchantStore store, Long id) {

		return productAttributeRepository.findByOptionValueId(store.getId(), id);

	}

	/**
	 * Returns all product attributes
	 */

	public List<ProductAttribute> getByProductId(MerchantStore store, Product product, Language language)
			throws ServiceException {
		return productAttributeRepository.findByProductId(store.getId(), product.getId(), language.getId());

	}

	public void saveOrUpdate(ProductAttribute productAttribute) {
		// if(productAttribute.getId()!=null && productAttribute.getId()>0) {
		// productAttributeRepository.update(productAttribute);
		// } else {
		productAttributeRepository.save(productAttribute);
		// }

	}

	public void delete(ProductAttribute attribute) {

		// override method, this allows the error that we try to remove a
		// detached instance
		attribute = this.getById(attribute.getId());
		//super.delete(attribute);

	}

}
