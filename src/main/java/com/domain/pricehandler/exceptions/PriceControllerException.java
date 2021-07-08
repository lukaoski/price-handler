package com.domain.pricehandler.exceptions;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * This class is used to inform consumer about why GET endpoint did not return price
 */
@Getter
public class PriceControllerException extends ResponseStatusException {
    private final String code;
    private final String details;

    @Builder
    public PriceControllerException(String message, Throwable cause, HttpStatus httpStatus, String code, String details) {
        super(httpStatus, message, cause);
        this.code = code;
        this.details = details;
    }
}
