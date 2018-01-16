/**
 * 
 */
package co.hooghly.commerce.business.utils;

import java.util.Locale;

import co.hooghly.commerce.business.ConversionException;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;


@Deprecated
public abstract class AbstractDataPopulator<Source,Target> implements DataPopulator<Source, Target>
{

 
   
    private Locale locale;

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	public Locale getLocale() {
		return locale;
	}
	

	@Override
	public Target populate(Source source, MerchantStore store, Language language) throws ConversionException{
	   return populate(source,createTarget(), store, language);
	}
	
	protected abstract Target createTarget();

   

}
