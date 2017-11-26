
package co.hooghly.commerce.web.populator;

import co.hooghly.commerce.business.ConversionException;
import co.hooghly.commerce.business.LanguageService;
import co.hooghly.commerce.business.utils.AbstractDataPopulator;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.Manufacturer;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.web.ui.ManufacturerDescription;
import co.hooghly.commerce.web.ui.PersistableManufacturer;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.Validate;

import java.util.HashSet;
import java.util.Set;



public class PersistableManufacturerPopulator extends AbstractDataPopulator<PersistableManufacturer, Manufacturer>
{
	
	
	private LanguageService languageService;

	@Override
	public Manufacturer populate(PersistableManufacturer source,
			Manufacturer target, MerchantStore store, Language language)
			throws ConversionException {
		
		Validate.notNull(languageService, "Requires to set LanguageService");
		
		try {
			
			target.setMerchantStore(store);
			target.setCode(source.getCode());
			

			if(!CollectionUtils.isEmpty(source.getDescriptions())) {
				Set<co.hooghly.commerce.domain.ManufacturerDescription> descriptions = new HashSet<co.hooghly.commerce.domain.ManufacturerDescription>();
				for(ManufacturerDescription description : source.getDescriptions()) {
					co.hooghly.commerce.domain.ManufacturerDescription desc = new co.hooghly.commerce.domain.ManufacturerDescription();
					desc.setManufacturer(target);
					desc.setDescription(description.getDescription());
					desc.setName(description.getName());
					Language lang = languageService.getByCode(description.getLanguage());
					if(lang==null) {
						throw new ConversionException("Language is null for code " + description.getLanguage() + " use language ISO code [en, fr ...]");
					}
					desc.setLanguage(lang);
					descriptions.add(desc);
				}
				target.setDescriptions(descriptions);
			}
		
		} catch (Exception e) {
			throw new ConversionException(e);
		}
	
		
		return target;
	}

	@Override
	protected Manufacturer createTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}

	public LanguageService getLanguageService() {
		return languageService;
	}


}
