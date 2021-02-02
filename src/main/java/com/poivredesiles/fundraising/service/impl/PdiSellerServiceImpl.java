package com.poivredesiles.fundraising.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poivredesiles.fundraising.exception.ResourceNotFoundException;
import com.poivredesiles.fundraising.imports.dto.GroupLink;
import com.poivredesiles.fundraising.imports.dto.Seller;
import com.poivredesiles.fundraising.model.PdiGroup;
import com.poivredesiles.fundraising.model.PdiSeller;
import com.poivredesiles.fundraising.model.user.RoleEnum;
import com.poivredesiles.fundraising.model.user.User;
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
	
	@Autowired
	private PasswordEncoder passwordEncoder;

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
			updateUsers(pdiSeller, seller);			
			pdiSellerRepository.save(pdiSeller);
		} else {
			log.error("Did not save invalid seller: {}", seller.toString());
		}
	}

	/**
	 * Create/Update the seller users : the "me" user (i.e. the seller) and the "buyer" user
	 * @param pdiSeller
	 * @param seller
	 */
	private void updateUsers(PdiSeller pdiSeller, Seller seller) {
		if(seller.hasUserInfo()) {
			// First set seller's user
			User sellerAsUser = pdiSeller.getMe();			
			if(sellerAsUser == null) {
				sellerAsUser = new User();
				sellerAsUser.setCreatedBy("system");
			}	
			// Seller can buy too
			sellerAsUser.clearRoles();
			sellerAsUser.addRole(RoleEnum.BUYER);
			sellerAsUser.addRole(RoleEnum.SELLER);
			if(seller.getAuthorization().equalsIgnoreCase("Responsable")) {
				sellerAsUser.addRole(RoleEnum.LEAD);
			}			
			String[] firstAndLastName = seller.getName().split(" ");
			if(firstAndLastName.length > 1) {
				sellerAsUser.setFirstname(firstAndLastName[0]);
				sellerAsUser.setLastname(firstAndLastName[1]);
			} else {
				sellerAsUser.setFirstname(seller.getName());
				sellerAsUser.setLastname("");
			}
			sellerAsUser.setUsername(seller.getCampaignCode());
			sellerAsUser.setPassword(passwordEncoder.encode(seller.getPassword()));
//			sellerAsUser.setLanguage(null); ??? TODO
			pdiSeller.setMe(sellerAsUser);		
			
			// Then set the seller's buyer user = the same for all users
			User buyer = pdiSeller.getBuyer();
			if(buyer == null) {
				buyer = new User();
				buyer.setCreatedBy("system");
			}		
			buyer.clearRoles();
			buyer.addRole(RoleEnum.BUYER);
			// Buyer has username equal to password
			buyer.setUsername(seller.getBuyerCode());
			buyer.setPassword(passwordEncoder.encode(seller.getBuyerCode()));
			pdiSeller.setBuyer(buyer);
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
