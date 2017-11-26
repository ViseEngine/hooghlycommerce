package co.hooghly.commerce.business;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.ProductAvailability;
import co.hooghly.commerce.repository.ProductAvailabilityRepository;

@Service("productAvailabilityService")
public class ProductAvailabilityService extends SalesManagerEntityServiceImpl<Long, ProductAvailability> {

	private ProductAvailabilityRepository productAvailabilityRepository;

	@Inject
	public ProductAvailabilityService(ProductAvailabilityRepository productAvailabilityRepository) {
		super(productAvailabilityRepository);
		this.productAvailabilityRepository = productAvailabilityRepository;
	}

	
	public void saveOrUpdate(ProductAvailability availability) throws ServiceException {

		if (availability.getId() != null && availability.getId() > 0) {

			this.update(availability);

		} else {
			this.create(availability);
		}

	}

}
