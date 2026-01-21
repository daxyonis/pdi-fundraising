package com.poivredesiles.fundraising.service.impl;

import com.poivredesiles.fundraising.service.RestApiService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

@Service
public class RestApiServiceImpl implements RestApiService {

    @Override
    public JsonNode post(String baseUrl, String uri, String bearerToken, Object requestBody) {
        RestClient client = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
        return client.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + bearerToken)
                .body(requestBody)
                .retrieve()
                .body(JsonNode.class);
    }
}
