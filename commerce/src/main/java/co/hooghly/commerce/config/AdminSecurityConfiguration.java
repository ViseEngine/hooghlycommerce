package co.hooghly.commerce.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;


import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@Order(1)
public class AdminSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests()
			.antMatchers("/admin/nonsecure/**","/webjars/**","/order/**").permitAll()
			.antMatchers("/admin/secure/**").hasRole("ADMIN")
			.and().formLogin()
			.passwordParameter("password").usernameParameter("username")
			.loginProcessingUrl("/admin/secure/authenticate")
			.failureUrl("/admin/nonsecure/login?error=SEC-0001")
			.loginPage("/admin/nonsecure/login")
			.defaultSuccessUrl("/admin/secure/home")
			.and()
			.logout().invalidateHttpSession(true)
			.logoutUrl("/admin/nonsecure/logout")
			.logoutSuccessUrl("/admin/nonsecure/login");
				
	}

	

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(
				passwordEncoder);
	}
	
	

}