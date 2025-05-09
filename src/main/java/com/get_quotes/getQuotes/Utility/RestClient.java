package com.get_quotes.getQuotes.Utility;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class RestClient {


    private final RestTemplate restTemplate = new RestTemplate();

    public <T> T postWithHeaders(String url, Object requestBody, HttpHeaders headers, Class<T> responseType) {
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<T> response = restTemplate.postForEntity(url, requestEntity, responseType);

        return response.getBody();
    }
}
