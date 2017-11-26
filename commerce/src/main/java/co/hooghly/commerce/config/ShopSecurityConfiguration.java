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
@Order(2)
public class ShopSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().
		authorizeRequests()
			.antMatchers("/shop/customer/**","/shop/**","/webjars/**","/order/**").permitAll()
			.antMatchers("/shop/customer/secure/**").hasRole("CUSTOMER")
			.and().formLogin()
			.passwordParameter("password").usernameParameter("username")
			.loginProcessingUrl("/shop/customer/authenticate")
			.failureUrl("/shop/customer/login?error=SEC-0001")
			.loginPage("/shop/customer/login")
			.defaultSuccessUrl("/shop/customer/home")
			.and()
			.logout().invalidateHttpSession(true)
			.logoutUrl("/shop/customer/logout")
			.logoutSuccessUrl("/shop/customer/login");
				
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(
				passwordEncoder);
	}
	
	

}