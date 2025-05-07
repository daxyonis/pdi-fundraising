package com.poivredesiles.fundraising.service;

import com.poivredesiles.fundraising.exception.InvalidOrderException;
import com.poivredesiles.fundraising.exception.OrderProcessingException;
import com.poivredesiles.fundraising.model.order.OrderHeader;
import com.poivredesiles.fundraising.resource.EntitySelector;
import com.poivredesiles.fundraising.resource.OrderResource;
import com.poivredesiles.fundraising.service.dto.OrderHeaderDTO;
import com.poivredesiles.fundraising.service.dto.PdiSellerDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

public interface OrderService {

	/**
	 * Create a new pending order from an order resource
	 * @param orderResource
	 * @return the newly created order (with pending state)
	 * @throws InvalidOrderException
	 */
	OrderHeader createNewOrder(OrderResource orderResource, Long sellerId, Locale locale) throws InvalidOrderException;

	/**
	 * Save the modified order
	 * @param pendingOrder
	 * @return
	 */
	OrderHeader save(OrderHeader order);

	/**
	 * Check an order is confirmed, then return it
	 * @param orderNumber	order number
	 * @return the confirmed order
	 */
	OrderHeaderDTO getConfirmedOrder(Long orderNumber);

	/**
	 * Confirm a successful (i.e. paid) order if pending
	 * @param orderNumber
	 */
	void confirmOrder(Long orderNumber);

	/**
	 * Get the list of orders assigned to a given seller
	 * @param seller the seller data transfer object
	 * @return the list of orders for this seller
	 */
	List<OrderHeaderDTO> getPaidOrdersForSeller(PdiSellerDTO seller);

	/**
	 * Delete one order : all its items and its header
	 * @param orderHeader
	 */
	void deleteOrder(OrderHeader orderHeader);

	/**
	 * Change order status to error
	 * @param orderNumber	the order number
	 */
	void markOrderAsError(Long orderNumber);

	/**
	 * Find one by order number
	 * @param orderNumber
	 * @return
	 */
	OrderHeader findByOrderNumber(Long orderNumber);

	/**
	 * Get the list of pending orders
	 * @return : the list of pending orders
	 */
	List<OrderHeaderDTO> getPendingOrders();

	/**
	 * Get the list of all orders
	 * @return : the list of pending orders
	 */
	List<OrderHeaderDTO> getOrders();

	/**
	 * Get a  list of orders, filtered, paginated and sorted
	 * @param entitySelector	the filters
	 * @param pageable			the pagination
	 * @return a page of orders DTO
	 */
	Page<OrderHeaderDTO> getOrders(EntitySelector entitySelector, Pageable pageable);

	/**
	 * Cancel an order
	 * @param orderNumber
	 */
	void cancelOrder(Long orderNumber);

	/**
	 * Resend the confirmations for a list of paid orders
	 * @param orderIds	the list of order ids
	 * @return
	 */
	List<OrderHeaderDTO> resendConfirmations(List<Long> orderIds);

	List<OrderHeaderDTO> resendCancellations(List<Long> orderIds);

	void validatePostPayment(Long orderNum, BigDecimal amount, String timestamp, Locale locale) throws OrderProcessingException;
}
