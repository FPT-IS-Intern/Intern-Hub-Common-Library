package com.intern.hub.library.common.autoconfig.context;

import com.intern.hub.library.common.context.RequestContext;
import com.intern.hub.library.common.context.RequestContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class ContextFilter extends OncePerRequestFilter {

  private static final String UNKNOWN_SOURCE = "unknown";

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
                                  @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain)  {
    String requestId = request.getHeader("X-Request-ID");
    String traceId = request.getHeader("X-Trace-ID");
    String source = request.getHeader("X-Source");

    if(requestId == null || requestId.isBlank()) {
      requestId = UUID.randomUUID().toString();
    }
    if(traceId == null || traceId.isBlank()) {
      traceId = UUID.randomUUID().toString();
    }
    if(source == null || source.isBlank()) {
      source = UNKNOWN_SOURCE;
    }

    RequestContext requestContext = new RequestContext(traceId, requestId, System.currentTimeMillis(), source);

    ScopedValue.where(RequestContextHolder.REQUEST_CONTEXT, requestContext).run(() -> next(filterChain, request, response));
  }

  private void next(FilterChain filterChain, HttpServletRequest request, HttpServletResponse response) {
    try {
      filterChain.doFilter(request, response);
    } catch (IOException | ServletException e) {
      throw new RuntimeException(e);
    }
  }

}
