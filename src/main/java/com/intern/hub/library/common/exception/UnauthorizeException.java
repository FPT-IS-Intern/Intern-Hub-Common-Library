package com.intern.hub.library.common.exception;

import lombok.Getter;

/**
 * Exception thrown when authentication is required or has failed.
 * <p>
 * This exception is used when a request requires authentication but the user
 * has not provided valid credentials or the authentication has expired.
 * This exception results in an HTTP 401 Unauthorized response when handled by the
 * {@link com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice}.
 * </p>
 *
 * @see com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice#handleUnauthorizeException
 */
public class UnauthorizeException extends RuntimeException {

  /**
   * The error code identifying the specific authentication failure.
   */
  @Getter
  private String code;

  /**
   * Constructs an UnauthorizeException with the specified error code.
   *
   * @param code the error code identifying the type of authentication failure
   */
  public UnauthorizeException(String code) {
    super();
    this.code = code;
  }

  /**
   * Constructs an UnauthorizeException with the specified error code and message.
   *
   * @param code    the error code identifying the type of authentication failure
   * @param message a detailed message describing the authentication failure
   */
  public UnauthorizeException(String code, String message) {
    super(message);
    this.code = code;
  }
}
