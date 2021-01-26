package com.poivredesiles.fundraising.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poivredesiles.fundraising.model.user.Role;

public interface RoleRepository extends JpaRepository<Role, String> {

}
