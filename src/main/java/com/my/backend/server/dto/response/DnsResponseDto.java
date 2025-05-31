package com.my.backend.server.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class DnsResponseDto {
    private List<DnsAnswerDto> answer;
    private String errorMessage;
}
