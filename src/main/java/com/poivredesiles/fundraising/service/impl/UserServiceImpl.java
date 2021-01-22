package com.poivredesiles.fundraising.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poivredesiles.fundraising.service.UserService;
import com.poivredesiles.fundraising.service.dto.UserDTO;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Override
	public UserDTO createUser(UserDTO user) {
		return null;
	}

}
