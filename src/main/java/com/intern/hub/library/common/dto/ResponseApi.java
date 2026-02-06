package com.intern.hub.library.common.dto;

/**
 * Generic API response wrapper that provides a standardized response structure.
 * <p>
 * This record encapsulates all API responses with a consistent format
 * containing
 * status information, optional data payload, and request metadata.
 * </p>
 *
 * <p>
 * <b>Success Response Structure:</b>
 * </p>
 * 
 * <pre>{@code
 * {
 *   "status": null,
 *   "data": { ... },
 *   "metaData": null
 * }
 * }</pre>
 *
 * <p>
 * <b>Error Response Structure (handled by global exception handler):</b>
 * </p>
 * 
 * <pre>{@code
 * {
 *   "status": {
 *     "code": "resource.not.found",
 *     "message": "User not found"
 *   },
 *   "data": null,
 *   "metaData": {
 *     "requestId": "abc-123",
 *     "traceId": "xyz-789",
 *     "timestamp": 1704067200000
 *   }
 * }
 * }</pre>
 *
 * <p>
 * <b>Usage:</b>
 * </p>
 * 
 * <pre>{@code
 * // For success responses, use factory methods
 * return ResponseApi.ok(user);
 *
 * // For errors, throw exceptions - global handler formats the response
 * throw new NotFoundException("user.not.found", "User not found");
 * }</pre>
 *
 * @param <T>      the type of the data payload
 * @param status   the response status containing code and message (null for
 *                 success)
 * @param data     the response data payload (null for error responses)
 * @param metaData metadata about the request/response (optional)
 * @see ResponseStatus
 * @see ResponseMetadata
 */
public record ResponseApi<T>(ResponseStatus status, T data, ResponseMetadata metaData) {

  /**
   * Creates a ResponseApi with full control over all fields.
   *
   * @param status   the response status
   * @param data     the response data
   * @param metaData the response metadata
   * @param <T>      the type of the data payload
   * @return a new ResponseApi instance
   */
  public static <T> ResponseApi<T> of(ResponseStatus status, T data, ResponseMetadata metaData) {
    return new ResponseApi<>(status, data, metaData);
  }

  /**
   * Creates a simple success response with data only.
   * <p>
   * Status and metadata are set to null for minimal response size.
   * </p>
   *
   * @param data the response data
   * @param <T>  the type of the data payload
   * @return a new ResponseApi instance with data only
   */
  public static <T> ResponseApi<T> ok(T data) {
    return new ResponseApi<>(
        ResponseStatus.ok(),
        data,
        ResponseMetadata.fromRequestId());
  }

  /**
   * Creates a success response with no content.
   * <p>
   * Status is set to OK, but data and metadata are null.
   * </p>
   *
   * @return a new ResponseApi instance with no content
   */
  public static ResponseApi<?> noContent() {
    return new ResponseApi<>(
        ResponseStatus.ok(),
        null,
        ResponseMetadata.fromRequestId());
  }

  /**
   * Creates a success response with data and metadata.
   * <p>
   * Status is set to null, but metadata is included for request tracing.
   * </p>
   *
   * @param data     the response data
   * @param metaData the response metadata
   * @param <T>      the type of the data payload
   * @return a new ResponseApi instance with data and metadata
   */
  public static <T> ResponseApi<T> ok(T data, ResponseMetadata metaData) {
    return new ResponseApi<>(
        null,
        data,
        metaData);
  }

}
