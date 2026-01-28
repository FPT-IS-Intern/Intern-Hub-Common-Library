package com.intern.hub.library.common.exception;

/**
 * Exception thrown when a requested resource cannot be found.
 * <p>
 * This exception is typically used when attempting to access a resource by ID
 * that does not exist in the system. This exception results in an HTTP 404 Not
 * Found
 * response when handled by the
 * {@link com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice}.
 * </p>
 *
 * @see com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice#handleNotFoundException
 */
public class NotFoundException extends BaseException {

  /**
   * Constructs a NotFoundException with the specified error code.
   *
   * @param code the error code identifying the type of not found error
   */
  public NotFoundException(String code) {
    super(code);
  }

  /**
   * Constructs a NotFoundException with the specified error code and message.
   *
   * @param code    the error code identifying the type of not found error
   * @param message a detailed message describing what resource was not found
   */
  public NotFoundException(String code, String message) {
    super(code, message);
  }

  /**
   * Constructs a NotFoundException with the specified error code, message, and
   * cause.
   *
   * @param code    the error code identifying the type of not found error
   * @param message a detailed message describing what resource was not found
   * @param cause   the cause of this exception
   */
  public NotFoundException(String code, String message, Throwable cause) {
    super(code, message, cause);
  }

}
