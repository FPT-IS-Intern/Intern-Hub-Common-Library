package com.intern.hub.library.common.dto;

import com.intern.hub.library.common.context.RequestContextHolder;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;

public record ResponseMetadata(String requestId, String traceId, String signature, long timestamp) {

  public static ResponseMetadata fromRequestId(String signature) {
    SpanContext spanContext = Span.current().getSpanContext();
    if (spanContext.isValid()) {
      return new ResponseMetadata(
          RequestContextHolder.REQUEST_CONTEXT.isBound() ? RequestContextHolder.get().requestId() : null,
          spanContext.getTraceId(),
          signature,
          System.currentTimeMillis());

    }
    return new ResponseMetadata(RequestContextHolder.get().requestId(), null, signature, System.currentTimeMillis());
  }

  public static ResponseMetadata fromRequestId() {
    return fromRequestId(null);
  }

}
