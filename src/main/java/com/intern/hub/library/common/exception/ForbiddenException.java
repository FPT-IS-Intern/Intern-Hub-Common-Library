package com.intern.hub.library.common.exception;

import lombok.Getter;

/**
 * Exception thrown when a user attempts to access a resource they don't have permission to access.
 * <p>
 * This exception is used when the user is authenticated but lacks the required permissions
 * for the requested operation. This exception results in an HTTP 403 Forbidden response
 * when handled by the {@link com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice}.
 * </p>
 *
 * @see com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice#handleForbiddenException
 * @see com.intern.hub.library.common.annotation.HasPermission
 */
public class ForbiddenException extends RuntimeException {

  /**
   * The error code identifying the specific type of forbidden access.
   */
  @Getter
  private String code;

  /**
   * Constructs a ForbiddenException with no code or message.
   */
  public ForbiddenException() {
    super();
  }

  /**
   * Constructs a ForbiddenException with the specified error code.
   *
   * @param code the error code identifying the type of forbidden access
   */
  public ForbiddenException(String code) {
    super();
    this.code = code;
  }

  /**
   * Constructs a ForbiddenException with the specified error code and message.
   *
   * @param code    the error code identifying the type of forbidden access
   * @param message a detailed message describing why access is forbidden
   */
  public ForbiddenException(String code, String message) {
    super(message);
    this.code = code;
  }

}
