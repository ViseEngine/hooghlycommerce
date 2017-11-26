package co.hooghly.commerce.util;

import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.shop.controller.ControllerConstants;

public class PageBuilderUtils {
	
	public static String build404(MerchantStore store) {
		return new StringBuilder().append(ControllerConstants.Tiles.Pages.notFound).append(".").append(store.getStoreTemplate()).toString();
	}
	
	public static String buildHomePage(MerchantStore store) {
		return "redirect:" + Constants.SHOP_URI;
	}

}
