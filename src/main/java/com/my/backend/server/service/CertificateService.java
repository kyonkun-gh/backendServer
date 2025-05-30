package com.my.backend.server.service;

import com.my.backend.server.dto.request.CertificateRequestDto;
import com.my.backend.server.dto.response.CertificateResponseDto;

import java.security.cert.CertificateException;

public interface CertificateService {
    CertificateResponseDto parseCertificate(CertificateRequestDto request) throws CertificateException;
}
