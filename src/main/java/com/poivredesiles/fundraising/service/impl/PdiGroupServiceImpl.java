package com.poivredesiles.fundraising.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poivredesiles.fundraising.exception.ResourceNotFoundException;
import com.poivredesiles.fundraising.imports.dto.Group;
import com.poivredesiles.fundraising.model.PdiCampaign;
import com.poivredesiles.fundraising.model.PdiGroup;
import com.poivredesiles.fundraising.repository.PdiCampaignRepository;
import com.poivredesiles.fundraising.repository.PdiGroupRepository;
import com.poivredesiles.fundraising.service.PdiGroupService;

@Service
@Transactional
public class PdiGroupServiceImpl implements PdiGroupService {

	private final Logger log = LoggerFactory.getLogger(PdiGroupServiceImpl.class);
			
	@Autowired
	private PdiGroupRepository pdiGroupRepository;
	
	@Autowired
	private PdiCampaignRepository pdiCampaignRepository;
	
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
		group.validate();
		pdiGroup.setNumber(group.getNumber());
		pdiGroup.setName(group.getName());
		pdiGroup.setLeaderNum(group.getLeaderNumber());
		
		updateCampaign(pdiGroup, group.getCampaignNumber());
		pdiGroupRepository.save(pdiGroup);
	}

	/**
	 * Update the campaign for a PDI group
	 * @param pdiGroup	PDI group to update
	 * @param campaignNumber the campaign number associated with this group
	 */
	private void updateCampaign(PdiGroup pdiGroup, String campaignNumber) {
		Optional<PdiCampaign> pdiCampaign =  pdiCampaignRepository.findOneByNumber(campaignNumber);
		if(pdiCampaign.isPresent()) {
			pdiGroup.setPdiCampaign(pdiCampaign.get());
			pdiGroup.setOrderType(pdiCampaign.get().getOrderType());
		} else {
			log.error("Cannot find campaign(number={}) for group(number={})", campaignNumber, pdiGroup.getNumber());
			throw new ResourceNotFoundException("Group has no associated Campaign.");
		}		
	}

}
