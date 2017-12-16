package co.hooghly.commerce.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.hooghly.commerce.domain.Category;

import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;

import co.hooghly.commerce.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CategoryService extends AbstractBaseBusinessDelegate<Category, Long> {

	private CategoryRepository categoryRepository;

	@Autowired
	public CategoryService(CategoryRepository categoryRepository) {
		super(categoryRepository);
		this.categoryRepository = categoryRepository;
	}

	public List<Category> findByStoreAndParent(MerchantStore store, Category parent) {
		return categoryRepository.findByMerchantStoreAndParent(store, parent);
	}

	@Transactional
	public void create(Category category) {
		String lineage = "";
		Category parent = category.getParent();
		
		if (parent != null) {
			log.info("parent depth - {}", parent.getLineage());
			lineage = parent.getLineage() + "/" + parent.getId();
			category.setDepth(parent.getDepth() + 1);
		} else {
			lineage = "/";
			category.setDepth(0);
		}
		category.setLineage(lineage);
		
		
		//validate store
		if(parent !=null && parent.getMerchantStore().getId().intValue()!=category.getMerchantStore().getId().intValue()) {
			throw new RuntimeException("Store id does not belong to specified parent id");
		}
		
		save(category);

	}

	@Transactional(readOnly = true)
	public List<Object[]> countProductsByCategories(MerchantStore store, List<Long> categoryIds) {
		return categoryRepository.countProductsByCategories(store, categoryIds);
	}

	@Transactional(readOnly = true)
	public List<Category> listByCodes(MerchantStore store, List<String> codes, Language language) {
		return categoryRepository.findByMerchantStoreIdAndCodeInOrderBySortOrderAsc(store.getId(), codes);
	}

	@Transactional(readOnly = true)
	public List<Category> listByIds(MerchantStore store, List<Long> ids, Language language) {
		return categoryRepository.findByMerchantStoreIdAndIdInOrderBySortOrderAsc(store.getId(), ids);
	}

	@Transactional(readOnly = true)
	public Category getByLanguage(long categoryId, Language language) {
		return categoryRepository.findOne(categoryId);
	}

	@Transactional(readOnly = true)
	public List<Category> listByLineage(MerchantStore store, String lineage) {
		return categoryRepository.findByMerchantStoreIdAndLineageLikeOrderByLineageAscSortOrderAsc(store.getId(), lineage);
	}

	@Transactional(readOnly = true)
	public List<Category> listByLineage(String storeCode, String lineage) {
		return categoryRepository.findByMerchantStoreCodeAndLineageLikeOrderByLineageAscSortOrderAsc(storeCode, lineage);
	}

	@Transactional(readOnly = true)
	public List<Category> listBySeUrl(MerchantStore store, String seUrl) {
		return categoryRepository.findByMerchantStoreIdAndSeUrlLikeOrderBySortOrderAsc(store.getId(), seUrl);
	}

	@Transactional(readOnly = true)
	public Category getBySeUrl(MerchantStore store, String seUrl) {
		return categoryRepository.findByMerchantStoreIdAndSeUrl(store.getId(), seUrl);
	}

	@Transactional(readOnly = true)
	public Category getByCode(MerchantStore store, String code) {
		return categoryRepository.findByMerchantStoreIdAndCode(store.getId(), code);
	}

	@Transactional(readOnly = true)
	public Category getByCode(String storeCode, String code) {
		return categoryRepository.findByMerchantStoreCodeAndCode(storeCode, code);
	}

	@Transactional(readOnly = true)
	public List<Category> listByParent(Category category) {

		return categoryRepository.listByStoreAndParent(null, category);

	}

	public List<Category> listByStoreAndParent(MerchantStore store, Category category) {

		return categoryRepository.listByStoreAndParent(store, category);

	}

	public List<Category> listByParent(Category category, Language language) {
		return categoryRepository.findByParentOrderByLineageAscSortOrderAsc(category);
	}
	
	

	//
	public void delete(Category category) {
		categoryRepository.delete(category);
	}
	
	

	public void addChild(Category parent, Category child) throws ServiceException {

		if (child == null || child.getMerchantStore() == null) {
			throw new ServiceException("Child category and merchant store should not be null");
		}

		try {

			if (parent == null) {

				// assign to root
				child.setParent(null);
				child.setDepth(0);
				// child.setLineage(new
				// StringBuilder().append("/").append(child.getId()).append("/").toString());
				child.setLineage("/");

			} else {

				Category p = findOne(parent.getId());// parent

				String lineage = p.getLineage();
				int depth = p.getDepth();// TODO sometimes null

				child.setParent(p);
				child.setDepth(depth + 1);
				child.setLineage(new StringBuilder().append(lineage).append(p.getId()).append("/").toString());

			}

			save(child);
			StringBuilder childLineage = new StringBuilder();
			childLineage.append(child.getLineage()).append(child.getId()).append("/");
			List<Category> subCategories = listByLineage(child.getMerchantStore(), childLineage.toString());

			// ajust all sub categories lineages
			if (subCategories != null && subCategories.size() > 0) {
				for (Category subCategory : subCategories) {
					if (child.getId() != subCategory.getId()) {
						addChild(child, subCategory);
					}
				}

			}
		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}

	public List<Category> listByDepth(MerchantStore store, int depth) {
		return categoryRepository.findByMerchantStoreIdAndDepthGreaterThanOrderByLineageAscSortOrderAsc(store.getId(), depth);
	}

	//@Cacheable(cacheNames = "category-cache-store-depth-lang", key="#store.id + '.' + depth + '.' + #language.id")
	@Transactional(readOnly = true)
	public List<Category> findByDepth(MerchantStore store, int depth, Language language) {
		return categoryRepository.findByMerchantStoreIdAndDepthOrderByLineageAscSortOrderAsc(store.getId(), depth);
	}

	public List<Category> getByName(MerchantStore store, String name, Language language) {

		return categoryRepository.findByMerchantStoreIdAndNameLikeOrderBySortOrderAsc(store.getId(), name);

	}

	public List<Category> listByStore(MerchantStore store) {

		return categoryRepository.findByMerchantStoreOrderByLineageAscSortOrderAsc(store);

	}

	public List<Category> listByStore(MerchantStore store, Language language) {

		return categoryRepository.findByMerchantStoreOrderByLineageAscSortOrderAsc(store);

	}

}
