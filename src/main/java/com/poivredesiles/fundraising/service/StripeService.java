package com.poivredesiles.fundraising.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poivredesiles.fundraising.config.properties.ApplicationProperties;
import com.poivredesiles.fundraising.exception.InvalidOrderException;
import com.poivredesiles.fundraising.exception.OrderProcessingException;
import com.poivredesiles.fundraising.model.order.OrderHeader;
import com.poivredesiles.fundraising.model.order.OrderItem;
import com.poivredesiles.fundraising.model.order.OrderStatusEnum;
import com.poivredesiles.fundraising.resource.OrderResource;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
@Transactional
public class StripeService {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private ApplicationProperties applicationProperties;
	
	@Autowired
	private MessageSource messageSource;
	
	@Value("${stripe.privateKey}")
    private String stripeApiKey;

	// Minimal total amount (in dollars) for which there is no transaction fee
	@Value("${application.order.minAmountForNoFee}")
	private BigDecimal minAmountForNoFee;
	
	// Transaction fee (in cents)
	@Value("${application.order.transactionFee}")
	private Long transactionFee;
	
	private final Logger log = LoggerFactory.getLogger(StripeService.class);
	
	private final BigDecimal HUNDRED = BigDecimal.valueOf(100);	
	
	/**
	 * Create Stripe Checkout Session
	 * Creates a pending PDI order with order items from the orderResource
	 * Then a Stripe session is created with information about the order items to charge.
	 * If order total < 10.00$, the PDI transaction fee is added as a Stripe Line Item.
	 *  
	 * @param orderItems
	 * @param locale
	 * @return
	 * @throws StripeException
	 * @throws InvalidOrderException 
	 * @throws OrderProcessingException 
	 */
	public Session createCheckoutSession(OrderResource orderResource, Locale locale) throws InvalidOrderException, OrderProcessingException {
		log.info("Creating new Stripe checkout session");
		final String YOUR_DOMAIN = applicationProperties.getUrl();
				
		Stripe.apiKey = stripeApiKey;	
		SessionCreateParams.Locale sessionLocale = SessionCreateParams.Locale.FR_CA;
		if(locale.getLanguage().equalsIgnoreCase("en")) {
			sessionLocale = SessionCreateParams.Locale.EN;
		}
		
		OrderHeader pendingOrder = orderService.createNewOrder(orderResource, locale);
		
		// Create the list of LineItem
		List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
		for(OrderItem orderItem : pendingOrder.getOrderItems()) {
			
			// Set name and description
			String name = orderItem.getProduct().getNameFr();
			String description = orderItem.getProduct().getDescriptionFr();
			if(locale.getLanguage().equalsIgnoreCase("en")) {
				name = orderItem.getProduct().getNameEn();
				description = orderItem.getProduct().getDescriptionEn();
			}
			// Build the line item info
			// Note: need to provide Stripe the amount in cents			
			lineItems.add(SessionCreateParams.LineItem.builder()
					.setQuantity(orderItem.getQuantity())
					.setPriceData(
							SessionCreateParams.LineItem.PriceData.builder()
							.setCurrency("cad")
							.setUnitAmount(orderItem.getProduct().getCategory().getUnitPrice().multiply(HUNDRED).longValue())
							.setProductData(
									SessionCreateParams.LineItem.PriceData.ProductData.builder()
		                              .setName(name)
		                              .setDescription(description)
		                              .build())
							.build())
					.build());			
		}
		
		if(pendingOrder.total().compareTo(minAmountForNoFee) < 0) {
			// Add line item for transaction fee : this is not saved into the PDI order 
			lineItems.add(SessionCreateParams.LineItem.builder()
					.setQuantity(1L)
					.setPriceData(
							SessionCreateParams.LineItem.PriceData.builder()
							.setCurrency("cad")
							.setUnitAmount(transactionFee)
							.setProductData(
									SessionCreateParams.LineItem.PriceData.ProductData.builder()
									.setName(messageSource.getMessage("order.transaction.fee", null, locale))
									.setDescription(messageSource.getMessage("order.transaction.feedescription", null, locale))
									.build())							
							.build())
					.build());
		}
	      
        SessionCreateParams params =
           SessionCreateParams.builder()
                  .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                  .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED)
                  .setMode(SessionCreateParams.Mode.PAYMENT)
                  .setSuccessUrl(YOUR_DOMAIN + "/commande/succes?session_id={CHECKOUT_SESSION_ID}")
                  .setCancelUrl(YOUR_DOMAIN + "/commande")
                  .setLocale(sessionLocale)
                  .addAllLineItem(lineItems)
                  .build();
         
		try {
			Session session = Session.create(params);
			pendingOrder.setStripeSessionId(session.getId());
	        orderService.save(pendingOrder);        
	        return session;
		} catch (StripeException e) {
			pendingOrder.setOrderStatus(OrderStatusEnum.ERROR);
			orderService.save(pendingOrder);
			log.error("Error trying to create Stripe checkout session : {}", e.getLocalizedMessage());
			throw new OrderProcessingException(messageSource.getMessage("order.error.checkout", null, locale));
		}  
                
	}
}
