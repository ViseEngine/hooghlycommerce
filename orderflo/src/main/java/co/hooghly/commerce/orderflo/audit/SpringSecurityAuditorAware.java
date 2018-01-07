package co.hooghly.commerce.orderflo.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is responsible for authenticating the user which logged
 * into application.
 * 
 * @author Rajiv
 *
 */
@Slf4j
 public class SpringSecurityAuditorAware implements AuditorAware<String>{
	
	public String getCurrentAuditor() {
		String user = null;
		
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	    if (authentication == null || !authentication.isAuthenticated()) {
	    	log.info("Returning - {}", user);
	    	
	    	return user;
	    }
	    
	    user = ((User)authentication.getPrincipal()).getUsername();
	    
	    log.info("# Returning - {}", user);
	    
	    return  user;
	  }
}
