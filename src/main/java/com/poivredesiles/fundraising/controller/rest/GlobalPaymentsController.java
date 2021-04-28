package com.poivredesiles.fundraising.controller.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import com.poivredesiles.fundraising.exception.InvalidOrderException;
import com.poivredesiles.fundraising.exception.OrderProcessingException;
import com.poivredesiles.fundraising.resource.OrderResource;
import com.poivredesiles.fundraising.service.GlobalPaymentsService;

@RestController
@RequestMapping("/api/global")
public class GlobalPaymentsController {

	@Autowired
	private LocaleResolver localeResolver;
	
	@Autowired
	private GlobalPaymentsService globalPaymentsService;

	@PostMapping("/checkout")
	@Secured({"ROLE_BUYER"})
	public String createHppJson(@RequestBody OrderResource orderResource, HttpServletRequest request) throws InvalidOrderException, OrderProcessingException {		
		String hppJson = globalPaymentsService.getHppJson(orderResource, localeResolver.resolveLocale(request));            		
	    return hppJson;
	}
	
	
	@PostMapping(value="/response")
	public String processResponse(@RequestBody String payload, Model model, HttpServletRequest request) throws OrderProcessingException {
		Long orderId = globalPaymentsService.processResponse(payload, localeResolver.resolveLocale(request));
		return "redirect:/commande/succes?orderId=" + orderId;
	}
}
