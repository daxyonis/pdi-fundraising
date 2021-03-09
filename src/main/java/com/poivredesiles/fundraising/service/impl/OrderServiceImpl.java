package com.poivredesiles.fundraising.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poivredesiles.fundraising.exception.InvalidOrderException;
import com.poivredesiles.fundraising.exception.ResourceNotFoundException;
import com.poivredesiles.fundraising.model.business.BusinessNumberTypeEnum;
import com.poivredesiles.fundraising.model.order.OrderHeader;
import com.poivredesiles.fundraising.model.order.OrderItem;
import com.poivredesiles.fundraising.model.order.OrderStatusEnum;
import com.poivredesiles.fundraising.model.product.PdiProduct;
import com.poivredesiles.fundraising.repository.group.PdiSellerRepository;
import com.poivredesiles.fundraising.repository.order.OrderHeaderRepository;
import com.poivredesiles.fundraising.repository.order.OrderItemRepository;
import com.poivredesiles.fundraising.repository.product.PdiProductRepository;
import com.poivredesiles.fundraising.resource.OrderItemResource;
import com.poivredesiles.fundraising.resource.OrderResource;
import com.poivredesiles.fundraising.service.BusinessNumberService;
import com.poivredesiles.fundraising.service.OrderService;
import com.poivredesiles.fundraising.service.dto.OrderHeaderDTO;
import com.poivredesiles.fundraising.service.dto.PdiSellerDTO;
import com.poivredesiles.fundraising.service.mapper.OrderHeaderMapper;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderHeaderRepository orderHeaderRepository;
	
	@Autowired
	private OrderItemRepository orderItemRepository;
	
	@Autowired
	private PdiSellerRepository pdiSellerRepository;
	
	@Autowired
	private BusinessNumberService businessNumberService; 
	
	@Autowired
	private PdiProductRepository pdiProductRepository;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private OrderHeaderMapper orderHeaderMapper;
	
	@Value("${application.order.confirmation}")
	private String orderConfirmationFormat;
	
	private final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

	@Override
	public OrderHeader createNewOrder(OrderResource orderResource, Locale locale) throws InvalidOrderException {
		OrderHeader orderHeader = null;
		if (validate(orderResource, locale)) {
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

	private boolean validate(OrderResource orderResource, Locale locale) throws InvalidOrderException {
		boolean valid = true;
		if(orderResource.getName().isBlank() || orderResource.getPhone().isBlank()) {
			log.error("Invalid name and/or phone field(s) in order.");
			throw new InvalidOrderException(messageSource.getMessage("order.error.requiredfields", null, locale));
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

	@Override
	public OrderHeaderDTO getConfirmedOrder(String sessionId) {
		Optional<OrderHeader> optionalOrderHeader = orderHeaderRepository.findByStripeSessionId(sessionId);
		if(optionalOrderHeader.isPresent()) {
			OrderHeader order = optionalOrderHeader.get();
			if(order.getConfirmationNumber() == null) {
				order.setConfirmationNumber(String.format(orderConfirmationFormat, order.getOrderNumber()));
				order.setOrderStatus(OrderStatusEnum.PAID);
			}
			OrderHeaderDTO orderDTO = orderHeaderMapper.toDto(order);
			return orderDTO;
		} else {
			throw new ResourceNotFoundException("Invalid argument");
		}
	}

	@Override
	public List<OrderHeaderDTO> getPaidOrdersForSeller(PdiSellerDTO seller) {
		Set<OrderHeader> orderHeaders = orderHeaderRepository.findByOrderStatusAndPdiSeller_id(OrderStatusEnum.PAID, seller.getId());
		return orderHeaderMapper.toDto(orderHeaders.stream().sorted(Comparator.comparing(OrderHeader::getId)).collect(Collectors.toList()));
	}

	@Override	
	public void deleteOrder(OrderHeader orderHeader) {
		if(orderHeader != null) {
			orderItemRepository.deleteInBatch(orderHeader.getOrderItems());
			orderHeaderRepository.delete(orderHeader);
			log.info("Deleted order #{}", orderHeader.getOrderNumber());
		} else {
			log.warn("Order could not be deleted : was null.");
		}
	}

}
