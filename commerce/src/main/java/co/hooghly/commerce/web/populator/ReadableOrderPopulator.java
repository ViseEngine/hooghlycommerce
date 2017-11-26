package co.hooghly.commerce.web.populator;

import co.hooghly.commerce.business.ConversionException;
import co.hooghly.commerce.business.utils.AbstractDataPopulator;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Order;
import co.hooghly.commerce.domain.OrderTotal;
import co.hooghly.commerce.domain.OrderTotalType;
import co.hooghly.commerce.web.ui.Address;
import co.hooghly.commerce.web.ui.ReadableDelivery;
import co.hooghly.commerce.web.ui.ReadableOrder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ReadableOrderPopulator extends
		AbstractDataPopulator<Order, ReadableOrder> {

	@Override
	public ReadableOrder populate(Order source, ReadableOrder target,
			MerchantStore store, Language language) throws ConversionException {
		
		
		
		target.setId(source.getId());
		target.setDatePurchased(source.getDatePurchased());
		target.setOrderStatus(source.getStatus());
		target.setCurrency(source.getCurrency().getCode());
		target.setCurrencyModel(source.getCurrency());
		if(source.getCustomerAgreement()!=null) {
			target.setCustomerAgreed(source.getCustomerAgreement());
		}
		if(source.getConfirmedAddress()!=null) {
			target.setConfirmedAddress(source.getConfirmedAddress());
		}
		
		co.hooghly.commerce.web.ui.OrderTotal taxTotal = null;
		co.hooghly.commerce.web.ui.OrderTotal shippingTotal = null;
		
		
		if(source.getBilling()!=null) {
			Address address = new Address();
			address.setCity(source.getBilling().getCity());
			address.setAddress(source.getBilling().getAddress());
			address.setCompany(source.getBilling().getCompany());
			address.setFirstName(source.getBilling().getFirstName());
			address.setLastName(source.getBilling().getLastName());
			address.setPostalCode(source.getBilling().getPostalCode());
			address.setPhone(source.getBilling().getTelephone());
			if(source.getBilling().getCountry()!=null) {
				address.setCountry(source.getBilling().getCountry().getIsoCode());
			}
			if(source.getBilling().getZone()!=null) {
				address.setZone(source.getBilling().getZone().getCode());
			}
			
			target.setBilling(address);
		}
		
		if(source.getDelivery()!=null) {
			ReadableDelivery address = new ReadableDelivery();
			address.setCity(source.getDelivery().getCity());
			address.setAddress(source.getDelivery().getAddress());
			address.setCompany(source.getDelivery().getCompany());
			address.setFirstName(source.getDelivery().getFirstName());
			address.setLastName(source.getDelivery().getLastName());
			address.setPostalCode(source.getDelivery().getPostalCode());
			address.setPhone(source.getDelivery().getTelephone());
			if(source.getDelivery().getCountry()!=null) {
				address.setCountry(source.getDelivery().getCountry().getIsoCode());
			}
			if(source.getDelivery().getZone()!=null) {
				address.setZone(source.getDelivery().getZone().getCode());
			}
			
			target.setDelivery(address);
		}
		
		List<co.hooghly.commerce.web.ui.OrderTotal> totals = new ArrayList<co.hooghly.commerce.web.ui.OrderTotal>();
		for(OrderTotal t : source.getOrderTotal()) {
			if(t.getOrderTotalType()==null) {
				continue;
			}
			if(t.getOrderTotalType().name().equals(OrderTotalType.TOTAL.name())) {
				co.hooghly.commerce.web.ui.OrderTotal totalTotal = createTotal(t);
				target.setTotal(totalTotal);
				totals.add(totalTotal);
			}
			else if(t.getOrderTotalType().name().equals(OrderTotalType.TAX.name())) {
				co.hooghly.commerce.web.ui.OrderTotal totalTotal = createTotal(t);
				if(taxTotal==null) {
					taxTotal = totalTotal;
				} else {
					BigDecimal v = taxTotal.getValue();
					v = v.add(totalTotal.getValue());
					taxTotal.setValue(v);
				}
				target.setTax(totalTotal);
				totals.add(totalTotal);
			}
			else if(t.getOrderTotalType().name().equals(OrderTotalType.SHIPPING.name())) {
				co.hooghly.commerce.web.ui.OrderTotal totalTotal = createTotal(t);
				if(shippingTotal==null) {
					shippingTotal = totalTotal;
				} else {
					BigDecimal v = shippingTotal.getValue();
					v = v.add(totalTotal.getValue());
					shippingTotal.setValue(v);
				}
				target.setShipping(totalTotal);
				totals.add(totalTotal);
			}
			else if(t.getOrderTotalType().name().equals(OrderTotalType.HANDLING.name())) {
				co.hooghly.commerce.web.ui.OrderTotal totalTotal = createTotal(t);
				if(shippingTotal==null) {
					shippingTotal = totalTotal;
				} else {
					BigDecimal v = shippingTotal.getValue();
					v = v.add(totalTotal.getValue());
					shippingTotal.setValue(v);
				}
				target.setShipping(totalTotal);
				totals.add(totalTotal);
			}
			else if(t.getOrderTotalType().name().equals(OrderTotalType.SUBTOTAL.name())) {
				co.hooghly.commerce.web.ui.OrderTotal subTotal = createTotal(t);
				totals.add(subTotal);
				
			}
			else {
				co.hooghly.commerce.web.ui.OrderTotal otherTotal = createTotal(t);
				totals.add(otherTotal);
			}
		}
		
		target.setTotals(totals);
		
		return target;
	}
	
	private co.hooghly.commerce.web.ui.OrderTotal createTotal(OrderTotal t) {
		co.hooghly.commerce.web.ui.OrderTotal totalTotal = new co.hooghly.commerce.web.ui.OrderTotal();
		totalTotal.setCode(t.getOrderTotalCode());
		totalTotal.setId(t.getId());
		totalTotal.setModule(t.getModule());
		totalTotal.setOrder(t.getSortOrder());
		totalTotal.setValue(t.getValue());
		return totalTotal;
	}

	@Override
	protected ReadableOrder createTarget() {

		return null;
	}

}
