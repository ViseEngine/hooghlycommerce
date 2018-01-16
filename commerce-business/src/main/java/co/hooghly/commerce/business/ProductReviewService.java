package co.hooghly.commerce.business;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.Customer;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.domain.ProductReview;
import co.hooghly.commerce.repository.ProductReviewRepository;

@Service
public class ProductReviewService extends
		SalesManagerEntityServiceImpl<Long, ProductReview> {


	private ProductReviewRepository productReviewRepository;
	
	@Autowired
	private ProductService productService;
	
	
	public ProductReviewService(
			ProductReviewRepository productReviewRepository) {
			super(productReviewRepository);
			this.productReviewRepository = productReviewRepository;
	}

	
	public List<ProductReview> getByCustomer(Customer customer) {
		return productReviewRepository.findByCustomer(customer.getId());
	}

	
	public List<ProductReview> getByProduct(Product product) {
		return productReviewRepository.findByProduct(product.getId());
	}
	
	
	public ProductReview getByProductAndCustomer(Long productId, Long customerId) {
		return productReviewRepository.findByProductAndCustomer(productId, customerId);
	}
	
	
	public List<ProductReview> getByProduct(Product product, Language language) {
		return productReviewRepository.findByProduct(product.getId(), language.getId());
	}
	
	public void create(ProductReview review) throws ServiceException {
		
		//adjust score
		
		//refresh product
		Product product = productService.findOne(review.getProduct().getId());
		
		//ajust product rating
		Integer count = 0;
		if(product.getProductReviewCount()!=null) {
			count = product.getProductReviewCount();
		}
				
		
		

		BigDecimal averageRating = product.getProductReviewAvg();
		if(averageRating==null) {
			averageRating = new BigDecimal(0);
		}
		//get reviews

		
		BigDecimal totalRating = averageRating.multiply(new BigDecimal(count));
		totalRating = totalRating.add(new BigDecimal(review.getReviewRating()));
		
		count = count + 1;
		double avg = totalRating.doubleValue() / count.intValue();
		
		product.setProductReviewAvg(new BigDecimal(avg));
		product.setProductReviewCount(count);
		super.create(review);
		
		productService.save(product);
		
	}

	/* (non-Javadoc)
	 * @see com.salesmanager.core.business.services.catalog.product.review.ProductReviewService#getByProductNoObjects(com.salesmanager.core.model.catalog.product.Product)
	 */
	
	public List<ProductReview> getByProductNoCustomers(Product product) {
		return productReviewRepository.findByProductNoCustomers(product.getId());
	}


}
