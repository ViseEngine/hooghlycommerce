package co.hooghly.commerce.web.populator;


import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import co.hooghly.commerce.business.ConversionException;
import co.hooghly.commerce.business.utils.AbstractDataPopulator;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.web.ui.ReadableMerchantStore;


public class ReadableMerchantStorePopulator extends
		AbstractDataPopulator<MerchantStore, ReadableMerchantStore> {

	@Override
	public ReadableMerchantStore populate(MerchantStore source,
			ReadableMerchantStore target, MerchantStore store, Language language)
			throws ConversionException {
		
		
		target.setId(source.getId());
		target.setCode(source.getCode());
		target.setDefaultLanguage(source.getDefaultLanguage().getCode());
		
		List<Language> languages = source.getLanguages();
		if(!CollectionUtils.isEmpty(languages)) {
			
			List<String> langs = new ArrayList<String>();
			for(Language lang : languages) {
				langs.add(lang.getCode());
			}
			
			target.setSupportedLanguages(langs);
		}
		
		
		return target;
	}

	@Override
	protected ReadableMerchantStore createTarget() {
		// TODO Auto-generated method stub
		return null;
	}

}
