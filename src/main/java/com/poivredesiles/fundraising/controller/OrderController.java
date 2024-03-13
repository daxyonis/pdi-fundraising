package com.poivredesiles.fundraising.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.poivredesiles.fundraising.config.properties.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;

import com.poivredesiles.fundraising.model.user.MyUserDetails;
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
	private LocaleResolver localeResolver;
			
	@Value("${global.service.url}")	
	private String globalServiceUrl;

	@Autowired
	private Environment env;

	@Autowired
	private ApplicationProperties applicationProperties;
	
	
	@GetMapping("/commande")
	public String order(@RequestParam(required = false) String lang,
						@RequestParam(required = false)boolean failure,
						@AuthenticationPrincipal MyUserDetails userDetails,
						Model model,
						HttpServletRequest request) {
		log.info("Requested Order Page");
		PdiSellerDTO seller = (PdiSellerDTO) model.getAttribute("seller");
		if(seller.isPdiCampaignClosed()) {
			return "views/closed";
		} else {
			if (lang == null) {
				lang = localeResolver.resolveLocale(request).getLanguage();
			}
			List<PdiProductDTO> products = pdiSellerService.getProductsForUser(userDetails, lang);
			model.addAttribute("products", products);						
			model.addAttribute("globalServiceUrl", globalServiceUrl);
			String applicationUrl = String.format("%s://%s:%d", request.getScheme(), request.getServerName(), request.getServerPort());
			model.addAttribute("applicationUrl", applicationUrl);
			model.addAttribute("failure", failure);

			// Set the response URL based on the environment
			boolean isDeployed = Arrays.asList(env.getActiveProfiles()).contains("prod") ||
					          Arrays.asList(env.getActiveProfiles()).contains("dev");
			if (isDeployed) {
				model.addAttribute("responseUrl","https://" + applicationProperties.getBaseUrl() + "/api/global/response");
			}

			return "views/order";
		}
	}		
	
	@GetMapping("/commande/succes")
	public String successfulOrder(@RequestParam(name = "orderNum", required = false) Long orderNumber, Model model) {
		OrderHeaderDTO order = orderService.getConfirmedOrder(orderNumber);
		model.addAttribute("order", order);
		return "views/order-success";		
	}
}
