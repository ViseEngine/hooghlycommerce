package co.hooghly.commerce.admin.controller;


import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import co.hooghly.commerce.business.CountryService;
import co.hooghly.commerce.business.UserService;


@Controller
@RequestMapping("/admin/secure/languages")
public class AdminLanguageController {

	@Inject
	CountryService countryService;

	@Inject
	UserService userService;

	@GetMapping("")
	public String findAll(Model model, HttpServletRequest request, HttpServletResponse response) {
	
		return "admin/languages";
	}
	
	@GetMapping("")
	public String install(Model model, HttpServletRequest request, HttpServletResponse response) {
	
		return "admin/languages";
	}
}
