package com.intern.hub.library.common.exception;

import lombok.Getter;

/**
 * Exception thrown when a client has sent too many requests in a given amount of time.
 * <p>
 * This exception is used for rate limiting scenarios. This exception results in an
 * HTTP 429 Too Many Requests response when handled by the
 * {@link com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice}.
 * </p>
 *
 * @see com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice#handleTooManyRequestException
 */
public class TooManyRequestException extends RuntimeException {

  /**
   * The error code identifying the specific rate limit that was exceeded.
   */
  @Getter
  private String code;

  /**
   * Constructs a TooManyRequestException with the specified error code.
   *
   * @param code the error code identifying the type of rate limit
   */
  public TooManyRequestException(String code) {
    super();
    this.code = code;
  }

  /**
   * Constructs a TooManyRequestException with the specified error code and message.
   *
   * @param code    the error code identifying the type of rate limit
   * @param message a detailed message about the rate limit
   */
  public TooManyRequestException(String code, String message) {
    super(message);
    this.code = code;
  }

}
