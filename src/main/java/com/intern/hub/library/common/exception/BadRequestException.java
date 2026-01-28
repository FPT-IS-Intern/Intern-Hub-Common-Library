package com.intern.hub.library.common.exception;

/**
 * Exception thrown when a client request is malformed or contains invalid data.
 * <p>
 * This exception results in an HTTP 400 Bad Request response when handled by
 * the
 * {@link com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice}.
 * </p>
 *
 * @see com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice#handleBadRequestException
 */
public class BadRequestException extends BaseException {

  /**
   * Constructs a BadRequestException with the specified error code.
   *
   * @param code the error code identifying the type of bad request
   */
  public BadRequestException(String code) {
    super(code);
  }

  /**
   * Constructs a BadRequestException with the specified error code and message.
   *
   * @param code    the error code identifying the type of bad request
   * @param message a detailed message describing what was wrong with the request
   */
  public BadRequestException(String code, String message) {
    super(code, message);
  }

  /**
   * Constructs a BadRequestException with the specified error code, message, and
   * cause.
   *
   * @param code    the error code identifying the type of bad request
   * @param message a detailed message describing what was wrong with the request
   * @param cause   the cause of this exception
   */
  public BadRequestException(String code, String message, Throwable cause) {
    super(code, message, cause);
  }

}
