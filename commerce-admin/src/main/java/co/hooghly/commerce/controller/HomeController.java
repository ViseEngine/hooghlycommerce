package co.hooghly.commerce.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/secure")
public class HomeController {

	@GetMapping("/home")
	public String displayDashboard(Principal principal, Model model) {

		model.addAttribute("user", principal);

		return "home";
	}

}
