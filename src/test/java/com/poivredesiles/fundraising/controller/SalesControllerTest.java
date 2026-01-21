package com.poivredesiles.fundraising.controller;

import com.poivredesiles.fundraising.config.SecurityConfig;
import com.poivredesiles.fundraising.config.properties.ApplicationProperties;
import com.poivredesiles.fundraising.filter.MaintenanceModeFilter;
import com.poivredesiles.fundraising.model.user.MyUserDetails;
import com.poivredesiles.fundraising.model.user.Role;
import com.poivredesiles.fundraising.model.user.RoleEnum;
import com.poivredesiles.fundraising.resource.MultiGroupRecap;
import com.poivredesiles.fundraising.service.OrderService;
import com.poivredesiles.fundraising.service.PdiCampaignService;
import com.poivredesiles.fundraising.service.PdiGroupService;
import com.poivredesiles.fundraising.service.dto.OrderHeaderDTO;
import com.poivredesiles.fundraising.service.dto.PdiCampaignRecapDTO;
import com.poivredesiles.fundraising.service.dto.PdiGroupRecapDTO;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WebMvcTest(SalesController.class)
@Import({SecurityConfig.class, MaintenanceModeFilter.class, ApplicationProperties.class})
@ActiveProfiles("test")
public class SalesControllerTest extends BaseControllerTest{

	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
	private PdiCampaignService pdiCampaignService;
	
	@MockitoBean
	private OrderService orderService;
	
	@MockitoBean(name = "pdiGroupService")
	private PdiGroupService pdiGroupService;
	
	
	@Test
	public void shouldNotGetSalesPageIfRoleBuyer() throws Exception {
		log.info("=====> Testing get sales page if buyer");
		user.setRoles(Set.of(new Role(RoleEnum.ROLE_BUYER)));		
		this.mockMvc
			.perform(get("/ventes").with(user(new MyUserDetails(user))))
			.andExpect(status().isForbidden());		
	}
	
	@Test
	public void getSalesPageOkIfSeller() throws Exception {
		log.info("=====> Testing get sales page if seller");
		user.setRoles(Set.of(new Role(RoleEnum.ROLE_SELLER)));		
		MyUserDetails userDetails = new MyUserDetails(user);
		when(pdiSellerService.getSellerForUser(userDetails)).thenReturn(seller);
		when(orderService.getPaidOrdersForSeller(seller)).thenReturn(new ArrayList<OrderHeaderDTO>());
		this.mockMvc
			.perform(get("/ventes").with(user(userDetails)))
			.andExpect(status().isOk())
			.andExpect(view().name("views/sales"))
			.andExpect(model().attribute("orders", Matchers.emptyCollectionOf(OrderHeaderDTO.class)));		
	}
	
	@Test
	public void shouldNotGetSummaryPageIfNotCampaignLeader() throws Exception {
		log.info("=====> Testing get summary page if not campaign leader");
		user.setRoles(Set.of(new Role(RoleEnum.ROLE_BUYER), new Role(RoleEnum.ROLE_SELLER),new Role(RoleEnum.ROLE_GROUP_LEADER)));		
		this.mockMvc
			.perform(get("/synthese").with(user(new MyUserDetails(user))))
			.andExpect(status().isForbidden());		
	}
	
	@Test
	public void getSummaryPageOkIfCampaignLeader() throws Exception {
		log.info("=====> Testing get summary page when campaign leader");
		user.setRoles(Set.of(new Role(RoleEnum.ROLE_CAMPAIGN_LEADER)));
		MyUserDetails userDetails = new MyUserDetails(user);
		when(pdiSellerService.getSellerForUser(userDetails)).thenReturn(seller);
		var recap = new PdiCampaignRecapDTO(2L, 2030L, 444L, "DTO inc", "Vente de garage", "11", "Toto", "0.00$", "0.00$", 3L, 0L, new ArrayList<PdiGroupRecapDTO>(), false, "toto@example.com");		
		when(pdiCampaignService.getCampaignRecapForLeader(user.getId())).thenReturn(recap);
		this.mockMvc
		.perform(get("/synthese").with(user(userDetails)))
		.andExpect(status().isOk())
		.andExpect(view().name("views/summary"))
		.andExpect(model().attribute("campaignRecap",recap));
	}
	
	@Test 
	public void groupLeaderCanAccessHisGroupSummary() throws Exception {
		log.info("=====> Testing group leader can access his group summary page ");
		user.setRoles(Set.of(new Role(RoleEnum.ROLE_GROUP_LEADER)));
		MyUserDetails userDetails = new MyUserDetails(user);
		when(pdiSellerService.getSellerForUser(userDetails)).thenReturn(seller);
		when(pdiGroupService.hasAccess(userDetails,9L)).thenReturn(true);
		var groupRecap = new PdiGroupRecapDTO(9L, "Classe 006", "Stef", 0L, 3L, "0.00$", List.of());
		when(pdiGroupService.getGroupRecap(9L)).thenReturn(groupRecap);
		this.mockMvc
		.perform(get("/synthese/groupe/9").with(user(userDetails)))
		.andExpect(status().isOk())
		.andExpect(view().name("views/summary-group"))
		.andExpect(model().attribute("groupRecap",groupRecap));
	}
	
	@Test
	public void groupLeaderShouldNotAccessAnotherGroupSummary() throws Exception {
		log.info("=====> Testing group leader cannot access another group summary page ");
		user.setRoles(Set.of(new Role(RoleEnum.ROLE_GROUP_LEADER)));
		MyUserDetails userDetails = new MyUserDetails(user);		
		when(pdiGroupService.hasAccess(userDetails,10L)).thenReturn(false);		
		this.mockMvc
		.perform(get("/synthese/groupe/10").with(user(userDetails)))
		.andExpect(status().isForbidden());
	}
	
	@Test
	public void shouldNotAccessMultiGroupSummaryIfNotGroupLeader() throws Exception {
		log.info("=====> Testing non group leader cannot access multi-group summary page ");
		user.setRoles(Set.of(new Role(RoleEnum.ROLE_BUYER), new Role(RoleEnum.ROLE_SELLER), new Role(RoleEnum.ROLE_CAMPAIGN_LEADER), new Role(RoleEnum.ROLE_ADMIN)));
		this.mockMvc
		.perform(get("/synthese/groupe/10").with(user(new MyUserDetails(user))))
		.andExpect(status().isForbidden());	
	}
	
	@Test
	public void groupLeaderCanAccessMultiGroupSummaryPage() throws Exception {
		log.info("=====> Testing group leader can access his multi groups summary page ");
		user.setRoles(Set.of(new Role(RoleEnum.ROLE_GROUP_LEADER)));
		MyUserDetails userDetails = new MyUserDetails(user);
		when(pdiSellerService.getSellerForUser(userDetails)).thenReturn(seller);		
		var multiGroupRecap = new MultiGroupRecap(List.of(), 0L, "0.00$");
		when(pdiGroupService.getMultiGroupRecapForLeader(user.getId())).thenReturn(multiGroupRecap);
		this.mockMvc
		.perform(get("/synthese/groupes").with(user(userDetails)))
		.andExpect(status().isOk())
		.andExpect(view().name("views/summary-group"))
		.andExpect(model().attribute("groupRecap",multiGroupRecap))
		.andExpect(model().attribute("pdiGroupRecaps",multiGroupRecap.getPdiGroupRecaps()));
	}
}
