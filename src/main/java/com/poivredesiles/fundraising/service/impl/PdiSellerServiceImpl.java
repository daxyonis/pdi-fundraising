package com.poivredesiles.fundraising.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poivredesiles.fundraising.exception.ResourceNotFoundException;
import com.poivredesiles.fundraising.imports.dto.GroupLink;
import com.poivredesiles.fundraising.imports.dto.Seller;
import com.poivredesiles.fundraising.model.group.PdiGroup;
import com.poivredesiles.fundraising.model.group.PdiSeller;
import com.poivredesiles.fundraising.model.order.OrderType;
import com.poivredesiles.fundraising.model.product.PdiProduct;
import com.poivredesiles.fundraising.model.user.MyUserDetails;
import com.poivredesiles.fundraising.model.user.Role;
import com.poivredesiles.fundraising.model.user.RoleEnum;
import com.poivredesiles.fundraising.model.user.User;
import com.poivredesiles.fundraising.repository.group.PdiGroupRepository;
import com.poivredesiles.fundraising.repository.group.PdiSellerRepository;
import com.poivredesiles.fundraising.service.PdiSellerService;
import com.poivredesiles.fundraising.service.UserService;
import com.poivredesiles.fundraising.service.dto.PdiProductDTO;
import com.poivredesiles.fundraising.service.mapper.PdiProductMapper;

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
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PdiProductMapper pdiProductMapper;
	
	private static Comparator<PdiProduct> compareByCategory = (p1, p2) -> p1.getCategory().getNumber().compareTo(p2.getCategory().getNumber());
	private static Comparator<PdiProduct> compareByName = Comparator.comparing(PdiProduct::getNameFr);
	private static Comparator<PdiProduct> compareByCategoryAndName = compareByCategory.thenComparing(compareByName);

	@Override
	public void importSellers(List<Seller> sellers) {
		if (sellers != null) {
			log.info("Importing {} sellers", sellers.size());
			for (Seller seller : sellers) {
				// Get the PdiSeller
				Optional<PdiSeller> pdiSeller = pdiSellerRepository.findOneByNumber(seller.getNumber());
				PdiSeller currentPdiSeller = null;
				if (pdiSeller.isPresent()) {
					updateSeller(pdiSeller.get(), seller);
					currentPdiSeller = pdiSeller.get();
				} else {
					// Create new PdiSeller
					PdiSeller newPdiSeller = new PdiSeller();
					newPdiSeller.setCreatedBy("system");
					updateSeller(newPdiSeller, seller);
					currentPdiSeller = newPdiSeller;
				}
				updateCampaignLeadership(currentPdiSeller);
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
				sellerAsUser.addRole(RoleEnum.ROLE_GROUP_LEADER);
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

	/**
	 * Check if the seller has the campaign leadership and if so, add the role
	 * @param currentPdiSeller
	 */
	private void updateCampaignLeadership(PdiSeller currentPdiSeller) {
		Long sellerNumber = currentPdiSeller.getNumber();
		try{
			String leaderNum = currentPdiSeller.getPdiGroup().getPdiCampaign().getLeaderNum();
			if(Long.valueOf(leaderNum).equals(sellerNumber) && !currentPdiSeller.getMe().getRoles().contains(new Role(RoleEnum.ROLE_CAMPAIGN_LEADER))) {
				currentPdiSeller.getMe().addRole(RoleEnum.ROLE_CAMPAIGN_LEADER);
				pdiSellerRepository.save(currentPdiSeller);
			}
		} catch (NullPointerException e) {
			log.error("Did not get the campaign leader number for seller", e);
		}
	}

	@Override
	public PdiSeller save(PdiSeller pdiSeller) {
		return pdiSellerRepository.save(pdiSeller);
	}

	@Override
	public List<PdiProductDTO> getProductsForUser(MyUserDetails userDetails) {
		Optional<User> user = userService.findUserById(userDetails.getUserId());
		if(user.isPresent()) {
			Optional<PdiSeller> pdiSeller = pdiSellerRepository.findOneByMeOrBuyer(user.get(), user.get());
			if(pdiSeller.isPresent()) {
				// Get the products for the order type of the group that the seller is part of
				PdiGroup pdiGroup = pdiSeller.get().getPdiGroup();
				OrderType orderType = pdiGroup.getOrderType();
				Set<PdiProduct> pdiProducts = orderType.getPdiProducts();					
				return pdiProductMapper.toDto(pdiProducts.stream().sorted(compareByCategoryAndName).collect(Collectors.toList()));
			} else {
				throw new ResourceNotFoundException("Seller not found !");
			}
		} else {
			throw new ResourceNotFoundException("Unknown user !");
		}		
	}
}
