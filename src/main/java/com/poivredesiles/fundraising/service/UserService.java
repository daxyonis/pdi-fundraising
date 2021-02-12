package com.poivredesiles.fundraising.service;

import java.util.Optional;

import com.poivredesiles.fundraising.model.user.User;
import com.poivredesiles.fundraising.service.dto.UserDTO;

public interface UserService {

	UserDTO createUser(UserDTO user);

	Optional<User> findUserById(Long userId);
}
