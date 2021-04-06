package com.poivredesiles.fundraising.service.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poivredesiles.fundraising.exception.InvalidUsernameException;
import com.poivredesiles.fundraising.model.user.User;
import com.poivredesiles.fundraising.repository.user.UserRepository;
import com.poivredesiles.fundraising.service.UserService;
import com.poivredesiles.fundraising.service.dto.UserDTO;

@Service
@Transactional
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private MessageSource messageSource;
	
	private Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

	@Override
	public UserDTO createUser(UserDTO user) {
		return null;
	}

	@Override
	public Optional<User> findUserById(Long userId) {
		return userRepository.findById(userId);
	}

	@Override
	public void deleteUser(User user) {
		if(user != null) {
			userRepository.delete(user);
			log.info("Deleted user {}", user.getUsername());
		} else {
			log.warn("Could not delete user : was null");
		}
	}

	@Override
	public void validateUsername(String userName) throws InvalidUsernameException {
		if(userRepository.countByUsername(userName) > 0) {
			throw new InvalidUsernameException(messageSource.getMessage("user.error.exists", null, LocaleContextHolder.getLocale()));
		}
	}

}
