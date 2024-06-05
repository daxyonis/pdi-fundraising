package com.poivredesiles.fundraising.controller.rest;

import com.poivredesiles.fundraising.resource.OrdersRequest;
import com.poivredesiles.fundraising.resource.datatables.DataTablesResponse;
import com.poivredesiles.fundraising.resource.EntitySelector;
import com.poivredesiles.fundraising.service.OrderService;
import com.poivredesiles.fundraising.service.dto.OrderHeaderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    private final Logger log = LoggerFactory.getLogger(AdminRestController.class);

    @Autowired
    private OrderService orderService;

    @PostMapping("/orders")
    @Secured("ROLE_ADMIN")
    public DataTablesResponse<OrderHeaderDTO> getOrders(@RequestBody OrdersRequest ordersRequest) {

        EntitySelector entitySelector = new EntitySelector(ordersRequest.getStartDate(), ordersRequest.getEndDate(), ordersRequest.getStatus(), ordersRequest.getSearch().getValue());
        Pageable pageable = PageRequest.of(ordersRequest.getStart(), ordersRequest.getLength(), ordersRequest.getSort());
        return new DataTablesResponse<>(orderService.getOrders(entitySelector, pageable), ordersRequest.getDraw());
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

    // action on a batch of orders
    @PostMapping("/orders/batch")
    @Secured("ROLE_ADMIN")
    public void batchAction(@RequestParam String action, @RequestBody List<Long> orderIds) {
        switch(action) {
            case "resend_confirm":
                orderService.resendConfirmations(orderIds);
                break;
            case "resend_cancel":
                orderService.resendCancellations(orderIds);
                break;
            default:
                throw new UnsupportedOperationException("Unknown batch action: " + action);
        }
    }
}
