package co.hooghly.commerce.business;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import co.hooghly.commerce.business.utils.CatalogServiceHelper;
import co.hooghly.commerce.business.utils.CoreConfiguration;
import co.hooghly.commerce.domain.Category;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.domain.ProductCriteria;

import co.hooghly.commerce.domain.ProductImage;
import co.hooghly.commerce.domain.ProductList;
import co.hooghly.commerce.domain.ProductRelationship;
import co.hooghly.commerce.domain.ProductReview;
import co.hooghly.commerce.domain.TaxClass;

import co.hooghly.commerce.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductService extends AbstractBaseBusinessDelegate<Product, Long> {

	ProductRepository productRepository;

	@Autowired
	CategoryService categoryService;

	@Autowired
	ProductAvailabilityService productAvailabilityService;

	@Autowired
	ProductPriceService productPriceService;

	@Autowired
	ProductOptionService productOptionService;

	@Autowired
	ProductOptionValueService productOptionValueService;

	@Autowired
	ProductAttributeService productAttributeService;

	@Autowired
	ProductRelationshipService productRelationshipService;

	@Autowired
	ProductImageService productImageService;

	@Autowired
	CoreConfiguration configuration;

	@Autowired
	ProductReviewService productReviewService;

	public ProductService(ProductRepository productRepository) {
		super(productRepository);
		this.productRepository = productRepository;
	}

	public List<Product> getProducts(List<Long> categoryIds) {

		Set<Long> ids = new HashSet<>(categoryIds);
		return productRepository.getProductsListByCategories(ids);

	}

	public List<Product> getProducts(List<Long> categoryIds, Language language) {

		Set<Long> ids = new HashSet<>(categoryIds);
		return productRepository.getProductsListByCategories(ids, language);
	}

	public Product getBySeUrl(MerchantStore store, String seUrl, Locale locale) {
		return productRepository.getByFriendlyUrl(store, seUrl, locale);
	}

	public Product getProductForLocale(long productId, Language language, Locale locale) {
		Product product = productRepository.getProductForLocale(productId, language, locale);

		CatalogServiceHelper.setToAvailability(product, locale);
		CatalogServiceHelper.setToLanguage(product, language.getId());
		return product;
	}

	public List<Product> getProductsForLocale(Category category, Language language, Locale locale) {
		Assert.notNull(category, "The category is null");

		// Get the category list
		StringBuilder lineage = new StringBuilder().append(category.getLineage()).append(category.getId()).append("/");
		List<Category> categories = categoryService.listByLineage(category.getMerchantStore(), lineage.toString());
		Set<Long> categoryIds = new HashSet<Long>();
		for (Category c : categories) {

			categoryIds.add(c.getId());

		}

		categoryIds.add(category.getId());

		// Get products
		List<Product> products = productRepository.getProductsForLocale(category.getMerchantStore(), categoryIds,
				language, locale);

		// Filter availability

		return products;
	}

	public ProductList listByStore(MerchantStore store, Language language, ProductCriteria criteria) {

		return productRepository.listByStore(store, language, criteria);
	}

	public List<Product> listByStore(MerchantStore store) {

		return productRepository.listByStore(store);
	}

	public List<Product> listByTaxClass(TaxClass taxClass) {
		return productRepository.listByTaxClass(taxClass);
	}

	public Product getByCode(String productCode, Language language) {
		return productRepository.getByCode(productCode, language);
	}

	public void delete(Product product) throws ServiceException {
		log.debug("Deleting product");
		Assert.notNull(product, "Product cannot be null");
		Assert.notNull(product.getMerchantStore(), "MerchantStore cannot be null in product");
		product = findOne(product.getId());// Prevents detached entity
												// error
		product.setCategories(null);

		Set<ProductImage> images = product.getImages();

		for (ProductImage image : images) {
			productImageService.removeProductImage(image);
		}

		product.setImages(null);

		// delete reviews
		List<ProductReview> reviews = productReviewService.getByProductNoCustomers(product);
		for (ProductReview review : reviews) {
			productReviewService.delete(review);
		}

		// related - featured
		List<ProductRelationship> relationships = productRelationshipService.listByProduct(product);
		for (ProductRelationship relationship : relationships) {
			productRelationshipService.delete(relationship);
		}

		// super.delete(product);

	}
	
	

}
