package com.intern.hub.library.common.exception;

/**
 * Constants used for exception handling throughout the library.
 * <p>
 * This class defines standard error messages and error codes that are used by
 * the exception classes and the global exception handler to provide consistent
 * error responses.
 * </p>
 *
 * <p><b>Error Messages:</b></p>
 * <ul>
 *   <li>{@link #UNKNOWN_ERROR} - Default message for unknown errors</li>
 *   <li>{@link #RESOURCE_NOT_FOUND} - Message when a resource is not found</li>
 *   <li>{@link #BAD_REQUEST} - Message for bad request errors</li>
 *   <li>{@link #CONFLICT_DATA} - Message for data conflict errors</li>
 *   <li>{@link #FORBIDDEN} - Message for forbidden access</li>
 *   <li>{@link #UNAUTHORIZED} - Message for authentication failures</li>
 *   <li>{@link #TOO_MANY_REQUESTS} - Message for rate limiting</li>
 * </ul>
 *
 * <p><b>Error Codes:</b></p>
 * <ul>
 *   <li>{@link #INTERNAL_SERVER_ERROR_DEFAULT_CODE} - Default code for internal errors</li>
 *   <li>{@link #NOT_FOUND_DEFAULT_CODE} - Default code for not found errors</li>
 *   <li>{@link #BAD_REQUEST_DEFAULT_CODE} - Default code for bad request errors</li>
 *   <li>{@link #CONFLICT_DATA_DEFAULT_CODE} - Default code for conflict errors</li>
 *   <li>{@link #FORBIDDEN_DEFAULT_CODE} - Default code for forbidden errors</li>
 *   <li>{@link #UNAUTHORIZED_DEFAULT_CODE} - Default code for unauthorized errors</li>
 *   <li>{@link #TOO_MANY_REQUESTS_DEFAULT_CODE} - Default code for rate limit errors</li>
 *   <li>{@link #INVALID_PARAMETER_DEFAULT_CODE} - Default code for invalid parameter errors</li>
 * </ul>
 *
 * @see com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice
 */
public final class ExceptionConstant {

  /** Default error message for unknown or unhandled errors. */
  public static final String UNKNOWN_ERROR = "Unknown error";

  /** Error message when a requested resource cannot be found. */
  public static final String RESOURCE_NOT_FOUND = "Resource not found";

  /** Error message for malformed or invalid requests. */
  public static final String BAD_REQUEST = "Bad request";

  /** Error message when data conflicts with existing state. */
  public static final String CONFLICT_DATA = "Conflict data";

  /** Error message when access to a resource is forbidden. */
  public static final String FORBIDDEN = "Forbidden";

  /** Error message when authentication is required or has failed. */
  public static final String UNAUTHORIZED = "Unauthorized";

  /** Error message when rate limits have been exceeded. */
  public static final String TOO_MANY_REQUESTS = "Too many requests";

  /** Default error code for internal server errors (HTTP 500). */
  public static final String INTERNAL_SERVER_ERROR_DEFAULT_CODE = "internal.server.error";

  /** Default error code for resource not found errors (HTTP 404). */
  public static final String NOT_FOUND_DEFAULT_CODE = "resource.not.found";

  /** Default error code for bad request errors (HTTP 400). */
  public static final String BAD_REQUEST_DEFAULT_CODE = "bad.request";

  /** Default error code for data conflict errors (HTTP 409). */
  public static final String CONFLICT_DATA_DEFAULT_CODE = "conflict.data";

  /** Default error code for forbidden access errors (HTTP 403). */
  public static final String FORBIDDEN_DEFAULT_CODE = "forbidden";

  /** Default error code for unauthorized errors (HTTP 401). */
  public static final String UNAUTHORIZED_DEFAULT_CODE = "unauthorized";

  /** Default error code for too many requests errors (HTTP 429). */
  public static final String TOO_MANY_REQUESTS_DEFAULT_CODE = "too.many.requests";

  /** Default error code for invalid parameter errors (HTTP 400). */
  public static final String INVALID_PARAMETER_DEFAULT_CODE = "invalid.parameter";

  private ExceptionConstant() {
    // Utility class - prevent instantiation
  }

}
