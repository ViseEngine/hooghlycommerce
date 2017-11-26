package co.hooghly.commerce.business;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.ProductAttribute;
import co.hooghly.commerce.domain.ProductOption;
import co.hooghly.commerce.repository.ProductOptionRepository;

@Service
public class ProductOptionService extends SalesManagerEntityServiceImpl<Long, ProductOption> {

	private ProductOptionRepository productOptionRepository;

	@Autowired
	private ProductAttributeService productAttributeService;

	public ProductOptionService(ProductOptionRepository productOptionRepository) {
		super(productOptionRepository);
		this.productOptionRepository = productOptionRepository;
	}

	public List<ProductOption> listByStore(MerchantStore store, Language language) throws ServiceException {

		return productOptionRepository.findByStoreId(store.getId(), language.getId());

	}

	public List<ProductOption> listReadOnly(MerchantStore store, Language language) throws ServiceException {

		return productOptionRepository.findByReadOnly(store.getId(), language.getId(), true);

	}

	public List<ProductOption> getByName(MerchantStore store, String name, Language language) throws ServiceException {

		try {
			return productOptionRepository.findByName(store.getId(), name, language.getId());
		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}

	public void saveOrUpdate(ProductOption entity) throws ServiceException {

		// save or update (persist and attach entities
		if (entity.getId() != null && entity.getId() > 0) {
			super.update(entity);
		} else {
			super.save(entity);
		}

	}

	public void delete(ProductOption entity) throws ServiceException {

		// remove all attributes having this option
		List<ProductAttribute> attributes = productAttributeService.getByOptionId(entity.getMerchantStore(),
				entity.getId());

		for (ProductAttribute attribute : attributes) {
			productAttributeService.delete(attribute);
		}

		ProductOption option = this.getById(entity.getId());

		// remove option
		super.delete(option);

	}

	public ProductOption getByCode(MerchantStore store, String optionCode) {
		return productOptionRepository.findByCode(store.getId(), optionCode);
	}

	public ProductOption getById(MerchantStore store, Long optionId) {
		return productOptionRepository.findOne(store.getId(), optionId);
	}

}
