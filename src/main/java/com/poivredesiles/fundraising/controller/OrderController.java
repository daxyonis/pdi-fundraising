package com.poivredesiles.fundraising.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poivredesiles.fundraising.model.user.MyUserDetails;
import com.poivredesiles.fundraising.model.user.RoleEnum;
import com.poivredesiles.fundraising.service.OrderService;
import com.poivredesiles.fundraising.service.PdiSellerService;
import com.poivredesiles.fundraising.service.dto.OrderHeaderDTO;
import com.poivredesiles.fundraising.service.dto.PdiProductDTO;
import com.poivredesiles.fundraising.service.dto.PdiSellerDTO;

@Controller
public class OrderController extends BaseController {

	private final Logger log = LoggerFactory.getLogger(OrderController.class);
	
	@Autowired
	private PdiSellerService pdiSellerService;
	
	@Autowired
	private OrderService orderService; 
	
	@Value("${stripe.publicKey}")	
	private String stripePublicKey;
	
	@GetMapping("/commande")
	public String order(Authentication authentication, Model model) {
		log.info("Requested Order Page");		
		MyUserDetails userDetails = (MyUserDetails)authentication.getPrincipal();
		PdiSellerDTO seller = pdiSellerService.getSellerForUser(userDetails);
		model.addAttribute("seller", seller);
		List<PdiProductDTO> products = pdiSellerService.getProductsForUser(userDetails);
		model.addAttribute("products", products);
				
		model.addAttribute("menuShowHome", userDetails.hasAnyAuthority(RoleEnum.ROLE_SELLER, RoleEnum.ROLE_GROUP_LEADER, RoleEnum.ROLE_CAMPAIGN_LEADER));
		model.addAttribute("menuShowSales", userDetails.hasAnyAuthority(RoleEnum.ROLE_GROUP_LEADER, RoleEnum.ROLE_CAMPAIGN_LEADER));
		model.addAttribute("menuShowOrder", userDetails.hasAnyAuthority(RoleEnum.ROLE_BUYER, RoleEnum.ROLE_SELLER, RoleEnum.ROLE_GROUP_LEADER, RoleEnum.ROLE_CAMPAIGN_LEADER));
		
		model.addAttribute("stripePublicApiKey", stripePublicKey);
		
		return "views/order";
	}
	
	@GetMapping("/commande/succes")
	public String successfulOrder(@RequestParam("session_id") String sessionId, Model model) {
		OrderHeaderDTO order = orderService.getConfirmedOrder(sessionId);
		model.addAttribute("order", order);
		return "views/order-success";
	}
}
