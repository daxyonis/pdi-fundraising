package com.poivredesiles.fundraising.controller.rest;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import com.poivredesiles.fundraising.resource.OrderItemResource;
import com.poivredesiles.fundraising.service.StripeService;
import com.stripe.exception.StripeException;
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
	public HashMap<String, String> createCheckoutSession(@RequestBody List<OrderItemResource> orderItems, HttpServletRequest request) throws StripeException {
		Session session = stripeService.createSession(orderItems, localeResolver.resolveLocale(request));            
		HashMap<String, String> responseData = new HashMap<String, String>();
	    responseData.put("id", session.getId());
	    return responseData;
	}
}
