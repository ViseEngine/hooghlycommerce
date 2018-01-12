package co.hooghly.commerce.web.populator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;

import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.business.ConversionException;
import co.hooghly.commerce.business.PricingService;
import co.hooghly.commerce.business.utils.AbstractDataPopulator;
import co.hooghly.commerce.domain.Category;
import co.hooghly.commerce.domain.FinalPrice;
import co.hooghly.commerce.domain.Language;

import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.domain.ProductAvailability;
import co.hooghly.commerce.domain.ProductDescription;
import co.hooghly.commerce.domain.ProductImage;
import co.hooghly.commerce.util.ImageFilePath;
import co.hooghly.commerce.web.ui.ReadableImage;
//import co.hooghly.commerce.web.ui.ReadableManufacturer;
import co.hooghly.commerce.web.ui.ReadableProduct;



public class ReadableProductPopulator extends
		AbstractDataPopulator<Product, ReadableProduct> {
	
	private PricingService pricingService;
	
	private ImageFilePath imageUtils;

	public ImageFilePath getimageUtils() {
		return imageUtils;
	}




	public void setimageUtils(ImageFilePath imageUtils) {
		this.imageUtils = imageUtils;
	}




	public PricingService getPricingService() {
		return pricingService;
	}




	public void setPricingService(PricingService pricingService) {
		this.pricingService = pricingService;
	}




	@Override
	public ReadableProduct populate(Product source,
			ReadableProduct target, MerchantStore store, Language language)
			throws ConversionException {
		Validate.notNull(pricingService, "Requires to set PricingService");
		Validate.notNull(imageUtils, "Requires to set imageUtils");
		
		try {
			

			
	
			target.setId(source.getId());
			target.setAvailable(source.isAvailable());
			target.setProductHeight(source.getProductHeight());
			target.setProductLength(source.getProductLength());
			target.setProductWeight(source.getProductWeight());
			target.setProductWidth(source.getProductWidth());
			target.setPreOrder(source.isPreOrder());
			target.setRefSku(source.getRefSku());
			target.setSortOrder(source.getSortOrder());
			
			if(source.getProductReviewAvg()!=null) {
				double avg = source.getProductReviewAvg().doubleValue();
				double rating = Math.round(avg * 2) / 2.0f;
				target.setRating(rating);
			}
			target.setProductVirtual(source.isProductVirtual());
			if(source.getProductReviewCount()!=null) {
				target.setRatingCount(source.getProductReviewCount().intValue());
			}
			/*if(description!=null) {
				co.hooghly.commerce.web.ui.ProductDescription tragetDescription = new co.hooghly.commerce.web.ui.ProductDescription();
				tragetDescription.setFriendlyUrl(description.getSeUrl());
				tragetDescription.setName(description.getName());
				if(!StringUtils.isBlank(description.getMetatagTitle())) {
					tragetDescription.setTitle(description.getMetatagTitle());
				} else {
					tragetDescription.setTitle(description.getName());
				}
				tragetDescription.setMetaDescription(description.getMetatagDescription());
				tragetDescription.setDescription(description.getDescription());
				tragetDescription.setHighlights(description.getProductHighlight());
				target.setDescription(tragetDescription);
			}*/
			
			/*if(source.getManufacturer()!=null) {
				ManufacturerDescription manufacturer = source.getManufacturer().getDescriptions().iterator().next(); 
				ReadableManufacturer manufacturerEntity = new ReadableManufacturer();
				co.hooghly.commerce.web.ui.ManufacturerDescription d = new co.hooghly.commerce.web.ui.ManufacturerDescription(); 
				d.setName(manufacturer.getName());
				manufacturerEntity.setDescription(d);
				manufacturerEntity.setId(manufacturer.getId());
				manufacturerEntity.setOrder(source.getManufacturer().getOrder());
				manufacturerEntity.setCode(source.getManufacturer().getCode());
				target.setManufacturer(manufacturerEntity);
			}*/
			
			ProductImage image = source.getProductImage();
			if(image!=null) {
				ReadableImage rimg = new ReadableImage();
				//rimg.setImageName(image.getProductImage());
				
				String contextPath = imageUtils.getContextPath();
				StringBuilder imagePath = new StringBuilder();
				//imagePath.append(contextPath).append(imageUtils.buildProductImageUtils(store, source.getSku(), image.getProductImage()));

				rimg.setImageUrl(imagePath.toString());
				
	
				rimg.setId(image.getId());
				target.setImage(rimg);
				
				//other images
				Set<ProductImage> images = source.getImages();
				if(images!=null && images.size()>0) {
					List<ReadableImage> imageList = new ArrayList<ReadableImage>();
					for(ProductImage img : images) {
						ReadableImage prdImage = new ReadableImage();
						//prdImage.setImageName(img.getProductImage());

						StringBuilder imgPath = new StringBuilder();
						//imgPath.append(contextPath).append(imageUtils.buildProductImageUtils(store, source.getSku(), img.getProductImage()));

						prdImage.setImageUrl(imgPath.toString());
						prdImage.setId(img.getId());
						prdImage.setImageType(img.getImageType());
						if(img.getProductImageUrl()!=null){
							prdImage.setExternalUrl(img.getProductImageUrl());
						}
						if(img.getImageType()==1 && img.getProductImageUrl()!=null) {//video
							prdImage.setVideoUrl(img.getProductImageUrl());
						}
						imageList.add(prdImage);
					}
					target
					.setImages(imageList);
				}
			}
			
			//remove products from invisible category -> set visible = false
/*			Set<Category> categories = source.getCategories();
			boolean isVisible = true;
			if(!CollectionUtils.isEmpty(categories)) {
				for(Category c : categories) {
					if(c.isVisible()) {
						isVisible = true;
						break;
					} else {
						isVisible = false;
					}
				}
			}*/
			
			//target.setVisible(isVisible);
			
	
			target.setSku(source.getSku());
			//target.setLanguage(language.getCode());
	
			FinalPrice price = pricingService.calculateProductPrice(source);

			target.setFinalPrice(pricingService.getDisplayAmount(price.getFinalPrice(), store));
			target.setPrice(price.getFinalPrice());
	
			if(price.isDiscounted()) {
				target.setDiscounted(true);
				target.setOriginalPrice(pricingService.getDisplayAmount(price.getOriginalPrice(), store));
			}
			
			//availability
			for(ProductAvailability availability : source.getAvailabilities()) {
				if(availability.getRegion().equals(Constants.ALL_REGIONS)) {//TODO REL 2.1 accept a region
					target.setQuantity(availability.getProductQuantity());
					target.setQuantityOrderMaximum(availability.getProductQuantityOrderMax());
					target.setQuantityOrderMinimum(availability.getProductQuantityOrderMin());
					if(availability.getProductQuantity().intValue() > 0 && target.isAvailable()) {
							target.setCanBePurchased(true);
					}
				}
			}
			
			
			return target;
		
		} catch (Exception e) {
			throw new ConversionException(e);
		}
	}




	@Override
	protected ReadableProduct createTarget() {
		// TODO Auto-generated method stub
		return null;
	}

}
