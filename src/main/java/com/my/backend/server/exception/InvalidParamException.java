package com.my.backend.server.exception;

public class InvalidParamException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidParamException(String message) {
        super(message);
    }
}
