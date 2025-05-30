package com.my.backend.server.service;

import com.my.backend.server.dto.request.CertificateRequestDto;
import com.my.backend.server.dto.response.CertificateResponseDto;

public interface CertificateService {
    CertificateResponseDto parseCertificate(CertificateRequestDto request);
}
