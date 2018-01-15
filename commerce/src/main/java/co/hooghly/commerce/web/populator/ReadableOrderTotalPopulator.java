package co.hooghly.commerce.web.populator;

import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.business.ConversionException;
import co.hooghly.commerce.business.PricingService;
import co.hooghly.commerce.business.utils.AbstractDataPopulator;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.OrderTotal;
import co.hooghly.commerce.util.LabelUtils;

import co.hooghly.commerce.web.ui.ReadableOrderTotal;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public class ReadableOrderTotalPopulator extends
		AbstractDataPopulator<OrderTotal, ReadableOrderTotal> {
	
	
	private PricingService pricingService;


	private LabelUtils messages;




	@Override
	public ReadableOrderTotal populate(OrderTotal source,
			ReadableOrderTotal target, MerchantStore store, Language language)
			throws ConversionException {
		
			Validate.notNull(pricingService,"PricingService must be set");
			Validate.notNull(messages,"LabelUtils must be set");
			
			Locale locale = language.computeLocale(store.getCountry());
		
			try {
				
				target.setCode(source.getOrderTotalCode());
				target.setId(source.getId());
				target.setModule(source.getModule());
				target.setOrder(source.getSortOrder());
				

				target.setTitle(messages.getMessage(source.getOrderTotalCode(), locale, source.getOrderTotalCode()));
				target.setText(source.getText());
				
				target.setValue(source.getValue());
				target.setTotal(pricingService.getDisplayAmount(source.getValue(), store));
				
				if(!StringUtils.isBlank(source.getOrderTotalCode())) {
					if(Constants.OT_DISCOUNT_TITLE.equals(source.getOrderTotalCode())) {
						target.setDiscounted(true);
					}
				}
				
			} catch(Exception e) {
				throw new ConversionException(e);
			}
			
			return target;
		
	}

	@Override
	protected ReadableOrderTotal createTarget() {
		return new ReadableOrderTotal();
	}
	
	public PricingService getPricingService() {
		return pricingService;
	}

	public void setPricingService(PricingService pricingService) {
		this.pricingService = pricingService;
	}
	
	public LabelUtils getMessages() {
		return messages;
	}

	public void setMessages(LabelUtils messages) {
		this.messages = messages;
	}

}