package co.hooghly.commerce.orderflo.startup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import co.hooghly.commerce.orderflo.business.OrderService;
import co.hooghly.commerce.orderflo.domain.Role;
import co.hooghly.commerce.orderflo.roles.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import co.hooghly.commerce.orderflo.business.RolesBusinessDelegate;

@Component
@Slf4j
public class RoleLoaderCommandLineRunner implements CommandLineRunner {
	
	@Autowired
	private OrderService orderService;
	
	
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	
	@Value("${orderflo.mode}")
	private String toolMode;
	
/*	//@Autowired
	private Role role;*/
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private RolesBusinessDelegate rolesbusinessDeligate;
	
	@Override
	public void run(String... args) throws Exception {
		
		String roles[]={"ADMIN","USER"};
		
		for(String role:roles)
		{
			boolean flag=((roleRepository.findByName(role)).isPresent());
			if(flag)
			{
				//do nothing
				log.info("Role already present = ", role);
			}
			else {
				 // create role
				provisionRole(role);
				
			}
			
		}
		
	//	String admin=((repository.findByName("ADMIN")).get()).getName();
		log.info("orderflo.MODE = {}", toolMode);
	}

	private void provisionRole( String role) 
	{
		Role r =new Role();
		r.setName(role);
		rolesbusinessDeligate.register(r);
		//repository.save(r);
	}


}