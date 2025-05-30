package com.my.backend.server.controller.api.v1;

import com.my.backend.server.dto.ApiResponseDto;
import com.my.backend.server.dto.request.CertificateRequestDto;
import com.my.backend.server.dto.response.CertificateResponseDto;
import com.my.backend.server.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.cert.CertificateException;
import java.util.Date;

@RestController
@RequestMapping("/api/v1/certificate")
public class CertificateController {

    @Autowired
    CertificateService certificateService;

    @PostMapping("/parser")
    public ApiResponseDto<CertificateResponseDto> getCertificate(@RequestBody CertificateRequestDto request) {
        // Logic to retrieve a certificate
        ApiResponseDto<CertificateResponseDto> response = new ApiResponseDto<>();

        try {
            CertificateResponseDto certificateResponseDto = certificateService.parseCertificate(request);

            response.setCode(10000);
            response.setMessage("Success");
            response.setData(certificateResponseDto);
            response.setTimestamp( new Date().toString() );
        } catch (IllegalArgumentException e) {
            response.setCode(10001);
            response.setMessage(e.getMessage());
            response.setData(null);
            response.setTimestamp(new Date().toString());
            return response;
        } catch (CertificateException e) {
            response.setCode(10002);
            response.setMessage(e.getMessage());
            response.setData(null);
            response.setTimestamp(new Date().toString());
            return response;
        }

        return response;
    }
}
