package com.poivredesiles.fundraising.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;

import com.poivredesiles.fundraising.exception.InvalidOrderException;
import com.poivredesiles.fundraising.exception.OrderProcessingException;
import com.poivredesiles.fundraising.model.order.OrderHeader;
import com.poivredesiles.fundraising.model.order.OrderItem;
import com.poivredesiles.fundraising.model.order.OrderStatusEnum;
import com.poivredesiles.fundraising.model.product.PdiProduct;
import com.poivredesiles.fundraising.resource.OrderItemResource;
import com.poivredesiles.fundraising.resource.OrderResource;

@ExtendWith(MockitoExtension.class)
public class GlobalPaymentsServiceTest {

	@Mock
	private OrderService orderService;
	
	@Mock
	private MessageSource messageSource;
	
	@Autowired
	@InjectMocks
	private GlobalPaymentsService globalPaymentsService;	
	
	private OrderResource orderResource;
	private OrderHeader orderHeader;
	
	private final Logger log = LoggerFactory.getLogger(GlobalPaymentsServiceTest.class);
	
	@BeforeEach
	public void setup() {
		ReflectionTestUtils.setField(globalPaymentsService, "globalMerchantId", "dev730552577821985541");
		ReflectionTestUtils.setField(globalPaymentsService, "globalSharedSecret", System.getenv("GLOBAL_SHARED_SECRET"));
		ReflectionTestUtils.setField(globalPaymentsService, "globalServiceUrl", "https://pay.sandbox.realexpayments.com/pay");
		ReflectionTestUtils.setField(globalPaymentsService, "currency", "USD");
		
		orderResource = new OrderResource();
		orderResource.setSellerId(1L);
		orderResource.setPhone("514-909-5505");
		orderResource.setEmail("abc@example.com");
		orderResource.setName("Bertrand Jujube");
		
		List<OrderItemResource> items = new ArrayList<>();
		items.add(new OrderItemResource(55L, 1L));
		items.add(new OrderItemResource(56L, 2L));
		items.add(new OrderItemResource(23L, 1L));
		
		orderResource.setItems(items);
		
		orderHeader = getNewOrder();
	}
	
	private OrderHeader getNewOrder() {
		OrderHeader header = new OrderHeader();
		header.setId(123L);
		header.setBuyerName(orderResource.getName());
		header.setBuyerPhone(orderResource.getPhone());
		header.setOrderNumber(123456789L);
		header.setOrderStatus(OrderStatusEnum.PENDING);		
		header.setBuyerLanguage("FR");
		
		PdiProduct product = new PdiProduct();
		product.setNameEn("Black salt");
		product.setNameFr("Sel noir");
		Set<OrderItem> orderItems = new LinkedHashSet<>();
		for(OrderItemResource orderItem : orderResource.getItems())
		{
			OrderItem item = new OrderItem();		
			item.setId(orderItem.getId());
			item.setQuantity(orderItem.getQty());
			item.setOrderNumber(header.getOrderNumber());
			item.setProductNumber("100" + item.getId().toString());
			item.setUnitPrice(BigDecimal.valueOf(item.getId()).divide(BigDecimal.valueOf(10L)));
			item.setProduct(product);
			orderItems.add(item);
		}
		header.setOrderItems(orderItems);
		
		return header;
	}
	
	@Test
	void getHppJsonTest() throws InvalidOrderException, OrderProcessingException {
		log.info("--------------------------- GlobalPaymentService.getHppJson TEST ----------------------");
		
		when(orderService.createNewOrder(orderResource, Locale.FRENCH)).thenReturn(orderHeader);
		
		String hppJson = globalPaymentsService.getHppJson(orderResource, Locale.FRENCH);
		
		log.info("Returned JSON : {}", hppJson);
		assertTrue(hppJson.contains("\"ORDER_ID\":\"123456789\""));
		assertTrue(hppJson.contains("\"AMOUNT\":\"1900\""));
		assertTrue(hppJson.contains("\"HPP_CUSTOMER_EMAIL\":\"abc@example.com\""));
		assertTrue(hppJson.contains("\"HPP_CUSTOMER_COUNTRY\":\"CA\""));
		assertTrue(hppJson.contains("\"HPP_CUSTOMER_PHONENUMBER_MOBILE\":\"1|5149095505\""));
		assertTrue(hppJson.contains("\"HPP_CUSTOMER_LASTNAME\":\"Jujube\""));
		assertTrue(hppJson.contains("\"HPP_CUSTOMER_FIRSTNAME\":\"Bertrand\""));

	}	
}
