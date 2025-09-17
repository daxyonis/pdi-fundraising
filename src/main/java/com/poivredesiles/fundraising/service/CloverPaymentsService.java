package com.poivredesiles.fundraising.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.poivredesiles.fundraising.config.properties.ApplicationProperties;
import com.poivredesiles.fundraising.exception.InvalidOrderException;
import com.poivredesiles.fundraising.exception.OrderProcessingException;
import com.poivredesiles.fundraising.model.order.OrderHeader;
import com.poivredesiles.fundraising.resource.ChargeRequest;
import com.poivredesiles.fundraising.resource.OrderResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Locale;


@Service
public class CloverPaymentsService {

    private final ApplicationProperties applicationProperties;

    private final OrderService orderService;

    private final MessageSource messageSource;

    private final Environment env;

    private final Logger log = LoggerFactory.getLogger(CloverPaymentsService.class);

    public CloverPaymentsService(ApplicationProperties applicationProperties,
                                 OrderService orderService,
                                 MessageSource messageSource,
                                 Environment env) {
        this.applicationProperties = applicationProperties;
        this.orderService = orderService;
        this.messageSource = messageSource;
        this.env = env;
    }

    private String getReceiptEmailTo(OrderHeader order) {
        String to = applicationProperties.getMail().getTo();

        //******************************************
        // Send to buyer only if in production
        if(to.equals("leader") && Arrays.asList(env.getActiveProfiles()).contains("prod")) {
            to = order.getBuyerEmail();
        }
        //******************************************
        return to;
    }

    /**
     * Charges an amount on a securely encrypted credit card
     * Creates a new pending order and calls Clover Ecommerce API to make the charge
     * @param orderResource     the order information
     * @param sellerId          the seller id
     * @param locale            the current request locale
     * @return
     */
    public Long chargeOrderAmount(OrderResource orderResource, Long sellerId, Locale locale) throws InvalidOrderException, OrderProcessingException {
        // Payment platform parameters
        String payUrl = applicationProperties.getPay().url();
        String bearerToken = applicationProperties.getPay().privateToken();

        // Create new order
        OrderHeader pendingOrder = orderService.createNewOrder(orderResource, sellerId, locale);
        log.info("Charging payment for order #{}", pendingOrder.getOrderNumber());

        RestClient client = RestClient.builder()
                .baseUrl(payUrl)
                .build();

        BigInteger total = BigDecimal.valueOf(100.0 * pendingOrder.getTotal().doubleValue()).toBigInteger();
        String description = "Campagne [" + pendingOrder.getCampaignName() + "], Commande #" + pendingOrder.getOrderNumber();
        String email =  getReceiptEmailTo(pendingOrder);
        ChargeRequest request = new ChargeRequest(total,
                "cad",
                description,
                pendingOrder.getOrderNumber().toString(),
                email,
                orderResource.getToken()
        );

        try {
            JsonNode response = client.post()
                    .uri("/charges")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + bearerToken)
                    .body(request)
                    .retrieve()
                    .body(JsonNode.class);

            if (response != null) {
                return processResponse(response, pendingOrder, locale);
            } else {
                orderService.markOrderAsError(pendingOrder.getOrderNumber());
                throw new InvalidOrderException("Charging for the order failed");
            }
        } catch (Exception e) {
            orderService.markOrderAsError(pendingOrder.getOrderNumber());
            throw new InvalidOrderException(e.getMessage());
        }
    }


    /**
     * Processes the payment response
     * @param response              response data sent by the payment platform following a transaction
     * @param pendingOrder          the order pending completion
     * @param locale                current application locale
     * @return                      the paid order number, if successful ; otherwise an exception is thrown
     * @throws OrderProcessingException     if the response makes no sense or indicates that the transaction failed
     */
    public Long processResponse(JsonNode response, OrderHeader pendingOrder, Locale locale) throws OrderProcessingException {

        try {
            String status = response.get("status").asText();
            log.info("Processing payment response for order #{}", pendingOrder.getOrderNumber());
            if(status != null && status.equalsIgnoreCase("succeeded")) {
                // Success !
                orderService.confirmOrder(pendingOrder.getOrderNumber(), response.get("id").asText(""));
                log.info("Order #{} confirmed !", pendingOrder.getOrderNumber());
                return pendingOrder.getOrderNumber();
            } else {
                log.error("Failed transaction for order #{}, status = {}", pendingOrder.getOrderNumber(), status);
                orderService.markOrderAsError(pendingOrder.getOrderNumber());
                throw new OrderProcessingException(messageSource.getMessage("order.error.failure", null, locale));
            }

        } catch (Exception e) {
            log.error("Error post-processing payment response : {}", e.getLocalizedMessage());
            orderService.markOrderAsError(pendingOrder.getOrderNumber());
            throw new OrderProcessingException(messageSource.getMessage("order.error.postprocess", null, locale));
        }

    }


}
