package co.hooghly.commerce.modules.order;

import java.io.ByteArrayOutputStream;

import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Order;


public interface InvoiceModule {
	
	public ByteArrayOutputStream createInvoice(MerchantStore store, Order order, Language language) throws Exception;

}
