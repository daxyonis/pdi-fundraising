package com.poivredesiles.fundraising.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.poivredesiles.fundraising.model.user.MyUserDetails;
import com.poivredesiles.fundraising.model.user.RoleEnum;
import com.poivredesiles.fundraising.service.PdiSellerService;
import com.poivredesiles.fundraising.service.dto.PdiProductDTO;
import com.poivredesiles.fundraising.service.dto.PdiSellerDTO;

@Controller
public class OrderController extends BaseController {

	private final Logger log = LoggerFactory.getLogger(OrderController.class);
	
	@Autowired
	private PdiSellerService pdiSellerService;
	
	@RequestMapping("/commande")
	public String commande(Authentication authentication, Model model) {
		log.info("Requested Order Page");		
		MyUserDetails userDetails = (MyUserDetails)authentication.getPrincipal();
		PdiSellerDTO seller = pdiSellerService.getSellerForUser(userDetails);
		model.addAttribute("seller", seller);
		List<PdiProductDTO> products = pdiSellerService.getProductsForUser(userDetails);
		model.addAttribute("products", products);
				
		model.addAttribute("menuShowHome", userDetails.hasAnyAuthority(RoleEnum.ROLE_SELLER, RoleEnum.ROLE_GROUP_LEADER, RoleEnum.ROLE_CAMPAIGN_LEADER));
		model.addAttribute("menuShowSales", userDetails.hasAnyAuthority(RoleEnum.ROLE_GROUP_LEADER, RoleEnum.ROLE_CAMPAIGN_LEADER));
		model.addAttribute("menuShowOrder", userDetails.hasAnyAuthority(RoleEnum.ROLE_BUYER, RoleEnum.ROLE_SELLER, RoleEnum.ROLE_GROUP_LEADER, RoleEnum.ROLE_CAMPAIGN_LEADER));
		
		return "views/order";
	}
}
