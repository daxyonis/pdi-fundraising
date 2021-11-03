package com.poivredesiles.fundraising.controller;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.poivredesiles.fundraising.model.user.User;
import com.poivredesiles.fundraising.service.PdiSellerService;
import com.poivredesiles.fundraising.service.dto.PdiSellerDTO;

public abstract class BaseControllerTest {

	@MockBean
	protected PdiSellerService pdiSellerService;
	
	protected static User user;
	protected static PdiSellerDTO seller = new PdiSellerDTO();
	
	@BeforeAll
	public static void setup() {
		user = new User();
		user.setId(1L);
		user.setUsername("bidon");
		user.setPassword("abcdefg");				
		seller.setId(5L);
		seller.setBuyerId(6L);		
		seller.setMeId(user.getId());
	}
}
