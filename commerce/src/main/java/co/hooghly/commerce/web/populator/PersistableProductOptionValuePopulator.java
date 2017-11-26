package co.hooghly.commerce.web.populator;

import co.hooghly.commerce.business.ConversionException;
import co.hooghly.commerce.business.LanguageService;
import co.hooghly.commerce.business.utils.AbstractDataPopulator;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.ProductOptionValue;
import co.hooghly.commerce.web.ui.PersistableProductOptionValue;
import co.hooghly.commerce.web.ui.ProductOptionValueDescription;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.Validate;

import java.util.HashSet;
import java.util.Set;



/**
 * Converts a PersistableProductOptionValue to
 * a ProductOptionValue model object

 *
 */
public class PersistableProductOptionValuePopulator extends
		AbstractDataPopulator<PersistableProductOptionValue, ProductOptionValue> {

	
	private LanguageService languageService;
	
	public LanguageService getLanguageService() {
		return languageService;
	}

	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}

	@Override
	public ProductOptionValue populate(PersistableProductOptionValue source,
			ProductOptionValue target, MerchantStore store, Language language)
			throws ConversionException {
		
		Validate.notNull(languageService, "Requires to set LanguageService");
		
		
		try {
			

			target.setMerchantStore(store);
			target.setProductOptionValueSortOrder(source.getOrder());
			target.setCode(source.getCode());
			
			if(!CollectionUtils.isEmpty(source.getDescriptions())) {
				Set<co.hooghly.commerce.domain.ProductOptionValueDescription> descriptions = new HashSet<co.hooghly.commerce.domain.ProductOptionValueDescription>();
				for(ProductOptionValueDescription desc  : source.getDescriptions()) {
					co.hooghly.commerce.domain.ProductOptionValueDescription description = new co.hooghly.commerce.domain.ProductOptionValueDescription();
					Language lang = languageService.getByCode(desc.getLanguage());
					if(lang==null) {
						throw new ConversionException("Language is null for code " + description.getLanguage() + " use language ISO code [en, fr ...]");
					}
					description.setLanguage(lang);
					description.setName(desc.getName());
					description.setTitle(desc.getTitle());
					description.setProductOptionValue(target);
					descriptions.add(description);
				}
				target.setDescriptions(descriptions);
			}
		
		} catch (Exception e) {
			throw new ConversionException(e);
		}
		
		
		return target;
	}

	@Override
	protected ProductOptionValue createTarget() {
		return null;
	}

}
