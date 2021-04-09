package com.poivredesiles.fundraising.model.user;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.poivredesiles.fundraising.model.AbstractAuditingEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name="user")
@Data
@EqualsAndHashCode(callSuper=false)
public class User extends AbstractAuditingEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String username;
	
	private String password;
	
	private String firstname;
	
	private String lastname;
	
	private String language;
	
	private boolean disabled = false;
	
	private boolean locked = false;
	
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
}
