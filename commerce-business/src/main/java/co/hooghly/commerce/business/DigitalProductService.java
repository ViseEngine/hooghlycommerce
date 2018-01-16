package co.hooghly.commerce.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import co.hooghly.commerce.domain.DigitalProduct;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.repository.DigitalProductRepository;

@Service
public class DigitalProductService extends SalesManagerEntityServiceImpl<Long, DigitalProduct> {

	private DigitalProductRepository digitalProductRepository;

	@Autowired
	ProductService productService;

	public DigitalProductService(DigitalProductRepository digitalProductRepository) {
		super(digitalProductRepository);
		this.digitalProductRepository = digitalProductRepository;
	}

	public void addProductFile(Product product, DigitalProduct digitalProduct, Object inputFile)
			throws ServiceException {

		Assert.notNull(digitalProduct, "DigitalProduct cannot be null");
		Assert.notNull(product, "Product cannot be null");
		digitalProduct.setProduct(product);

		try {

			// Assert.notNull(inputFile.getFile(),"InputContentFile.file cannot
			// be null");

			Assert.notNull(product.getMerchantStore(), "Product.merchantStore cannot be null");
			this.saveOrUpdate(digitalProduct);

			// productDownloadsFileManager.addFile(product.getMerchantStore().getCode(),
			// inputFile);

			product.setProductVirtual(true);
			productService.save(product);

		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			try {

				// if(inputFile.getFile()!=null) {
				// inputFile.getFile().close();
				// }

			} catch (Exception ignore) {
			}
		}

	}

	public DigitalProduct getByProduct(MerchantStore store, Product product) throws ServiceException {
		return digitalProductRepository.findByProduct(store.getId(), product.getId());
	}

	public void delete(DigitalProduct digitalProduct) throws ServiceException {

		Assert.notNull(digitalProduct, "DigitalProduct cannot be null");
		Assert.notNull(digitalProduct.getProduct(), "DigitalProduct.product cannot be null");
		// refresh file
		digitalProduct = this.getById(digitalProduct.getId());
		super.delete(digitalProduct);
		// productDownloadsFileManager.removeFile(digitalProduct.getProduct().getMerchantStore().getCode(),
		// FileContentType.PRODUCT, digitalProduct.getProductFileName());
		digitalProduct.getProduct().setProductVirtual(false);
		productService.save(digitalProduct.getProduct());
	}

	public void saveOrUpdate(DigitalProduct digitalProduct) throws ServiceException {

		Assert.notNull(digitalProduct, "DigitalProduct cannot be null");
		Assert.notNull(digitalProduct.getProduct(), "DigitalProduct.product cannot be null");
		if (digitalProduct.getId() == null || digitalProduct.getId().longValue() == 0) {
			super.save(digitalProduct);
		} else {
			super.create(digitalProduct);
		}

		digitalProduct.getProduct().setProductVirtual(true);
		productService.save(digitalProduct.getProduct());

	}

}
