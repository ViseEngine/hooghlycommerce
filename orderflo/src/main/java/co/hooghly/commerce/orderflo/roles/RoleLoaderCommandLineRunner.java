package co.hooghly.commerce.orderflo.roles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;


import co.hooghly.commerce.orderflo.business.OrderService;

import java.util.Arrays;
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

	@Override
	public void run(String... args) throws Exception {
		
		log.info("orderflo.MODE = {}", toolMode);
		
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