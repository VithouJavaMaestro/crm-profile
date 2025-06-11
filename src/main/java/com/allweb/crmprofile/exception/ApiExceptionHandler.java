package com.allweb.crmprofile.exception;

import com.crm.commons.specification.exception.Error;
import com.crm.commons.specification.exception.SimpWebResponse;
import com.crm.commons.specification.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

@RestControllerAdvice
public class ApiExceptionHandler {

  public static final String INVALID_REQUEST = "invalid_request";

  private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

  @ExceptionHandler(WebExchangeBindException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public SimpWebResponse handleMethodArgumentNotValidException(WebExchangeBindException e) {
    FieldError fieldError = e.getBindingResult().getFieldError();
    String errorMessage = fieldError.getDefaultMessage();
    return new SimpWebResponse(errorMessage, INVALID_REQUEST);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<SimpWebResponse> handleRuntimeException(RuntimeException e) {
    log.error(e.getMessage(), e);
    Error error = e.getClass().getAnnotation(Error.class);
    if (error == null) {
      return ResponseEntity.internalServerError()
          .body(new SimpWebResponse(e.getMessage(), "server_error"));
    } else {
      String message = error.message();
      if (!StringUtils.hasText(message)) {
        message = e.getMessage();
      }
      String code = error.code();
      if (!StringUtils.hasText(code)) {
        code = error.code();
      }
      return ResponseEntity.status(error.status()).body(new SimpWebResponse(message, code));
    }
  }
}
