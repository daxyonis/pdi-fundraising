package com.poivredesiles.fundraising.controller;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.poivredesiles.fundraising.model.user.Role;
import com.poivredesiles.fundraising.model.user.RoleEnum;
import com.poivredesiles.fundraising.model.user.User;
import com.poivredesiles.fundraising.service.PdiSellerService;
import com.poivredesiles.fundraising.service.dto.PdiSellerDTO;

public abstract class BaseControllerTest {

	@MockBean
	protected PdiSellerService pdiSellerService;
	
	protected static User user;
	protected static PdiSellerDTO seller = new PdiSellerDTO();
	protected static User buyer;
	
	@BeforeAll
	public static void setup() {
		user = new User();
		user.setId(1L);
		user.setUsername("bidon");
		user.setPassword("abcdefg");
		buyer = new User();
		buyer.setId(2L);
		buyer.setUsername("bob");
		buyer.setPassword("uvwxyz");
		buyer.setRoles(Set.of(new Role(RoleEnum.ROLE_BUYER)));
		seller.setId(5L);
		seller.setBuyerId(buyer.getId());		
		seller.setMeId(user.getId());		
	}
}
