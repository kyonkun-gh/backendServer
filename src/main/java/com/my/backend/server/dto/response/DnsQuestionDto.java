package com.my.backend.server.dto.response;

import lombok.Data;

@Data
public class DnsQuestionDto {
    private String name;
    private int type;
}
