package com.poivredesiles.fundraising.controller.rest;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
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
	
	// Note: expected content type from Global is 'application/x-www-form-urlencoded;charset=UTF-8'
	@PostMapping(value="/response")
	public void processResponse(@RequestBody MultiValueMap<String, String> responseData, HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			Long orderNumber = globalPaymentsService.processResponse(responseData, localeResolver.resolveLocale(request));
			response.sendRedirect("/commande/succes?orderNum=" + orderNumber);
		} catch (OrderProcessingException e) {
			response.sendRedirect("/commande?failure=true");
		}		
	}
}
