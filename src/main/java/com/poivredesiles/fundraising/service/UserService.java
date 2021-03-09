package com.poivredesiles.fundraising.service;

import java.util.Optional;

import com.poivredesiles.fundraising.model.user.User;
import com.poivredesiles.fundraising.service.dto.UserDTO;

public interface UserService {

	/**
	 * Create a new user
	 * @param user
	 * @return
	 */
	UserDTO createUser(UserDTO user);

	/**
	 * Find one user given its id
	 * @param userId
	 * @return
	 */
	Optional<User> findUserById(Long userId);

	/**
	 * Delete one user
	 * @param buyer
	 */
	void deleteUser(User user);
}
