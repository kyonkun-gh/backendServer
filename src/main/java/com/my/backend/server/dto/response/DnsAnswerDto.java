package com.my.backend.server.dto.response;

import lombok.Data;

@Data
public class DnsAnswerDto {
    private String name;
    private int type;
    private int ttl;
    private String data;
}
