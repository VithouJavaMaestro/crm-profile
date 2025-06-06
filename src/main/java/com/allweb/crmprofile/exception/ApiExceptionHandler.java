package com.allweb.crmprofile.exception;

import com.crm.commons.specification.exception.BadRequestException;
import com.crm.commons.specification.exception.Error;
import com.crm.commons.specification.exception.SimpWebResponse;
import com.crm.commons.specification.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ApiExceptionHandler {

  public static final String INVALID_REQUEST = "invalid_request";

  public static final String UNEXPECTED_ERROR = "unexpected_error";
  private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

  @ExceptionHandler(WebExchangeBindException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Mono<SimpWebResponse> handleMethodArgumentNotValidException(WebExchangeBindException e) {
    FieldError fieldError = e.getBindingResult().getFieldError();
    if (fieldError == null) {
      return Mono.just(new SimpWebResponse(INVALID_REQUEST, INVALID_REQUEST));
    }
    String errorMessage = fieldError.getDefaultMessage();
    return Mono.just(new SimpWebResponse(errorMessage, INVALID_REQUEST));
  }

  @ExceptionHandler(RuntimeException.class)
  public Mono<SimpWebResponse> handleRuntimeException(RuntimeException e) {
    return Mono.create(
        sink -> {
          log.error(e.getMessage(), e);
          Error error = e.getClass().getAnnotation(Error.class);
          SimpWebResponse simpWebResponse;
          if (error == null) {
            simpWebResponse = new SimpWebResponse(UNEXPECTED_ERROR, "server_error");
          } else {
            String message = error.message();
            if (!StringUtils.hasText(message)) {
              message = e.getMessage();
            }
            String code = error.code();
            if (!StringUtils.hasText(code)) {
              code = error.code();
            }
            simpWebResponse = new SimpWebResponse(message, code);
          }
          sink.success(simpWebResponse);
        });
  }
}
