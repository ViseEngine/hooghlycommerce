package co.hooghly.commerce.controller;


import co.hooghly.commerce.business.CountryService;
import co.hooghly.commerce.business.CurrencyService;
import co.hooghly.commerce.business.EmailService;
import co.hooghly.commerce.business.LanguageService;
import co.hooghly.commerce.business.MerchantStoreService;
import co.hooghly.commerce.business.UserService;
import co.hooghly.commerce.business.ZoneService;
import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.constants.EmailConstants;
import co.hooghly.commerce.domain.Country;
import co.hooghly.commerce.domain.Currency;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.User;
import co.hooghly.commerce.domain.Zone;
import co.hooghly.commerce.domain.admin.Menu;
import co.hooghly.commerce.domain.admin.Size;
import co.hooghly.commerce.domain.admin.Weight;
import co.hooghly.commerce.modules.email.Email;
//import co.hooghly.commerce.util.DateUtil;
//import co.hooghly.commerce.util.EmailUtils;
//import co.hooghly.commerce.util.FilePathUtils;
//import co.hooghly.commerce.util.LabelUtils;
//import co.hooghly.commerce.util.LocaleUtils;
import co.hooghly.commerce.util.UserUtils;
import co.hooghly.commerce.web.ui.AjaxResponse;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

@Controller
@Slf4j
public class MerchantStoreController {
	
	
	
	@Autowired
	private MerchantStoreService merchantStoreService;
	
	@Autowired
	private CountryService countryService;
	
	@Autowired
	private ZoneService zoneService;
	
	@Autowired
	private LanguageService languageService;
	
	@Autowired
	private CurrencyService currencyService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private MessageSource messageSource;
	
	//@Autowired
	//private LabelUtils messages;
	
	//@Autowired
	//private EmailService emailService;
	
	//@Autowired
	//private EmailUtils emailUtils;
	
	//@Autowired
	//private FilePathUtils filePathUtils;
	
	
	private final static String NEW_STORE_TMPL = "email_template_new_store.ftl";
	
	
	@GetMapping("/secure/store")
	public String displayStores(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {

		model.addAttribute("stores",merchantStoreService.findAll());
		
		return "stores";
	}
	
	@GetMapping("/store/{id}")
	public String displayMerchantStore(@PathVariable("id") Long id, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		
		
		MerchantStore store = merchantStoreService.findOne(id);
		return displayMerchantStore(store, model, request, response, locale);
	}
	
	@PreAuthorize("hasRole('STORE')")
	@RequestMapping(value="/admin/store/storeCreate.html", method=RequestMethod.GET)
	public String displayMerchantStoreCreate(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		
		MerchantStore store = new MerchantStore();
		
		MerchantStore sessionStore = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		store.setCurrency(sessionStore.getCurrency());
		store.setCountry(sessionStore.getCountry());
		store.setZone(sessionStore.getZone());
		//store.setStorestateprovince(sessionStore.getStorestateprovince());
		store.setLanguages(sessionStore.getLanguages());
		store.setDomainName(sessionStore.getDomainName());
		

		return displayMerchantStore(store, model, request, response, locale);
	}
	
	
	
	private String displayMerchantStore(MerchantStore store, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		
		
		
		Language language = (Language)request.getAttribute("LANGUAGE");
		List<Language> languages = languageService.getLanguages();
		List<Currency> currencies = currencyService.list();
		Date dt = store.getInBusinessSince();
		if(dt!=null) {
			//store.setDateBusinessSince(DateUtil.formatDate(dt));
		} else {
			//store.setDateBusinessSince(DateUtil.formatDate(new Date()));
		}
		
		//get countries
		List<Country> countries = countryService.getCountries(language);
		
		List<Weight> weights = new ArrayList<Weight>();
		weights.add(new Weight("LB",messageSource.getMessage("label.generic.weightunit.LB", null, locale)));
		weights.add(new Weight("KG",messageSource.getMessage("label.generic.weightunit.KG", null, locale)));
		
		List<Size> sizes = new ArrayList<Size>();
		sizes.add(new Size("CM",messageSource.getMessage("label.generic.sizeunit.CM", null, locale)));
		sizes.add(new Size("IN",messageSource.getMessage("label.generic.sizeunit.IN", null, locale)));
		
		//display menu

		model.addAttribute("countries", countries);
		model.addAttribute("languages",languages);
		model.addAttribute("currencies",currencies);
		
		model.addAttribute("weights",weights);
		model.addAttribute("sizes",sizes);
		model.addAttribute("store", store);
		
		
		return "admin/storedetail";
		
		
	}
	
	@PreAuthorize("hasRole('STORE')")
	@RequestMapping(value="/admin/store/store.html", method=RequestMethod.GET)
	public String displayMerchantStore(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		return displayMerchantStore(store, model, request, response, locale);
	}
	

	//@PreAuthorize("hasRole('STORE')")
	@RequestMapping(value="/admin/store/save.html", method=RequestMethod.POST)
	public String saveMerchantStore(@Valid @ModelAttribute("store") MerchantStore store, BindingResult result, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		
		
		MerchantStore sessionStore = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);

		if(store.getId()!=null) {
			if(store.getId().intValue() != sessionStore.getId().intValue()) {
				return "redirect:/admin/store/store.html";
			}
		}
		
		Date date = new Date();
		if(!StringUtils.isBlank(store.getDateBusinessSince())) {
			try {
				//date = DateUtil.getDate(store.getDateBusinessSince());
				store.setInBusinessSince(date);
			} catch (Exception e) {
				ObjectError error = new ObjectError("dateBusinessSince",messageSource.getMessage("message.invalid.date", null, locale));
				result.addError(error);
			}
		}
		
		List<Currency> currencies = currencyService.list();
		
		List<Country> con =  countryService.getAll();
		
		
		Language language = /*(Language)request.getAttribute("LANGUAGE");*/ store.getDefaultLanguage();
		List<Language> languages = languageService.getLanguages();
		
		//get countries
		List<Country> countries = countryService.getCountries(language);
		
		List<Weight> weights = new ArrayList<Weight>();
		try {
		weights.add(new Weight("LB",messageSource.getMessage("label.generic.weightunit.LB", null, locale)));
		weights.add(new Weight("KG",messageSource.getMessage("label.generic.weightunit.KG", null, locale)));
		}catch(Exception e) {
			e.printStackTrace();
		}
		List<Size> sizes = new ArrayList<Size>();
		sizes.add(new Size("CM",messageSource.getMessage("label.generic.sizeunit.CM", null, locale)));
		sizes.add(new Size("IN",messageSource.getMessage("label.generic.sizeunit.IN", null, locale)));
		
		model.addAttribute("weights",weights);
		model.addAttribute("sizes",sizes);
		
		model.addAttribute("countries", countries);
		model.addAttribute("languages",languages);
		model.addAttribute("currencies",currencies);
		
		
		Country c = store.getCountry();
		List<Zone> zonesList = zoneService.getZones(c, language);
		
		/*if((zonesList==null || zonesList.size()==0) && StringUtils.isBlank(store.getStorestateprovince())) {
			
			ObjectError error = new ObjectError("zone.code",messageSource.getMessage("merchant.zone.invalid", null, locale));
			result.addError(error);
			
		}*/

		if (result.hasErrors()) {
			return "admin-store";
		}
		
		//get country
		Country country = store.getCountry();
		country = countryService.getByCode(country.getIsoCode());
		Zone zone = store.getZone();
		if(zone!=null) {
			zone = zoneService.getByCode(zone.getCode());
		}
		Currency currency = store.getCurrency();
		currency = currencyService.getById(currency.getId());

		List<Language> supportedLanguages = store.getLanguages();
		List<Language> supportedLanguagesList = new ArrayList<Language>();
		Map<String,Language> languagesMap = languageService.getLanguagesMap();
		for(Language lang : supportedLanguages) {
			
			Language l = languagesMap.get(lang.getCode());
			if(l!=null) {
				supportedLanguagesList.add(l);
			}
			
		}
		
		Language defaultLanguage = store.getDefaultLanguage();
		//defaultLanguage = languageService.getById(defaultLanguage.getId());
		if(defaultLanguage!=null) {
			store.setDefaultLanguage(defaultLanguage);
		}
		
		//Locale storeLocale = LocaleUtils.getLocale(defaultLanguage);
		
		store.setStoreTemplate(sessionStore.getStoreTemplate());
		store.setCountry(country);
		store.setZone(zone);
		store.setCurrency(currency);
		store.setDefaultLanguage(defaultLanguage);
		store.setLanguages(supportedLanguagesList);
		store.setLanguages(supportedLanguagesList);

		
		merchantStoreService.saveOrUpdate(store);
		
		/*if(!store.getCode().equals(sessionStore.getCode())) {//create store
			//send email
			
			try {


				Map<String, String> templateTokens = emailUtils.createEmailObjectsMap(request.getContextPath(), store, messages, storeLocale);
				templateTokens.put(EmailConstants.EMAIL_NEW_STORE_TEXT, messages.getMessage("email.newstore.text", storeLocale));
				templateTokens.put(EmailConstants.EMAIL_STORE_NAME, messages.getMessage("email.newstore.name",new String[]{store.getStorename()},storeLocale));
				templateTokens.put(EmailConstants.EMAIL_ADMIN_STORE_INFO_LABEL, messages.getMessage("email.newstore.info",storeLocale));

				templateTokens.put(EmailConstants.EMAIL_ADMIN_URL_LABEL, messages.getMessage("label.adminurl",storeLocale));
				templateTokens.put(EmailConstants.EMAIL_ADMIN_URL, filePathUtils.buildAdminUri(store, request));
	
				
				Email email = new Email();
				email.setFrom(store.getStorename());
				email.setFromEmail(store.getStoreEmailAddress());
				email.setSubject(messages.getMessage("email.newstore.title",storeLocale));
				email.setTo(store.getStoreEmailAddress());
				email.setTemplateName(NEW_STORE_TMPL);
				email.setTemplateTokens(templateTokens);
	
	
				
				emailService.sendHtmlEmail(store, email);
			
			} catch (Exception e) {
				LOGGER.error("Cannot send email to user",e);
			}
			
		}*/

		sessionStore = merchantStoreService.getMerchantStore(sessionStore.getCode());
		
		
		//update session store
		request.getSession().setAttribute(Constants.ADMIN_STORE, sessionStore);


		model.addAttribute("success","success");
		model.addAttribute("store", store);

		
		return "admin-store";
	}
	
	@PreAuthorize("hasRole('AUTH')")
	@RequestMapping(value="/admin/store/checkStoreCode.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> checkStoreCode(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		String code = request.getParameter("code");


		AjaxResponse resp = new AjaxResponse();
		
		final HttpHeaders httpHeaders= new HttpHeaders();
	    httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		try {
			
			if(StringUtils.isBlank(code)) {
				resp.setStatus(AjaxResponse.CODE_ALREADY_EXIST);
				return new ResponseEntity<String>(resp.toJSONString(),httpHeaders,HttpStatus.OK);
			}
			
			MerchantStore store = merchantStoreService.getByCode(code);
		


			
			if(store!=null) {
				resp.setStatus(AjaxResponse.CODE_ALREADY_EXIST);
				return new ResponseEntity<String>(resp.toJSONString(),httpHeaders,HttpStatus.OK);
			}



			resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

		} catch (Exception e) {
			log.error("Error while getting user", e);
			resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		
		
		return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	
	
	@PreAuthorize("hasRole('STORE_ADMIN')")
	@RequestMapping(value="/admin/store/remove.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> removeMerchantStore(HttpServletRequest request, Locale locale) throws Exception {

		String sMerchantStoreId = request.getParameter("storeId");

		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
	    httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		
		try {
			
			Long storeId = Long.parseLong(sMerchantStoreId);
			MerchantStore store = merchantStoreService.findOne(storeId);
			
			User user = userService.getByUserName(request.getRemoteUser());
			
			/**
			 * In order to remove a Store the logged in ser must be SUPERADMIN
			 */

			//check if the user removed has group SUPERADMIN
			boolean isSuperAdmin = false;
			if(UserUtils.userInGroup(user, Constants.GROUP_SUPERADMIN)) {
				isSuperAdmin = true;
			}

			
			if(!isSuperAdmin) {
				resp.setStatusMessage(messageSource.getMessage("message.security.caanotremovesuperadmin", null, locale));
				resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);			
				String returnString = resp.toJSONString();
				return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			//merchantStoreService.delete(store);
			
			resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

		
		
		} catch (Exception e) {
			log.error("Error while deleting product price", e);
			resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();

		return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		
	}
	


}
