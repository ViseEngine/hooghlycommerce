package co.hooghly.commerce.business;

import java.util.ArrayList;
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
import co.hooghly.commerce.domain.ProductDescription;
import co.hooghly.commerce.domain.ProductImage;
import co.hooghly.commerce.domain.ProductList;
import co.hooghly.commerce.domain.ProductRelationship;
import co.hooghly.commerce.domain.ProductReview;
import co.hooghly.commerce.domain.TaxClass;

import co.hooghly.commerce.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductService extends AbstractBaseBusinessDelegate<Product, Long>  {

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

	
	public void addProductDescription(Product product, ProductDescription description) throws ServiceException {

		if (product.getDescriptions() == null) {
			product.setDescriptions(new HashSet<ProductDescription>());
		}

		product.getDescriptions().add(description);
		description.setProduct(product);
		update(product);

	}

	
	public List<Product> getProducts(List<Long> categoryIds) throws ServiceException {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		Set ids = new HashSet(categoryIds);
		return productRepository.getProductsListByCategories(ids);

	}

	public Product getById(Long productId) {
		return productRepository.getById(productId);
	}

	
	public List<Product> getProducts(List<Long> categoryIds, Language language) throws ServiceException {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		Set<Long> ids = new HashSet(categoryIds);
		return productRepository.getProductsListByCategories(ids, language);
	}

	
	public ProductDescription getProductDescription(Product product, Language language) {
		for (ProductDescription description : product.getDescriptions()) {
			if (description.getLanguage().equals(language)) {
				return description;
			}
		}
		return null;
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
		product = this.getById(product.getId());// Prevents detached entity
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

		//super.delete(product);
		
		

	}

	
	public void create(Product product) {
		this.saveOrUpdate(product);
		
	}

	
	public void update(Product product) throws ServiceException {
		this.saveOrUpdate(product);
		
	}

	private void saveOrUpdate(Product product) throws ServiceException {
		log.debug("Save or update product ");
		Assert.notNull(product, "product cannot be null");
		Assert.notNull(product.getAvailabilities(), "product must have at least one availability");
		Assert.notEmpty(product.getAvailabilities(), "product must have at least one availability");

		// List of original images
		Set<ProductImage> originalProductImages = null;

		if (product.getId() != null && product.getId() > 0) {
			originalProductImages = product.getImages();
		}

		/** save product first **/
		super.save(product);

		/**
		 * Image creation needs extra service to save the file in the CMS, 
		 */
		//TODO - Handle this with event
		List<Long> newImageIds = new ArrayList<Long>();
		Set<ProductImage> images = product.getImages();

		try {

			if (images != null && images.size() > 0) {
				for (ProductImage image : images) {
					if (image.getImage() != null && (image.getId() == null || image.getId() == 0L)) {
						image.setProduct(product);

						//InputStream inputStream = image.getImage();
						//InputStream inputStream = null;
						//ImageContentFile cmsContentImage = new ImageContentFile();
						//cmsContentImage.setFileName(image.getProductImage());
						//cmsContentImage.setFile(inputStream);
						//cmsContentImage.setFileContentType(FileContentType.PRODUCT);

						productImageService.addProductImage(product, image);
						newImageIds.add(image.getId());
					} else {
						productImageService.save(image);
						newImageIds.add(image.getId());
					}
				}
			}

			// cleanup old images
			if (originalProductImages != null) {
				for (ProductImage image : originalProductImages) {
					if (!newImageIds.contains(image.getId())) {
						//productImageService.delete(image);
					}
				}
			}

		} catch (Exception e) {
			log.error("Cannot save images " + e.getMessage());
		}

	}

}
