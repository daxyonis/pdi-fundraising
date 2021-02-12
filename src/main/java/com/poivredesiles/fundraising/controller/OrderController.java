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
import com.poivredesiles.fundraising.service.PdiSellerService;
import com.poivredesiles.fundraising.service.dto.PdiProductDTO;

@Controller
public class OrderController extends BaseController {

	private final Logger log = LoggerFactory.getLogger(OrderController.class);
	
	@Autowired
	private PdiSellerService pdiSellerService;
	
	@RequestMapping("/commande")
	public String commande(Authentication authentication, Model model) {
		log.info("Requested Order Page");
		MyUserDetails userDetails = (MyUserDetails)authentication.getPrincipal();
		List<PdiProductDTO> products = pdiSellerService.getProductsForUser(userDetails);
		model.addAttribute("products", products);		
		return "views/order";
	}
}
