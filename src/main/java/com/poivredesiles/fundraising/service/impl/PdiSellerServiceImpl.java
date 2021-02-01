package com.poivredesiles.fundraising.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poivredesiles.fundraising.exception.ResourceNotFoundException;
import com.poivredesiles.fundraising.imports.dto.GroupLink;
import com.poivredesiles.fundraising.imports.dto.Seller;
import com.poivredesiles.fundraising.model.PdiGroup;
import com.poivredesiles.fundraising.model.PdiSeller;
import com.poivredesiles.fundraising.repository.PdiGroupRepository;
import com.poivredesiles.fundraising.repository.PdiSellerRepository;
import com.poivredesiles.fundraising.service.PdiSellerService;

@Service
@Transactional
public class PdiSellerServiceImpl implements PdiSellerService {

	private final Logger log = LoggerFactory.getLogger(PdiSellerServiceImpl.class);

	@Autowired
	private PdiSellerRepository pdiSellerRepository;

	@Autowired
	private PdiGroupRepository pdiGroupRepository;

	@Override
	public void importSellers(List<Seller> sellers, List<GroupLink> groupLinks) {
		if (sellers != null) {
			log.info("Importing {} sellers", sellers.size());
			for (Seller seller : sellers) {
				// Get the group link for this seller
				Optional<GroupLink> groupLinkForSeller = groupLinks.stream()
						.filter(gl -> gl.getSellerNumber().equals(seller.getNumber())).findFirst();
				if (groupLinkForSeller.isPresent()) {
					// Then get the PdiSeller
					Optional<PdiSeller> pdiSeller = pdiSellerRepository.findOneByNumber(seller.getNumber());
					if (pdiSeller.isPresent()) {
						updateSeller(pdiSeller.get(), seller, groupLinkForSeller.get());
					} else {
						// Create new PdiSeller
						PdiSeller newPdiSeller = new PdiSeller();
						newPdiSeller.setCreatedBy("system");
						updateSeller(newPdiSeller, seller, groupLinkForSeller.get());
					}
				} else {
					// This seller will not be saved
					log.error("Group Link not found for seller(number={})", seller.getNumber());
					// throw new PdiImportDataException("Cannot link Seller to Group");
				}
			}
		}

	}

	/**
	 * Update a PDI seller
	 * 
	 * @param pdiSeller the entity to update
	 * @param seller    the input seller data
	 * @param groupLink the input group link for this seller
	 */
	private void updateSeller(PdiSeller pdiSeller, Seller seller, GroupLink groupLink) {
		if (seller.valid()) {
			pdiSeller.setNumber(seller.getNumber());
			pdiSeller.setName(seller.getName());
			updateSellerGroup(pdiSeller, groupLink);
			pdiSellerRepository.save(pdiSeller);
		} else {
			log.error("Did not save invalid seller: {}", seller.toString());
		}
	}

	/**
	 * Update the group for this seller
	 * 
	 * @param pdiSeller entity to update
	 * @param groupLink input group link data that defines to what group this seller
	 *                  belongs
	 */
	private void updateSellerGroup(PdiSeller pdiSeller, GroupLink groupLink) {
		Optional<PdiGroup> pdiGroup = pdiGroupRepository.findOneByNumber(groupLink.getGroupNumber());
		if (pdiGroup.isPresent()) {
			pdiSeller.setPdiGroup(pdiGroup.get());
		} else {
			log.error("No PdiGroup found(number={})", groupLink.getGroupNumber());
			throw new ResourceNotFoundException("PdiGroup not found for PdiSeller.");
		}
	}

}
