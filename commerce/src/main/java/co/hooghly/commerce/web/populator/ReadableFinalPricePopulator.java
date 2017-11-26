package co.hooghly.commerce.web.populator;

import org.apache.commons.lang.Validate;

import co.hooghly.commerce.business.ConversionException;
import co.hooghly.commerce.business.PricingService;
import co.hooghly.commerce.business.utils.AbstractDataPopulator;
import co.hooghly.commerce.domain.FinalPrice;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.web.ui.ReadableProductPrice;

public class ReadableFinalPricePopulator extends
		AbstractDataPopulator<FinalPrice, ReadableProductPrice> {
	
	
	private PricingService pricingService;

	public PricingService getPricingService() {
		return pricingService;
	}

	public void setPricingService(PricingService pricingService) {
		this.pricingService = pricingService;
	}

	@Override
	public ReadableProductPrice populate(FinalPrice source,
			ReadableProductPrice target, MerchantStore store, Language language)
			throws ConversionException {
		Validate.notNull(pricingService,"pricingService must be set");
		
		try {
			
			target.setOriginalPrice(pricingService.getDisplayAmount(source.getOriginalPrice(), store));
			if(source.isDiscounted()) {
				target.setDiscounted(true);
				target.setFinalPrice(pricingService.getDisplayAmount(source.getDiscountedPrice(), store));
			} else {
				target.setFinalPrice(pricingService.getDisplayAmount(source.getFinalPrice(), store));
			}
			
		} catch(Exception e) {
			throw new ConversionException("Exception while converting to ReadableProductPrice",e);
		}
		
		
		
		return target;
	}

	@Override
	protected ReadableProductPrice createTarget() {
		// TODO Auto-generated method stub
		return null;
	}

}
