package com.intern.hub.library.common.dto;

import com.intern.hub.library.common.context.RequestContextHolder;

/**
 * Metadata about the API response, including request identification and timing information.
 * <p>
 * This record provides traceability information for each API response, which is useful
 * for debugging, logging, and distributed tracing across microservices.
 * </p>
 *
 * @param requestId the unique identifier for the current request
 * @param traceId   the distributed tracing ID for correlating requests across services
 * @param signature an optional cryptographic signature for response verification
 * @param timestamp the timestamp (in milliseconds since epoch) when the response was created
 * @see ResponseApi
 * @see com.intern.hub.library.common.context.RequestContext
 */
public record ResponseMetadata(String requestId, String traceId, String signature, long timestamp) {

  /**
   * Creates a new ResponseMetadata instance populated from the current {@link com.intern.hub.library.common.context.RequestContext}.
   * <p>
   * This factory method automatically retrieves the request ID and trace ID from the
   * current request context and sets the timestamp to the current system time.
   * </p>
   *
   * @return a new ResponseMetadata instance with values from the current request context
   * @throws IllegalStateException if no RequestContext is bound to the current scope
   */
  public static ResponseMetadata fromRequestId() {
    return new ResponseMetadata(
        RequestContextHolder.get().requestId(),
        RequestContextHolder.get().traceId(),
        null,
        System.currentTimeMillis());
  }

}
