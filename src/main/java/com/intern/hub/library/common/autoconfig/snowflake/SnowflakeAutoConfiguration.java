package com.intern.hub.library.common.autoconfig.snowflake;

import com.intern.hub.library.common.utils.Snowflake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot auto-configuration for the {@link Snowflake} ID generator.
 * <p>
 * This auto-configuration creates a {@link Snowflake} bean that can be used throughout
 * the application for generating unique distributed IDs.
 * </p>
 *
 * <p><b>Configuration Properties:</b></p>
 * <ul>
 *   <li>{@code snowflake.machine-id} - The unique machine ID (0-1023). Defaults to 1.</li>
 * </ul>
 *
 * <p><b>Usage in application.yml:</b></p>
 * <pre>{@code
 * snowflake:
 *   machine-id: 5
 * }</pre>
 *
 * <p>The Snowflake bean is only created if no other {@link Snowflake} bean already exists
 * in the application context ({@code @ConditionalOnMissingBean}).</p>
 *
 * @see Snowflake
 */
@AutoConfiguration
public class SnowflakeAutoConfiguration {

  /**
   * The machine ID used for generating Snowflake IDs.
   * <p>Must be unique across all instances in a distributed system (range: 0-1023).</p>
   */
  @Value("${snowflake.machine-id:1}")
  private long machineId;

  /**
   * Creates a {@link Snowflake} bean configured with the specified machine ID.
   *
   * @return a new Snowflake instance
   */
  @Bean
  @ConditionalOnMissingBean(Snowflake.class)
  public Snowflake snowflake() {
    return new Snowflake(machineId);
  }

}
