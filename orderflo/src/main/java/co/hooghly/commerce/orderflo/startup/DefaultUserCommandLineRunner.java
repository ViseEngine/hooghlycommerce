package co.hooghly.commerce.orderflo.startup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import co.hooghly.commerce.orderflo.business.OrderService;
import co.hooghly.commerce.orderflo.business.UserBusinessDelegate;
import co.hooghly.commerce.orderflo.domain.User;
import co.hooghly.commerce.orderflo.roles.repository.RoleRepository;
import co.hooghly.commerce.orderflo.roles.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(2)
public class DefaultUserCommandLineRunner implements CommandLineRunner {
	
	@Autowired
	private OrderService orderService;
	
	
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	
	@Value("${orderflo.mode}")
	private String toolMode;
	
/*	//@Autowired
	private Role role;*/
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserBusinessDelegate usersbusinessDeligate;
	
	@Override
	public void run(String... args) throws Exception {
		
		String emails[]={"demo@d.com"};
		
		for(String email:emails)
		{
			User user=userRepository.findByEmail(email);
			if(user !=null)
			{
				//do nothing
				log.info("Default user already present = ", email);
			}
			else {
				 // create default user
				defaultUserWithAdminRole(email);
				
			}
			
		}
		log.info("orderflo.MODE = {}", toolMode);
	}

	private void defaultUserWithAdminRole( String email) 
	{
		User demoUser =new User();
		demoUser.setFirstName("Demo");
		demoUser.setLastName("Demo");
		demoUser.setEmail(email);
		demoUser.setPassword("demo");
		demoUser.setRole("ADMIN");
		log.info("User service has been requested to insert ::", email);
		usersbusinessDeligate.register(demoUser);
		System.out.println(email);
	}


}