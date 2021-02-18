package com.poivredesiles.fundraising.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.poivredesiles.fundraising.model.user.MyUserDetails;
import com.poivredesiles.fundraising.service.OrderService;
import com.poivredesiles.fundraising.service.PdiSellerService;
import com.poivredesiles.fundraising.service.dto.OrderHeaderDTO;
import com.poivredesiles.fundraising.service.dto.PdiSellerDTO;

@Controller
public class SalesController extends BaseController {

	private final Logger log = LoggerFactory.getLogger(SalesController.class);
	
	@Autowired
	private PdiSellerService pdiSellerService;
	
	@Autowired
	private OrderService orderService;
	
	@GetMapping("/ventes")
	public String sales(@AuthenticationPrincipal MyUserDetails userDetails, Model model) {		
		log.info("Requested Sales Page");		
		PdiSellerDTO seller = pdiSellerService.getSellerForUser(userDetails);
		List<OrderHeaderDTO> orders = orderService.getPaidOrdersForSeller(seller);
		model.addAttribute("seller", seller);
		model.addAttribute("orders", orders);
		return "views/sales";
	}
}
