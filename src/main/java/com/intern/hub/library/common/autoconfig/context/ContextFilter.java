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

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
                                  @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain)  {
    String requestId = request.getHeader("X-Request-ID");
    if(requestId == null || requestId.isBlank()) {
      requestId = UUID.randomUUID().toString();
    }
    RequestContext requestContext = new RequestContext(requestId);
    next(requestContext, filterChain, request, response);
  }

  private void next(RequestContext requestContext,
                    FilterChain filterChain,
                    HttpServletRequest request,
                    HttpServletResponse response) {
    ScopedValue.where(RequestContextHolder.REQUEST_CONTEXT, requestContext).run(() -> {
      try {
        filterChain.doFilter(request, response);
      } catch (IOException | ServletException e) {
        throw new RuntimeException(e);
      }
    });
  }

}
