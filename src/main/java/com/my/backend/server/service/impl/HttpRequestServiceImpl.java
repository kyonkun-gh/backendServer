package com.my.backend.server.service.impl;

import com.my.backend.server.service.HttpRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpRequestServiceImpl implements HttpRequestService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public <T> ResponseEntity<T> sendGetRequest(String url, Class<T> responseType) {
        // Logic to send a GET request to the specified URL
        return restTemplate.getForEntity(url, responseType);
    }
}
