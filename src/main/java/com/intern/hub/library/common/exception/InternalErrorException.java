package com.intern.hub.library.common.exception;

/**
 * Exception thrown when an internal server error occurs.
 * <p>
 * This exception is used for unexpected errors that are not the client's fault.
 * This exception results in an HTTP 500 Internal Server Error response when
 * handled
 * by the
 * {@link com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice}.
 * </p>
 *
 * @see com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice#handleInternalErrorException
 */
public class InternalErrorException extends BaseException {

  /**
   * Constructs an InternalErrorException with the specified error code.
   *
   * @param code the error code identifying the type of internal error
   */
  public InternalErrorException(String code) {
    super(code);
  }

  /**
   * Constructs an InternalErrorException with the specified error code and
   * message.
   *
   * @param code    the error code identifying the type of internal error
   * @param message a detailed message describing the error
   */
  public InternalErrorException(String code, String message) {
    super(code, message);
  }

  /**
   * Constructs an InternalErrorException with the specified error code, message,
   * and cause.
   *
   * @param code    the error code identifying the type of internal error
   * @param message a detailed message describing the error
   * @param cause   the cause of this exception
   */
  public InternalErrorException(String code, String message, Throwable cause) {
    super(code, message, cause);
  }

}
