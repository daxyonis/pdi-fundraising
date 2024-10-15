package com.poivredesiles.fundraising.model.user;

import com.poivredesiles.fundraising.converter.StringCryptoConverter;
import com.poivredesiles.fundraising.model.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="user")
@Data
@EqualsAndHashCode(callSuper=false)
public class User extends AbstractAuditingEntity implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	private String username;
	
	private String password;

	@Convert(converter = StringCryptoConverter.class)
	private String firstname;
	@Convert(converter = StringCryptoConverter.class)
	private String lastname;
	
	private String language;
	
	private boolean disabled = false;
	
	@ManyToMany(fetch = FetchType.EAGER)    
    private Set<Role> roles = new HashSet<>(); 
	
	public void addRole(RoleEnum roleEnum) {
		if(roles == null) {
			roles = new HashSet<>();
		}
		roles.add(new Role(roleEnum));
	}

	public void clearRoles() {
		roles.clear();		
	}
	
	public boolean hasRole(RoleEnum roleEnum) {
		if(roles == null) {
			return false;
		} else {
			return roles.contains(new Role(roleEnum));
		}
	}

	@Override
	public String toString(){
		return "User [id=" + id + ", username=" + username + ", firstname=" + firstname + ", lastname=" + lastname + ", language=" + language + ", disabled=" + disabled + "]";
	}
}
