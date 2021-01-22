package com.poivredesiles.fundraising.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poivredesiles.fundraising.model.Role;

public interface RoleRepository extends JpaRepository<Role, String> {

}
