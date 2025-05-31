package com.my.backend.server.service;

import com.my.backend.server.dto.response.DnsResponseDto;

public interface DnsService {
    DnsResponseDto resolveDns(String domain, String type);
}
