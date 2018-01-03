package co.hooghly.commerce.orderflo.startup;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import co.hooghly.commerce.orderflo.business.RolesBusinessDelegate;
import co.hooghly.commerce.orderflo.business.UserBusinessDelegate;
import co.hooghly.commerce.orderflo.domain.Role;
import co.hooghly.commerce.orderflo.domain.User;
import co.hooghly.commerce.orderflo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;


@Component
@Slf4j
@Order(2)
public class UserCommandLineRunner implements CommandLineRunner {


	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserBusinessDelegate userbusinessDelegate;

	@Autowired
	private RolesBusinessDelegate rolebusinessDelegate;
	
	@Autowired
	private PasswordEncoder encoder;

	@Override
	public void run(String... args) throws Exception {

		String emails[] = { "orderflo@hooghly.com" };

		for (String email : emails) {
			Optional<User> user = userRepository.findByEmail(email);
			if (!user.isPresent()) {
				defaultUserWithAdminRole(email);
			}

		}
		
	}
	
	
	private void defaultUserWithAdminRole(String email) {
	
		Role role = rolebusinessDelegate.findByName("ADMIN").get();
		
		log.info("Role - {}", role.getClass());
		log.info("Role - {}", role.getId());
		
		
		User defaultUser = new User();
		defaultUser.setFirstName("OrderFlo");
		defaultUser.setLastName("Admin");
		defaultUser.setEmail(email);
		defaultUser.setPassword(encoder.encode("demo"));
		defaultUser.getRoles().add(role);
	
		log.info("User service has been requested to insert ::", email);
		userbusinessDelegate.save(defaultUser);
		
	}

}