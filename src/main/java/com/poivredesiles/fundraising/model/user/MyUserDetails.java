package com.poivredesiles.fundraising.model.user;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Wrapper over User class that implements
 * UserDetails needed for UserDetailsService
 * (Security with authorities)
 * @author evita
 *
 */
public class MyUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;
	private User user;
	
	public MyUserDetails(User user) {
		this.user = user;
	}
	
	public Long getUserId() {
		return user.getId();
	}
	
	public boolean hasAnyAuthority(RoleEnum... values) {
		boolean hasOneAuthority = false;
		if(values.length > 0) {
			for(RoleEnum value : values) {
				if(user.hasRole(value)) {
					return true;
				}
			}
		}
		return hasOneAuthority;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> grantedAuthorities = user.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
		return grantedAuthorities;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !user.isLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return !user.isDisabled();
	}

}
