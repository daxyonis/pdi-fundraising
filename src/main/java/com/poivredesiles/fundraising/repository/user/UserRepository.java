package com.poivredesiles.fundraising.repository.user;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poivredesiles.fundraising.model.user.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User getUserByUsername(String username);

	long countByUsername(String username);
	
	List<User> findAllByLockedAndLastModifiedDateBefore(Boolean locked, Instant instant);
	
}
