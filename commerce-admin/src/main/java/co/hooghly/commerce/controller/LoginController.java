package co.hooghly.commerce.controller;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;


@Controller
public class LoginController {

	@GetMapping({ "/nonsecure/login", "/" })
	public String displayLogin(Principal principal) {

		String view = "login";

		if (principal instanceof UserDetails) {
			view = "redirect:/dashboard";
		}

		return view;

	}

}
