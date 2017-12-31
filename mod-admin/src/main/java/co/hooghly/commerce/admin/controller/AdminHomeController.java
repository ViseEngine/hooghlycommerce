package co.hooghly.commerce.admin.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import co.hooghly.commerce.business.CountryService;
import co.hooghly.commerce.business.SystemConfigurationService;
import co.hooghly.commerce.business.UserService;
import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.domain.Country;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.User;

@Controller
@RequestMapping("/admin/secure")
public class AdminHomeController {

	@Autowired
	CountryService countryService;

	@Autowired
	UserService userService;
	
	
	private SystemConfigurationService systemConfigurationService;

	@GetMapping("/home")
	public String displayDashboard(Model model, HttpServletRequest request, HttpServletResponse response) {
		Language language = (Language) request.getAttribute("LANGUAGE");

		// display menu
		Map<String, String> activeMenus = new HashMap<String, String>();
		activeMenus.put("home", "home");

		model.addAttribute("activeMenus", activeMenus);

		// get store information
		MerchantStore store = (MerchantStore) request.getAttribute(Constants.ADMIN_STORE);

		Map<String, Country> countries = countryService.getCountriesMap(language);

		Country storeCountry = store.getCountry();
		Country country = countries.get(storeCountry.getIsoCode());

		String sCurrentUser = request.getRemoteUser();
		User currentUser = userService.getByUserName(sCurrentUser);

		model.addAttribute("store", store);
		model.addAttribute("country", country);
		model.addAttribute("user", currentUser);
		// get last 10 orders
		// OrderCriteria orderCriteria = new OrderCriteria();
		// orderCriteria.setMaxCount(10);
		// orderCriteria.setOrderBy(CriteriaOrderBy.DESC);

		return "admin/home";
	}
	
	@GetMapping("/demo")
	public String showDemoDataStatus(Model model, HttpServletRequest request, HttpServletResponse response) {
		

		return "admin/demo";
	}
	
	@GetMapping("/load")
	public String loadDemoData(Model model, HttpServletRequest request, HttpServletResponse response) {
		

		return "admin/demo";
	}
}
