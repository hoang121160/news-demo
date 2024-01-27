package com.example.demoweb.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MasterException extends RuntimeException {
    protected final HttpStatus exceptionCode;
    protected final String detailMessage;

    public MasterException(HttpStatus exceptionCode, String message) {
        this.detailMessage = message;
        this.exceptionCode = exceptionCode;
    }

    @Override
    public String toString() {
        return exceptionCode.toString() + ": " + detailMessage;
    }
}
