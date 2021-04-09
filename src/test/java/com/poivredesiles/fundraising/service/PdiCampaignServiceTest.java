package com.poivredesiles.fundraising.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import com.poivredesiles.fundraising.imports.ImportsUtils;
import com.poivredesiles.fundraising.imports.dto.Campaign;
import com.poivredesiles.fundraising.model.group.PdiCampaign;
import com.poivredesiles.fundraising.model.order.OrderType;
import com.poivredesiles.fundraising.repository.group.PdiCampaignRepository;
import com.poivredesiles.fundraising.repository.order.OrderTypeRepository;
import com.poivredesiles.fundraising.service.impl.PdiCampaignServiceImpl;

@ExtendWith(MockitoExtension.class)
public class PdiCampaignServiceTest {

	@Mock
	private PdiCampaignRepository pdiCampaignRepository;
	
	@Mock
	private OrderTypeRepository orderTypeRepository;
	
	@Autowired
	@InjectMocks
	private PdiCampaignServiceImpl pdiCampaignService;
		
	private Campaign campaign;
	
	private PdiCampaign pdiCampaign;
	
	OrderType orderType;
	
	@BeforeEach
	public void setUp() {
		campaign = new Campaign();
		campaign.setBlocked(false);
		campaign.setDueDate(new Date());
		campaign.setLeaderEmail("toto@example.com");
		campaign.setLeaderNumber("12345");
		campaign.setNumber(9999L);
		campaign.setNumTypeBC(123L);
		campaign.setOrganizationName("UNITTEST");
		campaign.setOrganizationNumber("0000");
		campaign.setProject("Unit test");
		
		pdiCampaign = getPdiCampaignFrom(campaign);
		
		orderType = new OrderType();
		orderType.setId(1L);
		orderType.setNumber(campaign.getNumTypeBC());
	}
	
	private PdiCampaign getPdiCampaignFrom(Campaign campaign) {
		PdiCampaign myPdiCampaign = new PdiCampaign();
		myPdiCampaign.setBlocked(false);
		myPdiCampaign.setDueDate(ImportsUtils.convertToLocalDate(campaign.getDueDate()));
		myPdiCampaign.setLeaderEmail(campaign.getLeaderEmail());
		myPdiCampaign.setLeaderNum(campaign.getLeaderNumber());
		myPdiCampaign.setNumber(campaign.getNumber());
		myPdiCampaign.setOrderTypeNum(campaign.getNumTypeBC());
		myPdiCampaign.setOrganizationName(campaign.getOrganizationName());
		myPdiCampaign.setOrganizationNum(campaign.getOrganizationNumber());
		myPdiCampaign.setProject(campaign.getProject());
		return myPdiCampaign;
	}
	
	@AfterEach
	public void tearDown() {
		campaign = null;
		pdiCampaign = null;
		orderType = null; 
	}
	
	private Campaign modifiedCampaign(OrderType orderType) {
		Campaign modifiedCampaign = new Campaign();
		modifiedCampaign.setBlocked(campaign.isBlocked());
		try {
			modifiedCampaign.setDueDate(ImportsUtils.parseDate("27/12/2035", new SimpleDateFormat("dd/M/yyyy")));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		modifiedCampaign.setLeaderEmail("james.bond@example.com");
		modifiedCampaign.setLeaderNumber("007");
		modifiedCampaign.setNumber(campaign.getNumber());
		modifiedCampaign.setNumTypeBC(orderType.getNumber());
		modifiedCampaign.setOrganizationName(campaign.getOrganizationName().toLowerCase());
		modifiedCampaign.setOrganizationNumber(campaign.getOrganizationNumber());
		modifiedCampaign.setProject("New project");
		return modifiedCampaign;
	}
	
	@Test
	void importNewCampaignTest() {
		// Check import works correctly				
		when(pdiCampaignRepository.findOneByNumber(campaign.getNumber())).thenReturn(Optional.ofNullable(null));
		when(orderTypeRepository.findByNumber(campaign.getNumTypeBC())).thenReturn(Optional.of(orderType));
		
		pdiCampaignService.importCampaigns(Arrays.asList(campaign));				
		
		verify(pdiCampaignRepository, times(1)).findOneByNumber(campaign.getNumber());
		verify(pdiCampaignRepository, times(1)).save(pdiCampaign);
		verify(orderTypeRepository, times(1)).findByNumber(campaign.getNumTypeBC());		
	}
	
	@Test
	void importExistingCampaignTest() {
		OrderType modifiedOrderType = new OrderType();
		modifiedOrderType.setId(2L);
		modifiedOrderType.setNumber(500L);
		
		Campaign modifiedCampaign = modifiedCampaign(modifiedOrderType);	
		
		// Check we can import same campaign twice with modifications
		when(pdiCampaignRepository.findOneByNumber(modifiedCampaign.getNumber())).thenReturn(Optional.of(pdiCampaign));
		when(orderTypeRepository.findByNumber(modifiedCampaign.getNumTypeBC())).thenReturn(Optional.of(modifiedOrderType));
					
		pdiCampaignService.importCampaigns(Arrays.asList(modifiedCampaign));
		
		// modify pdiCampaign to match the modifications
		PdiCampaign updatedPdiCampaign = getPdiCampaignFrom(modifiedCampaign);
		
		verify(pdiCampaignRepository, times(1)).findOneByNumber(modifiedCampaign.getNumber());
		verify(pdiCampaignRepository, times(1)).save(updatedPdiCampaign);
		verify(orderTypeRepository, times(1)).findByNumber(modifiedCampaign.getNumTypeBC());	
	}
}
