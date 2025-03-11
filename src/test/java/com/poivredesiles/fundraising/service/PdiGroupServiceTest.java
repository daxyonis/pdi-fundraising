package com.poivredesiles.fundraising.service;

import com.poivredesiles.fundraising.model.group.PdiCampaign;
import com.poivredesiles.fundraising.model.group.PdiGroup;
import com.poivredesiles.fundraising.model.group.PdiSeller;
import com.poivredesiles.fundraising.model.user.MyUserDetails;
import com.poivredesiles.fundraising.model.user.Role;
import com.poivredesiles.fundraising.model.user.RoleEnum;
import com.poivredesiles.fundraising.model.user.User;
import com.poivredesiles.fundraising.repository.group.PdiCampaignRepository;
import com.poivredesiles.fundraising.repository.group.PdiGroupRepository;
import com.poivredesiles.fundraising.service.impl.PdiGroupServiceImpl;
import com.poivredesiles.fundraising.service.mapper.PdiGroupRecapMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PdiGroupServiceTest {

	@Mock
	private PdiGroupRepository pdiGroupRepository;
	
	@Mock
	private PdiCampaignRepository pdiCampaignRepository;
	
	@Mock
	private PdiGroupRecapMapper pdiGroupRecapMapper;
	
	@InjectMocks
	private PdiGroupServiceImpl pdiGroupService;
	
	private static User user = new User();	
	
	@BeforeAll
	public static void setup() {
		user.setFirstname("Eva");
		user.setLastname("Maciejko");
		user.setId(88L);
		user.setLanguage("FR");
		user.setUsername("emaciejko");
		user.setPassword("aaabbbccc");
	}
	
	private PdiCampaign getCampaign() {
		PdiCampaign campaign = new PdiCampaign();			
		return campaign;
	}
	
	private PdiGroup getGroup1() {
		PdiGroup group = new PdiGroup();
		group.setId(1L);
		group.setPdiCampaign(getCampaign());
		group.getPdiCampaign().setPdiGroups(Set.of(group));
		PdiSeller seller = new PdiSeller();
		seller.setMe(user);
		group.setPdiSellers(Set.of(seller));
		return group;
	}
	
	private PdiGroup getGroup2() {
		PdiGroup group2 = new PdiGroup();
		group2.setId(2L);
		User user = new User();
		user.setId(77L);
		user.setLanguage("FR");
		user.setUsername("toto");
		user.setPassword("xxyyzz");
		PdiSeller seller = new PdiSeller();
		seller.setMe(user);
		group2.setPdiSellers(Set.of(seller));
		return group2;
	}
	
	@Test
	public void campaignLeaderShouldHaveAccess() {	
		user.setRoles(Set.of(new Role(RoleEnum.ROLE_CAMPAIGN_LEADER)));
		MyUserDetails currentUser = new MyUserDetails(user);
		PdiGroup group1 = getGroup1();
		PdiGroup group2 = getGroup2();
		group2.setPdiCampaign(group1.getPdiCampaign());
		group1.getPdiCampaign().setPdiGroups(Set.of(group1, group2));				
		when(pdiGroupRepository.findById(group2.getId())).thenReturn(Optional.of(group2));
		
		// Even if user is in group1, since he is campaign leader should have access to group2
		var hasAccess = pdiGroupService.hasAccess(currentUser, group2.getId());
		assertTrue(hasAccess);
	}
	
	
	@Test
	public void sellerOfTheGroupShouldHaveAccess() {
		user.setRoles(Set.of(new Role(RoleEnum.ROLE_SELLER)));
		MyUserDetails currentUser = new MyUserDetails(user);
		PdiGroup group = getGroup1();
		when(pdiGroupRepository.findById(group.getId())).thenReturn(Optional.of(group));
		var hasAccess = pdiGroupService.hasAccess(currentUser, group.getId());
		assertTrue(hasAccess);
	}
	
	@Test
	public void sellerOfAnotherGroupShouldNotHaveAccess() {
		user.setRoles(Set.of(new Role(RoleEnum.ROLE_SELLER)));
		MyUserDetails currentUser = new MyUserDetails(user);
		PdiGroup group = getGroup2();
		when(pdiGroupRepository.findById(group.getId())).thenReturn(Optional.of(group));
		var hasAccess = pdiGroupService.hasAccess(currentUser, group.getId());
		assertFalse(hasAccess);
	}
	
	@Test
	public void groupLeaderShouldHaveAccess() {
		user.setRoles(Set.of(new Role(RoleEnum.ROLE_SELLER)));
		MyUserDetails currentUser = new MyUserDetails(user);
		PdiGroup group = getGroup2();
		PdiSeller seller = new PdiSeller();
		seller.setMe(user);
		group.setGroupLeader(seller);
		when(pdiGroupRepository.findById(group.getId())).thenReturn(Optional.of(group));
		var hasAccess = pdiGroupService.hasAccess(currentUser, group.getId());
		assertTrue(hasAccess);
	}
}
