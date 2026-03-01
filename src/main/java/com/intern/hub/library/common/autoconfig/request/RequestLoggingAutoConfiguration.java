package com.intern.hub.library.common.autoconfig.request;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import tools.jackson.databind.ObjectMapper;

/**
 * Auto-configuration for HTTP request and response logging.
 *
 * <p>
 * This configuration registers a {@link RequestLoggingFilter} as a servlet filter
 * that can log request headers, request bodies, and response bodies for
 * {@code application/json} requests. All logging is <b>opt-in</b>: every logging
 * category is disabled by default and must be explicitly enabled via properties.
 * </p>
 *
 * <p><b>Enabling/Disabling the auto-configuration:</b></p>
 * <p>
 * The auto-configuration is active by default for servlet-based web applications and
 * can be disabled entirely by setting:
 * </p>
 * <pre>{@code
 * common:
 *   logging:
 *     enabled: false
 * }</pre>
 *
 * <p><b>Enabling individual log categories:</b></p>
 * <pre>{@code
 * common:
 *   logging:
 *     request: true     # log the request body
 *     response: true    # log the response body
 *     header: true      # log the request headers
 * }</pre>
 *
 * <p><b>Masking sensitive data:</b></p>
 * <pre>{@code
 * common:
 *   logging:
 *     mask-headers:
 *       - authorization
 *       - cookie
 *       - x-api-key
 *     mask-fields:
 *       - password
 *       - accessToken
 *       - refreshToken
 *       - cardNumber
 * }</pre>
 *
 * <p>
 * The filter runs at {@link Ordered#LOWEST_PRECEDENCE} so it executes after the
 * {@link com.intern.hub.library.common.autoconfig.context.ContextFilter ContextFilter},
 * ensuring the {@link com.intern.hub.library.common.context.RequestContext RequestContext}
 * (and its {@code requestId}) is available when log messages are emitted.
 * </p>
 *
 * <p>
 * The filter bean is only created when a {@link ObjectMapper} bean is present in the
 * application context (provided automatically by Spring Boot's Jackson auto-configuration).
 * </p>
 *
 * @see RequestLoggingFilter
 * @see LoggingProperties
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(name = "common.logging.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(LoggingProperties.class)
public class RequestLoggingAutoConfiguration {

  /**
   * Registers the {@link RequestLoggingFilter} with the lowest filter precedence.
   * <p>
   * The filter is applied to all URL patterns ({@code /*}) and is only created
   * when a {@link ObjectMapper} bean is available in the application context.
   * </p>
   *
   * @param loggingProperties the logging configuration properties
   * @param objectMapper      the Jackson {@link ObjectMapper} used for JSON body masking
   * @return a {@link FilterRegistrationBean} configured with the {@link RequestLoggingFilter}
   */
  @Bean
  @ConditionalOnBean(ObjectMapper.class)
  public FilterRegistrationBean<RequestLoggingFilter> requestLoggingFilter(
      LoggingProperties loggingProperties, ObjectMapper objectMapper) {
    FilterRegistrationBean<RequestLoggingFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new RequestLoggingFilter(loggingProperties, objectMapper));
    registrationBean.addUrlPatterns("/*");
    registrationBean.setOrder(Ordered.LOWEST_PRECEDENCE);
    return registrationBean;
  }

}
