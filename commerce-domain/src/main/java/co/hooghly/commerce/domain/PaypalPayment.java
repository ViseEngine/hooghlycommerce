package co.hooghly.commerce.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * When the user performs a payment using paypal
 **/
@Data
@EqualsAndHashCode(callSuper=true)
public class PaypalPayment extends Payment {
	
	//express checkout
	private String payerId;
	private String paymentToken;
	
	public PaypalPayment() {
		super.setPaymentType(PaymentType.PAYPAL);
	}
	
}
