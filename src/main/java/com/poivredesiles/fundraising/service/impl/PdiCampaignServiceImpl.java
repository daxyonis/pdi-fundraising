package com.poivredesiles.fundraising.service.impl;

import static com.poivredesiles.fundraising.imports.ImportsUtils.convertToLocalDate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poivredesiles.fundraising.exception.ResourceNotFoundException;
import com.poivredesiles.fundraising.imports.dto.Campaign;
import com.poivredesiles.fundraising.model.group.PdiCampaign;
import com.poivredesiles.fundraising.model.group.PdiGroup;
import com.poivredesiles.fundraising.model.group.PdiSeller;
import com.poivredesiles.fundraising.model.order.OrderType;
import com.poivredesiles.fundraising.model.user.Role;
import com.poivredesiles.fundraising.model.user.RoleEnum;
import com.poivredesiles.fundraising.model.user.User;
import com.poivredesiles.fundraising.repository.group.PdiCampaignRepository;
import com.poivredesiles.fundraising.repository.group.PdiSellerRepository;
import com.poivredesiles.fundraising.repository.order.OrderTypeRepository;
import com.poivredesiles.fundraising.resource.ExportFileNames;
import com.poivredesiles.fundraising.service.MailService;
import com.poivredesiles.fundraising.service.PdiCampaignService;
import com.poivredesiles.fundraising.service.PdiSellerService;
import com.poivredesiles.fundraising.service.dto.PdiCampaignDTO;
import com.poivredesiles.fundraising.service.dto.PdiCampaignRecapDTO;
import com.poivredesiles.fundraising.service.mapper.PdiCampaignMapper;
import com.poivredesiles.fundraising.service.mapper.PdiCampaignRecapMapper;

@Service
@Transactional
public class PdiCampaignServiceImpl implements PdiCampaignService {

	private final Logger log = LoggerFactory.getLogger(PdiCampaignServiceImpl.class);

	@Autowired
	private PdiCampaignRepository pdiCampaignRepository;

	@Autowired
	private OrderTypeRepository orderTypeRepository;
	
	@Autowired
	private PdiCampaignMapper pdiCampaignMapper;
	
	@Autowired
	private PdiSellerService pdiSellerService;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private PdiCampaignRecapMapper pdiCampaignRecapMapper;
	
	@Autowired
	private PdiSellerRepository pdiSellerRepository;

	@Override
	public void importCampaigns(List<Campaign> campaigns) {
		if (campaigns != null) {
			log.info("Importing {} campaigns", campaigns.size());
			for (Campaign campaign : campaigns) {
				Optional<PdiCampaign> pdiCampaign = pdiCampaignRepository.findOneByNumber(campaign.getNumber());
				if (pdiCampaign.isPresent()) {
					updateCampaign(pdiCampaign.get(), campaign);
				} else {
					// Create new campaign
					PdiCampaign newPdiCampaign = new PdiCampaign();
					newPdiCampaign.setCreatedBy("system");
					updateCampaign(newPdiCampaign, campaign);
				}
			}
		}
	}

	/**
	 * Update the PDI campaign
	 * 
	 * @param pdiCampaign PDI campaign to be updated/created
	 * @param campaign
	 */
	private void updateCampaign(PdiCampaign pdiCampaign, Campaign campaign) {
		// First validate the input data is valid
		if (campaign.valid()) {
			pdiCampaign.setNumber(campaign.getNumber());
			pdiCampaign.setProject(campaign.getProject());
			pdiCampaign.setBlocked(campaign.isBlocked());
			pdiCampaign.setDueDate(convertToLocalDate(campaign.getDueDate()));
			pdiCampaign.setLeaderNum(campaign.getLeaderNumber());
			pdiCampaign.setLeaderEmail(campaign.getLeaderEmail());
			pdiCampaign.setOrderTypeNum(campaign.getNumTypeBC());
			pdiCampaign.setOrganizationNum(campaign.getOrganizationNumber());
			pdiCampaign.setOrganizationName(campaign.getOrganizationName());

			updateOrderType(pdiCampaign);
			pdiCampaignRepository.save(pdiCampaign);
		} else {
			log.warn("Did not save invalid campaign: {}", campaign.toString());
		}
	}

	private void updateOrderType(PdiCampaign pdiCampaign) {
		Optional<OrderType> orderType = orderTypeRepository.findByNumber(pdiCampaign.getOrderTypeNum());
		if (orderType.isPresent()) {
			pdiCampaign.setOrderType(orderType.get());
		} else {
			log.warn("Did not find orderType for orderTypeNum={}", pdiCampaign.getOrderTypeNum());			
		}
	}

	@Override
	public boolean thereAreActiveCampaigns() {
		return (pdiCampaignRepository.countByBlockedFalse() > 0);
	}

	@Override
	public PdiCampaignDTO close(Long id) {
		Optional<PdiCampaign> campaign = pdiCampaignRepository.findById(id);
		if(campaign.isPresent()) {
			PdiCampaign pdiCampaign = campaign.get(); 
			disableCampaignUsers(pdiCampaign, false);
			sendEmailToLeader(pdiCampaign);
			pdiCampaign.setClosed(true);
			pdiCampaign.setClosedDate(LocalDate.now());
			pdiCampaign = pdiCampaignRepository.save(pdiCampaign);
			return pdiCampaignMapper.toDto(pdiCampaign); 
		} else {			
			throw new ResourceNotFoundException(String.format("Campagne avec id %d introuvable.", id));
		}
	}

	private void disableCampaignUsers(PdiCampaign pdiCampaign, boolean disableAll) {
		Role campaignLeader = new Role(RoleEnum.ROLE_CAMPAIGN_LEADER);
		for(PdiGroup pdiGroup : pdiCampaign.getPdiGroups()) {
			for(PdiSeller pdiSeller : pdiGroup.getPdiSellers()) {
				User me = pdiSeller.getMe();
				// Disable this user if he is not null AND (we disable all OR this user is not a campaign leader) 
				boolean disableMe = (me!= null) && (disableAll || !me.getRoles().contains(campaignLeader)); 
				if(disableMe) {																		
					pdiSeller.getMe().setDisabled(true);					
				}
				// Always disable the buyer
				if(pdiSeller.getBuyer() != null) {
					pdiSeller.getBuyer().setDisabled(true);
				}
				pdiSeller = pdiSellerService.save(pdiSeller);
			}
		}		
	}

	/**
	 * Send an email with the campaign overview
	 * to the campaign leader (if specified in PDI campaign)
	 * @param pdiCampaign
	 */
	private void sendEmailToLeader(PdiCampaign pdiCampaign) {
		if(pdiCampaign.getLeaderEmail() != null && !pdiCampaign.getLeaderEmail().isBlank()) {
			PdiCampaignRecapDTO campaignRecap = pdiCampaignRecapMapper.toDto(pdiCampaign);
			// Set the leader name as it is not in the entity
			Optional<PdiSeller> leader = pdiCampaign.getPdiGroups().stream()
													.flatMap(g -> g.getPdiSellers().stream())
													.filter(s -> s.getMe().hasRole(RoleEnum.ROLE_CAMPAIGN_LEADER))
													.findFirst();
			if(leader.isPresent()) {
				campaignRecap.setLeaderName(leader.get().getName());
				campaignRecap.setEmailTo(pdiCampaign.getLeaderEmail());
				mailService.sendCampaignRecapEmail(campaignRecap);
			} else {
				log.warn("Could not send email : leader not found for campaign {}", pdiCampaign.getId());
			}			
		} else {
			log.warn("No leader email specified for campaign {}", pdiCampaign.getId());
		}
	}

	@Override
	public List<PdiCampaignDTO> findAll(Boolean active, Boolean blocked) {
		List<PdiCampaign> pdiCampaigns;
		if (active != null) {
			if (blocked == null) {
				pdiCampaigns = pdiCampaignRepository.findAllByClosed(!active);
			} else {
				pdiCampaigns = pdiCampaignRepository.findAllByClosedAndBlocked(!active, blocked);
			}
		} else {			
			throw new UnsupportedOperationException("This findAll not implemented !");
		}
		
		return pdiCampaignMapper.toDto(pdiCampaigns);
	}

	@Override
	public PdiCampaignDTO block(Long id) {
		Optional<PdiCampaign> campaign = pdiCampaignRepository.findById(id);
		if(campaign.isPresent()) {
			PdiCampaign pdiCampaign = campaign.get(); 
			disableCampaignUsers(pdiCampaign, true);			
			pdiCampaign.setBlocked(true);
			pdiCampaign.setBlockedDate(LocalDate.now());
			pdiCampaign = pdiCampaignRepository.save(pdiCampaign);
			return pdiCampaignMapper.toDto(pdiCampaign); 
		} else {
			throw new ResourceNotFoundException(String.format("Campagne avec id %d introuvable.", id));
		}
	}

	@Override
	public PdiCampaignDTO export(Long id, ExportFileNames exportFileNames) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PdiCampaignRecapDTO getCampaignRecapForLeader(Long userId) {
		// Get the seller(=leader) number
		Optional<PdiSeller> pdiSeller = pdiSellerRepository.findByMe_id(userId);
		if(pdiSeller.isEmpty()) {
			throw new ResourceNotFoundException("Invalid user id");
		}
		// Then find the campaign for this leader
		Optional<PdiCampaign> pdiCampaign = pdiCampaignRepository.findByLeaderNum(pdiSeller.get().getNumber().toString());
		if(pdiCampaign.isEmpty()) {
			throw new ResourceNotFoundException("Campaign not found!");			
		}
		// return the recap DTO
		PdiCampaignRecapDTO campaignRecap = pdiCampaignRecapMapper.toDto(pdiCampaign.get());
		// Set the leader name as it is not in the entity
		campaignRecap.setLeaderName(pdiSeller.get().getName());
		return campaignRecap;
	}
	
	
	
}
