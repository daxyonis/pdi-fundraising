package com.poivredesiles.fundraising.service.impl;

import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poivredesiles.fundraising.exception.InvalidUsernameException;
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
import com.poivredesiles.fundraising.service.dto.PdiSellerDTO;
import com.poivredesiles.fundraising.service.mapper.PdiProductMapper;
import com.poivredesiles.fundraising.service.mapper.PdiSellerMapper;

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
	
	@Autowired
	private PdiSellerMapper pdiSellerMapper;
	
	@Autowired
	private MessageSource messageSource;

	private static final Collator frCollator = Collator.getInstance(new Locale("fr", "CA"));
	private static final Collator enCollator = Collator.getInstance(new Locale("en", "CA"));
	private static Comparator<PdiProduct> compareByCategory = (p1, p2) -> p1.getCategory().getNumber().compareTo(p2.getCategory().getNumber());
	private static Comparator<PdiProduct> compareByNameFr = (p1, p2) -> frCollator.compare(p1.getNameFr(), p2.getNameFr());
	private static Comparator<PdiProduct> compareByNameEn = (p1, p2) -> enCollator.compare(p1.getNameEn(), p2.getNameEn());
	private static Comparator<PdiProduct> compareByCategoryAndNameFr = compareByCategory.thenComparing(compareByNameFr);
	private static Comparator<PdiProduct> compareByCategoryAndNameEn = compareByCategory.thenComparing(compareByNameEn);

	@Override
	public void importSellers(List<Seller> sellers) throws InvalidUsernameException {
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
	 * @throws InvalidUsernameException 
	 */
	private void updateSeller(PdiSeller pdiSeller, Seller seller) throws InvalidUsernameException {
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
	 * @throws InvalidUsernameException 
	 */
	private void updateUsers(PdiSeller pdiSeller, Seller seller) throws InvalidUsernameException {
		if (seller.hasUserInfo()) {
			// First set seller's user
			User sellerAsUser = pdiSeller.getMe();
			if (sellerAsUser == null) {
				userService.validateUsername(seller.getCampaignCode());
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
				userService.validateUsername(seller.getBuyerCode());
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
			if(pdiSeller.getMe() != null && pdiSeller.getMe().hasRole(RoleEnum.ROLE_GROUP_LEADER)) {
				// special case for the group leader
				pdiGroup.get().setGroupLeader(pdiSeller);
				if(groupLink.isGroupForLeaderSales()) {
					// Set the main group = the sales group
					pdiSeller.setPdiGroup(pdiGroup.get());
				}					
				// add this group in the list of groups the seller can view
				pdiSeller.getPdiGroups().add(pdiGroup.get());				
			} else {
				pdiSeller.setPdiGroup(pdiGroup.get());
			}
			updateCampaignLeadership(pdiSeller);
		} else {
			log.error("No PdiGroup found(number={}) for seller #{}", groupLink.getGroupNumber(), pdiSeller.getNumber());
			throw new ResourceNotFoundException(String.format("Aucun groupe (no=%d) trouvé pour le vendeur (no=%d)", groupLink.getGroupNumber(), pdiSeller.getNumber()));
		}
	}

	/**
	 * Check if the seller has the campaign leadership and if so, add the role
	 * @param currentPdiSeller
	 */
	private void updateCampaignLeadership(PdiSeller currentPdiSeller) {
		Long sellerNumber = currentPdiSeller.getNumber();
		if(currentPdiSeller.getPdiGroup() != null) {
			String leaderNum = currentPdiSeller.getPdiGroup().getPdiCampaign().getLeaderNum();
			if(!leaderNum.isBlank() && 
				Long.valueOf(leaderNum).equals(sellerNumber) && 
				!currentPdiSeller.getMe().getRoles().contains(new Role(RoleEnum.ROLE_CAMPAIGN_LEADER))) {
				currentPdiSeller.getMe().addRole(RoleEnum.ROLE_CAMPAIGN_LEADER);
				pdiSellerRepository.save(currentPdiSeller);
				log.info("Added canpaign leadership to seller number={}", sellerNumber);
			}
		} else {			
			log.warn("Did not get the campaign leader number for seller number={}", sellerNumber);
		}
	}

	@Override
	public PdiSeller save(PdiSeller pdiSeller) {
		return pdiSellerRepository.save(pdiSeller);
	}

	@Override
	public List<PdiProductDTO> getProductsForUser(MyUserDetails userDetails, String lang) {
		Optional<User> user = userService.findUserById(userDetails.getUserId());
		if(user.isPresent()) {
			Optional<PdiSeller> pdiSeller = pdiSellerRepository.findOneByMeOrBuyer(user.get(), user.get());
			if(pdiSeller.isPresent()) {
				// Get the products for the order type of the group that the seller is part of
				PdiGroup pdiGroup = pdiSeller.get().getPdiGroup();
				OrderType orderType = pdiGroup.getOrderType();
				Set<PdiProduct> pdiProducts = orderType.getPdiProducts();
				if (lang.equalsIgnoreCase("fr")) {
					return pdiProductMapper.toDto(pdiProducts.stream().sorted(compareByCategoryAndNameFr).collect(Collectors.toList()));
				} else {
					return pdiProductMapper.toDto(pdiProducts.stream().sorted(compareByCategoryAndNameEn).collect(Collectors.toList()));
				}
			} else {
				log.error("Seller not found for connected user id={}", user.get().getId());
				throw new ResourceNotFoundException(messageSource.getMessage("seller.error.notfound", null, LocaleContextHolder.getLocale()));
			}
		} else {
			log.error("User with id={}, username={} is unknown", userDetails.getUserId(), userDetails.getUsername());
			throw new ResourceNotFoundException("Unknown user !");
		}		
	}

	@Override
	public PdiSellerDTO getSellerForUser(MyUserDetails userDetails) {
		Optional<User> user = userService.findUserById(userDetails.getUserId());
		if(user.isPresent()) {
			Optional<PdiSeller> pdiSeller = pdiSellerRepository.findOneByMeOrBuyer(user.get(), user.get());
			if(pdiSeller.isPresent()) {
				return pdiSellerMapper.toDto(pdiSeller.get());
			} else {
				log.error("Seller not found for connected user id={}", user.get().getId());
				throw new ResourceNotFoundException(messageSource.getMessage("seller.error.notfound", null, LocaleContextHolder.getLocale()));
			}
		} else {
			log.error("User with id={}, username={} is unknown", userDetails.getUserId(), userDetails.getUsername());
			throw new ResourceNotFoundException("Unknown user !");
		}	
	}

	@Override
	public void deletePdiSeller(PdiSeller pdiSeller) {
		if(pdiSeller != null) {
			// First delete the users		
			userService.deleteUser(pdiSeller.getBuyer());
			if(pdiSeller.getMe() != null && !pdiSeller.getMe().hasRole(RoleEnum.ROLE_ADMIN)) {
				userService.deleteUser(pdiSeller.getMe());
			}
			pdiSellerRepository.delete(pdiSeller);
			log.info("Deleted seller #{}", pdiSeller.getNumber());
		} else {
			log.warn("Could not delete seller : was null.");		
		}
	}
}
