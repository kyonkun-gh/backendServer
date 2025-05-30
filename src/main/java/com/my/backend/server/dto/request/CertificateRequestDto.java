package com.my.backend.server.dto.request;

import lombok.Data;
import lombok.NonNull;

@Data
public class CertificateRequestDto {

    @NonNull
    private String certificateB64;
}
