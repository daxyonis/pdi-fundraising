package com.poivredesiles.fundraising.controller.rest;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import com.poivredesiles.fundraising.exception.InvalidOrderException;
import com.poivredesiles.fundraising.exception.OrderProcessingException;
import com.poivredesiles.fundraising.resource.OrderResource;
import com.poivredesiles.fundraising.service.StripeService;
import com.stripe.model.checkout.Session;

@RestController
@RequestMapping("/api/checkout")
public class StripeController {
	
	@Autowired
	private StripeService stripeService;
	
	@Autowired
	private LocaleResolver localeResolver;	

	@PostMapping("/session")
	@Secured({"ROLE_BUYER"})
	public HashMap<String, String> createCheckoutSession(@RequestBody OrderResource orderResource, HttpServletRequest request) throws InvalidOrderException, OrderProcessingException {
		String applicationUrl = String.format("%s://%s:%d", request.getScheme(), request.getServerName(), request.getServerPort());
		Session session = stripeService.createCheckoutSession(orderResource, localeResolver.resolveLocale(request), applicationUrl);            
		HashMap<String, String> responseData = new HashMap<String, String>();
	    responseData.put("id", session.getId());
	    return responseData;
	}
}
