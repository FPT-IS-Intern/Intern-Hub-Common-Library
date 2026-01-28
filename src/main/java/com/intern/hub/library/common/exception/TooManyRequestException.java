package com.intern.hub.library.common.exception;

/**
 * Exception thrown when a client has sent too many requests in a given time
 * period.
 * <p>
 * This exception is used for rate limiting scenarios. This exception results in
 * an
 * HTTP 429 Too Many Requests response when handled by the
 * {@link com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice}.
 * </p>
 *
 * @see com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice#handleTooManyRequestException
 */
public class TooManyRequestException extends BaseException {

  /**
   * Constructs a TooManyRequestException with the specified error code.
   *
   * @param code the error code identifying the rate limit type
   */
  public TooManyRequestException(String code) {
    super(code);
  }

  /**
   * Constructs a TooManyRequestException with the specified error code and
   * message.
   *
   * @param code    the error code identifying the rate limit type
   * @param message a detailed message describing the rate limit
   */
  public TooManyRequestException(String code, String message) {
    super(code, message);
  }

  /**
   * Constructs a TooManyRequestException with the specified error code, message,
   * and cause.
   *
   * @param code    the error code identifying the rate limit type
   * @param message a detailed message describing the rate limit
   * @param cause   the cause of this exception
   */
  public TooManyRequestException(String code, String message, Throwable cause) {
    super(code, message, cause);
  }

}
