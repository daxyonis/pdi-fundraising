package com.poivredesiles.fundraising.controller.rest;

import com.poivredesiles.fundraising.exception.InvalidOrderException;
import com.poivredesiles.fundraising.exception.OrderProcessingException;
import com.poivredesiles.fundraising.model.user.MyUserDetails;
import com.poivredesiles.fundraising.resource.OrderResource;
import com.poivredesiles.fundraising.service.BamboraPaymentsService;
import com.poivredesiles.fundraising.service.PdiSellerService;
import com.poivredesiles.fundraising.service.dto.PdiSellerDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import java.io.IOException;

@RestController
@RequestMapping("/api/pay")
public class BamboraPaymentsController {
    private final Logger log = LoggerFactory.getLogger(BamboraPaymentsController.class);

    @Autowired
    private LocaleResolver localeResolver;

    @Autowired
    private BamboraPaymentsService bamboraPaymentsService;

    @Autowired
    private PdiSellerService pdiSellerService;

    @PostMapping("/checkout")
    @Secured({"ROLE_BUYER"})
    public String createCheckoutUrl(@RequestBody OrderResource orderResource,
                                    @AuthenticationPrincipal MyUserDetails userDetails,
                                    HttpServletRequest request) throws InvalidOrderException, OrderProcessingException {
        PdiSellerDTO seller = pdiSellerService.getSellerForUser(userDetails);
        return bamboraPaymentsService.getCheckoutUrl(orderResource, seller.getId(), localeResolver.resolveLocale(request));
    }

    // This method must not be secured because called by Bambora
    @GetMapping(value="/callback")
    public void processResponse(@RequestParam MultiValueMap<String, String> responseData,
                                HttpServletRequest request,
                                HttpServletResponse response) throws IOException {
        try {
            Long orderNumber = bamboraPaymentsService.processResponse(responseData, localeResolver.resolveLocale(request));
            response.sendRedirect("/commande/succes?orderNum=" + orderNumber);
        } catch (OrderProcessingException e) {
            response.sendRedirect("/commande?failure=true");
        }
    }
}
