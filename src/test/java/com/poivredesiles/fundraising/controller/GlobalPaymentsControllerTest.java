package com.poivredesiles.fundraising.controller;

import com.poivredesiles.fundraising.config.SecurityConfig;
import com.poivredesiles.fundraising.controller.rest.GlobalPaymentsController;
import com.poivredesiles.fundraising.resource.OrderResource;
import com.poivredesiles.fundraising.service.GlobalPaymentsService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WebMvcTest(GlobalPaymentsController.class)
@Import(SecurityConfig.class)
public class GlobalPaymentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GlobalPaymentsService globalPaymentsService;

    private static OrderResource orderResource = new OrderResource();

    @BeforeAll
    public static void init() {
        orderResource.setName("Bob");
        orderResource.setEmail("bob@example.com");
        orderResource.setPhone("0123456789");
        orderResource.setSellerId(1L);
    }

    @Test
    public void shouldNotAccessCheckoutWhenAnonymous() throws Exception {
        log.info("=====> Try to access checkout when anonymous...");
        this.mockMvc.perform(
                        post("/api/global/checkout")
                            .with(csrf()))
                 .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void canAccessCheckoutWithNoCsrf() throws Exception {
        log.info("=====> Try to access checkout with no CSRF...");
        when(globalPaymentsService.getHppJson(orderResource, Locale.FRENCH)).thenReturn("ok");
        this.mockMvc.perform(
                        post("/api/global/checkout")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\": \"Bob\", \"email\":\"bob@example.com\", \"phone\": \"0123456789\", \"sellerId\": \"1\"}")
                            .with(user("bidon").roles("BUYER")))
                .andExpect(status().isOk());
    }

    @Test
    public void canAccessCheckoutIfAuthenticated() throws Exception {
        log.info("=====> Try to access campaigns when authenticated...");
        when(globalPaymentsService.getHppJson(orderResource, Locale.FRENCH)).thenReturn("ok");
        this.mockMvc.perform(
                        post("/api/global/checkout")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\": \"Bob\", \"email\":\"bob@example.com\", \"phone\": \"0123456789\", \"sellerId\": \"1\"}")
                            .with(user("bidon").roles("BUYER")))
                .andExpect(status().isOk());
    }


    @Test
    public void canAccessResponseIfAnonymous() throws Exception {
        log.info("=====> Try to access response when anonymous...");
        MultiValueMap<String, String> responseData = new LinkedMultiValueMap<>();
        responseData.add("hppResponse", "adfkjaslfkja");
        when(globalPaymentsService.processResponse(responseData, Locale.FRENCH)).thenReturn(2250L);

        MockHttpServletRequestBuilder request = post("/api/global/response");
        request.content("hppResponse=adfkjaslfkja");
        request.locale(Locale.FRENCH);
        request.contentType(MediaType.APPLICATION_FORM_URLENCODED);

        this.mockMvc.perform(request)
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("/commande/succes?orderNum=2250"));
    }
}
