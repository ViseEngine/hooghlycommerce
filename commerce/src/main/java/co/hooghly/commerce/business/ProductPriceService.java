package co.hooghly.commerce.business;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.ProductPrice;
import co.hooghly.commerce.domain.ProductPriceDescription;
import co.hooghly.commerce.repository.ProductPriceRepository;

@Service
public class ProductPriceService extends SalesManagerEntityServiceImpl<Long, ProductPrice> {

	public ProductPriceService(ProductPriceRepository productPriceRepository) {
		super(productPriceRepository);
	}

	public void addDescription(ProductPrice price, ProductPriceDescription description) throws ServiceException {
		price.getDescriptions().add(description);
		// description.setPrice(price);
		update(price);
	}

	public void saveOrUpdate(ProductPrice price) throws ServiceException {

		if (price.getId() != null && price.getId() > 0) {
			this.update(price);
		} else {

			Set<ProductPriceDescription> descriptions = price.getDescriptions();
			price.setDescriptions(new HashSet<ProductPriceDescription>());
			this.create(price);
			for (ProductPriceDescription description : descriptions) {
				description.setProductPrice(price);
				this.addDescription(price, description);
			}

		}

	}

	public void delete(ProductPrice price) throws ServiceException {

		// override method, this allows the error that we try to remove a
		// detached instance
		price = this.getById(price.getId());
		super.delete(price);

	}

}
