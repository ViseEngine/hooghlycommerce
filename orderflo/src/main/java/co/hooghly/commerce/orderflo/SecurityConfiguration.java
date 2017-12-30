package co.hooghly.commerce.orderflo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import co.hooghly.commerce.orderflo.audit.SpringSecurityAuditorAware;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Bean
	public AuditorAware<String> auditorProvider() {
		return new SpringSecurityAuditorAware();
	}


	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/webjars/**", "/user/login", "/user/register", "/user/savenew","/user/activate","/install/**")
				.permitAll().antMatchers("/admin/**").hasRole("ADMIN").antMatchers("/studio/**")
				.hasAnyRole("ADMIN", "USER").antMatchers("/**/**").hasAnyRole("ADMIN", "USER").and().formLogin()
				.passwordParameter("password").usernameParameter("username").loginProcessingUrl("/authenticate")
				.failureUrl("/user/login?error=SEC-0001").loginPage("/user/login").defaultSuccessUrl("/lead/new?form=lead").and()
				.logout().logoutUrl("/user/logout").logoutSuccessUrl("/signin");
	}

	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(
				encoder());
	}
	
	

}
