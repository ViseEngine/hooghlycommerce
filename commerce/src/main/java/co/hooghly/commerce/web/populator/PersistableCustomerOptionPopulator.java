package co.hooghly.commerce.web.populator;

import co.hooghly.commerce.business.ConversionException;
import co.hooghly.commerce.business.LanguageService;
import co.hooghly.commerce.business.utils.AbstractDataPopulator;
import co.hooghly.commerce.domain.CustomerOption;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.web.ui.CustomerOptionDescription;
import co.hooghly.commerce.web.ui.PersistableCustomerOption;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

public class PersistableCustomerOptionPopulator extends
		AbstractDataPopulator<PersistableCustomerOption, CustomerOption> {

	
	private LanguageService languageService;
	
	@Override
	public CustomerOption populate(PersistableCustomerOption source,
			CustomerOption target, MerchantStore store, Language language)
			throws ConversionException {
		
		
		Validate.notNull(languageService, "Requires to set LanguageService");
		
		
		try {
			
			target.setCode(source.getCode());
			target.setMerchantStore(store);
			target.setSortOrder(source.getOrder());
			if(!StringUtils.isBlank(source.getType())) {
				target.setCustomerOptionType(source.getType());
			} else {
				target.setCustomerOptionType("TEXT");
			}
			target.setPublicOption(true);
			
			if(!CollectionUtils.isEmpty(source.getDescriptions())) {
				Set<co.hooghly.commerce.domain.CustomerOptionDescription> descriptions = new HashSet<co.hooghly.commerce.domain.CustomerOptionDescription>();
				for(CustomerOptionDescription desc  : source.getDescriptions()) {
					co.hooghly.commerce.domain.CustomerOptionDescription description = new co.hooghly.commerce.domain.CustomerOptionDescription();
					Language lang = languageService.getByCode(desc.getLanguage());
					if(lang==null) {
						throw new ConversionException("Language is null for code " + description.getLanguage() + " use language ISO code [en, fr ...]");
					}
					description.setLanguage(lang);
					description.setName(desc.getName());
					description.setTitle(desc.getTitle());
					description.setCustomerOption(target);
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
	protected CustomerOption createTarget() {
		return null;
	}

	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}

	public LanguageService getLanguageService() {
		return languageService;
	}

}
