package com.poivredesiles.fundraising.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Locale;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.poivredesiles.fundraising.controller.rest.PdiCampaignController;
import com.poivredesiles.fundraising.service.PdiCampaignService;
import com.poivredesiles.fundraising.service.dto.PdiCampaignDTO;

import lombok.extern.slf4j.Slf4j;

/**
 * Here we are testing only the MVC controller layer
 * An application context is created with only the specified controller
 * (here PdiCampaignController). Spring Security configuration is taken
 * into account automatically (the SecurityConfig.class)
*
 * @author evita
 *
 */
@Slf4j
@WebMvcTest(PdiCampaignController.class)
public class PdiCampaignControllerTest {
	
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PdiCampaignService pdiCampaignService;		

	@Test
	public void shouldNotAccessCampaignsWhenAnonymous() throws Exception {
		log.info("=====> Try to access campaigns when anonymous...");
		this.mockMvc.perform(
						get("/api/campaign/"))
					.andExpect(status().isFound())
					.andExpect(redirectedUrlPattern("**/login"));
	}
	
	@Test
	public void canAccessCampaignsIfAuthenticated() throws Exception {
		log.info("=====> Try to access campaigns when authenticated...");
		when(pdiCampaignService.findAll(false, false)).thenReturn(new ArrayList<>());
		this.mockMvc.perform(
						get("/api/campaign/")
						.with(user("bidon").roles("BUYER")))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON));					
	}
	
	@Test
	public void shouldNotCloseCampaignWhenRoleNotAdminOrCampaignLeader() throws Exception {
		log.info("=====> Try to close campaign when authenticated but not admin or campaign leader...");
		this.mockMvc.perform(
					post("/api/campaign/5/close")
					.with(csrf())
					.with(user("bidon").roles("BUYER", "SELLER", "GROUP_LEADER"))
				).andExpect(status().isForbidden());
	}
	
	@Test 
	public void canCloseCampaignIfAdminOrCampaignLeader() throws Exception {		
		var closedCampaign = new PdiCampaignDTO();
		closedCampaign.setId(5L);
		var role = RandomUtils.nextInt(0, 2) == 0 ? "ADMIN" : "CAMPAIGN_LEADER";
		log.info("=====> Try to close campaign when role = {}...", role);
		when(pdiCampaignService.close(5L, Locale.FRENCH)).thenReturn(closedCampaign);
		this.mockMvc.perform(
				post("/api/campaign/5/close")
				.with(csrf())
				.with(user("bidon").roles(role))
			).andExpect(status().isOk());
	}
	
	@Test
	public void shouldNotBlockCampaignIfNotAdmin() throws Exception {
		log.info("=====> Try to block campaign when not admin...");
		this.mockMvc.perform(
				post("/api/campaign/5/block")
				.with(csrf())
				.with(user("bidon").roles("BUYER", "SELLER", "GROUP_LEADER","CAMPAIGN_LEADER"))
			).andExpect(status().isForbidden());
	}
	
	@Test
	public void canBlockCampaignIfAdmin() throws Exception {
		log.info("=====> Try to block campaign when admin...");
		var blockedCampaign = new PdiCampaignDTO();
		blockedCampaign.setId(5L);
		when(pdiCampaignService.block(5L)).thenReturn(blockedCampaign);
		this.mockMvc.perform(
				post("/api/campaign/5/block")
				.with(csrf())
				.with(user("bidon").roles("ADMIN"))
			).andExpect(status().isOk());
	}
	
	
	@Test
	public void shouldNotDownloadHeadersIfNotAdmin() throws Exception {
		log.info("=====> Try to download campaign headers when not admin...");
		this.mockMvc.perform(
				get("/api/campaign/5/download/headers?filename=blah")				
				.with(user("bidon").roles("BUYER", "SELLER", "GROUP_LEADER","CAMPAIGN_LEADER"))
			).andExpect(status().isForbidden());
	}
	
	@Test
	public void canDownloadHeadersIfAdmin() throws Exception {
		log.info("=====> Try to download campaign headers when admin...");
		this.mockMvc.perform(
				get("/api/campaign/5/download/headers?filename=blah")				
				.with(user("bidon").roles("ADMIN"))
			).andExpect(status().isOk());
	}
	
	@Test
	public void shouldNotDownloadDetailsIfNotAdmin() throws Exception {
		log.info("=====> Try to download campaign details when not admin...");
		this.mockMvc.perform(
				get("/api/campaign/5/download/details?filename=blah")				
				.with(user("bidon").roles("BUYER", "SELLER", "GROUP_LEADER","CAMPAIGN_LEADER"))
			).andExpect(status().isForbidden());
	}
	
	@Test
	public void canDownloadDetailsIfAdmin() throws Exception {
		log.info("=====> Try to download campaign details when admin...");
		this.mockMvc.perform(
				get("/api/campaign/5/download/details?filename=blah")				
				.with(user("bidon").roles("ADMIN"))
			).andExpect(status().isOk());
	}
}
