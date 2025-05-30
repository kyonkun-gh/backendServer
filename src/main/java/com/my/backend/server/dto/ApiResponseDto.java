package com.my.backend.server.dto;

import lombok.Data;

@Data
public class ApiResponseDto<T> {
    private int code;
    private String message;
    private T data;
    private String timestamp;
}
