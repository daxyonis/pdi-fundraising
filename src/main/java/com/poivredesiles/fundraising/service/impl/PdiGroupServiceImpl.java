package com.poivredesiles.fundraising.service.impl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poivredesiles.fundraising.exception.ResourceNotFoundException;
import com.poivredesiles.fundraising.imports.dto.Group;
import com.poivredesiles.fundraising.model.group.PdiCampaign;
import com.poivredesiles.fundraising.model.group.PdiGroup;
import com.poivredesiles.fundraising.model.group.PdiSeller;
import com.poivredesiles.fundraising.repository.group.PdiCampaignRepository;
import com.poivredesiles.fundraising.repository.group.PdiGroupRepository;
import com.poivredesiles.fundraising.repository.group.PdiSellerRepository;
import com.poivredesiles.fundraising.service.PdiGroupService;
import com.poivredesiles.fundraising.service.dto.PdiGroupRecapDTO;
import com.poivredesiles.fundraising.service.mapper.PdiGroupRecapMapper;

@Service
@Transactional
public class PdiGroupServiceImpl implements PdiGroupService {

	private final Logger log = LoggerFactory.getLogger(PdiGroupServiceImpl.class);
			
	@Autowired
	private PdiGroupRepository pdiGroupRepository;
	
	@Autowired
	private PdiCampaignRepository pdiCampaignRepository;
	
	@Autowired
	private PdiSellerRepository pdiSellerRepository;
	
	@Autowired
	private PdiGroupRecapMapper pdiGroupRecapMapper;
	
	@Override
	public void importGroups(List<Group> groups) {
		if(groups != null) {
			log.info("Importing {} groups", groups.size());
			for(Group group : groups) {				
				Optional<PdiGroup> pdiGroup = pdiGroupRepository.findOneByNumber(group.getNumber());
				if(pdiGroup.isPresent()) {
					updateGroup(pdiGroup.get(), group);
				} else {
					// Create new campaign
					PdiGroup newPdiGroup = new PdiGroup();
					newPdiGroup.setCreatedBy("system");					
					updateGroup(newPdiGroup, group);
				}
			}
		}	
	}

	/**
	 * Update a PDI group
	 * @param pdiGroup	entity to be updated
	 * @param group	input data
	 */
	private void updateGroup(PdiGroup pdiGroup, Group group) {
		if(group.valid()) {
			pdiGroup.setNumber(group.getNumber());
			pdiGroup.setName(group.getName());
			pdiGroup.setLeaderNum(group.getLeaderNumber());
			
			updateCampaign(pdiGroup, group.getCampaignNumber());
			pdiGroupRepository.save(pdiGroup);
		} else {
			log.warn("Did not save invalid group: {}", group.toString());
		}
	}

	/**
	 * Update the campaign for a PDI group
	 * @param pdiGroup	PDI group to update
	 * @param campaignNumber the campaign number associated with this group
	 */
	private void updateCampaign(PdiGroup pdiGroup, Long campaignNumber) {
		Optional<PdiCampaign> pdiCampaign =  pdiCampaignRepository.findOneByNumber(campaignNumber);
		if(pdiCampaign.isPresent()) {
			pdiGroup.setPdiCampaign(pdiCampaign.get());
			pdiGroup.setOrderType(pdiCampaign.get().getOrderType());
		} else {
			log.error("Cannot find campaign(number={}) for group(number={})", campaignNumber, pdiGroup.getNumber());
			throw new ResourceNotFoundException(String.format("La campagne numéro %d associée au groupe numéro %d est introuvable", campaignNumber, pdiGroup.getNumber()));
		}		
	}

	@Override
	public PdiGroupRecapDTO getGroupRecap(Long groupId) {
		Optional<PdiGroup> group = pdiGroupRepository.findById(groupId);
		if(group.isEmpty()) {
			throw new ResourceNotFoundException("Group not found !");
		}
		PdiGroupRecapDTO groupRecap = pdiGroupRecapMapper.toDto(group.get());
		try {
			// TRy to find the group leader from leader number
			Optional<PdiSeller> groupLeader = pdiSellerRepository.findOneByNumber(Long.parseLong(group.get().getLeaderNum()));
			groupRecap.setGroupLeaderName(groupLeader.get().getName());
		} catch (NumberFormatException | NoSuchElementException e) {
			log.error("Cannot find a seller corresponding to group leader number=" + group.get().getLeaderNum(), e);			
		}
		return groupRecap;
	}

}
