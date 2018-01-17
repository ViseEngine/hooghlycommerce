package co.hooghly.commerce.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@Order(1)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests()
			.antMatchers("/nonsecure/**","/webjars/**","/order/**").permitAll()
			.antMatchers("/secure/**").hasRole("ADMIN")
			.and().formLogin()
			.passwordParameter("password").usernameParameter("username")
			.loginProcessingUrl("/secure/authenticate")
			.failureUrl("/nonsecure/login?error=SEC-0001")
			.loginPage("/nonsecure/login")
			.defaultSuccessUrl("/secure/home")
			.and()
			.logout().invalidateHttpSession(true)
			.logoutUrl("/nonsecure/logout")
			.logoutSuccessUrl("/nonsecure/login");
				
	}

	

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(
				passwordEncoder());
	}
	
	

}