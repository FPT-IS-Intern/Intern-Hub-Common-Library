package com.intern.hub.library.common.autoconfig.request;

import com.intern.hub.library.common.context.RequestContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servlet filter that logs HTTP request headers, request bodies, and response bodies
 * for {@code application/json} content-type requests.
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
 * <p>
 * Requests with a content-type other than {@code application/json} pass through
 * without any buffering or logging.
 * </p>
 *
 * @see LoggingProperties
 * @see RequestLoggingAutoConfiguration
 */
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter implements Ordered {

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
    String contentType = request.getContentType();
    if (!loggingProperties.isRequest() &&
        !loggingProperties.isHeader() &&
        !loggingProperties.isResponse() &&
        (contentType == null || !contentType.toLowerCase().startsWith("application/json"))) {
      filterChain.doFilter(request, response);
      return;
    }
    String uri = request.getRequestURI();
    String method = request.getMethod();
    String requestId = RequestContextHolder.get().requestId();
    ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, 8192);
    ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
    try {
      filterChain.doFilter(wrappedRequest, wrappedResponse);
    } finally {
      try {
        if (loggingProperties.isHeader()) {
          StringBuilder headers = new StringBuilder();
          request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            if (maskHeaders.contains(headerName.toLowerCase())) {
              headers.append(headerName).append("=******; ");
            } else {
              String headerValue = request.getHeader(headerName);
              headers.append(headerName).append("=").append(headerValue).append("; ");
            }
          });
          log.info("Headers: method={}, uri={}, requestId={}, headers={}", method, uri, requestId, headers);
        }
        if (loggingProperties.isRequest()) {
          String requestBody = maskJson(new String(wrappedRequest.getContentAsByteArray(), wrappedRequest.getCharacterEncoding()));
          log.info("Request: method={}, uri={}, requestId={}, body={}", method, uri, requestId, requestBody);
        }
        if (loggingProperties.isResponse()) {
          String responseContentType = wrappedResponse.getContentType();
          int status = wrappedResponse.getStatus();
          if (responseContentType != null && responseContentType.toLowerCase().startsWith("application/json")) {
            String responseBody = maskJson(new String(wrappedResponse.getContentAsByteArray(), wrappedResponse.getCharacterEncoding()));
            log.info("Response: method={}, uri={}, requestId={}, status={}, body={}", method, uri, requestId, status, responseBody);
          } else {
            log.info("Response: method={}, uri={}, requestId={}, status={}", method, uri, requestId, status);
          }
        }
      } catch (Exception e) {
        log.warn("Failed to log request/response: {}", e.getMessage());
      } finally {
        wrappedResponse.copyBodyToResponse();
      }
    }
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
