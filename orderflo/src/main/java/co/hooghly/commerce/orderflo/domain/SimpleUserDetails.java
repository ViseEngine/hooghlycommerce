package co.hooghly.commerce.orderflo.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class SimpleUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;
	private User user;
	private List<GrantedAuthority> authorities;
	
	private static final String ROLE_PREFIX = "ROLE_";

	public SimpleUserDetails(User user) {
		this.user = user;
		authorities = getAuthorities(user.getRoles());
	}

	private List<GrantedAuthority> getAuthorities(List<Role> roles) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		for (Role role : roles) {
			
			authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + role.getName()));

		}

		return authorities;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		return authorities;
	}

	@Override
	public String getPassword() {

		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {

		return true;
	}

	@Override
	public boolean isEnabled() {

		return true;
	}

}
