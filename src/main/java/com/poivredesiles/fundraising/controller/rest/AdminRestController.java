package com.poivredesiles.fundraising.controller.rest;

import com.poivredesiles.fundraising.exception.PdiExportDataException;
import com.poivredesiles.fundraising.resource.EntitySelector;
import com.poivredesiles.fundraising.resource.OrdersRequest;
import com.poivredesiles.fundraising.resource.datatables.DataTablesResponse;
import com.poivredesiles.fundraising.service.NotificationService;
import com.poivredesiles.fundraising.service.OrderService;
import com.poivredesiles.fundraising.service.dto.NotificationSettingsDTO;
import com.poivredesiles.fundraising.service.dto.OrderHeaderDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    private final Logger log = LoggerFactory.getLogger(AdminRestController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/orders")
    @Secured("ROLE_ADMIN")
    public DataTablesResponse<OrderHeaderDTO> getOrders(@RequestBody OrdersRequest ordersRequest) {

        EntitySelector entitySelector = new EntitySelector(ordersRequest.getStartDate(), ordersRequest.getEndDate(), ordersRequest.getStatus(), ordersRequest.getSearch().getValue());
        Pageable pageable = Pageable.unpaged();
        if (ordersRequest.getLength() > 0) {
            int page = ordersRequest.getStart() / ordersRequest.getLength();
            pageable = PageRequest.of(page, ordersRequest.getLength(), ordersRequest.getSort());
        }
        return new DataTablesResponse<>(orderService.getOrders(entitySelector, pageable), ordersRequest.getDraw());
    }

    // confirm one order
    @PutMapping("/orders/confirm")
    @Secured("ROLE_ADMIN")
    public void confirmOrder(@RequestParam Long orderNumber) {
        orderService.confirmOrder(orderNumber, "");
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
    public int batchAction(@RequestParam String action, @RequestBody List<Long> orderIds) {
        switch(action) {
            case "resend_confirm":
                List<OrderHeaderDTO> reconfirmedOrders = orderService.resendConfirmations(orderIds);
                return reconfirmedOrders.size();
            case "resend_cancel":
                List<OrderHeaderDTO> recancelledOrders = orderService.resendCancellations(orderIds);
                return recancelledOrders.size();
            default:
                throw new UnsupportedOperationException("Unknown batch action: " + action);
        }
    }

    // download filtered orders as CSV
    @PostMapping("/orders/download")
    @Secured("ROLE_ADMIN")
    public void downloadFilteredOrders(
            @RequestBody OrdersRequest ordersRequest,
            HttpServletResponse response) throws PdiExportDataException, IOException {

        log.info("Exporting filtered orders to CSV");
        response.setContentType("text/csv");
        String filename = "orders-export-" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".csv";
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

        EntitySelector entitySelector = new EntitySelector(
                ordersRequest.getStartDate(),
                ordersRequest.getEndDate(),
                ordersRequest.getStatus(),
                ordersRequest.getSearch() != null ? ordersRequest.getSearch().getValue() : null
        );

        Pageable pageable = Pageable.unpaged();
        if (ordersRequest.getOrder() != null && !ordersRequest.getOrder().isEmpty()) {
            pageable = PageRequest.of(0, Integer.MAX_VALUE, ordersRequest.getSort());
        }

        orderService.exportFilteredOrders(entitySelector, pageable, response.getWriter());
    }

    @PostMapping("/notificationsettings")
    @Secured("ROLE_ADMIN")
    public String updateNotifications(@RequestBody NotificationSettingsDTO notificationSettings) {
        notificationService.updateNotificationSettings(notificationSettings);
        return "OK";
    }
}
