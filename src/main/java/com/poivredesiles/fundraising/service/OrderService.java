package com.poivredesiles.fundraising.service;

import java.util.List;
import java.util.Locale;

import com.poivredesiles.fundraising.exception.InvalidOrderException;
import com.poivredesiles.fundraising.model.order.OrderHeader;
import com.poivredesiles.fundraising.resource.OrderResource;
import com.poivredesiles.fundraising.service.dto.OrderHeaderDTO;
import com.poivredesiles.fundraising.service.dto.PdiSellerDTO;

public interface OrderService {

	/**
	 * Create a new pending order from an order resource
	 * @param orderResource
	 * @return the newly created order (with pending state)
	 * @throws InvalidOrderException
	 */
	OrderHeader createNewOrder(OrderResource orderResource, Locale locale) throws InvalidOrderException;

	/**
	 * Save the modified order
	 * @param pendingOrder
	 * @return
	 */
	OrderHeader save(OrderHeader order);

	/**
	 * Confirm a successful (i.e. paid) order if pending, then return it
	 * @param orderNumber	order number
	 * @return the confirmed order
	 */
	OrderHeaderDTO getConfirmedOrder(Long orderNumber);

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
	
}
