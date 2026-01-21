package com.poivredesiles.fundraising.controller;

import com.poivredesiles.fundraising.config.SecurityConfig;
import com.poivredesiles.fundraising.config.properties.ApplicationProperties;
import com.poivredesiles.fundraising.filter.MaintenanceModeFilter;
import com.poivredesiles.fundraising.imports.CsvImportService;
import com.poivredesiles.fundraising.model.user.MyUserDetails;
import com.poivredesiles.fundraising.model.user.Role;
import com.poivredesiles.fundraising.model.user.RoleEnum;
import com.poivredesiles.fundraising.service.PdiCampaignService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@Slf4j
@WebMvcTest(MainController.class)
@Import({SecurityConfig.class, MaintenanceModeFilter.class, ApplicationProperties.class})
@ActiveProfiles("test")
public class MainControllerTest extends BaseControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
	private CsvImportService csvImportService;
	
	@MockitoBean
	private PdiCampaignService pdiCampaignService;
	
	@Test	
	public void testHomePageGivenUserIsBuyer() throws Exception {
		log.info("Test what is home page for buyer");		
		MyUserDetails userDetails = new MyUserDetails(buyer);
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

	@Test	
	public void testHomePageGivenUserIsGroupLeader() throws Exception {
		log.info("Test what is home page for group leader : ONE group");
		user.setRoles(Set.of(new Role(RoleEnum.ROLE_SELLER), new Role(RoleEnum.ROLE_GROUP_LEADER)));
		MyUserDetails userDetails = new MyUserDetails(user);
		seller.setNumGroups(1);
		seller.setPdiGroupId(11L);
		when(pdiSellerService.getSellerForUser(userDetails)).thenReturn(seller);		
		
		this.mockMvc
		.perform(get("/").with(user(userDetails)))
		.andExpect(status().is3xxRedirection())
		.andExpect(view().name("redirect:/synthese/groupe/11"));
		
		log.info("Test what is home page for group leader : MULTI groups");
		seller.setNumGroups(4);
		
		this.mockMvc
		.perform(get("/").with(user(userDetails)))
		.andExpect(status().is3xxRedirection())
		.andExpect(view().name("redirect:/synthese/groupes"));
	}
	
	@Test
	public void testHomePageGivenUserIsCampaignLeader() throws Exception {
		log.info("Test what is home page for campaign leader");
		user.setRoles(Set.of(new Role(RoleEnum.ROLE_SELLER), new Role(RoleEnum.ROLE_CAMPAIGN_LEADER)));
		MyUserDetails userDetails = new MyUserDetails(user);
		when(pdiSellerService.getSellerForUser(userDetails)).thenReturn(seller);
		
		this.mockMvc
		.perform(get("/").with(user(userDetails)))
		.andExpect(status().is3xxRedirection())
		.andExpect(view().name("redirect:/synthese"));
	}
	
	@Test
	public void testHomePageGivenUserIsAdmin() throws Exception {
		log.info("Test what is home page for Admin");		
		user.setRoles(Set.of(new Role(RoleEnum.ROLE_ADMIN)));
		MyUserDetails userDetails = new MyUserDetails(user);
		
		this.mockMvc
		.perform(get("/").with(user(userDetails)))
		.andExpect(status().is3xxRedirection())
		.andExpect(view().name("redirect:/admin"));
	}
}
