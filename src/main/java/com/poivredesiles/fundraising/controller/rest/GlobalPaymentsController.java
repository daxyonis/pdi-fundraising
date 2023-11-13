package com.poivredesiles.fundraising.controller.rest;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import com.poivredesiles.fundraising.exception.InvalidOrderException;
import com.poivredesiles.fundraising.exception.OrderProcessingException;
import com.poivredesiles.fundraising.resource.OrderResource;
import com.poivredesiles.fundraising.service.GlobalPaymentsService;

@RestController
@RequestMapping("/api/global")
public class GlobalPaymentsController {

	private final Logger log = LoggerFactory.getLogger(GlobalPaymentsController.class);

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
	
	// Note: expected content type from Global is 'application/x-www-form-urlencoded;charset=UTF-8'
	@PostMapping(value="/response")
	@Secured({"ROLE_BUYER"})
	public void processResponse(@RequestBody MultiValueMap<String, String> responseData, HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			Long orderNumber = globalPaymentsService.processResponse(responseData, localeResolver.resolveLocale(request));
			response.sendRedirect("/commande/succes?orderNum=" + orderNumber);
		} catch (OrderProcessingException e) {
			response.sendRedirect("/commande?failure=true");
		}		
	}

	@PostMapping("/logs")
	public void logRequest(@RequestBody Map<String, String> request) {
		log.info("Global Payments Request: {}", request);
	}
}
