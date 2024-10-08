package com.poivredesiles.fundraising.service.impl;

import com.poivredesiles.fundraising.exception.InvalidOrderException;
import com.poivredesiles.fundraising.exception.ResourceNotFoundException;
import com.poivredesiles.fundraising.model.business.BusinessNumberTypeEnum;
import com.poivredesiles.fundraising.model.order.OrderHeader;
import com.poivredesiles.fundraising.model.order.OrderItem;
import com.poivredesiles.fundraising.model.order.OrderStatusEnum;
import com.poivredesiles.fundraising.model.product.PdiProduct;
import com.poivredesiles.fundraising.repository.group.PdiSellerRepository;
import com.poivredesiles.fundraising.repository.order.OrderHeaderProjection;
import com.poivredesiles.fundraising.repository.order.OrderHeaderRepository;
import com.poivredesiles.fundraising.repository.order.OrderItemRepository;
import com.poivredesiles.fundraising.repository.product.PdiProductRepository;
import com.poivredesiles.fundraising.resource.EntitySelector;
import com.poivredesiles.fundraising.resource.OrderItemResource;
import com.poivredesiles.fundraising.resource.OrderResource;
import com.poivredesiles.fundraising.service.BusinessNumberService;
import com.poivredesiles.fundraising.service.DateUtils;
import com.poivredesiles.fundraising.service.MailService;
import com.poivredesiles.fundraising.service.OrderService;
import com.poivredesiles.fundraising.service.dto.OrderHeaderDTO;
import com.poivredesiles.fundraising.service.dto.OrderItemDTO;
import com.poivredesiles.fundraising.service.dto.PdiSellerDTO;
import com.poivredesiles.fundraising.service.mapper.OrderHeaderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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

	@Autowired
	private MailService mailService;
	
	@Value("${application.order.confirmation}")
	private String orderConfirmationFormat;

	@Autowired
	private DateUtils dateUtils;

	
	private final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

	@Override
	public OrderHeader createNewOrder(OrderResource orderResource, Long sellerId, Locale locale) throws InvalidOrderException {
		OrderHeader orderHeader = null;
		if (validate(orderResource, locale)) {
			orderHeader = new OrderHeader();
			orderHeader.setOrderNumber(businessNumberService.getNextNumber(BusinessNumberTypeEnum.ORDER));
			orderHeader.setBuyerName(orderResource.getName());
			orderHeader.setBuyerPhone(orderResource.getPhone());
			orderHeader.setBuyerEmail(orderResource.getEmail());
			orderHeader.setBuyerNote(orderResource.getNote());
			orderHeader.setBuyerLanguage(locale.getLanguage());
			orderHeader.setCreatedBy("system");
			orderHeader.setPdiSeller(pdiSellerRepository.findById(sellerId).orElseThrow());
			setOrderItems(orderHeader, orderResource.getItems());
			orderHeaderRepository.save(orderHeader);
		} 
		return orderHeader;				
	}

	private void setOrderItems(OrderHeader orderHeader, List<OrderItemResource> orderItemResources) {
		for(OrderItemResource orderItemResource : orderItemResources) {
			// Get original product referenced
			PdiProduct pdiProduct = pdiProductRepository.findById(orderItemResource.getId()).orElseThrow();
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
			throw new InvalidOrderException(messageSource.getMessage("order.error.requiredfields", null, locale));
		}
//		if(!orderResource.getPhone().matches("/^\\(?\\d{3}\\)?\\s*(\\.|-)?\\d{3}\\s*(\\.|-)?\\d{4}.*$/")) {
//			throw new InvalidOrderException("Invalid phone number format !");
//		}
		if(orderResource.getEmail() == null || orderResource.getEmail().isBlank()) {
			throw new InvalidOrderException(messageSource.getMessage("order.error.email", null, locale));
		}
		
//		valid = validateAddress(orderResource.getAddress(), locale);
		
		return valid;
	}

	@Override
	public OrderHeader save(OrderHeader order) {
		return orderHeaderRepository.save(order);
	}



	@Override
	public OrderHeaderDTO getConfirmedOrder(Long orderNumber) {
		Optional<OrderHeader> optionalOrderHeader = orderHeaderRepository.findOneByOrderNumber(orderNumber);
		if(optionalOrderHeader.isPresent()) {
			OrderHeader order = optionalOrderHeader.get();
			if(order.getConfirmationNumber() != null && order.getOrderStatus() == OrderStatusEnum.PAID) {
				OrderHeaderDTO orderDTO = orderHeaderMapper.toDto(order);
				sortOrderItems(orderDTO);
				return orderDTO;
			} else {
				throw new ResourceNotFoundException("Order not confirmed");
			}
		} else {
			throw new ResourceNotFoundException("Invalid argument");
		}
	}

	private void sortOrderItems(OrderHeaderDTO orderDTO) {
		List<OrderItemDTO> sortedOrderItems = orderDTO.getOrderItems();
		if (orderDTO.getBuyerLanguage().equalsIgnoreCase("fr")) {
			Collections.sort(sortedOrderItems, Comparator.comparing(OrderItemDTO::getUnitPrice).reversed()
					.thenComparing(OrderItemDTO::getFormatFr)
					.thenComparing(OrderItemDTO::getNameFr));
		} else {
			Collections.sort(sortedOrderItems, Comparator.comparing(OrderItemDTO::getUnitPrice).reversed()
					.thenComparing(OrderItemDTO::getFormatEn)
					.thenComparing(OrderItemDTO::getNameEn));
		}
	}

	@Override
	public void confirmOrder(Long orderNumber) {
		Optional<OrderHeader> optionalOrderHeader = orderHeaderRepository.findOneByOrderNumber(orderNumber);
		if(optionalOrderHeader.isPresent()) {
			OrderHeader order = optionalOrderHeader.get();
			if (order.getConfirmationNumber() == null || order.getCancelDate() != null) {
				order.setConfirmationNumber(String.format(orderConfirmationFormat, order.getOrderNumber()));
				order.setOrderStatus(OrderStatusEnum.PAID);
				order.setConfirmationDate(Instant.now());
				OrderHeaderDTO orderHeaderDto = orderHeaderMapper.toDto(order);
				sortOrderItems(orderHeaderDto);
				mailService.sendOrderConfirmationEmail(orderHeaderDto, Locale.forLanguageTag(order.getBuyerLanguage()));
			}
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
			orderItemRepository.deleteAllInBatch(orderHeader.getOrderItems());
			orderHeaderRepository.delete(orderHeader);
			log.info("Deleted order #{}", orderHeader.getOrderNumber());
		} else {
			log.warn("Order could not be deleted : was null.");
		}
	}

	@Override
	public void markOrderAsError(Long orderNumber) {
		Optional<OrderHeader> optionalOrderHeader = orderHeaderRepository.findOneByOrderNumber(orderNumber);
		if(optionalOrderHeader.isPresent()) {
			optionalOrderHeader.get().setOrderStatus(OrderStatusEnum.ERROR);
		} else {
			throw new ResourceNotFoundException("Invalid argument");
		}		
	}

	@Override
	public OrderHeader findByOrderNumber(Long orderNumber) {
		Optional<OrderHeader> optionalOrderHeader = orderHeaderRepository.findOneByOrderNumber(orderNumber);
		if(optionalOrderHeader.isPresent()) {
			return  optionalOrderHeader.get();
		} else {
			throw new ResourceNotFoundException("Invalid argument");
		}
	}

	@Override
	public List<OrderHeaderDTO> getPendingOrders() {
		List<OrderHeader> orderHeaders = orderHeaderRepository.findByOrderStatusOrderByIdDesc(OrderStatusEnum.PENDING);
		return orderHeaderMapper.toDto(orderHeaders);
	}

	@Override
	public List<OrderHeaderDTO> getOrders() {
		List<OrderHeader> orderHeaders = orderHeaderRepository.findAllByOrderByIdDesc();
		return orderHeaderMapper.toDto(orderHeaders);
	}

	@Override
	public void cancelOrder(Long orderNumber) {
		Optional<OrderHeader> optionalOrderHeader = orderHeaderRepository.findOneByOrderNumber(orderNumber);
		if(optionalOrderHeader.isPresent()) {
			OrderHeader order = optionalOrderHeader.get();
			var previousOrderStatus = order.getOrderStatus();
			if (order.getOrderStatus() == OrderStatusEnum.PENDING || order.getOrderStatus() == OrderStatusEnum.PAID) {
				order.setOrderStatus(OrderStatusEnum.CANCELLED);
				order.setCancelDate(Instant.now());
				OrderHeaderDTO orderHeaderDto = orderHeaderMapper.toDto(order);
				sortOrderItems(orderHeaderDto);
				if (previousOrderStatus == OrderStatusEnum.PAID) {
					mailService.sendOrderCancelEmail(orderHeaderDto, Locale.forLanguageTag(order.getBuyerLanguage()));
				}
			} else {
				throw new UnsupportedOperationException("Cannot cancel order in status " + order.getOrderStatus());
			}
		} else {
			throw new ResourceNotFoundException("Invalid argument");
		}
	}

	@Override
	public List<OrderHeaderDTO> resendConfirmations(List<Long> orderIds) {
		List<OrderHeader> orderHeaders = orderHeaderRepository.findAllByOrderStatusAndIdIn(OrderStatusEnum.PAID, orderIds);
		List<OrderHeaderDTO> orderHeaderDtos = new ArrayList<>();
		for (OrderHeader orderHeader : orderHeaders) {
			OrderHeaderDTO orderHeaderDto = orderHeaderMapper.toDto(orderHeader);
			sortOrderItems(orderHeaderDto);
			mailService.sendOrderConfirmationEmail(orderHeaderDto, Locale.forLanguageTag(orderHeader.getBuyerLanguage()));
            orderHeaderDtos.add(orderHeaderDto);
		}
		return orderHeaderDtos;
	}

	@Override
	public List<OrderHeaderDTO> resendCancellations(List<Long> orderIds) {
		List<OrderHeader> orderHeaders = orderHeaderRepository.findAllByOrderStatusAndIdIn(OrderStatusEnum.CANCELLED, orderIds);
		List<OrderHeaderDTO> orderHeaderDtos = new ArrayList<>();
		for (OrderHeader orderHeader : orderHeaders) {
			OrderHeaderDTO orderHeaderDto = orderHeaderMapper.toDto(orderHeader);
			sortOrderItems(orderHeaderDto);
			mailService.sendOrderCancelEmail(orderHeaderDto, Locale.forLanguageTag(orderHeader.getBuyerLanguage()));
			orderHeaderDtos.add(orderHeaderDto);
		}
		return orderHeaderDtos;
	}

	@Override
	public Page<OrderHeaderDTO> getOrders(EntitySelector entitySelector, Pageable pageable) {

		// Build the specification given the filters in entitySelector
		Specification<OrderHeader> spec = Specification.where(null);
		if (entitySelector.getStartDate() != null && entitySelector.getEndDate() != null) {
			spec = spec.and((root, query, cb) -> cb.between(root.get("createdDate"), dateUtils.convertToInstant(entitySelector.getStartDate()), dateUtils.convertToInstant(entitySelector.getEndDate())));
		}

		if (entitySelector.getStatus() != null && !entitySelector.getStatus().isEmpty()) {
			try{
				OrderStatusEnum orderStatus = OrderStatusEnum.valueOf(entitySelector.getStatus());
				spec = spec.and((root, query, cb) -> cb.equal(root.get("orderStatus"), orderStatus));
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid order status");
			}
		}

		if (entitySelector.getSearch() != null && !entitySelector.getSearch().isEmpty()) {
			// Test that the search string is a number
			if (entitySelector.getSearch().matches("\\d+")) {
				Long orderNumber = Long.parseLong(entitySelector.getSearch());
				Specification<OrderHeader> searchSpec = Specification.where((root, query, cb) -> cb.equal(root.get("orderNumber"), orderNumber));
				spec = spec.and(searchSpec);
			}
			else {
				log.debug("Search string is not a number");
				// We need to do it this way because textual search fields are encrypted
				ArrayList<Long> ids = getIdsOfMatchingStringFields(entitySelector.getSearch().toLowerCase());
				log.info("Found {} orders matching search string", ids.size());
				Specification<OrderHeader> searchSpec = Specification.where((root, query, cb) -> root.get("id").in(ids));
				spec = spec.and(searchSpec);
			}
		}

		Page<OrderHeader> orders = orderHeaderRepository.findAll(spec, pageable);

		return orders.map(orderHeaderMapper::toDto);
	}

	private ArrayList<Long> getIdsOfMatchingStringFields(String lowerCaseSearch) {
		ArrayList<Long> ids = new ArrayList<>();
		long numOrders = orderHeaderRepository.count();
		int i=0;
		do {
			Page<OrderHeaderProjection> fields = orderHeaderRepository.findAllProjectedBy(PageRequest.of(i, 100));
			fields.forEach(field -> {
				String buyerName = field.getBuyerName() == null ? "" : field.getBuyerName().toLowerCase();
				String buyerEmail = field.getBuyerEmail() == null ? "" : field.getBuyerEmail().toLowerCase();
				if (buyerName.contains(lowerCaseSearch) || buyerEmail.contains(lowerCaseSearch)) {
					ids.add(field.getId());
				}
			});
			i += fields.getNumberOfElements();
			log.info("Found {} orders matching search string out of {}", ids.size(), i);
		} while (i < numOrders);
		return ids;
	}

}
