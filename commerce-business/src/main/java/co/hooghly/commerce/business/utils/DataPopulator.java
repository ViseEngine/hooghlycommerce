/**
 * 
 */
package co.hooghly.commerce.business.utils;

import co.hooghly.commerce.business.ConversionException;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;


public interface DataPopulator<Source,Target>
{


    public Target populate(Source source,Target target, MerchantStore store, Language language) throws ConversionException;
    public Target populate(Source source, MerchantStore store, Language language) throws ConversionException;

   
}
