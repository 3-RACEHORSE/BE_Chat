package com.skyhorsemanpower.chatService.common.response;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

public class ExceptionResponse extends ResponseEntity<String> {

    @Builder
    public ExceptionResponse(ResponseStatus responseStatus) {
        super(responseStatus.getMessage(), HttpStatus.valueOf(responseStatus.getCode()));
    }
}
