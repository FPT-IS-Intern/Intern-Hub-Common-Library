package com.intern.hub.library.common.context;

/**
 * Immutable record representing the context of the current HTTP request.
 * <p>
 * This record holds metadata about the current request such as trace ID, request ID,
 * start time, and source information. It is typically populated at the beginning of
 * each request (e.g., in a filter or interceptor) and can be accessed throughout
 * the request lifecycle.
 * </p>
 *
 * @param traceId   the distributed tracing ID for correlating logs across services
 * @param requestId the unique identifier for this specific request
 * @param startTime the timestamp (in milliseconds) when the request started
 * @param source    the source or origin of the request (e.g., client identifier, service name)
 * @see RequestContextHolder
 */
public record RequestContext(
    String traceId,
    String requestId,
    Long startTime,
    String source
) {
}
