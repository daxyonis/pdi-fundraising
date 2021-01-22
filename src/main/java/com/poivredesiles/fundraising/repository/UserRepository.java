package com.poivredesiles.fundraising.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poivredesiles.fundraising.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User getUserByUsername(String username);

}
