package com.poivredesiles.fundraising.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
