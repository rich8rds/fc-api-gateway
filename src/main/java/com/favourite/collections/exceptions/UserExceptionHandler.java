/* Collections #2024 */
package com.favourite.collections.exceptions;

import com.favourite.collections.commons.core.data.ApiError;
import com.favourite.collections.commons.core.data.ApiSubError;
import com.favourite.collections.commons.core.data.ApiValidationError;
import com.favourite.collections.commons.core.exceptions.AbstractPlatformException;
import com.favourite.collections.commons.core.exceptions.GlobalExceptionHandler;
import com.favourite.collections.commons.useradmin.exception.ConstraintValidationException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class UserExceptionHandler extends GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Found Some Errors in MethodArgumentNotValidException", ex);
        List<ApiSubError> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(new ApiValidationError(fieldName, errorMessage));
        });

        ApiError errorResponse = ApiError.builder().message("Error found!").debugMessage("error.validation.on.field")
                .subErrors(errors).build();
        return ResponseEntity.status(400).body(errorResponse);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ApiError> webExchangeBindException(WebExchangeBindException ex) {
        log.error("Found Some Errors in WebExchangeBindException", ex);
        List<ApiSubError> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(new ApiValidationError(fieldName, errorMessage));
        });

        ApiError errorResponse = ApiError.builder().message("Error found!").debugMessage("error.validation.on.field")
                .subErrors(errors).build();
        return ResponseEntity.status(400).body(errorResponse);
    }

    @ExceptionHandler(AbstractPlatformException.class)
    public ResponseEntity<ApiError> abstractPlatformException(AbstractPlatformException ex) {

        ApiError errorResponse = ApiError.builder().message(ex.getDefaultUserMessage())
                .globalMessageCode(ex.getGlobalisationMessageCode()).debugMessage(ex.getLocalizedMessage())
                .subErrors(ex.getApiErrors()).build();

        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(ConstraintValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> constraintValidationException(ConstraintValidationException ex) {

        ApiError errorResponse = ApiError.builder().message(ex.getDefaultUserMessage())
                .globalMessageCode(ex.getGlobalisationMessageCode()).debugMessage(ex.getLocalizedMessage())
                .subErrors(ex.getApiErrors()).build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(BeanInstantiationException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ResponseEntity<ApiError> beanInstantiationException(BeanInstantiationException ex) {

        ApiError errorResponse = ApiError.builder().message(ex.getMessage())
                .globalMessageCode("Error instantiating bean").debugMessage(ex.getLocalizedMessage())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ResponseEntity<ApiError> usernameNotFoundException(UsernameNotFoundException ex) {

        ApiError errorResponse = ApiError.builder().message(ex.getMessage())
                .globalMessageCode("Error instantiating bean").debugMessage(ex.getLocalizedMessage())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> usernameNotFoundException(ExpiredJwtException ex) {

        ApiError errorResponse = ApiError.builder().message(ex.getMessage())
                .globalMessageCode("JWT has expired").debugMessage(ex.getLocalizedMessage())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

}
