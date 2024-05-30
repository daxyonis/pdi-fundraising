package com.poivredesiles.fundraising.controller.rest;

import com.poivredesiles.fundraising.model.order.OrderBatchActionEnum;
import com.poivredesiles.fundraising.resource.EntitySelector;
import com.poivredesiles.fundraising.service.OrderService;
import com.poivredesiles.fundraising.service.dto.OrderHeaderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderRestController {

    @Autowired
    private OrderService orderService;

    /**
     * Perform a batch action on orders
     * Send confirmation email for all paid orders within a time range
     * @param entitySelector the entity selector for filtering orders
     * @return success msg
     */
    @PostMapping("/batch")
    @Secured("ROLE_ADMIN")
    public List<OrderHeaderDTO> resendPaidOrdersConfirmation(@RequestParam OrderBatchActionEnum batchAction, @RequestBody EntitySelector entitySelector) {
        switch(batchAction) {
            case RECONFIRM_PAID:
                return orderService.reconfirmOrdersWithin(entitySelector);
            default:
                throw new UnsupportedOperationException("Unknown batch action: " + batchAction);
        }
    }
}
