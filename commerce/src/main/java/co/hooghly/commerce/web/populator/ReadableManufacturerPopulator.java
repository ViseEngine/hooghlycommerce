package co.hooghly.commerce.web.populator;

import co.hooghly.commerce.business.ConversionException;
import co.hooghly.commerce.business.utils.AbstractDataPopulator;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.ManufacturerDescription;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.web.ui.ReadableManufacturer;

import java.util.Set;

public class ReadableManufacturerPopulator extends AbstractDataPopulator<co.hooghly.commerce.domain.Manufacturer,ReadableManufacturer>
{



	
	@Override
	public ReadableManufacturer populate(
			co.hooghly.commerce.domain.Manufacturer source,
			ReadableManufacturer target, MerchantStore store, Language language) throws ConversionException {
		target.setId(source.getId());
		if(source.getDescriptions()!=null && source.getDescriptions().size()>0) {
			
				Set<ManufacturerDescription> descriptions = source.getDescriptions();
				ManufacturerDescription description = null;
				for(ManufacturerDescription desc : descriptions) {
					if(desc.getLanguage().getCode().equals(language.getCode())) {
						description = desc;
						break;
					}
				}
				
				target.setOrder(source.getOrder());
				target.setId(source.getId());
				target.setCode(source.getCode());
				
				if (description != null) {
					co.hooghly.commerce.web.ui.ManufacturerDescription d = new co.hooghly.commerce.web.ui.ManufacturerDescription();
					d.setName(description.getName());
					d.setDescription(description.getDescription());
					target.setDescription(d);
				}

		}

		return target;
	}

    @Override
    protected ReadableManufacturer createTarget()
    {
        return null;
    }
}
