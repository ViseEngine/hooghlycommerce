package co.hooghly.commerce.orderflo.simpleusers;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import co.hooghly.commerce.orderflo.domain.User;


public class SimpleUsers implements UserDetails{
	
	private static final long serialVersionUID = 1L;
	private User user;
	private List<GrantedAuthority> authorities;
	String auth;
	
	SimpleUsers(User user,String auth)
	{
		this.user=user;
		this.auth=auth;
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
		//return user.isAccountNonExpired();
		return false;
	}


	@Override
	public boolean isAccountNonLocked() {
	//	return user.isAccountNonLocked();
		return true;
	}


	@Override
	public boolean isCredentialsNonExpired() {
		//return user.isCredentialsNonExpired();
		return true;
	}


	@Override
	public boolean isEnabled() {
		//return !user.isDeleted();
		return true;
	}

}
