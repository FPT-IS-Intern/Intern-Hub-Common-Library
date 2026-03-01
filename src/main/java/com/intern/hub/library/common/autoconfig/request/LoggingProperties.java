package com.intern.hub.library.common.autoconfig.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties for the request/response logging filter.
 * <p>
 * All properties are bound under the {@code common.logging} prefix.
 * </p>
 *
 * <p><b>Example {@code application.yml}:</b></p>
 * <pre>{@code
 * common:
 *   logging:
 *     request: true
 *     response: true
 *     header: false
 *     mask-headers:
 *       - authorization
 *       - cookie
 *     mask-fields:
 *       - password
 *       - accessToken
 * }</pre>
 *
 * @see RequestLoggingAutoConfiguration
 * @see RequestLoggingFilter
 */
@ConfigurationProperties(prefix = "common.logging")
@Getter
@Setter
public class LoggingProperties {

  /**
   * Whether to log the request body. Defaults to {@code false}.
   */
  private boolean request = false;

  /**
   * Whether to log the response body. Defaults to {@code false}.
   */
  private boolean response = false;

  /**
   * Whether to log the request headers. Defaults to {@code false}.
   */
  private boolean header = false;

  /**
   * Header names whose values will be masked in the log output.
   * Matching is case-insensitive.
   * Defaults to {@code [authorization, cookie, set-cookie]}.
   */
  private List<String> maskHeaders = new ArrayList<>(List.of("authorization", "cookie", "set-cookie"));

  /**
   * JSON field names whose values will be replaced with {@code ******} in the log output.
   * Defaults to {@code [password, accessToken, refreshToken]}.
   */
  private List<String> maskFields = new ArrayList<>(List.of("password", "accessToken", "refreshToken"));

}
