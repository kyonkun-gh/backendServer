package com.my.backend.server.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.backend.server.dto.response.DnsAnswerDto;
import com.my.backend.server.dto.response.DnsGoogleDto;
import com.my.backend.server.dto.response.DnsResponseDto;
import com.my.backend.server.service.DnsService;
import com.my.backend.server.service.HttpRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

@Service
public class DnsServiceImpl implements DnsService {

    @Autowired
    private HttpRequestService httpRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public DnsResponseDto resolveDns(String domain, String type) {
        // Logic to resolve DNS records for the given domain and type

        StringBuilder sb = new StringBuilder();
        sb.append("https://dns.google/resolve?name=").append(domain);
        if ( type != null && !type.isEmpty()) {
            sb.append("&type=").append(type);
        }

        DnsResponseDto dnsResponseDto = new DnsResponseDto();
        try {
            ResponseEntity<String> responseEntity = httpRequestService.sendGetRequest(sb.toString(), String.class);
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to resolve DNS: " + responseEntity.getStatusCode());
            }

            DnsGoogleDto dnsGoogleDto = objectMapper.readValue(responseEntity.getBody(), DnsGoogleDto.class);
            if (dnsGoogleDto.getStatus() == 0 && dnsGoogleDto.getAnswer() != null) {
                dnsResponseDto.setAnswer(dnsGoogleDto.getAnswer());
            }

        } catch (HttpClientErrorException e) {
            // Handle HTTP client error exceptions
            throw new RuntimeException(e.getMessage(), e);
        } catch (Exception e) {
            // Handle other exceptions
            throw new RuntimeException(e.getMessage(), e);
        }

        return dnsResponseDto;
    }
}
