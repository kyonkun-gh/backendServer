package com.my.backend.server.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DnsGoogleDto {
    @JsonProperty("Status")
    private int status;

    @JsonProperty("TC")
    private boolean tC;

    @JsonProperty("RD")
    private boolean rD;

    @JsonProperty("RA")
    private boolean rA;

    @JsonProperty("AD")
    private boolean aD;

    @JsonProperty("CD")
    private boolean cD;

    @JsonProperty("Question")
    private List<DnsQuestionDto> question;

    @JsonProperty("Answer")
    private List<DnsAnswerDto> answer;

    @JsonProperty("Authority")
    private List<DnsAuthorityDto> authority;

    @JsonProperty("Comment")
    private String comment;
}
