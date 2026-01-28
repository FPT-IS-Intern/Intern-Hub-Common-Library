package com.intern.hub.library.common.exception;

/**
 * Exception thrown when a user attempts to access a resource they don't have
 * permission to access.
 * <p>
 * This exception is used when the user is authenticated but lacks the required
 * permissions
 * for the requested operation. This exception results in an HTTP 403 Forbidden
 * response
 * when handled by the
 * {@link com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice}.
 * </p>
 *
 * @see com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice#handleForbiddenException
 */
public class ForbiddenException extends BaseException {

  /**
   * Constructs a ForbiddenException with no code or message.
   */
  public ForbiddenException() {
    super(ExceptionConstant.FORBIDDEN_DEFAULT_CODE);
  }

  /**
   * Constructs a ForbiddenException with the specified error code.
   *
   * @param code the error code identifying the type of forbidden access
   */
  public ForbiddenException(String code) {
    super(code);
  }

  /**
   * Constructs a ForbiddenException with the specified error code and message.
   *
   * @param code    the error code identifying the type of forbidden access
   * @param message a detailed message describing why access is forbidden
   */
  public ForbiddenException(String code, String message) {
    super(code, message);
  }

  /**
   * Constructs a ForbiddenException with the specified error code, message, and
   * cause.
   *
   * @param code    the error code identifying the type of forbidden access
   * @param message a detailed message describing why access is forbidden
   * @param cause   the cause of this exception
   */
  public ForbiddenException(String code, String message, Throwable cause) {
    super(code, message, cause);
  }

}
