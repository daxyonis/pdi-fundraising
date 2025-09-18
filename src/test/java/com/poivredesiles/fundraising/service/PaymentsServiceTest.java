package com.poivredesiles.fundraising.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.poivredesiles.fundraising.config.properties.ApplicationProperties;
import com.poivredesiles.fundraising.exception.InvalidOrderException;
import com.poivredesiles.fundraising.exception.OrderProcessingException;
import com.poivredesiles.fundraising.model.group.PdiCampaign;
import com.poivredesiles.fundraising.model.group.PdiGroup;
import com.poivredesiles.fundraising.model.group.PdiSeller;
import com.poivredesiles.fundraising.model.order.OrderHeader;
import com.poivredesiles.fundraising.model.order.OrderItem;
import com.poivredesiles.fundraising.resource.ChargeRequest;
import com.poivredesiles.fundraising.resource.OrderItemResource;
import com.poivredesiles.fundraising.resource.OrderResource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentsServiceTest {

	@Mock
	private ApplicationProperties applicationProperties;

	@Mock
	private OrderService orderService;

	@Mock
	private MessageSource messageSource;

	@Mock
	private Environment env;

	@Mock
	private RestApiService restApiService;

	@InjectMocks
	private CloverPaymentsService service;

	private final Logger log = LoggerFactory.getLogger(PaymentsServiceTest.class);

	private final static String BASE_URL = "https://scl-sandbox.dev.clover.com/v1";
	private final static String TOKEN = "token";

	private static OrderResource orderResource;

	private OrderHeader orderHeader;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private static ApplicationProperties.Pay pay;
	private static ApplicationProperties.Mail mail;
	private static PdiSeller seller;

	@BeforeAll
	public static void init() {
		orderResource = new OrderResource();
		orderResource.setName("Bobby Brown");
		orderResource.setPhone("514-804-4040");
		orderResource.setNote("test");
		orderResource.setEmail("bobby@test.com");
		orderResource.setToken(TOKEN);
		orderResource.setItems(List.of(
				new OrderItemResource(110L, 1L),
				new OrderItemResource(113L, 2L)
		));
		pay = new ApplicationProperties.Pay();
		pay.setUrl(BASE_URL);
		pay.setPrivateToken(TOKEN);

		mail = new ApplicationProperties.Mail();
		mail.setTo("toto@test.com");

		seller = new PdiSeller();
		seller.setPdiGroup(new PdiGroup());
		seller.getPdiGroup().setPdiCampaign(new PdiCampaign());
		seller.getPdiGroup().getPdiCampaign().setProject("Ecole ABC");
	}

	@BeforeEach
	public void setup() throws InvalidOrderException {
		orderHeader = new OrderHeader();
		orderHeader.setOrderNumber(123L);
		orderHeader.setBuyerEmail(orderResource.getEmail());
		orderHeader.setBuyerName(orderResource.getName());
		orderHeader.setBuyerPhone(orderResource.getPhone());
		orderHeader.setBuyerNote(orderResource.getNote());
		orderHeader.setBuyerLanguage(Locale.FRENCH.getLanguage());
		orderHeader.setPdiSeller(seller);

		OrderItem item1 = new OrderItem();
		item1.setHeader(orderHeader);
		item1.setQuantity(1L);
		item1.setUnitPrice(BigDecimal.valueOf(5L));
		OrderItem item2 = new OrderItem();
		item2.setHeader(orderHeader);
		item2.setQuantity(2L);
		item2.setUnitPrice(BigDecimal.valueOf(10L));
		orderHeader.setOrderItems(Set.of(item1, item2));


//		when(env.getActiveProfiles()).thenReturn(new String[]{"test"});
	}

	@Test
	void chargeAmount_shouldReturnOrderNumber_whenSuccessful() throws InvalidOrderException, OrderProcessingException {
		when(applicationProperties.getPay()).thenReturn(pay);
		when(applicationProperties.getMail()).thenReturn(mail);
		when(orderService.createNewOrder(orderResource, 1L, Locale.FRENCH)).thenReturn(orderHeader);

		String description = "Campagne [Ecole ABC], Commande #123";
		ChargeRequest request = new ChargeRequest(BigInteger.valueOf(2500L),
				"cad",
				description,
				"123",
				"toto@test.com",
				TOKEN
		);

		ObjectNode response = objectMapper.createObjectNode();
		response.put("status", "succeeded");
		response.put("id", "txn-001");

		when(restApiService.post(BASE_URL, "/charges", TOKEN, request)).thenReturn(response);

		Long result = service.chargeOrderAmount(orderResource, 1L, Locale.FRENCH);
		assertThat(result).isEqualTo(123L);
	}

	@Test
	void processResponse_shouldConfirmOrder_whenSucceeded() throws Exception {

		ObjectNode response = objectMapper.createObjectNode();
		response.put("status", "succeeded");
		response.put("id", "txn-001");

		Long result = service.processResponse(response, orderHeader, Locale.ENGLISH);

		assertThat(result).isEqualTo(123L);
		verify(orderService).confirmOrder(123L, "txn-001");
	}
}
