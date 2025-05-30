package com.my.backend.server.service.impl;

import com.my.backend.server.dto.request.CertificateRequestDto;
import com.my.backend.server.dto.response.CertificateResponseDto;
import com.my.backend.server.service.CertificateService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

@Service
public class CertificateServiceImpl implements CertificateService {
    // Implement the methods defined in CertificateService interface here

    @Override
    public CertificateResponseDto parseCertificate(CertificateRequestDto request) {
        String certB64 = request.getCertificateB64();
        byte[] decode = Base64.getDecoder().decode(certB64);
        CertificateResponseDto certificateResponseDto = new CertificateResponseDto();

        try {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate)factory.generateCertificate(new ByteArrayInputStream(decode));

            certificateResponseDto.setSerialNumber(certificate.getSerialNumber().toString(16));
            certificateResponseDto.setSigAlgName(certificate.getSigAlgName());
            certificateResponseDto.setNotBefore(certificate.getNotBefore());
            certificateResponseDto.setNotAfter(certificate.getNotAfter());
            certificateResponseDto.setExtendedKeyUsage(certificate.getExtendedKeyUsage());
            certificateResponseDto.setSubjectAlternativeNames(certificate.getSubjectAlternativeNames());
            certificateResponseDto.setKeyUsage(certificate.getKeyUsage());
        } catch (CertificateException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return certificateResponseDto;
    }
}
