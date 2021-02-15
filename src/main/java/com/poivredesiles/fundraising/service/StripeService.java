package com.poivredesiles.fundraising.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.poivredesiles.fundraising.config.properties.ApplicationProperties;
import com.poivredesiles.fundraising.resource.OrderItemResource;
import com.poivredesiles.fundraising.service.dto.PdiProductDTO;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class StripeService {

	@Autowired
	private PdiProductService pdiProductService;
	
	@Autowired
	private ApplicationProperties applicationProperties;
	
	@Value("${STRIPE_API_TEST_KEY}")
    private String stripeApiKey;
	
	private final Logger log = LoggerFactory.getLogger(StripeService.class);
	
	private final BigDecimal HUNDRED = BigDecimal.valueOf(100);
	
	public Session createSession(List<OrderItemResource> orderItems, Locale locale) throws StripeException {
		final String YOUR_DOMAIN = applicationProperties.getUrl();
				
		Stripe.apiKey = stripeApiKey;	
		SessionCreateParams.Locale sessionLocale = SessionCreateParams.Locale.FR_CA;
		if(locale.getLanguage().equalsIgnoreCase("en")) {
			sessionLocale = SessionCreateParams.Locale.EN;
		}
		
		// Create the list of LineItem
		List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
		for(OrderItemResource orderItem : orderItems) {
			// Get original product referenced
			Optional<PdiProductDTO> pdiProduct = pdiProductService.findOne(orderItem.getId());
			if(pdiProduct.isPresent()) {
				// Set name and description
				String name = pdiProduct.get().getNameFr();
				String description = pdiProduct.get().getDescriptionFr();
				if(locale.getLanguage().equalsIgnoreCase("en")) {
					name = pdiProduct.get().getNameEn();
					description = pdiProduct.get().getDescriptionEn();
				}
			// Build the line item info
			lineItems.add(SessionCreateParams.LineItem.builder()
					.setQuantity(orderItem.getQty())
					.setPriceData(
							SessionCreateParams.LineItem.PriceData.builder()
							.setCurrency("cad")
							.setUnitAmount(pdiProduct.get().getUnitPrice().multiply(HUNDRED).longValue())
							.setProductData(
									SessionCreateParams.LineItem.PriceData.ProductData.builder()
		                              .setName(name)
		                              .setDescription(description)
		                              .build())
							.build())
					.build());
			}
		}
	      
        SessionCreateParams params =
           SessionCreateParams.builder()
                  .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                  .setMode(SessionCreateParams.Mode.PAYMENT)
                  .setSuccessUrl(YOUR_DOMAIN + "/commande/succes")
                  .setCancelUrl(YOUR_DOMAIN + "/commande")
                  .setLocale(sessionLocale)
                  .addAllLineItem(lineItems)
                  .build();

        Session session = Session.create(params);
        return session;
	}
}
