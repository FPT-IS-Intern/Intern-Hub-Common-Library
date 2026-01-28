package com.intern.hub.library.common.exception;

/**
 * Exception thrown when data conflicts with existing state in the system.
 * <p>
 * This exception is typically used in scenarios such as duplicate entries,
 * optimistic locking failures, or version conflicts. This exception results
 * in an HTTP 409 Conflict response when handled by the
 * {@link com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice}.
 * </p>
 *
 * @see com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice#handleConflictDataException
 */
public class ConflictDataException extends BaseException {

  /**
   * Constructs a ConflictDataException with the specified error code.
   *
   * @param code the error code identifying the type of data conflict
   */
  public ConflictDataException(String code) {
    super(code);
  }

  /**
   * Constructs a ConflictDataException with the specified error code and message.
   *
   * @param code    the error code identifying the type of data conflict
   * @param message a detailed message describing the conflict
   */
  public ConflictDataException(String code, String message) {
    super(code, message);
  }

  /**
   * Constructs a ConflictDataException with the specified error code, message,
   * and cause.
   *
   * @param code    the error code identifying the type of data conflict
   * @param message a detailed message describing the conflict
   * @param cause   the cause of this exception
   */
  public ConflictDataException(String code, String message, Throwable cause) {
    super(code, message, cause);
  }

}
