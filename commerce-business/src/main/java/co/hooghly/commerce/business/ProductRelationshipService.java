package co.hooghly.commerce.business;

import java.util.List;


import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.domain.ProductRelationship;
import co.hooghly.commerce.domain.ProductRelationshipType;
import co.hooghly.commerce.repository.ProductRelationshipRepository;

@Service
public class ProductRelationshipService extends SalesManagerEntityServiceImpl<Long, ProductRelationship> {

	private ProductRelationshipRepository productRelationshipRepository;

	public ProductRelationshipService(ProductRelationshipRepository productRelationshipRepository) {
		super(productRelationshipRepository);
		this.productRelationshipRepository = productRelationshipRepository;
	}

	public void saveOrUpdate(ProductRelationship relationship) throws ServiceException {

		if (relationship.getId() != null && relationship.getId() > 0) {

			this.update(relationship);

		} else {
			this.create(relationship);
		}

	}

	public void addGroup(MerchantStore store, String groupName) throws ServiceException {
		ProductRelationship relationship = new ProductRelationship();
		relationship.setCode(groupName);
		relationship.setStore(store);
		relationship.setActive(true);
		this.save(relationship);
	}

	public List<ProductRelationship> getGroups(MerchantStore store) {
		return productRelationshipRepository.getGroups(store);
	}

	public void deleteGroup(MerchantStore store, String groupName) throws ServiceException {
		List<ProductRelationship> entities = productRelationshipRepository.getByGroup(store, groupName);
		for (ProductRelationship relation : entities) {
			this.delete(relation);
		}
	}

	public void deactivateGroup(MerchantStore store, String groupName) throws ServiceException {
		List<ProductRelationship> entities = productRelationshipRepository.getByGroup(store, groupName);
		for (ProductRelationship relation : entities) {
			relation.setActive(false);
			this.saveOrUpdate(relation);
		}
	}

	public void activateGroup(MerchantStore store, String groupName) throws ServiceException {
		List<ProductRelationship> entities = this.getByGroup(store, groupName);
		for (ProductRelationship relation : entities) {
			relation.setActive(true);
			this.saveOrUpdate(relation);
		}
	}

	public void delete(ProductRelationship relationship) throws ServiceException {

		// throws detached exception so need to query first
		relationship = this.getById(relationship.getId());
		super.delete(relationship);

	}

	public List<ProductRelationship> listByProduct(Product product) throws ServiceException {

		return productRelationshipRepository.listByProducts(product);

	}

	public List<ProductRelationship> getByType(MerchantStore store, Product product, ProductRelationshipType type,
			Language language) throws ServiceException {

		return productRelationshipRepository.getByType(store, type.name(), product, language);

	}

	public List<ProductRelationship> getByType(MerchantStore store, ProductRelationshipType type, Language language)
			throws ServiceException {
		return productRelationshipRepository.getByType(store, type.name(), language);
	}

	public List<ProductRelationship> getByType(MerchantStore store, ProductRelationshipType type)
			throws ServiceException {

		return productRelationshipRepository.getByType(store, type.name());

	}

	public List<ProductRelationship> getByGroup(MerchantStore store, String groupName) throws ServiceException {

		return productRelationshipRepository.getByType(store, groupName);

	}

	public List<ProductRelationship> getByGroup(MerchantStore store, String groupName, Language language)
			throws ServiceException {

		return productRelationshipRepository.getByType(store, groupName, language);

	}

	public List<ProductRelationship> getByType(MerchantStore store, Product product, ProductRelationshipType type)
			throws ServiceException {

		return productRelationshipRepository.getByType(store, type.name(), product);

	}

}
