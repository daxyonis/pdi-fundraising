package com.poivredesiles.fundraising.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poivredesiles.fundraising.model.user.User;
import com.poivredesiles.fundraising.repository.user.UserRepository;
import com.poivredesiles.fundraising.service.UserService;
import com.poivredesiles.fundraising.service.dto.UserDTO;

@Service
@Transactional
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDTO createUser(UserDTO user) {
		return null;
	}

	@Override
	public Optional<User> findUserById(Long userId) {
		return userRepository.findById(userId);
	}

}
