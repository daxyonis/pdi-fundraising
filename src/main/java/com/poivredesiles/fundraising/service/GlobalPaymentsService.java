package com.poivredesiles.fundraising.service;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.global.api.entities.Address;
import com.global.api.entities.HostedPaymentData;
import com.global.api.entities.enums.AddressType;
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
	
	@Value("${global.merchantId}")
    private String globalMerchantId;
	
	@Value("${global.sharedSecret}")
    private String globalSharedSecret;
	
	private final Logger log = LoggerFactory.getLogger(GlobalPaymentsService.class);

	public String getHppJson(OrderResource orderResource, Locale locale) throws InvalidOrderException, OrderProcessingException {
		log.info("Charging payment");		
		
		// Create new order
		OrderHeader pendingOrder = orderService.createNewOrder(orderResource, locale);
		
		// configure client, request and HPP settings
		GatewayConfig config = new GatewayConfig();
		config.setMerchantId(globalMerchantId);
		//config.setAccountId("internet");		
		config.setSharedSecret(globalSharedSecret);
		config.setServiceUrl("https://pay.sandbox.realexpayments.com/pay");		
		
		HostedPaymentConfig hostedPaymentConfig = new HostedPaymentConfig();
		hostedPaymentConfig.setLanguage(locale.getLanguage());
		hostedPaymentConfig.setVersion(HppVersion.Version2);		
		config.setHostedPaymentConfig(hostedPaymentConfig);
		
		// Add 3D Secure 2 Mandatory and Recommended Fields
		HostedPaymentData hostedPaymentData = new HostedPaymentData();
		hostedPaymentData.setCustomerEmail(orderResource.getEmail());		
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
						    .withCurrency("CAD")
						    .withHostedPaymentData(hostedPaymentData)
						    .withAddress(billingAddress, AddressType.Billing)	
						    .withOrderId(pendingOrder.getId().toString())						    
						    .serialize();		    		    
		    return hppJson;
		} catch (ApiException e) {
			pendingOrder.setOrderStatus(OrderStatusEnum.ERROR);
			orderService.save(pendingOrder);
			log.error("Error trying to create checkout session : {}", e.getLocalizedMessage());
			throw new OrderProcessingException(messageSource.getMessage("order.error.checkout", null, locale));
		}
	}
}
