package com.my.backend.server.service;

import org.springframework.http.ResponseEntity;

public interface HttpRequestService {
    <T> ResponseEntity<T> sendGetRequest(String url, Class<T> responseType);
}
