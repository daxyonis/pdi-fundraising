package com.poivredesiles.fundraising.service;

import com.poivredesiles.fundraising.config.properties.ApplicationProperties;
import com.poivredesiles.fundraising.exception.InvalidOrderException;
import com.poivredesiles.fundraising.model.order.OrderHeader;
import com.poivredesiles.fundraising.model.order.OrderItem;
import com.poivredesiles.fundraising.model.order.OrderStatusEnum;
import com.poivredesiles.fundraising.model.product.PdiProduct;
import com.poivredesiles.fundraising.resource.OrderItemResource;
import com.poivredesiles.fundraising.resource.OrderResource;
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

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentsServiceTest {

	@Mock
	private OrderService orderService;
	
	@Mock
	private MessageSource messageSource;

	@Mock
	private ApplicationProperties applicationProperties;
	
	@Autowired
	@InjectMocks
	private BamboraPaymentsService bamboraPaymentsService;
	
	private OrderResource orderResource;
	private OrderHeader orderHeader;
	
	private final Logger log = LoggerFactory.getLogger(PaymentsServiceTest.class);
	
	@BeforeEach
	public void setup() {
		orderResource = new OrderResource();
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
		header.setBuyerEmail(orderResource.getEmail());
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
	void getCheckoutUrlTest() throws InvalidOrderException {
		log.info("--------------------------- BamboraPaymentService.getCheckoutUrl TEST ----------------------");

		when(orderService.createNewOrder(orderResource, 1L, Locale.FRENCH)).thenReturn(orderHeader);

		// Setup the application properties
		ApplicationProperties.Pay pay = new ApplicationProperties.Pay();
		pay.setUrl("https://web.na.bambora.com/scripts/payment/payment.asp");
		pay.setHashKey("abcdefg");
		pay.setMerchantId("1234567890");
		when(applicationProperties.getPay()).thenReturn(pay);
		
		String checkoutUrl = bamboraPaymentsService.getCheckoutUrl(orderResource, 1L, Locale.FRENCH);
		
		log.info("Returned JSON : {}", checkoutUrl);
		assertTrue(checkoutUrl.contains("merchant_id=1234567890"));
		assertTrue(checkoutUrl.contains("trnOrderNumber=123456789"));
		assertTrue(checkoutUrl.contains("trnAmount=19.00"));
		assertTrue(checkoutUrl.contains("shipPhoneNumber=514-909-5505"));
		assertTrue(checkoutUrl.contains("ordName=Bertrand+Jujube"));

	}	
}
