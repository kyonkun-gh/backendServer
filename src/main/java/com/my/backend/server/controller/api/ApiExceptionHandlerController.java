package com.my.backend.server.controller.api;

import com.my.backend.server.dto.ApiResponseDto;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestControllerAdvice
public class ApiExceptionHandlerController {

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponseDto<?> illegalArgumentHandler(Exception e) {
        e.printStackTrace();
        ApiResponseDto<?> response = new ApiResponseDto<>();
        response.setCode(100001);
        response.setMessage(e.getMessage());
        response.setData(null);
        response.setTimestamp(new Date().toString());
        return response;
    }

    @ExceptionHandler(RuntimeException.class)
    public ApiResponseDto<?> runtimeExceptionHandler(Exception e) {
        e.printStackTrace();
        ApiResponseDto<?> response = new ApiResponseDto<>();
        response.setCode(100002);
        response.setMessage(e.getMessage());
        response.setData(null);
        response.setTimestamp(new Date().toString());
        return response;
    }
}
