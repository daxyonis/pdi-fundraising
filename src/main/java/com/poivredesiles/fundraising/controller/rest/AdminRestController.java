package com.poivredesiles.fundraising.controller.rest;

import com.poivredesiles.fundraising.service.OrderService;
import com.poivredesiles.fundraising.service.dto.OrderHeaderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    private final Logger log = LoggerFactory.getLogger(AdminRestController.class);

    @Autowired
    private OrderService orderService;

    @GetMapping("/orders/pending")
    @Secured("ROLE_ADMIN")
    public List<OrderHeaderDTO> getPendingOrders() {
        return orderService.getPendingOrders();
    }

    // confirm one order
    @PutMapping("/orders/confirm")
    @Secured("ROLE_ADMIN")
    public void confirmOrder(@RequestParam Long orderNumber) {
        orderService.confirmOrder(orderNumber);
    }

    // cancel one order
    @PutMapping("/orders/cancel")
    @Secured("ROLE_ADMIN")
    public void cancelOrder(@RequestParam Long orderNumber) {
        orderService.cancelOrder(orderNumber);
    }
}
