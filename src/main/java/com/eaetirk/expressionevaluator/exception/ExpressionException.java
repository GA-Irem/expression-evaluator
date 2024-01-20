package com.eaetirk.expressionevaluator.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class ExpressionException extends RuntimeException {

    private String code;
    
    private String message;
    
    private String reason;

    private HttpStatus httpStatus;

    
}
