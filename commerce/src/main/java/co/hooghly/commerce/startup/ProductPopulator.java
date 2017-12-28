package co.hooghly.commerce.startup;


import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

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
import co.hooghly.commerce.domain.ProductDescription;
import co.hooghly.commerce.domain.ProductImage;
import co.hooghly.commerce.domain.ProductPrice;
import co.hooghly.commerce.domain.ProductPriceDescription;
import co.hooghly.commerce.domain.ProductRelationship;
import co.hooghly.commerce.domain.ProductRelationshipType;
import co.hooghly.commerce.domain.ProductType;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(12)
public class ProductPopulator extends AbstractDataPopulator {
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

		log.info("12.0 - Populating product type.");

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
		Manufacturer samsung = manufacturerService.getByCode(store, "samsung");
		Manufacturer lg = manufacturerService.getByCode(store, "lg");
		Manufacturer sony = manufacturerService.getByCode(store, "sony");
		Manufacturer nokia = manufacturerService.getByCode(store, "nokia");
		Manufacturer apple = manufacturerService.getByCode(store, "apple");

		Language en = languageService.getByCode("en");
		

		Category mobile = categoryService.getByCode(store, "mobile");
		Category accessories = categoryService.getByCode(store, "accessories");
		
		

		/*// PRODUCT 1

		Product product = new Product();
		product.setProductHeight(new BigDecimal(10));
		product.setProductLength(new BigDecimal(3));
		product.setProductWidth(new BigDecimal(6));
		product.setSku("TB12345");
		product.setManufacturer(apple);
		product.setType(generalType);
		product.setMerchantStore(store);
		product.setProductShipeable(true);

		// Availability
		ProductAvailability availability = new ProductAvailability();
		availability.setProductDateAvailable(date);
		availability.setProductQuantity(100);
		availability.setRegion("*");
		availability.setProduct(product);// associate with product

		ProductPrice dprice = new ProductPrice();
		dprice.setDefaultPrice(true);
		dprice.setProductPriceAmount(new BigDecimal(739.99));
		dprice.setProductAvailability(availability);

		ProductPriceDescription dpd = new ProductPriceDescription();
		dpd.setName("Base price");
		dpd.setProductPrice(dprice);
		dpd.setLanguage(en);

		dprice.getDescriptions().add(dpd);

		availability.getPrices().add(dprice);
		product.getAvailabilities().add(availability);

		// Product description
		ProductDescription description = new ProductDescription();
		description.setName("IPhone 6");
		description.setTitle("IPhone 6");
		description.setSubtitle("16GB/64GB");
		
		description.setLanguage(en);
		description.setSeUrl("/shop/product/iphone-6");
		description.setProduct(product);

		product.getDescriptions().add(description);

		product.getCategories().add(mobile);
		

		productService.create(product);

		try {
			ClassPathResource classPathResource = new ClassPathResource("/demo/ustora/h4-slide.png");
			InputStream inStream = classPathResource.getInputStream();
			this.saveFile(inStream, "spring.png", product);
		} catch (Exception e) {
			log.error("Error while reading demo file spring.png", e);
		}

		// PRODUCT 2

		Product product2 = new Product();
		product2.setProductHeight(new BigDecimal(4));
		product2.setProductLength(new BigDecimal(3));
		product2.setProductWidth(new BigDecimal(1));
		product2.setSku("TB2468");
		product2.setManufacturer(nokia);
		product2.setType(generalType);
		product2.setMerchantStore(store);
		product2.setProductShipeable(true);

		// Product description
		description = new ProductDescription();
		description.setName("/shop/product/Nokia 4");
		description.setTitle("Nokia 4");
		description.setSubtitle("Dual SIM");
		description.setLanguage(en);
		description.setProduct(product2);
		description.setSeUrl("nokia-4");

		product2.getDescriptions().add(description);

		product2.getCategories().add(mobile);
		

		// Availability
		ProductAvailability availability2 = new ProductAvailability();
		availability2.setProductDateAvailable(date);
		availability2.setProductQuantity(100);
		availability2.setRegion("*");
		availability2.setProduct(product2);// associate with product

		ProductPrice dprice2 = new ProductPrice();
		dprice2.setDefaultPrice(true);
		dprice2.setProductPriceAmount(new BigDecimal(179.99));
		dprice2.setProductAvailability(availability2);

		dpd = new ProductPriceDescription();
		dpd.setName("Base price");
		dpd.setProductPrice(dprice2);
		dpd.setLanguage(en);

		dprice2.getDescriptions().add(dpd);

		availability2.getPrices().add(dprice2);
		product2.getAvailabilities().add(availability2);

		productService.create(product2);

		try {
			ClassPathResource classPathResource = new ClassPathResource("/demo/ustora/h4-slide5.png");
			InputStream inStream = classPathResource.getInputStream();
			this.saveFile(inStream, "node.jpg", product2);
		} catch (Exception e) {
			log.error("Error while reading demo file node.jpg", e);
		}

		// PRODUCT 3

		Product product3 = new Product();
		product3.setProductHeight(new BigDecimal(4));
		product3.setProductLength(new BigDecimal(3));
		product3.setProductWidth(new BigDecimal(1));
		product3.setSku("NB1111");
		product3.setManufacturer(lg);
		product3.setType(generalType);
		product3.setMerchantStore(store);
		product3.setProductShipeable(true);

		// Product description
		description = new ProductDescription();
		description.setName("Programming for PAAS");
		description.setLanguage(en);
		description.setProduct(product3);
		description.setSeUrl("programming-for-paas");

		product3.getDescriptions().add(description);

		product3.getCategories().add(mobile);

		// Availability
		ProductAvailability availability3 = new ProductAvailability();
		availability3.setProductDateAvailable(date);
		availability3.setProductQuantity(100);
		availability3.setRegion("*");
		availability3.setProduct(product3);// associate with product

		ProductPrice dprice3 = new ProductPrice();
		dprice3.setDefaultPrice(true);
		dprice3.setProductPriceAmount(new BigDecimal(19.99));
		dprice3.setProductAvailability(availability3);

		dpd = new ProductPriceDescription();
		dpd.setName("Base price");
		dpd.setProductPrice(dprice3);
		dpd.setLanguage(en);

		dprice3.getDescriptions().add(dpd);

		availability3.getPrices().add(dprice3);
		product3.getAvailabilities().add(availability3);

		productService.create(product3);

		try {
			ClassPathResource classPathResource = new ClassPathResource("/demo/ustora/h4-slide3.png");
			InputStream inStream = classPathResource.getInputStream();
			this.saveFile(inStream, "paas.JPG", product3);
		} catch (Exception e) {
			log.error("Error while reading demo file paas.jpg", e);
		}

		// PRODUCT 4
		Product product4 = new Product();
		product4.setProductHeight(new BigDecimal(4));
		product4.setProductLength(new BigDecimal(3));
		product4.setProductWidth(new BigDecimal(1));
		product4.setSku("SF333345");
		product4.setManufacturer(sony);
		product4.setType(generalType);
		product4.setMerchantStore(store);
		product4.setProductShipeable(true);

		// Product description
		description = new ProductDescription();
		description.setName("IPhone 6");
		description.setTitle("IPhone 6");
		description.setSubtitle("16GB/32GB");
		description.setLanguage(en);
		description.setProduct(product4);
		description.setSeUrl("/shop/product/iphone-6");

		product4.getDescriptions().add(description);

		product4.getCategories().add(mobile);

		// Availability
		ProductAvailability availability4 = new ProductAvailability();
		availability4.setProductDateAvailable(date);
		availability4.setProductQuantity(100);
		availability4.setRegion("*");
		availability4.setProduct(product4);// associate with product

		ProductPrice dprice4 = new ProductPrice();
		dprice4.setDefaultPrice(true);
		dprice4.setProductPriceAmount(new BigDecimal(18.99));
		dprice4.setProductAvailability(availability4);

		dpd = new ProductPriceDescription();
		dpd.setName("Base price");
		dpd.setProductPrice(dprice4);
		dpd.setLanguage(en);

		dprice4.getDescriptions().add(dpd);

		availability4.getPrices().add(dprice4);
		product4.getAvailabilities().add(availability4);

		productService.create(product4);

		try {
			ClassPathResource classPathResource = new ClassPathResource("/demo/ustora/h4-slide.png");
			InputStream inStream = classPathResource.getInputStream();
			this.saveFile(inStream, "android.jpg", product4);
		} catch (Exception e) {
			log.error("Error while reading demo file android.jpg", e);
		}

		// PRODUCT 5
		Product product5 = new Product();
		product5.setProductHeight(new BigDecimal(4));
		product5.setProductLength(new BigDecimal(3));
		product5.setProductWidth(new BigDecimal(1));
		product5.setSku("SF333346");
		product5.setManufacturer(apple);
		product5.setType(generalType);
		product5.setMerchantStore(store);
		product5.setProductShipeable(true);

		// Product description
		description = new ProductDescription();
		description.setName("HeadPhones & Music Player");
		description.setTitle("HeadPhones & Music Player");
		description.setSubtitle("");
		
		description.setLanguage(en);
		description.setProduct(product5);
		description.setSeUrl("/shop/product/headphones-music-player");

		product5.getDescriptions().add(description);

		product5.getCategories().add(mobile);

		// Availability
		ProductAvailability availability5 = new ProductAvailability();
		availability5.setProductDateAvailable(date);
		availability5.setProductQuantity(100);
		availability5.setRegion("*");
		availability5.setProduct(product5);// associate with product

		// productAvailabilityService.create(availability5);

		ProductPrice dprice5 = new ProductPrice();
		dprice5.setDefaultPrice(true);
		dprice5.setProductPriceAmount(new BigDecimal(769));
		dprice5.setProductAvailability(availability5);

		dpd = new ProductPriceDescription();
		dpd.setName("Base price");
		dpd.setProductPrice(dprice5);
		dpd.setLanguage(en);

		dprice5.getDescriptions().add(dpd);

		availability5.getPrices().add(dprice5);
		product5.getAvailabilities().add(availability5);

		productService.create(product5);

		try {
			ClassPathResource classPathResource = new ClassPathResource("/demo/ustora/h4-slide4.png");
			InputStream inStream = classPathResource.getInputStream();
			this.saveFile(inStream, "iphone-7-plus", product5);
		} catch (Exception e) {
			log.error("Error while reading demo file android2.jpg", e);
		}

		// PRODUCT 6

		Product product6 = new Product();
		product6.setProductHeight(new BigDecimal(4));
		product6.setProductLength(new BigDecimal(3));
		product6.setProductWidth(new BigDecimal(1));
		product6.setSku("LL333444");
		product6.setManufacturer(samsung);
		product6.setType(generalType);
		product6.setMerchantStore(store);
		product6.setProductShipeable(true);

		// Product description
		description = new ProductDescription();
		description.setName("Samsung Galaxy J3 Emerge");
		description.setTitle("Samsung Galaxy J3 Emerge");
		description.setSubtitle("4 Cameras");
		description.setLanguage(en);
		description.setProduct(product6);
		description.setSeUrl("/shop/product/samsung-galaxy-j3-emerge");

		product6.getDescriptions().add(description);

		product6.getCategories().add(accessories);

		// Availability
		ProductAvailability availability6 = new ProductAvailability();
		availability6.setProductDateAvailable(date);
		availability6.setProductQuantity(100);
		availability6.setRegion("*");
		availability6.setProduct(product6);// associate with product

		// productAvailabilityService.create(availability6);

		ProductPrice dprice6 = new ProductPrice();
		dprice6.setDefaultPrice(true);
		dprice6.setProductPriceAmount(new BigDecimal(59.99));
		dprice6.setProductAvailability(availability6);

		dpd = new ProductPriceDescription();
		dpd.setName("Base price");
		dpd.setProductPrice(dprice6);
		dpd.setLanguage(en);

		dprice6.getDescriptions().add(dpd);

		availability6.getPrices().add(dprice6);
		product6.getAvailabilities().add(availability6);

		productService.create(product6);

		try {

			ClassPathResource classPathResource = new ClassPathResource("/demo/ustora/slide-01.jpg");
			InputStream inStream = classPathResource.getInputStream();
			this.saveFile(inStream, "samsung-galaxy-j3-emerge", product6);
		} catch (Exception e) {
			log.error("Error while reading demo file google.jpg", e);
		}

		// featured items

		ProductRelationship relationship = new ProductRelationship();
		relationship.setActive(true);
		relationship.setCode(ProductRelationshipType.FEATURED_ITEM.name());
		relationship.setStore(store);
		relationship.setRelatedProduct(product);

		productRelationshipService.saveOrUpdate(relationship);

		relationship = new ProductRelationship();
		relationship.setActive(true);
		relationship.setCode(ProductRelationshipType.FEATURED_ITEM.name());
		relationship.setStore(store);
		relationship.setRelatedProduct(product6);

		productRelationshipService.saveOrUpdate(relationship);

		relationship = new ProductRelationship();
		relationship.setActive(true);
		relationship.setCode(ProductRelationshipType.FEATURED_ITEM.name());
		relationship.setStore(store);
		relationship.setRelatedProduct(product5);

		productRelationshipService.saveOrUpdate(relationship);

		relationship = new ProductRelationship();
		relationship.setActive(true);
		relationship.setCode(ProductRelationshipType.FEATURED_ITEM.name());
		relationship.setStore(store);
		relationship.setRelatedProduct(product2);

		productRelationshipService.saveOrUpdate(relationship);
		
		//related product
		
		relationship = new ProductRelationship();
		relationship.setActive(true);
		relationship.setCode(ProductRelationshipType.RELATED_ITEM.name());
		relationship.setStore(store);
		relationship.setProduct(product6);
		relationship.setRelatedProduct(product2);

		productRelationshipService.saveOrUpdate(relationship);
		
		relationship = new ProductRelationship();
		relationship.setActive(true);
		relationship.setCode(ProductRelationshipType.RELATED_ITEM.name());
		relationship.setStore(store);
		relationship.setProduct(product6);
		relationship.setRelatedProduct(product3);

		productRelationshipService.saveOrUpdate(relationship);
		
		relationship = new ProductRelationship();
		relationship.setActive(true);
		relationship.setCode(ProductRelationshipType.RELATED_ITEM.name());
		relationship.setStore(store);
		relationship.setProduct(product6);
		relationship.setRelatedProduct(product);

		productRelationshipService.saveOrUpdate(relationship);
*/

	}

	private void saveFile(InputStream fis, String name, Product product) throws Exception {

		if (fis == null) {
			return;
		}

		final byte[] is = IOUtils.toByteArray(fis);
		

		ProductImage productImage = new ProductImage();
		productImage.setProductImage(name);
		productImage.setProduct(product);
		productImage.setImage(is);

		//productImageService.addProductImage(null, productImage, null);

	}

}
