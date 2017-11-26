package co.hooghly.commerce.modules.shipping;

import java.util.List;

import co.hooghly.commerce.business.ServiceException;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.PackageDetails;
import co.hooghly.commerce.domain.ShippingProduct;

public interface Packaging {
	
	public List<PackageDetails> getBoxPackagesDetails(
			List<ShippingProduct> products, MerchantStore store) throws ServiceException;
	
	public List<PackageDetails> getItemPackagesDetails(
			List<ShippingProduct> products, MerchantStore store) throws ServiceException;

}
