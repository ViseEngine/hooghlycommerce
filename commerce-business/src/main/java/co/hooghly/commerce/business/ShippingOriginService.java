package co.hooghly.commerce.business;

import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.ShippingOrigin;
import co.hooghly.commerce.repository.ShippingOriginRepository;

@Service
public class ShippingOriginService extends SalesManagerEntityServiceImpl<Long, ShippingOrigin> {

	private ShippingOriginRepository shippingOriginRepository;

	public ShippingOriginService(ShippingOriginRepository shippingOriginRepository) {
		super(shippingOriginRepository);
		this.shippingOriginRepository = shippingOriginRepository;
	}

	public ShippingOrigin getByStore(MerchantStore store) {

		ShippingOrigin origin = shippingOriginRepository.findByStore(store.getId());
		return origin;
	}

}
