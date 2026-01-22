package com.intern.hub.library.common.exception;

import lombok.Getter;

/**
 * Exception thrown when an internal server error occurs.
 * <p>
 * This exception is used to wrap unexpected errors or indicate that something
 * went wrong on the server side. This exception results in an HTTP 500 Internal Server Error
 * response when handled by the {@link com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice}.
 * </p>
 *
 * @see com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice#handleInternalErrorException
 */
public class InternalErrorException extends RuntimeException {

  /**
   * The error code identifying the specific type of internal error.
   */
  @Getter
  private String code;

  /**
   * Constructs an InternalErrorException with the specified error code.
   *
   * @param code the error code identifying the type of internal error
   */
  public InternalErrorException(String code) {
    super();
    this.code = code;
  }

  /**
   * Constructs an InternalErrorException with the specified error code and message.
   *
   * @param code    the error code identifying the type of internal error
   * @param message a detailed message describing the internal error
   */
  public InternalErrorException(String code, String message) {
    super(message);
    this.code = code;
  }
}
