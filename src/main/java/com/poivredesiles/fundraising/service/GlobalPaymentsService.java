package com.poivredesiles.fundraising.service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.global.api.entities.Address;
import com.global.api.entities.HostedPaymentData;
import com.global.api.entities.Transaction;
import com.global.api.entities.enums.AddressType;
import com.global.api.entities.enums.FraudFilterMode;
import com.global.api.entities.enums.HppVersion;
import com.global.api.entities.exceptions.ApiException;
import com.global.api.serviceConfigs.GatewayConfig;
import com.global.api.serviceConfigs.HostedPaymentConfig;
import com.global.api.services.HostedService;
import com.poivredesiles.fundraising.exception.InvalidOrderException;
import com.poivredesiles.fundraising.exception.OrderProcessingException;
import com.poivredesiles.fundraising.model.order.OrderHeader;
import com.poivredesiles.fundraising.model.order.OrderStatusEnum;
import com.poivredesiles.fundraising.resource.AddressResource;
import com.poivredesiles.fundraising.resource.OrderResource;

@Service
@Transactional
public class GlobalPaymentsService {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private MessageSource messageSource;
	
	@Value("${global.merchant.id}")
    private String globalMerchantId;
	
	@Value("${global.shared.secret}")
    private String globalSharedSecret;
	
	@Value("${global.service.url}")
	private String globalServiceUrl;
	
	@Value("${global.currency}")
	private String currency;
	
	private final Logger log = LoggerFactory.getLogger(GlobalPaymentsService.class);

	/**
	 * Form the json string for GP HPP
	 * See https://developer.globalpay.com/ecommerce/hosted-payment-page#hpp
	 * @param orderResource	the order details
	 * @param locale		the current user locale 
	 * @return
	 * @throws InvalidOrderException	if order has invalid fields
	 * @throws OrderProcessingException	if HPP json could not be formed
	 */
	public String getHppJson(OrderResource orderResource, Locale locale) throws InvalidOrderException, OrderProcessingException {
		log.info("Charging payment");		
		
		// Create new order
		OrderHeader pendingOrder = orderService.createNewOrder(orderResource, locale);
		
		// configure client, request and HPP settings
		GatewayConfig config = getGatewayConfig();		
		
		HostedPaymentConfig hostedPaymentConfig = new HostedPaymentConfig();
		hostedPaymentConfig.setLanguage(locale.getLanguage());
		hostedPaymentConfig.setVersion(HppVersion.Version2);		
		hostedPaymentConfig.setFraudFilterMode(FraudFilterMode.Passive);
		config.setHostedPaymentConfig(hostedPaymentConfig);
		
		// Add 3D Secure 2 Mandatory and Recommended Fields
		HostedPaymentData hostedPaymentData = new HostedPaymentData();
		hostedPaymentData.setCustomerEmail(orderResource.getEmail());
		hostedPaymentData.setCustomerPhoneMobile(orderResource.getPhone());
		hostedPaymentData.setCustomerFirstName(orderResource.getName());
		hostedPaymentData.setSupplimentaryData(new HashMap<>(Map.of("detail", pendingOrder.getDetail())));
		hostedPaymentData.setAddressesMatch(false);		
		
		AddressResource addr = orderResource.getAddress();
		Address billingAddress = new Address();
		billingAddress.setStreetAddress1(addr.getLine1());
		billingAddress.setStreetAddress2(addr.getLine2());
		billingAddress.setStreetAddress3(addr.getLine3());
		billingAddress.setCity(addr.getCity());
		billingAddress.setPostalCode(addr.getPostalCode());
		billingAddress.setCountry(addr.getCountry().code());						
		
		try {
			HostedService service = new HostedService(config);
		    String hppJson = service.charge(pendingOrder.getTotal())
						    .withCurrency(currency)
						    .withHostedPaymentData(hostedPaymentData)
						    .withAddress(billingAddress, AddressType.Billing)	
						    .withOrderId(pendingOrder.getId().toString())								    
						    .serialize();		    		    
		    return hppJson;
		} catch (ApiException e) {
			pendingOrder.setOrderStatus(OrderStatusEnum.ERROR);
			orderService.save(pendingOrder);
			log.error("Error trying to create checkout info : {}", e.getLocalizedMessage());
			throw new OrderProcessingException(messageSource.getMessage("order.error.checkout", null, locale));
		}
	}

	/**
	 * Create a GatewayConfig with Global Payments account
	 * credentials
	 * 
	 * @return	a new GatewayConfig
	 */
	private GatewayConfig getGatewayConfig() {
		GatewayConfig config = new GatewayConfig();
		config.setMerchantId(globalMerchantId);
		config.setSharedSecret(globalSharedSecret);
		config.setServiceUrl(globalServiceUrl);
		return config;
	}

	/**
	 * Once payment has been processed, GP returns a response.
	 * Here we analyze it : 
	 * 		- if response indicates transaction success, we return the order id
	 * 		- if response indicates transaction failure, we throw an exception
	 * @param hppResponse
	 * @return
	 * @throws OrderProcessingException	
	 * @throws JsonProcessingException 
	 */
	public Long processResponse(MultiValueMap<String, String> responseData, Locale locale) throws OrderProcessingException {
		
		GatewayConfig config = getGatewayConfig();
		try {
			HostedService service = new HostedService(config);
			 
			// get the response json from the form data
			String hppResponse = responseData.getFirst("hppResponse");
		    Transaction response = service.parseResponse(hppResponse, true);
		    String orderId = response.getOrderId();
		    String responseCode = response.getResponseCode();
		    String responseMessage = response.getResponseMessage();
//		    HashMap<String, String> responseValues = response.getResponseValues(); // get values accessible by key
		    //String fraudFilterResult = responseValues.get("HPP_FRAUDFILTER_RESULT"); // PASS
		    if(responseCode.compareTo("00") == 0) {
		    	// Success !
		    	return Long.parseLong(orderId);
		    } else {
		    	log.error("Failed transaction, code = {}, message={}", responseCode, responseMessage);
		    	orderService.markOrderAsError(Long.parseLong(orderId));
		    	throw new OrderProcessingException(messageSource.getMessage("order.error.failure", null, locale));
		    }
			   
		} catch (ApiException e) {
			log.error("Error post-processing payment response : {}", e.getLocalizedMessage());
			throw new OrderProcessingException(messageSource.getMessage("order.error.postprocess", null, locale));
		}		
		
	}
}
