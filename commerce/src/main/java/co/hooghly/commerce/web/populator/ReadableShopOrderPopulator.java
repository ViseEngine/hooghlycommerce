package co.hooghly.commerce.web.populator;

import co.hooghly.commerce.business.ConversionException;
import co.hooghly.commerce.business.utils.AbstractDataPopulator;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.web.ui.Address;
import co.hooghly.commerce.web.ui.PersistableCustomer;
import co.hooghly.commerce.web.ui.ReadableCustomer;
import co.hooghly.commerce.web.ui.ReadableShopOrder;
import co.hooghly.commerce.web.ui.ShopOrder;

public class ReadableShopOrderPopulator extends
		AbstractDataPopulator<ShopOrder, ReadableShopOrder> {

	@Override
	public ReadableShopOrder populate(ShopOrder source,
			ReadableShopOrder target, MerchantStore store, Language language)
			throws ConversionException {
		
		//not that much is required
		
		
		//customer
		
		try {
			
			ReadableCustomer customer = new ReadableCustomer();
			PersistableCustomer persistableCustomer = source.getCustomer();


			customer.setEmailAddress(persistableCustomer.getEmailAddress());
			if(persistableCustomer.getBilling()!=null) {
				Address address = new Address();
				address.setCity(persistableCustomer.getBilling().getCity());
				address.setCompany(persistableCustomer.getBilling().getCompany());
				address.setFirstName(persistableCustomer.getBilling().getFirstName());
				address.setLastName(persistableCustomer.getBilling().getLastName());
				address.setPostalCode(persistableCustomer.getBilling().getPostalCode());
				address.setPhone(persistableCustomer.getBilling().getPhone());
				if(persistableCustomer.getBilling().getCountry()!=null) {
					address.setCountry(persistableCustomer.getBilling().getCountry());
				}
				if(persistableCustomer.getBilling().getZone()!=null) {
					address.setZone(persistableCustomer.getBilling().getZone());
				}
				
				customer.setBilling(address);
			}
			
			if(persistableCustomer.getDelivery()!=null) {
				Address address = new Address();
				address.setCity(persistableCustomer.getDelivery().getCity());
				address.setCompany(persistableCustomer.getDelivery().getCompany());
				address.setFirstName(persistableCustomer.getDelivery().getFirstName());
				address.setLastName(persistableCustomer.getDelivery().getLastName());
				address.setPostalCode(persistableCustomer.getDelivery().getPostalCode());
				address.setPhone(persistableCustomer.getDelivery().getPhone());
				if(persistableCustomer.getDelivery().getCountry()!=null) {
					address.setCountry(persistableCustomer.getDelivery().getCountry());
				}
				if(persistableCustomer.getDelivery().getZone()!=null) {
					address.setZone(persistableCustomer.getDelivery().getZone());
				}
				
				customer.setDelivery(address);
			}
			
			//TODO if ship to billing enabled, set delivery = billing
			
			
/*			if(persistableCustomer.getAttributes()!=null) {
				for(PersistableCustomerAttribute attribute : persistableCustomer.getAttributes()) {
					ReadableCustomerAttribute readableAttribute = new ReadableCustomerAttribute();
					readableAttribute.setId(attribute.getId());
					ReadableCustomerOption option = new ReadableCustomerOption();
					option.setId(attribute.getCustomerOption().getId());
					option.setCode(attribute.getCustomerOption());
					
					CustomerOptionDescription d = new CustomerOptionDescription();
					d.setDescription(attribute.getCustomerOption().getDescriptionsSettoList().get(0).getDescription());
					d.setName(attribute.getCustomerOption().getDescriptionsSettoList().get(0).getName());
					option.setDescription(d);
					
					readableAttribute.setCustomerOption(option);
					
					ReadableCustomerOptionValue optionValue = new ReadableCustomerOptionValue();
					optionValue.setId(attribute.getCustomerOptionValue().getId());
					CustomerOptionValueDescription vd = new CustomerOptionValueDescription();
					vd.setDescription(attribute.getCustomerOptionValue().getDescriptionsSettoList().get(0).getDescription());
					vd.setName(attribute.getCustomerOptionValue().getDescriptionsSettoList().get(0).getName());
					optionValue.setCode(attribute.getCustomerOptionValue().getCode());
					optionValue.setDescription(vd);
					
					
					readableAttribute.setCustomerOptionValue(optionValue);
					customer.getAttributes().add(readableAttribute);
				}
			}*/
			
			target.setCustomer(customer);
		
		} catch (Exception e) {
			throw new ConversionException(e);
		}
		
		
		
		return target;
	}

	@Override
	protected ReadableShopOrder createTarget() {
		return null;
	}

}
