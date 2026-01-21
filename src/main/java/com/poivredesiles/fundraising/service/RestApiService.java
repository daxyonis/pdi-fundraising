package com.poivredesiles.fundraising.service;

import tools.jackson.databind.JsonNode;

public interface RestApiService {

    JsonNode post(String baseUrl, String uri, String bearerToken, Object requestBody);
}
