package com.poivredesiles.fundraising.service.impl;

import static com.poivredesiles.fundraising.imports.ImportsUtils.convertToLocalDate;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poivredesiles.fundraising.imports.dto.Campaign;
import com.poivredesiles.fundraising.model.group.PdiCampaign;
import com.poivredesiles.fundraising.model.order.OrderType;
import com.poivredesiles.fundraising.repository.group.PdiCampaignRepository;
import com.poivredesiles.fundraising.repository.order.OrderTypeRepository;
import com.poivredesiles.fundraising.service.PdiCampaignService;

@Service
@Transactional
public class PdiCampaignServiceImpl implements PdiCampaignService {

	private final Logger log = LoggerFactory.getLogger(PdiCampaignServiceImpl.class);

	@Autowired
	private PdiCampaignRepository pdiCampaignRepository;

	@Autowired
	private OrderTypeRepository orderTypeRepository;

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

}
