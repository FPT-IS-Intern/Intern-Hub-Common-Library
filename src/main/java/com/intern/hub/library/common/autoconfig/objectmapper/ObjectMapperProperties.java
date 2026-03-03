package com.intern.hub.library.common.autoconfig.objectmapper;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Jackson {@code ObjectMapper} auto-configuration.
 * <p>
 * All properties are bound under the {@code common.object-mapper} prefix.
 * </p>
 *
 * <p><b>Example {@code application.yml}:</b></p>
 * <pre>{@code
 * common:
 *   object-mapper:
 *     serialize-long-as-string: true
 * }</pre>
 *
 * @see ObjectMapperAutoConfiguration
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "common.object-mapper")
public class ObjectMapperProperties {

    /**
     * Whether to serialize {@code Long}, {@code long}, and {@code BigInteger} values as JSON
     * strings instead of JSON numbers.
     * <p>
     * This is useful when the API is consumed by JavaScript clients, which cannot safely
     * represent 64-bit integers (values beyond ±2<sup>53</sup>) as {@code number} without
     * losing precision.
     * </p>
     * <p>
     * When enabled, a field declared as {@code Long id = 123456789012345678L} is serialized as
     * {@code "id": "123456789012345678"} rather than {@code "id": 123456789012345678}.
     * </p>
     * <p>
     * Default: {@code true}
     * </p>
     */
    private boolean serializeLongAsString = true;

}

