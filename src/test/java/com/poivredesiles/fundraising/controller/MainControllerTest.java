package com.poivredesiles.fundraising.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.poivredesiles.fundraising.imports.CsvImportService;
import com.poivredesiles.fundraising.model.user.MyUserDetails;
import com.poivredesiles.fundraising.model.user.Role;
import com.poivredesiles.fundraising.model.user.RoleEnum;
import com.poivredesiles.fundraising.service.PdiCampaignService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebMvcTest(MainController.class)
public class MainControllerTest extends BaseControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private CsvImportService csvImportService;
	
	@MockBean
	private PdiCampaignService pdiCampaignService;
	
	@Test	
	public void testHomePageGivenUserIsBuyer() throws Exception {
		log.info("Test what is home page for buyer");
		user.setRoles(Set.of(new Role(RoleEnum.ROLE_BUYER)));
		MyUserDetails userDetails = new MyUserDetails(user);
		when(pdiSellerService.getSellerForUser(userDetails)).thenReturn(seller);
		
		this.mockMvc
		.perform(get("/").with(user(userDetails)))
		.andExpect(status().is3xxRedirection())
		.andExpect(view().name("redirect:/commande"));
	}
	
	@Test	
	public void testHomePageGivenUserIsSeller() throws Exception {
		log.info("Test what is home page for seller");
		user.setRoles(Set.of(new Role(RoleEnum.ROLE_SELLER)));
		MyUserDetails userDetails = new MyUserDetails(user);
		when(pdiSellerService.getSellerForUser(userDetails)).thenReturn(seller);
		
		this.mockMvc
		.perform(get("/").with(user(userDetails)))
		.andExpect(status().is3xxRedirection())
		.andExpect(view().name("redirect:/ventes"));
	}
	
}
