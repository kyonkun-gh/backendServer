package com.my.backend.server.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DnsAnswerDto {
    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private int type;

    @JsonProperty("TTL")
    private int TTL;

    @JsonProperty("data")
    private String data;
}
