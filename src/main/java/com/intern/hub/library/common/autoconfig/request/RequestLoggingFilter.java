package com.intern.hub.library.common.autoconfig.request;

import com.intern.hub.library.common.context.RequestContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.core.Ordered;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servlet filter that logs HTTP request headers, request bodies, and response bodies
 * for {@code application/json} and {@code text/*} content-type requests.
 *
 * <p>
 * Logging is controlled by {@link LoggingProperties}. By default all logging is
 * disabled ({@code common.logging.request}, {@code common.logging.response}, and
 * {@code common.logging.header} are all {@code false}).
 * </p>
 *
 * <p>
 * Sensitive data is automatically masked. Header names listed in
 * {@link LoggingProperties#getMaskHeaders()} are replaced with {@code ******} and
 * JSON field names listed in {@link LoggingProperties#getMaskFields()} are replaced
 * with {@code ******} before the payload is written to the log.
 * </p>
 *
 * <p>
 * The filter uses {@link ContentCachingRequestWrapper} and
 * {@link ContentCachingResponseWrapper} to buffer the bodies so they can be read
 * more than once without affecting the downstream handler. The response body is
 * always copied back to the original response via
 * {@link ContentCachingResponseWrapper#copyBodyToResponse()} in the {@code finally}
 * block.
 * </p>
 *
 * <p>
 * This filter is registered automatically by {@link RequestLoggingAutoConfiguration}
 * and runs at {@link Ordered#LOWEST_PRECEDENCE} so that the
 * {@link com.intern.hub.library.common.autoconfig.context.ContextFilter ContextFilter}
 * (which runs at {@link Ordered#HIGHEST_PRECEDENCE}) has already populated the
 * {@link com.intern.hub.library.common.context.RequestContext RequestContext} before
 * logging occurs.
 * </p>
 *
 * @see LoggingProperties
 * @see RequestLoggingAutoConfiguration
 */
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter implements Ordered {

  private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

  private final LoggingProperties loggingProperties;
  private final ObjectMapper objectMapper;

  private final Set<String> maskHeaders;
  private final Set<String> maskFields;

  public RequestLoggingFilter(LoggingProperties loggingProperties, ObjectMapper objectMapper) {
    this.loggingProperties = loggingProperties;
    this.objectMapper = objectMapper;
    this.maskHeaders = loggingProperties.getMaskHeaders().stream()
        .map(String::toLowerCase)
        .collect(Collectors.toCollection(HashSet::new));
    this.maskFields = new HashSet<>(loggingProperties.getMaskFields());
  }

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
                                  @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {
    if (!loggingProperties.isRequest() &&
        !loggingProperties.isHeader() &&
        !loggingProperties.isResponse()) {
      filterChain.doFilter(request, response);
      return;
    }

    String uri = request.getRequestURI();
    if (loggingProperties.getExcludePaths().stream().anyMatch(pattern -> ANT_PATH_MATCHER.match(pattern, uri))) {
      filterChain.doFilter(request, response);
      return;
    }

    ContentCachingRequestWrapper wrappedRequest = null;
    if (loggingProperties.isRequest()) {
      wrappedRequest = new ContentCachingRequestWrapper(request, loggingProperties.getMaxBodyBytes());
    }
    ContentCachingResponseWrapper wrappedResponse = null;
    if (loggingProperties.isResponse()) {
      wrappedResponse = new ContentCachingResponseWrapper(response);
    }

    try {
      filterChain.doFilter(wrappedRequest != null ? wrappedRequest : request, wrappedResponse != null ? wrappedResponse : response);
    } finally {
      log(request, wrappedRequest, wrappedResponse);
      wrappedRequest = null; // help GC
      wrappedResponse = null; // help GC
    }
  }

  /**
   * Logs headers/request/response and ensures the buffered response body is always
   * flushed back to the client. This method intentionally swallows all exceptions so
   * that a logging failure never masks a real downstream exception.
   */
  private void log(HttpServletRequest request,
                   ContentCachingRequestWrapper wrappedRequest,
                   ContentCachingResponseWrapper wrappedResponse) {
    try {
      if (loggingProperties.isHeader()) logHeaders(request);
      if (loggingProperties.isRequest()) {
        String requestContentType = request.getContentType() != null ? request.getContentType().toLowerCase() : "";
        logRequest(request, wrappedRequest, requestContentType);
      }
      if (loggingProperties.isResponse()) logResponse(request, wrappedResponse);
    } catch (Exception e) {
      log.warn("Failed to log request/response: {}", e.getMessage());
    } finally {
      if (wrappedResponse != null) {
        try {
          wrappedResponse.copyBodyToResponse();
        } catch (IOException e) {
          log.warn("Failed to copy response body to client: {}", e.getMessage());
        }
      }
    }
  }

  private void logHeaders(HttpServletRequest request) {
    String requestId = RequestContextHolder.get().requestId();
    StringJoiner headers = new StringJoiner(", ");
    request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
      if (maskHeaders.contains(headerName.toLowerCase())) {
        headers.add(headerName + "=******");
      } else {
        headers.add(headerName + "=" + request.getHeader(headerName));
      }
    });
    log.info("Headers: method={}, uri={}, requestId={}, headers={}", request.getMethod(), request.getRequestURI(), requestId, headers);
  }

  private void logRequest(HttpServletRequest request, ContentCachingRequestWrapper wrappedRequest, String contentType) {
    String requestId = RequestContextHolder.get().requestId();
    if (contentType.startsWith("application/json") || contentType.startsWith("text/")) {
      byte[] body = wrappedRequest.getContentAsByteArray();
      String requestBody = maskJson(new String(body, StandardCharsets.UTF_8));
      if (body.length >= loggingProperties.getMaxBodyBytes()) {
        log.info("Request: method={}, uri={}, requestId={}, body={} [TRUNCATED at {} bytes]",
            request.getMethod(), request.getRequestURI(), requestId, requestBody, loggingProperties.getMaxBodyBytes());
      } else {
        log.info("Request: method={}, uri={}, requestId={}, body={}", request.getMethod(), request.getRequestURI(), requestId, requestBody);
      }
      return;
    }
    log.info("Request: method={}, uri={}, requestId={}", request.getMethod(), request.getRequestURI(), requestId);
  }

  private void logResponse(HttpServletRequest request, ContentCachingResponseWrapper wrappedResponse) {
    String requestId = RequestContextHolder.get().requestId();
    int status = wrappedResponse.getStatus();
    String responseContentType = wrappedResponse.getContentType() != null ? wrappedResponse.getContentType().toLowerCase() : "";
    if (responseContentType.startsWith("application/json") || responseContentType.startsWith("text/")) {
      String responseBody = maskJson(new String(wrappedResponse.getContentAsByteArray(), StandardCharsets.UTF_8));
      log.info("Response: method={}, uri={}, requestId={}, status={}, body={}", request.getMethod(), request.getRequestURI(), requestId, status, responseBody);
      return;
    }
    log.info("Response: method={}, uri={}, requestId={}, status={}", request.getMethod(), request.getRequestURI(), requestId, status);
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }

  private String maskJson(String body) {
    if (body == null || body.isBlank() || maskFields.isEmpty()) {
      return body;
    }
    try {
      JsonNode jsonNode = objectMapper.readTree(body);
      maskNode(jsonNode);
      return objectMapper.writeValueAsString(jsonNode);
    } catch (JacksonException e) {
      return body;
    }
  }

  private void maskNode(JsonNode node) {
    if (node.isObject()) {
      ObjectNode objectNode = (ObjectNode) node;
      List<Map.Entry<String, JsonNode>> fields = new ArrayList<>(node.properties());
      for (Map.Entry<String, JsonNode> entry : fields) {
        String fieldName = entry.getKey();
        JsonNode child = entry.getValue();
        if (maskFields.contains(fieldName)) {
          objectNode.put(fieldName, "******");
        } else if (child.isObject() || child.isArray()) {
          maskNode(child);
        }
      }
    } else if (node.isArray()) {
      node.forEach(this::maskNode);
    }
  }

}
