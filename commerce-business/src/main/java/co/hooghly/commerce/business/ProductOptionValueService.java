package co.hooghly.commerce.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.ProductAttribute;
import co.hooghly.commerce.domain.ProductOptionValue;
import co.hooghly.commerce.repository.ProductOptionValueRepository;

@Service
public class ProductOptionValueService extends SalesManagerEntityServiceImpl<Long, ProductOptionValue> {

	@Autowired
	private ProductAttributeService productAttributeService;

	private ProductOptionValueRepository productOptionValueRepository;

	
	public ProductOptionValueService(ProductOptionValueRepository productOptionValueRepository) {
		super(productOptionValueRepository);
		this.productOptionValueRepository = productOptionValueRepository;
	}

	public List<ProductOptionValue> listByStore(MerchantStore store, Language language) throws ServiceException {

		return productOptionValueRepository.findByStoreId(store.getId(), language.getId());
	}

	public List<ProductOptionValue> listByStoreNoReadOnly(MerchantStore store, Language language)
			throws ServiceException {

		return productOptionValueRepository.findByReadOnly(store.getId(), language.getId(), false);
	}

	public List<ProductOptionValue> getByName(MerchantStore store, String name, Language language)
			throws ServiceException {

		try {
			return productOptionValueRepository.findByName(store.getId(), name, language.getId());
		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}

	public void saveOrUpdate(ProductOptionValue entity) throws ServiceException {

		// save or update (persist and attach entities
		if (entity.getId() != null && entity.getId() > 0) {

			super.update(entity);

		} else {

			super.save(entity);

		}

	}

	public void delete(ProductOptionValue entity) throws ServiceException {

		// remove all attributes having this option
		List<ProductAttribute> attributes = productAttributeService.getByOptionValueId(entity.getMerchantStore(),
				entity.getId());

		for (ProductAttribute attribute : attributes) {
			productAttributeService.delete(attribute);
		}

		ProductOptionValue option = getById(entity.getId());

		// remove option
		super.delete(option);

	}

	public ProductOptionValue getByCode(MerchantStore store, String optionValueCode) {
		return productOptionValueRepository.findByCode(store.getId(), optionValueCode);
	}

	public ProductOptionValue getById(MerchantStore store, Long optionValueId) {
		return productOptionValueRepository.findOne(store.getId(), optionValueId);
	}

}
