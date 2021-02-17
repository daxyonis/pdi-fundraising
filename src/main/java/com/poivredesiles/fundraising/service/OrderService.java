package com.poivredesiles.fundraising.service;

import java.util.Locale;

import com.poivredesiles.fundraising.exception.InvalidOrderException;
import com.poivredesiles.fundraising.model.order.OrderHeader;
import com.poivredesiles.fundraising.resource.OrderResource;
import com.poivredesiles.fundraising.service.dto.OrderHeaderDTO;

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
	 * @param sessionId	Stripe session id
	 * @return the confirmed order
	 */
	OrderHeaderDTO getConfirmedOrder(String sessionId);
	
}
