package com.my.backend.server.dto.response;

import lombok.Data;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
public class CertificateResponseDto {
    private String serialNumber;
    private String sigAlgName;
    private Date notBefore;
    private Date notAfter;
    private List<?> extendedKeyUsage;
    private Collection<List<?>> subjectAlternativeNames;
    private boolean[] keyUsage;
}
