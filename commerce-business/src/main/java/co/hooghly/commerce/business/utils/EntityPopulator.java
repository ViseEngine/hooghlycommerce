/**
 * 
 */
package co.hooghly.commerce.business.utils;

import co.hooghly.commerce.business.ConversionException;
import co.hooghly.commerce.domain.MerchantStore;


public interface EntityPopulator<Source,Target>
{

    public Target populateToEntity(Source source, Target target, MerchantStore store)  throws ConversionException;
    public Target populateToEntity(Source source) throws ConversionException;
}
