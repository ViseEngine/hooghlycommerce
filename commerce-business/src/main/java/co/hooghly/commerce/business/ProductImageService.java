package co.hooghly.commerce.business;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;


import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.domain.ProductImage;
import co.hooghly.commerce.domain.ProductImageDescription;
import co.hooghly.commerce.domain.ProductImageSize;
import co.hooghly.commerce.repository.ProductImageRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductImageService extends AbstractBaseBusinessDelegate<ProductImage, Long>
		 {

	private ProductImageRepository productImageRepository;
	
	
	public ProductImageService(ProductImageRepository productImageRepository) {
		super(productImageRepository);
		this.productImageRepository = productImageRepository;
	}

	public ProductImage getById(Long id) {
		return productImageRepository.findOne(id);
	}

	
	public void addProductImages(Product product, List<ProductImage> productImages) throws ServiceException {

		try {
			for (ProductImage productImage : productImages) {

				Assert.notNull(productImage.getImage(), "Product Image cannot be null.");

				//InputStream inputStream = productImage.getImage();
				InputStream inputStream = null;
				//ImageContentFile cmsContentImage = new ImageContentFile();
				//cmsContentImage.setFileName(productImage.getProductImage());
				//cmsContentImage.setFile(inputStream);
				//cmsContentImage.setFileContentType(FileContentType.PRODUCT);

				//addProductImage(product, productImage, cmsContentImage);
			}

		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}

	
	
	
	public void saveOrUpdate(ProductImage productImage) throws ServiceException {

		super.save(productImage);

	}

	public void addProductImageDescription(ProductImage productImage, ProductImageDescription description)
			throws ServiceException {

		if (productImage.getDescriptions() == null) {
			productImage.setDescriptions(new ArrayList<ProductImageDescription>());
		}

		productImage.getDescriptions().add(description);
		description.setProductImage(productImage);
		save(productImage);

	}

	// TODO get default product image

	
	public Object getProductImage(ProductImage productImage, ProductImageSize size) throws ServiceException {

		ProductImage pi = new ProductImage();
		String imageName = productImage.getProductImage();
		if (size == ProductImageSize.LARGE) {
			imageName = "L-" + imageName;
		}

		if (size == ProductImageSize.SMALL) {
			imageName = "S-" + imageName;
		}

		pi.setProductImage(imageName);
		pi.setProduct(productImage.getProduct());

		//OutputContentFile outputImage = null;
				
				//productFileManager.getProductImage(pi);

		return null;

	}

	
	public Object getProductImage(final String storeCode, final String productCode, final String fileName,
			final ProductImageSize size) throws ServiceException {
		Object outputImage =  null;
				//productFileManager.getProductImage(storeCode, productCode, fileName, size);
		return outputImage;

	}

	
	public List<?> getProductImages(Product product) throws ServiceException {
		return null;
		//return productFileManager.getImages(product);
	}

	
	public void removeProductImage(ProductImage productImage) throws ServiceException {

		if (!StringUtils.isBlank(productImage.getProductImage())) {
			//productFileManager.removeProductImage(productImage);// managed
																// internally
		}

		ProductImage p = this.getById(productImage.getId());

		//this.delete(p);

	}

	public void addProductImage(Product product, ProductImage image) {
		// TODO Auto-generated method stub
		
	}
}
