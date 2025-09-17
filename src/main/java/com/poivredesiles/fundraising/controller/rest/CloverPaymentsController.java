package com.poivredesiles.fundraising.controller.rest;

import com.poivredesiles.fundraising.exception.InvalidOrderException;
import com.poivredesiles.fundraising.exception.OrderProcessingException;
import com.poivredesiles.fundraising.model.user.MyUserDetails;
import com.poivredesiles.fundraising.resource.OrderResource;
import com.poivredesiles.fundraising.service.CloverPaymentsService;
import com.poivredesiles.fundraising.service.PdiSellerService;
import com.poivredesiles.fundraising.service.dto.PdiSellerDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import java.io.IOException;

@RestController
@RequestMapping("/api/pay")
public class CloverPaymentsController {
    private final Logger log = LoggerFactory.getLogger(CloverPaymentsController.class);

    @Autowired
    private LocaleResolver localeResolver;

    @Autowired
    private CloverPaymentsService paymentsService;

    @Autowired
    private PdiSellerService pdiSellerService;

    @PostMapping("/charge")
    @Secured({"ROLE_BUYER"})
    public String chargeOrderAmount(@RequestBody OrderResource orderResource,
                                    @AuthenticationPrincipal MyUserDetails userDetails,
                                    HttpServletRequest request,
                                    HttpServletResponse response) throws IOException, InvalidOrderException, OrderProcessingException {
        PdiSellerDTO seller = pdiSellerService.getSellerForUser(userDetails);
        try {
            Long orderNumber = paymentsService.chargeOrderAmount(orderResource, seller.getId(), localeResolver.resolveLocale(request));
            return "/commande/succes?orderNum=" + orderNumber;
        } catch (OrderProcessingException e) {
            return "/commande?failure=true";
        }
    }

    @PostMapping("/webhook")
    public void processWebhook(){
        // TODO
    }

}
