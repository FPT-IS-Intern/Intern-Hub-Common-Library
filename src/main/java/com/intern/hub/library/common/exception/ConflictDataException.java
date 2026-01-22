package com.intern.hub.library.common.exception;

import lombok.Getter;

/**
 * Exception thrown when a request conflicts with the current state of a resource.
 * <p>
 * This is typically used when attempting to create a resource that already exists,
 * or when updating a resource that has been modified since it was last read.
 * This exception results in an HTTP 409 Conflict response when handled by the
 * {@link com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice}.
 * </p>
 *
 * @see com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice#handleConflictDataException
 */
public class ConflictDataException extends RuntimeException {

  /**
   * The error code identifying the specific type of conflict.
   */
  @Getter
  private String code;

  /**
   * Constructs a ConflictDataException with the specified error code.
   *
   * @param code the error code identifying the type of conflict
   */
  public ConflictDataException(String code) {
    super();
    this.code = code;
  }

  /**
   * Constructs a ConflictDataException with the specified error code and message.
   *
   * @param code    the error code identifying the type of conflict
   * @param message a detailed message describing the conflict
   */
  public ConflictDataException(String code, String message) {
    super(message);
    this.code = code;
  }

}
