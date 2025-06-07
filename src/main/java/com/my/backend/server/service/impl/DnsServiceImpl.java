package com.my.backend.server.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.backend.server.dto.response.DnsAnswerDto;
import com.my.backend.server.dto.response.DnsGoogleDto;
import com.my.backend.server.dto.response.DnsResponseDto;
import com.my.backend.server.enums.DnsType;
import com.my.backend.server.service.DnsService;
import com.my.backend.server.service.HttpRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.ResolverConfig;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

@Service
public class DnsServiceImpl implements DnsService {

    @Autowired
    private HttpRequestService httpRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    public DnsServiceImpl() {
        List<InetSocketAddress> resolvers = ResolverConfig.getCurrentConfig().servers();
        for ( InetSocketAddress resolver : resolvers ) {
            System.out.println( String.format("dig resolver is %s", resolver.getHostName()) );
        }
    }

    @Override
    public DnsResponseDto resolveDns(String domain, String type, String method) {
        // Logic to resolve DNS records for the given domain and type
        if ( method == null || method.equals("doh") ) {
            return doh(domain, type);
        }

        return dig(domain, type);
    }

    public DnsResponseDto doh(String domain, String type) {
        if ( type == null || type.isEmpty() ) {
            type = "A"; // Default to A record if no type is specified
        }

        String url = UriComponentsBuilder.fromHttpUrl("https://dns.google/resolve")
                .queryParam("name", domain)
                .queryParam("type", type)
                .toUriString();

        DnsResponseDto dnsResponseDto = new DnsResponseDto();
        try {
            ResponseEntity<String> responseEntity = httpRequestService.sendGetRequest(url, String.class);
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("解析 DNS失敗: " + responseEntity.getStatusCode());
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

    public DnsResponseDto dig(String domain, String type) {
        DnsType dnsType = DnsType.fromNameOrValue(type);
        if ( dnsType == null ) {
            throw new IllegalArgumentException("不支援的 DNS type: " + type);
        }

        DnsResponseDto dnsResponseDto = new DnsResponseDto();
        try {
            List<DnsAnswerDto> dnsAnswerDtos = new ArrayList<>();

            // Check if the domain has CNAME records
            Lookup lookup = new Lookup(domain, Type.CNAME);
            lookup.run();
            if ( lookup.getResult() == Lookup.SUCCESSFUL ) {
                for ( Record record : lookup.getAnswers() ) {
                    DnsAnswerDto dnsAnswerDto = new DnsAnswerDto();
                    dnsAnswerDto.setName(record.getName().toString());
                    dnsAnswerDto.setType(record.getType());
                    dnsAnswerDto.setTTL((int)record.getTTL());
                    dnsAnswerDto.setData(record.rdataToString());
                    dnsAnswerDtos.add(dnsAnswerDto);
                }
            }

            if ( dnsType.getCode() != Type.CNAME ) {
                // Perform the DNS lookup for the specified type
                lookup = new Lookup(domain, dnsType.getCode());
                lookup.run();
                if ( lookup.getResult() == Lookup.SUCCESSFUL ) {
                    for ( Record record : lookup.getAnswers() ) {
                        DnsAnswerDto dnsAnswerDto = new DnsAnswerDto();
                        dnsAnswerDto.setName(record.getName().toString());
                        dnsAnswerDto.setType(record.getType());
                        dnsAnswerDto.setTTL((int)record.getTTL());
                        dnsAnswerDto.setData(record.rdataToString());
                        dnsAnswerDtos.add(dnsAnswerDto);
                    }
                }
            }

            if ( !dnsAnswerDtos.isEmpty() ) {
                dnsResponseDto.setAnswer(dnsAnswerDtos);
            }
        } catch (TextParseException e) {
            throw new RuntimeException(e);
        }

        return dnsResponseDto;
    }
}
