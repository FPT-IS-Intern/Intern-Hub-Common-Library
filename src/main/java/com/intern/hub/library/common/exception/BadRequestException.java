package com.intern.hub.library.common.exception;

import lombok.Getter;

/**
 * Exception thrown when a client request is malformed or contains invalid data.
 * <p>
 * This exception results in an HTTP 400 Bad Request response when handled by the
 * {@link com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice}.
 * </p>
 *
 * @see com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice#handleBadRequestException
 */
public class BadRequestException extends RuntimeException {

  /**
   * The error code identifying the specific type of bad request error.
   */
  @Getter
  private String code;

  /**
   * Constructs a BadRequestException with the specified error code.
   *
   * @param code the error code identifying the type of bad request
   */
  public BadRequestException(String code) {
    super();
    this.code = code;
  }

  /**
   * Constructs a BadRequestException with the specified error code and message.
   *
   * @param code    the error code identifying the type of bad request
   * @param message a detailed message describing what was wrong with the request
   */
  public BadRequestException(String code, String message) {
    super(message);
    this.code = code;
  }
}
