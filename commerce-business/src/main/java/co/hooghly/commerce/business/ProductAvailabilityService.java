package co.hooghly.commerce.business;



import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.ProductAvailability;
import co.hooghly.commerce.repository.ProductAvailabilityRepository;

@Service
public class ProductAvailabilityService extends AbstractBaseBusinessDelegate<ProductAvailability, Long> {

	private ProductAvailabilityRepository productAvailabilityRepository;

	
	public ProductAvailabilityService(ProductAvailabilityRepository productAvailabilityRepository) {
		super(productAvailabilityRepository);
		this.productAvailabilityRepository = productAvailabilityRepository;
	}

	
	public void saveOrUpdate(ProductAvailability availability) throws ServiceException {
		save(availability);

	}

}
