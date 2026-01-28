package com.intern.hub.library.common.exception;

/**
 * Exception thrown when authentication is required but not provided or is
 * invalid.
 * <p>
 * This exception is used when the user is not authenticated or has invalid
 * credentials.
 * This exception results in an HTTP 401 Unauthorized response when handled by
 * the
 * {@link com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice}.
 * </p>
 *
 * @see com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice#handleUnauthorizeException
 */
public class UnauthorizeException extends BaseException {

  /**
   * Constructs an UnauthorizeException with no code or message.
   */
  public UnauthorizeException() {
    super(ExceptionConstant.UNAUTHORIZED_DEFAULT_CODE);
  }

  /**
   * Constructs an UnauthorizeException with the specified error code.
   *
   * @param code the error code identifying the type of authentication failure
   */
  public UnauthorizeException(String code) {
    super(code);
  }

  /**
   * Constructs an UnauthorizeException with the specified error code and message.
   *
   * @param code    the error code identifying the type of authentication failure
   * @param message a detailed message describing the authentication failure
   */
  public UnauthorizeException(String code, String message) {
    super(code, message);
  }

  /**
   * Constructs an UnauthorizeException with the specified error code, message,
   * and cause.
   *
   * @param code    the error code identifying the type of authentication failure
   * @param message a detailed message describing the authentication failure
   * @param cause   the cause of this exception
   */
  public UnauthorizeException(String code, String message, Throwable cause) {
    super(code, message, cause);
  }

}
