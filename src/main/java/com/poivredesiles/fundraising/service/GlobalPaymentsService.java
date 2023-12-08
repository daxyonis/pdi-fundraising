package com.poivredesiles.fundraising.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import com.global.api.entities.HostedPaymentData;
import com.global.api.entities.Transaction;
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
import com.poivredesiles.fundraising.resource.OrderResource;

@Service
@Transactional
public class GlobalPaymentsService {

	private final OrderService orderService;

	private final MailService mailService;

	private final MessageSource messageSource;

	private final Environment env;

	private final ObjectMapper mapper = new ObjectMapper();
	
	@Value("${global.merchant.id}")
    private String globalMerchantId;
	
	@Value("${global.shared.secret}")
    private String globalSharedSecret;
	
	@Value("${global.service.url}")
	private String globalServiceUrl;
	
	@Value("${global.currency}")
	private String currency;
	
	private final Logger log = LoggerFactory.getLogger(GlobalPaymentsService.class);

	public GlobalPaymentsService(OrderService orderService, MailService mailService, MessageSource messageSource, Environment env) {
		this.orderService = orderService;
		this.mailService = mailService;
		this.messageSource = messageSource;
		this.env = env;
	}

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
		
		// Create new order
		OrderHeader pendingOrder = orderService.createNewOrder(orderResource, locale);
		log.info("Charging payment for order #{}", pendingOrder.getOrderNumber());
		
		// configure client, request and HPP settings
		GatewayConfig config = getGatewayConfig();		
		
		HostedPaymentConfig hostedPaymentConfig = new HostedPaymentConfig();
		hostedPaymentConfig.setLanguage(locale.getLanguage());
		hostedPaymentConfig.setVersion(HppVersion.Version2);		
		hostedPaymentConfig.setFraudFilterMode(FraudFilterMode.Passive);
		config.setHostedPaymentConfig(hostedPaymentConfig);

		String[] names = orderResource.getName().split("\\s");
		String firstName = "";
		String lastName = "";
		if (names.length > 0) {
			firstName = names[0];
		}
		if (names.length > 1) {
			lastName = Arrays.stream( Arrays.copyOfRange(names, 1, names.length)).reduce("", (s1,s2) -> s1 + " " + s2);
			lastName = lastName.trim();
		}

		// Add 3D Secure 2 Mandatory and Recommended Fields
		HostedPaymentData hostedPaymentData = new HostedPaymentData();
		hostedPaymentData.setCustomerEmail(orderResource.getEmail());
		// Expected format is country code|number
		hostedPaymentData.setCustomerPhoneMobile("1|" + orderResource.getStrippedPhone(10));
		log.debug("PaymentData.phone = {}", hostedPaymentData.getCustomerPhoneMobile());
		hostedPaymentData.setCustomerFirstName(firstName);
		hostedPaymentData.setCustomerLastName(lastName);
		hostedPaymentData.setCustomerCountry("CA");
		hostedPaymentData.setSupplimentaryData(new HashMap<>(Map.of("detail", pendingOrder.getDetail())));
		hostedPaymentData.setAddressesMatch(true);
		
		try {
			HostedService service = new HostedService(config);

			String hppJson = service.charge(pendingOrder.getTotal())
							.withCurrency(currency)
						    .withHostedPaymentData(hostedPaymentData)
							.withFraudFilter(FraudFilterMode.Passive)
						    .withOrderId(pendingOrder.getOrderNumber().toString())
							.serialize();

			try {
				// Keep the timestamp for verification purposes
				JsonNode hppNode = mapper.readTree(hppJson);
				pendingOrder.setPayTimestamp(hppNode.get("TIMESTAMP").asText());

				// Add other HPP settings
				((ObjectNode) hppNode).put("HPP_CAPTURE_ADDRESS", "true");
				((ObjectNode) hppNode).put("HPP_DO_NOT_RETURN_ADDRESS", "true");
				((ObjectNode) hppNode).put("HPP_REMOVE_SHIPPING", "true");

				if (locale.getLanguage().equalsIgnoreCase("fr")) {
					((ObjectNode) hppNode).put("HPP_LANG", "fr_ca");
				}

				hppJson = mapper.writeValueAsString(hppNode);

			} catch (JsonProcessingException e) {
			}

			log.debug("hppJson = {}", hppJson);
			return hppJson;
		} catch (ApiException e) {
			pendingOrder.setOrderStatus(OrderStatusEnum.ERROR);
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
	 * @param responseData
	 * @return
	 * @throws OrderProcessingException
	 */
	public Long processResponse(MultiValueMap<String, String> responseData, Locale locale) throws OrderProcessingException {

		// get the response json from the form data
		String hppResponse = responseData.getFirst("hppResponse");
		if (hppResponse == null || hppResponse.isBlank()) {
			log.error("No hppResponse provided.");
			throw new OrderProcessingException(messageSource.getMessage("order.error.invalidresponse", null, locale));
		}

		GatewayConfig config = getGatewayConfig();
		try {
			HostedService service = new HostedService(config);
		    Transaction response = service.parseResponse(hppResponse, true);
		    String orderNumber = response.getOrderId();
		    String responseCode = response.getResponseCode();
		    String responseMessage = response.getResponseMessage();
		    HashMap<String, String> responseValues = response.getResponseValues(); // get values accessible by key
			log.info("Processing Global Response for order #{}", orderNumber);
		    if(responseCode.compareTo("00") == 0) {
		    	// Success !
				this.validateOrderResponse(responseValues, locale);
				this.orderService.confirmOrder(Long.parseLong(orderNumber));
				log.info("Order #{} confirmed !", orderNumber);
		    	return Long.parseLong(orderNumber);
		    } else {
		    	log.error("Failed transaction for order #{}, code = {}, message={}", orderNumber, responseCode, responseMessage);
		    	orderService.markOrderAsError(Long.parseLong(orderNumber));
		    	throw new OrderProcessingException(messageSource.getMessage("order.error.failure", null, locale));
		    }
			   
		} catch (Exception e) {
			log.error("Error post-processing payment response : {}", e.getLocalizedMessage());
			throw new OrderProcessingException(messageSource.getMessage("order.error.postprocess", null, locale));
		}
	}

	/**
	 * Check the response values insuring that the returned transaction
	 * is valid and can be linked to the real order.
	 * @param responseValues    response values map as returned by Global HPP service
	 * @param locale
	 */
	private void validateOrderResponse(HashMap<String, String> responseValues, Locale locale) {

//		String errorMessage = "";
		try {
			// Check the fraud filter
			String fraudFilterResult = responseValues.get("HPP_FRAUDFILTER_RESULT"); // PASS
			if (!fraudFilterResult.equalsIgnoreCase("PASS")) {
				log.error("Error checking payment response : fraud filter result = {}", fraudFilterResult);
				// Do not throw an error because fraud mode is passive
				// Passive means that although the fraud rules will run and will get the appropriate response values,
				// the resulting action (for example, Hold or Block) will not execute. See https://developer.globalpay.com/ecommerce/fraud-management#hpp-guide
			}

			// Check some order fields
			String orderId = responseValues.get("ORDER_ID");
			OrderHeader orderHeader = orderService.findByOrderNumber(Long.parseLong(orderId));

			// Check the amount
			String strAmount = responseValues.get("AMOUNT");
			BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(strAmount));
			BigDecimal difference = orderHeader.getTotal().multiply(BigDecimal.valueOf(100)).subtract(amount).abs();
			if (difference.doubleValue() > Double.MIN_VALUE) {
				log.error("Error checking payment response : amounts don't match, order nb = {}, difference= {}", orderId, difference.toEngineeringString());
				throw new OrderProcessingException(messageSource.getMessage("order.error.postprocess", null, locale));
			}

			// Check the timestamp
			String timestamp = responseValues.get("TIMESTAMP");
			if ( timestamp.compareTo(orderHeader.getPayTimestamp()) != 0) {
				log.error("Error checking payment response : timestamp doesn't match = expected: {} -  received: {}", orderHeader.getPayTimestamp(), timestamp);
				throw new OrderProcessingException(messageSource.getMessage("order.error.postprocess", null, locale));
			}
		} catch (Exception e) {
			log.error("Error while trying to verify hpp response", e);
		}
/**
		if ( !errorMessage.isBlank() ) {
			ErrorMessage error = new ErrorMessage("Error checking payment response",
					"Running profile(s) " + Arrays.stream(env.getActiveProfiles()).reduce("", (a,b) -> a + " " + b),
					errorMessage);
			mailService.sendErrorEmail(error);
		}
 **/
	}
}
