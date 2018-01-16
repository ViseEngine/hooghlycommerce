package co.hooghly.commerce.startup;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import co.hooghly.commerce.business.CategoryService;
import co.hooghly.commerce.business.LanguageService;
import co.hooghly.commerce.business.ManufacturerService;
import co.hooghly.commerce.business.MerchantStoreService;
import co.hooghly.commerce.business.ProductImageService;
import co.hooghly.commerce.business.ProductRelationshipService;
import co.hooghly.commerce.business.ProductService;
import co.hooghly.commerce.business.ProductTypeService;
import co.hooghly.commerce.domain.Category;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.Manufacturer;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.domain.ProductAvailability;

import co.hooghly.commerce.domain.ProductImage;
import co.hooghly.commerce.domain.ProductPrice;
import co.hooghly.commerce.domain.ProductPriceDescription;
import co.hooghly.commerce.domain.ProductRelationship;
import co.hooghly.commerce.domain.ProductRelationshipType;
import co.hooghly.commerce.domain.ProductType;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(11)
public class ProductPopulator extends AbstractDataPopulator {

	@Value("classpath:demo-data/product*.yml")
	private Resource[] resources;

	public ProductPopulator() {
		super("PRODUCT");
	}

	@Autowired
	protected ManufacturerService manufacturerService;

	@Autowired
	protected MerchantStoreService merchantService;

	@Autowired
	protected ProductTypeService productTypeService;

	@Autowired
	protected LanguageService languageService;

	@Autowired
	protected CategoryService categoryService;

	@Autowired
	protected ProductService productService;

	@Autowired
	private ProductRelationshipService productRelationshipService;

	@Autowired
	protected ProductImageService productImageService;

	private void createProductType() {

		log.info("11.0 - Populating product type.");

		ProductType productType = new ProductType();
		productType.setCode(ProductType.GENERAL_TYPE);
		productTypeService.save(productType);

	}

	@Override
	public void runInternal(String... args) throws Exception {
		createProductType();

		log.info("12.1 - Populating products");
		Date date = new Date(System.currentTimeMillis());
		// Add products
		// ProductType generalType = productTypeService.

		// create a merchant
		MerchantStore store = merchantService.getMerchantStore(MerchantStore.DEFAULT_STORE);
		ProductType generalType = productTypeService.getProductType(ProductType.GENERAL_TYPE);

		Language en = languageService.getByCode("en");

		for (Resource r : resources) {

			Yaml yaml = new Yaml();
			Map data = (Map) yaml.load(r.getInputStream());

			log.info("data - {}", data);

			List<Map<String, ?>> products = (List<Map<String, ?>>) data.get("products");

			for (Map<String, ?> items : products) {

				Product product = new Product();
				product.setProductHeight(new BigDecimal((Double) items.get("height")));
				product.setProductLength(new BigDecimal((Double) items.get("length")));
				product.setProductWidth(new BigDecimal((Double) items.get("width")));
				product.setSku((String) items.get("sku"));
				Manufacturer manufacturer = manufacturerService.getByCode(store, (String) items.get("karbon"));
				product.setManufacturer(manufacturer);
				product.setType(generalType);
				product.setMerchantStore(store);
				product.setProductShipeable(true);

				// Availability
				ProductAvailability availability = new ProductAvailability();
				availability.setProductDateAvailable(date);
				availability.setProductQuantity((Integer) (items.get("qty")));
				availability.setRegion("*");
				availability.setProduct(product);// associate with product

				ProductPrice dprice = new ProductPrice();
				dprice.setDefaultPrice(true);
				dprice.setProductPriceAmount(new BigDecimal((Double) (items.get("price"))));
				
				dprice.setProductAvailability(availability);

				ProductPriceDescription dpd = new ProductPriceDescription();
				dpd.setName((String) items.get("price_desc"));
				dpd.setProductPrice(dprice);
				dpd.setLanguage(en);

				dprice.getDescriptions().add(dpd);

				availability.getPrices().add(dprice);

				product.getAvailabilities().add(availability);

				// Product description
				product.setName((String) items.get("product_name"));
				product.setTitle((String) items.get("title"));
				product.setSubtitle((String) items.get("subtitle"));

				
				product.setSeUrl((String) items.get("se-url"));
				

				

				Category category = categoryService.getByCode(store, (String) items.get("category"));

				product.getCategories().add(category);

				productService.save(product);

				// ASSOCIATE PRODUCT IMAGE to CMS content

				// featured items

				ProductRelationship relationship = new ProductRelationship();
				relationship.setActive(true);
				relationship.setCode(ProductRelationshipType.FEATURED_ITEM.name());
				relationship.setStore(store);
				relationship.setProduct(product);

				productRelationshipService.saveOrUpdate(relationship);
				
				
				List<String> images = (List<String>) items.get("images");
				
				boolean defaultImage = true;				
				for(String img : images) {
				
					ProductImage productImage = new ProductImage();
					productImage.setName(img);
					productImage.setProductImageUrl(img);
					productImage.setProduct(product);
					
					if(defaultImage){
						defaultImage = false;
					}
					else{
						productImage.setDefaultImage(defaultImage);
					}
					
					productImageService.save(productImage);
				}
			}
		}

		/*
		 * 
		 * 
		 * 
		 * //related product
		 * 
		 * relationship = new ProductRelationship();
		 * relationship.setActive(true);
		 * relationship.setCode(ProductRelationshipType.RELATED_ITEM.name());
		 * relationship.setStore(store); relationship.setProduct(product6);
		 * relationship.setRelatedProduct(product2);
		 * 
		 * productRelationshipService.saveOrUpdate(relationship);
		 * 
		 * relationship = new ProductRelationship();
		 * relationship.setActive(true);
		 * relationship.setCode(ProductRelationshipType.RELATED_ITEM.name());
		 * relationship.setStore(store); relationship.setProduct(product6);
		 * relationship.setRelatedProduct(product3);
		 * 
		 * productRelationshipService.saveOrUpdate(relationship);
		 * 
		 * relationship = new ProductRelationship();
		 * relationship.setActive(true);
		 * relationship.setCode(ProductRelationshipType.RELATED_ITEM.name());
		 * relationship.setStore(store); relationship.setProduct(product6);
		 * relationship.setRelatedProduct(product);
		 * 
		 * productRelationshipService.saveOrUpdate(relationship);
		 */

	}

	private void saveFile(InputStream fis, String name, Product product) {
		/*
		 * if (fis == null) { return; }
		 * 
		 * final byte[] is = IOUtils.toByteArray(fis);
		 * 
		 * 
		 * ProductImage productImage = new ProductImage();
		 * productImage.setProductImage(name); productImage.setProduct(product);
		 * productImage.setImage(is);
		 */

		// productImageService.addProductImage(null, productImage, null);

	}

}
