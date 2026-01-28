package com.intern.hub.library.common.autoconfig.context;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

/**
 * Auto-configuration for Request Context management.
 * <p>
 * This configuration registers a servlet filter that automatically populates
 * {@link com.intern.hub.library.common.context.RequestContext} for each
 * incoming HTTP request.
 * The context includes trace ID, request ID, start time, and source
 * information.
 * </p>
 *
 * <p>
 * <b>Enabling/Disabling:</b>
 * </p>
 * <p>
 * This auto-configuration is enabled by default for servlet-based web
 * applications.
 * It can be disabled by setting:
 * </p>
 * 
 * <pre>{@code
 * common:
 *   context:
 *     enabled: false
 * }</pre>
 *
 * <p>
 * <b>HTTP Headers:</b>
 * </p>
 * <p>
 * The filter reads the following headers from incoming requests:
 * </p>
 * <ul>
 * <li>{@code X-Request-ID} - Unique identifier for the request (auto-generated
 * if missing)</li>
 * <li>{@code X-Trace-ID} - Distributed tracing ID (auto-generated if
 * missing)</li>
 * <li>{@code X-Source} - Source/origin of the request (defaults to
 * "unknown")</li>
 * </ul>
 *
 * @see ContextFilter
 * @see com.intern.hub.library.common.context.RequestContext
 * @see com.intern.hub.library.common.context.RequestContextHolder
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(name = "common.context.enabled", havingValue = "true", matchIfMissing = true)
public class ContextAutoConfiguration {

  /**
   * Registers the context filter with the highest precedence.
   * <p>
   * The filter is applied to all URL patterns ({@code /*}) and runs before
   * all other filters to ensure context is available throughout the request
   * lifecycle.
   * </p>
   *
   * @return a FilterRegistrationBean configured with the ContextFilter
   */
  @Bean
  public FilterRegistrationBean<ContextFilter> contextFilter() {
    FilterRegistrationBean<ContextFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new ContextFilter());
    registrationBean.addUrlPatterns("/*");
    registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return registrationBean;
  }

}
