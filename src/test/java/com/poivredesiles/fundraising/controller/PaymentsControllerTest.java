package com.poivredesiles.fundraising.controller;

import com.poivredesiles.fundraising.config.SecurityConfig;
import com.poivredesiles.fundraising.config.properties.ApplicationProperties;
import com.poivredesiles.fundraising.controller.rest.CloverPaymentsController;
import com.poivredesiles.fundraising.filter.MaintenanceModeFilter;
import com.poivredesiles.fundraising.model.user.MyUserDetails;
import com.poivredesiles.fundraising.resource.OrderResource;
import com.poivredesiles.fundraising.service.CloverPaymentsService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Locale;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(CloverPaymentsController.class)
@Import({SecurityConfig.class, MaintenanceModeFilter.class, ApplicationProperties.class})
@ActiveProfiles("test")
public class PaymentsControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CloverPaymentsService paymentsService;

    private final static OrderResource orderResource = new OrderResource();

    @BeforeAll
    public static void init() {
        orderResource.setName("Bob");
        orderResource.setEmail("bob@example.com");
        orderResource.setPhone("0123456789");
    }

    @Test
    public void shouldNotAccessCheckoutWhenAnonymous() throws Exception {
        log.info("=====> Try to access checkout when anonymous...");
        this.mockMvc.perform(
                        post("/api/pay/charge")
                            .with(csrf()))
                 .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void shouldNotAccessCheckoutWithoutCsrf() throws Exception {
        log.info("=====> Try to access checkout with no CSRF...");
        this.mockMvc.perform(
                        post("/api/pay/charge")
                            .with(user("bidon").roles("BUYER")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void canAccessCheckoutIfAuthenticated() throws Exception {
        log.info("=====> Try to access campaigns when authenticated...");
        MyUserDetails userDetails = new MyUserDetails(buyer);
        when(pdiSellerService.getSellerForUser(userDetails)).thenReturn(seller);
        when(paymentsService.chargeOrderAmount(orderResource, 1L, Locale.FRENCH)).thenReturn(10L);
        this.mockMvc.perform(
                        post("/api/pay/charge")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\": \"Bob\", \"email\":\"bob@example.com\", \"phone\": \"0123456789\", \"sellerId\": \"1\"}")
                            .with(user(userDetails)))
                .andExpect(status().isOk());
    }

}
