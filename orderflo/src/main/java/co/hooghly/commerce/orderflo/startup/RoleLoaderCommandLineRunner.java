package co.hooghly.commerce.orderflo.startup;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import co.hooghly.commerce.orderflo.business.OrderService;
import co.hooghly.commerce.orderflo.domain.Role;
import co.hooghly.commerce.orderflo.roles.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RoleLoaderCommandLineRunner implements CommandLineRunner {
	
	@Autowired
	private OrderService orderService;
	
	
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	
	@Value("${orderflo.mode}")
	private String toolMode;
	
	//@Autowired
	private Role role;
	
	@Autowired
	private RoleRepository repository;
	
	@Override
	public void run(String... args) throws Exception {
		
		/*Optional<Role> r1=repository.findByName("USER");
		Role a=r1.get();
		String user=a.getName();
		
		Optional<Role> r2=repository.findByName("ADMIN");
		
		String admin=((repository.findByName("ADMIN")).get()).getName();
		
		if(role.getName().equalsIgnoreCase("USER") || role.getName().equalsIgnoreCase("ADMIN"))
		{
			// do nothing
		}	
		else{
			
			// create role 
			Role roles=new Role();
			roles.setId(1L);
			roles.setName("ADMIN");
			
			//save using roles service class
		}*/
		
		log.info("orderflo.MODE = {}", toolMode);
		System.out.println("...Rajiv Before Application started....");
		/*if(StringUtil.equals("DEV", toolMode)) {
			//drop everything except crx schema
			//DbRefreshEvent dfe = new DbRefreshEvent(this);
//			eventPublisher.publishEvent(dfe);
		}*/
		
		//1.provision master db segment
//		provisionMasterDb();
		
		//2.provision system user
		//3. activate system user
//		provisionSystemUser();
				
		//4. create demo user, no role set so default to ADMIN
		//This is similar to when user signs up
		//If user signs up and no tenant is present in db then role = ADMIN
		//If user signs up and tenant is present in db then role = USER
		
		//5.activate demo user, including the schema by event UserWithTenantActivatedEvent
	//	provisionDemoUser();
				
		//6.Refresh tenants
	//	refreshTenants();
		
	}

	

/*	private void refreshTenants() {
		TenantDbRefreshEvent e = new TenantDbRefreshEvent(this);
		eventPublisher.publishEvent(e);
		
	}*/

/*	private void provisionDemoUser() {
		Tenant tDemo = new Tenant();
		tDemo.setKey("demo");
		
		User demoUser = new User();
		demoUser.setFirstName("Demo");
		demoUser.setLastName("Demo");
		demoUser.setEmail("demo@d.com");
		demoUser.setPassword("demo");
		demoUser.setTenant(tDemo);
		
		userBusinsessDelegate.register(demoUser);
		
		userBusinsessDelegate.activate("demo@d.com");
		
	}*/

/*	private void provisionSystemUser() {
		Role r = new Role();
		r.setName(SUPERADMIN);
		Tenant t = new Tenant();
		t.setKey(DEFAULT_TENANT_ID);
		User systemUser = new User();
		systemUser.setFirstName("System");
		systemUser.setLastName("User");
		systemUser.setRoles(Arrays.asList(r));
		systemUser.setEmail("su@sys.co");
		systemUser.setPassword("system");
		systemUser.setTenant(t);
		
		userBusinsessDelegate.register(systemUser);
		userBusinsessDelegate.activate("su@sys.co");
	}*/

/*	private void provisionMasterDb() {
		MasterDbRefreshEvent masterDbRefreshEvent = new MasterDbRefreshEvent(this,DEFAULT_TENANT_ID);
		eventPublisher.publishEvent(masterDbRefreshEvent);
		
	}*/
	

}