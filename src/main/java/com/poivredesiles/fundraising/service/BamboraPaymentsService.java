package com.poivredesiles.fundraising.service;

import com.poivredesiles.fundraising.config.properties.ApplicationProperties;
import com.poivredesiles.fundraising.exception.InvalidOrderException;
import com.poivredesiles.fundraising.exception.OrderProcessingException;
import com.poivredesiles.fundraising.model.order.OrderHeader;
import com.poivredesiles.fundraising.resource.OrderResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Map;


@Service
public class BamboraPaymentsService {

    private ApplicationProperties applicationProperties;

    private final OrderService orderService;

    private final MailService mailService;

    private final MessageSource messageSource;

    private final Environment env;

    private final Logger log = LoggerFactory.getLogger(BamboraPaymentsService.class);

    public BamboraPaymentsService(ApplicationProperties applicationProperties,
                                  OrderService orderService,
                                  MailService mailService,
                                  MessageSource messageSource,
                                  Environment env) {
        this.applicationProperties = applicationProperties;
        this.orderService = orderService;
        this.mailService = mailService;
        this.messageSource = messageSource;
        this.env = env;
    }

    /**
     * Generates the secured payment checkout URL
     * Creates a new order and gathers relevant info to include into the checkout URL
     * @param orderResource     the order information
     * @param sellerId          the seller id
     * @param locale            the current request locale
     * @return
     */
    public String getCheckoutUrl(OrderResource orderResource, Long sellerId, Locale locale) throws InvalidOrderException {
        // Payment platform parameters
        String payUrl = applicationProperties.getPay().url();
        String merchantId = applicationProperties.getPay().merchantId();
        String hashKey = applicationProperties.getPay().hashKey();

        // Create new order
        OrderHeader pendingOrder = orderService.createNewOrder(orderResource, sellerId, locale);
        log.info("Charging payment for order #{}", pendingOrder.getOrderNumber());

        // All the infos we want to include into the payment form
        String amount = pendingOrder.getTotal().toString();
        String name = URLEncoder.encode(pendingOrder.getBuyerName(), StandardCharsets.UTF_8);
        String email = URLEncoder.encode(pendingOrder.getBuyerEmail(), StandardCharsets.UTF_8);
        String phone = URLEncoder.encode(pendingOrder.getBuyerPhone(), StandardCharsets.UTF_8);
        String orderNumber = pendingOrder.getOrderNumber().toString();
        String language = pendingOrder.getBuyerLanguage().equalsIgnoreCase("FR") ? "FRE" : "EN";
        String timestamp = pendingOrder.getPayTimestamp();

        String hashData = "merchant_id=" + merchantId + "&trnAmount=" + amount +
                          "&trnOrderNumber=" + orderNumber +
                          "&ordName=" + name +
                          "&ordEmailAddress=" + email +
                          "&shipPhoneNumber=" + phone +
                          "&trnLanguage=" + language +
                          "&ref1=" + timestamp;

        String hashInput = hashData + hashKey;
        try {
            String hash = generateSHA1Hash(hashInput);
            return String.format("%s?%s&hashValue=%s", payUrl, hashData, hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Hashes a string input
     * @param input     the string to hash using SHA-1
     * @return          the hashed string
     * @throws NoSuchAlgorithmException
     */
    private String generateSHA1Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(digest);
    }


    /**
     * Processes the payment response
     * @param responseData          response data sent by the payment platform following a transaction
     * @param locale                current application locale
     * @return                      the paid order number, if successful ; otherwise an exception is thrown
     * @throws OrderProcessingException     if the response makes no sense or indicates that the transaction failed
     */
    public Long processResponse(MultiValueMap<String, String> responseData, Locale locale) throws OrderProcessingException {

        Map<String, String> responseValues = responseData.toSingleValueMap();
        String orderNum = responseValues.get("trnOrderNumber");
        String transactionType = responseValues.get("trnType");
        if (orderNum == null || orderNum.isBlank() || transactionType == null || !transactionType.equals("P")) {
            log.error("No response provided.");
            throw new OrderProcessingException(messageSource.getMessage("order.error.invalidresponse", null, locale));
        }

        try {
            String responseCode = responseValues.get("trnApproved");
            String responseMessage = responseValues.get("messageText");
            log.info("Processing Global Response for order #{}", orderNum);
            if(responseCode != null && responseCode.compareTo("1") == 0) {
                // Success !
                validateOrderResponse(responseValues, locale);
                orderService.confirmOrder(Long.parseLong(orderNum));
                log.info("Order #{} confirmed !", orderNum);
                return Long.parseLong(orderNum);
            } else {
                log.error("Failed transaction for order #{}, code = {}, message={}", orderNum, responseCode, responseMessage);
                orderService.markOrderAsError(Long.parseLong(orderNum));
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
     * @param responseValues    values map as returned by the payment platform response
     * @param locale            current request locale
     */
    private void validateOrderResponse(Map<String, String> responseValues, Locale locale) throws OrderProcessingException {

        // Check the order
        Long orderNum= Long.parseLong(responseValues.get("trnOrderNumber"));
        // Check the amount
        String strAmount = responseValues.get("trnAmount");
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(strAmount));
        // Check the timestamp
        String timestamp = responseValues.get("ref1");

        orderService.validatePostPayment(orderNum, amount, timestamp, locale);
    }
}
