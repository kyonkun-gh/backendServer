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
import org.springframework.web.util.UriComponentsBuilder;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.ResolverConfig;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DnsServiceImpl implements DnsService {

    @Autowired
    private HttpRequestService httpRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Map<String, Integer> DNS_TYPE_MAP = new HashMap<>();
    static {
        DNS_TYPE_MAP.put("A", Type.A);
        DNS_TYPE_MAP.put("NS", Type.NS);
        DNS_TYPE_MAP.put("CNAME", Type.CNAME);
        DNS_TYPE_MAP.put("SOA", Type.SOA);
        DNS_TYPE_MAP.put("PTR", Type.PTR);
        DNS_TYPE_MAP.put("MX", Type.MX);
        DNS_TYPE_MAP.put("TXT", Type.TXT);
        DNS_TYPE_MAP.put("AAAA", Type.AAAA);
        DNS_TYPE_MAP.put("SRV", Type.SRV);
        DNS_TYPE_MAP.put("CAA", Type.CAA);

        DNS_TYPE_MAP.put("1", Type.A);
        DNS_TYPE_MAP.put("2", Type.NS);
        DNS_TYPE_MAP.put("5", Type.CNAME);
        DNS_TYPE_MAP.put("6", Type.SOA);
        DNS_TYPE_MAP.put("12", Type.PTR);
        DNS_TYPE_MAP.put("15", Type.MX);
        DNS_TYPE_MAP.put("16", Type.TXT);
        DNS_TYPE_MAP.put("28", Type.AAAA);
        DNS_TYPE_MAP.put("33", Type.SRV);
        DNS_TYPE_MAP.put("257", Type.CAA);
    }

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
        int dnsType = getDnsType(type);
        if ( dnsType == -1 ) {
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

            if ( dnsType != Type.CNAME ) {
                // Perform the DNS lookup for the specified type
                lookup = new Lookup(domain, dnsType);
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

    private int getDnsType(String type) {
        if (type == null || type.isEmpty()) {
            return Type.A;
        }

        return DNS_TYPE_MAP.getOrDefault(type.toUpperCase(), -1);
    }
}
