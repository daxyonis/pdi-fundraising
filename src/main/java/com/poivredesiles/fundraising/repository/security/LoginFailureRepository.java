package com.poivredesiles.fundraising.repository.security;

import java.sql.Timestamp;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poivredesiles.fundraising.model.security.LoginFailure;
import com.poivredesiles.fundraising.model.user.User;

public interface LoginFailureRepository extends JpaRepository<LoginFailure, Long> {

	int countAllByUserAndCreatedDateIsAfter(User user, Timestamp timestamp);
}
