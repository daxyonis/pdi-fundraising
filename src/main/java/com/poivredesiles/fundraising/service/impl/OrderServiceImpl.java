package com.poivredesiles.fundraising.service.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poivredesiles.fundraising.exception.InvalidOrderException;
import com.poivredesiles.fundraising.model.business.BusinessNumberTypeEnum;
import com.poivredesiles.fundraising.model.order.OrderHeader;
import com.poivredesiles.fundraising.model.order.OrderItem;
import com.poivredesiles.fundraising.model.product.PdiProduct;
import com.poivredesiles.fundraising.repository.group.PdiSellerRepository;
import com.poivredesiles.fundraising.repository.order.OrderHeaderRepository;
import com.poivredesiles.fundraising.repository.product.PdiProductRepository;
import com.poivredesiles.fundraising.resource.OrderItemResource;
import com.poivredesiles.fundraising.resource.OrderResource;
import com.poivredesiles.fundraising.service.BusinessNumberService;
import com.poivredesiles.fundraising.service.OrderService;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderHeaderRepository orderHeaderRepository;
	
	@Autowired
	private PdiSellerRepository pdiSellerRepository;
	
	@Autowired
	private BusinessNumberService businessNumberService; 
	
	@Autowired
	private PdiProductRepository pdiProductRepository;

	@Override
	public OrderHeader createNewOrder(OrderResource orderResource, Locale locale) throws InvalidOrderException {
		OrderHeader orderHeader = null;
		if (validate(orderResource)) {
			orderHeader = new OrderHeader();
			orderHeader.setOrderNumber(businessNumberService.getNextNumber(BusinessNumberTypeEnum.ORDER));
			orderHeader.setBuyerName(orderResource.getName());
			orderHeader.setBuyerPhone(orderResource.getPhone());
			orderHeader.setBuyerNote(orderResource.getNote());
			orderHeader.setBuyerLanguage(locale.getLanguage());
			orderHeader.setCreatedBy("system");
			orderHeader.setPdiSeller(pdiSellerRepository.getOne(orderResource.getSellerId()));
			setOrderItems(orderHeader, orderResource.getItems());
			orderHeaderRepository.save(orderHeader);
		} 
		return orderHeader;				
	}

	private void setOrderItems(OrderHeader orderHeader, List<OrderItemResource> orderItemResources) {
		for(OrderItemResource orderItemResource : orderItemResources) {
			// Get original product referenced
			PdiProduct pdiProduct = pdiProductRepository.getOne(orderItemResource.getId());
			OrderItem orderItem = new OrderItem();
			orderItem.setCreatedBy("system");
			orderItem.setOrderNumber(orderHeader.getOrderNumber());
			orderItem.setProduct(pdiProduct);
			orderItem.setProductNumber(pdiProduct.getProductNumber());
			orderItem.setQuantity(orderItemResource.getQty());
			orderItem.setUnitPrice(pdiProduct.getCategory().getUnitPrice());
			orderHeader.addOrderItem(orderItem);
		}
		
	}

	private boolean validate(OrderResource orderResource) throws InvalidOrderException {
		boolean valid = true;
		if(orderResource.getName().isBlank() || orderResource.getPhone().isBlank()) {
			throw new InvalidOrderException("Name and phone number cannot be blank !");
		}
//		if(!orderResource.getPhone().matches("/^\\(?\\d{3}\\)?\\s*(\\.|-)?\\d{3}\\s*(\\.|-)?\\d{4}.*$/")) {
//			throw new InvalidOrderException("Invalid phone number format !");
//		}
		return valid;
	}

	@Override
	public OrderHeader save(OrderHeader order) {
		return orderHeaderRepository.save(order);
	}

}
