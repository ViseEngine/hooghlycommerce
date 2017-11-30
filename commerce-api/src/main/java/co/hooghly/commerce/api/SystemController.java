package co.hooghly.commerce.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseBody;

import co.hooghly.commerce.business.ModuleConfigurationService;
import co.hooghly.commerce.domain.IntegrationModule;

import lombok.extern.slf4j.Slf4j;

/**
 * Rest services for the system configuration
 *
 * 
 */
@Controller
@RequestMapping("/api")
@Slf4j
public class SystemController {

	@Autowired
	private ModuleConfigurationService moduleConfigurationService;

	/**
	 * Creates or updates a configuration module. A JSON has to be created on
	 * the client side which represents an object that will create a new module
	 * (payment, shipping ...) which can be used and configured from the
	 * administration tool. Here is an example of configuration accepted
	 * 
	 * { "module": "PAYMENT", "code": "paypal-express-checkout",
	 * "type":"paypal", "version":"104.0", "regions": ["*"],
	 * "image":"icon-paypal.png",
	 * "configuration":[{"env":"TEST","scheme":"","host":"","port":"","uri":"","config1":"https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token="},{"env":"PROD","scheme":"","host":"","port":"","uri":"","config1":"https://www.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token="}]
	 * 
	 * }
	 *
	 * 
	 * @param json
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@PostMapping(value = "/system/modules", consumes = "application/json")
	@ResponseBody
	public IntegrationModule createOrUpdateModule(@RequestBody final String json, HttpServletRequest request,
			HttpServletResponse response) {
		log.debug("Creating or updating an integration module : " + json);
		return moduleConfigurationService.createOrUpdateModule(json);
	}

	@PostMapping(value = "/system/optin")
	@ResponseBody
	public IntegrationModule createOptin(@RequestBody final String json, HttpServletRequest request,
			HttpServletResponse response) {

		log.debug("Creating an optin : " + json);
		return moduleConfigurationService.createOrUpdateModule(json);

	}

	@DeleteMapping(value = "/system/optin/{code}")
	@ResponseBody
	public IntegrationModule deleteOptin(@RequestBody final String code, HttpServletRequest request,
			HttpServletResponse response) {

		log.debug("Delete optin : " + code);
		return moduleConfigurationService.createOrUpdateModule(code);

	}

	@PostMapping(value = "/system/optin/{code}/customer", consumes = "application/json")
	@ResponseBody
	public IntegrationModule createOptinCustomer(@RequestBody final String code, HttpServletRequest request,
			HttpServletResponse response) {

		return moduleConfigurationService.createOrUpdateModule(code);

	}

}
