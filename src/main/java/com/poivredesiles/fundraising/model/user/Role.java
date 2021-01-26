package com.poivredesiles.fundraising.model.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="role")
@Data
public class Role {

	@Id
	@Column(length = 50)
	private String name;
	
	public Role() {}
	public Role(RoleEnum roleEnum) {
		this.name = roleEnum.name();
	}	
}
