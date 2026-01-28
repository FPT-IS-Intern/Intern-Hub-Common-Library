package com.intern.hub.library.common.autoconfig.snowflake;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Snowflake ID generator.
 * <p>
 * These properties can be configured in your {@code application.yml} or
 * {@code application.properties}:
 * </p>
 *
 * <pre>{@code
 * snowflake:
 *   machine-id: 5
 * }</pre>
 *
 * @see SnowflakeAutoConfiguration
 * @see com.intern.hub.library.common.utils.Snowflake
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "snowflake")
public class SnowflakeProperties {

    /**
     * The unique machine ID used for generating Snowflake IDs.
     * <p>
     * Must be unique across all instances in a distributed system.
     * Valid range: 0-1023.
     * </p>
     * <p>
     * Default: 1
     * </p>
     */
    private long machineId = 1;

}
