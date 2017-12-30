package co.hooghly.commerce.orderflo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import co.hooghly.commerce.orderflo.business.UserBusinessDelegate;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserBusinessDelegate userBusinessDelegate;
	
	@RequestMapping("/login")
	public String goToSignOrHome() {
		System.out.println(" inside login ..");
		String view = "login";

		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal instanceof UserDetails) {
			view = "redirect:/lead/new?form=formLead";
			view = "login";
		}
		System.out.println(" View returned .."+view);
		return view;
	}
	@RequestMapping("/logout")
	public String logout() {
		return "signinform";
	}
	
	@GetMapping(value="/register")
	public String goToSignUp() {
		return "signupform";
	}
	
	@RequestMapping("/success")
	public String successPage() {
		System.out.println("Inside Success block");
		return "success";
	}
	

}