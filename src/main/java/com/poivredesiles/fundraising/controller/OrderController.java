package com.poivredesiles.fundraising.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;

import com.poivredesiles.fundraising.model.user.MyUserDetails;
import com.poivredesiles.fundraising.resource.ResourceUtils;
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
	
	@Autowired
	private ResourceUtils resourceUtils;
	
	@Autowired
	private LocaleResolver localeResolver;
			
	@Value("${global.service.url}")	
	private String globalServiceUrl;
	
	
	@GetMapping("/commande")
	public String order(@RequestParam(required = false)boolean failure, @AuthenticationPrincipal MyUserDetails userDetails, Model model, HttpServletRequest request) {
		log.info("Requested Order Page");		
		PdiSellerDTO seller = pdiSellerService.getSellerForUser(userDetails);
		model.addAttribute("seller", seller);
		if(seller.isPdiCampaignClosed()) {
			return "views/closed";
		} else {
			List<PdiProductDTO> products = pdiSellerService.getProductsForUser(userDetails);
			model.addAttribute("products", products);						
			model.addAttribute("globalServiceUrl", globalServiceUrl);
			model.addAttribute("provinces", resourceUtils.getProvincesMap(localeResolver.resolveLocale(request)));
			String applicationUrl = String.format("%s://%s:%d", request.getScheme(), request.getServerName(), request.getServerPort());
			model.addAttribute("applicationUrl", applicationUrl);
			model.addAttribute("failure", failure);
			return "views/order";
		}
	}		
	
	@GetMapping("/commande/succes")
	public String successfulOrder(@RequestParam(name = "orderId", required = false) Long orderId, Model model) {
		OrderHeaderDTO order = orderService.getConfirmedOrder(orderId);
		model.addAttribute("order", order);
		return "views/order-success";		
	}
}
