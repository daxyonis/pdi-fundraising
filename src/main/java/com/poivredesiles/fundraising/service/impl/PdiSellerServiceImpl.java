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
	public void importSellers(List<Seller> sellers) {
		if (sellers != null) {
			log.info("Importing {} sellers", sellers.size());
			for (Seller seller : sellers) {
				// Get the PdiSeller
				Optional<PdiSeller> pdiSeller = pdiSellerRepository.findOneByNumber(seller.getNumber());
				if (pdiSeller.isPresent()) {
					updateSeller(pdiSeller.get(), seller);
				} else {
					// Create new PdiSeller
					PdiSeller newPdiSeller = new PdiSeller();
					newPdiSeller.setCreatedBy("system");
					updateSeller(newPdiSeller, seller);
				}
			}
		}

	}

	@Override	
	public void linkSellersToGroup(List<GroupLink> groupLinks) {
		if (groupLinks != null) {
			log.info("Importing {} groupLinks", groupLinks.size());
			for (GroupLink groupLink : groupLinks) {
				Optional<PdiSeller> pdiSeller = pdiSellerRepository.findOneByNumber(groupLink.getSellerNumber());
				if (pdiSeller.isPresent()) {
					updateSellerGroup(pdiSeller.get(), groupLink);
				} else {
					log.warn("Did not find seller for groupLink {}", groupLink.toString());
//					throw new ResourceNotFoundException("PdiSeller not found.");
				}
			}
		}
	}

	/**
	 * Update a PDI seller
	 * 
	 * @param pdiSeller the entity to update
	 * @param seller    the input seller data
	 */
	private void updateSeller(PdiSeller pdiSeller, Seller seller) {
		if (seller.valid()) {
			pdiSeller.setNumber(seller.getNumber());
			pdiSeller.setName(seller.getName());			
			updateUsers(pdiSeller, seller);
			pdiSellerRepository.save(pdiSeller);
		} else {
			log.warn("Did not save invalid seller: {}", seller.toString());
		}
	}

	/**
	 * Create/Update the seller users : the "me" user (i.e. the seller) and the
	 * "buyer" user
	 * 
	 * @param pdiSeller
	 * @param seller
	 */
	private void updateUsers(PdiSeller pdiSeller, Seller seller) {
		if (seller.hasUserInfo()) {
			// First set seller's user
			User sellerAsUser = pdiSeller.getMe();
			if (sellerAsUser == null) {
				sellerAsUser = new User();
				sellerAsUser.setCreatedBy("system");
			}
			// Seller can buy too
			sellerAsUser.clearRoles();
			sellerAsUser.addRole(RoleEnum.ROLE_BUYER);
			sellerAsUser.addRole(RoleEnum.ROLE_SELLER);
			if (seller.getAuthorization().equalsIgnoreCase("Responsable")) {
				sellerAsUser.addRole(RoleEnum.ROLE_SUPERVISOR);
			}
			String[] firstAndLastName = seller.getName().split(" ");
			if (firstAndLastName.length > 1) {
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
			if (buyer == null) {
				buyer = new User();
				buyer.setCreatedBy("system");
			}
			buyer.clearRoles();
			buyer.addRole(RoleEnum.ROLE_BUYER);
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
