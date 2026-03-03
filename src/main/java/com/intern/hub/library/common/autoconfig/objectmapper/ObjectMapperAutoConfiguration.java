package com.intern.hub.library.common.autoconfig.objectmapper;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigInteger;

/**
 * Spring Boot auto-configuration for a pre-configured Jackson {@link ObjectMapper}.
 *
 * <p>
 * This auto-configuration creates an {@link ObjectMapper} bean with sensible defaults
 * for Spring Boot microservices:
 * </p>
 * <ul>
 *   <li>{@code FAIL_ON_UNKNOWN_PROPERTIES} disabled — unknown JSON fields are silently ignored.</li>
 *   <li>{@code FAIL_ON_NULL_FOR_PRIMITIVES} disabled — {@code null} values for primitives are tolerated.</li>
 *   <li>All Jackson modules on the classpath are auto-registered via {@code findAndAddModules()}.</li>
 *   <li>Optionally serializes {@code Long}, {@code long}, and {@code BigInteger} as JSON strings
 *       (see {@code common.object-mapper.serialize-long-as-string}).</li>
 * </ul>
 *
 * <p>
 * The {@link ObjectMapper} bean is only created when no other {@link ObjectMapper} bean exists in
 * the application context ({@code @ConditionalOnMissingBean}).
 * </p>
 *
 * <p><b>Enabling / Disabling:</b></p>
 * <pre>{@code
 * common:
 *   object-mapper:
 *     enabled: false   # disable this auto-configuration entirely
 * }</pre>
 *
 * <p><b>Configuring serialization:</b></p>
 * <pre>{@code
 * common:
 *   object-mapper:
 *     serialize-long-as-string: true   # serialize Long/BigInteger as JSON string (default: true)
 * }</pre>
 *
 * @see ObjectMapperProperties
 */
@AutoConfiguration
@ConditionalOnProperty(name = "common.object-mapper.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(ObjectMapperProperties.class)
public class ObjectMapperAutoConfiguration {

    /**
     * Creates a pre-configured {@link ObjectMapper} bean.
     *
     * <p>
     * The mapper has {@code FAIL_ON_UNKNOWN_PROPERTIES} and
     * {@code FAIL_ON_NULL_FOR_PRIMITIVES} disabled, and all classpath Jackson modules
     * auto-registered. When {@code common.object-mapper.serialize-long-as-string} is
     * {@code true} (the default), {@code Long} and {@code BigInteger} values are
     * serialized as JSON strings.
     * </p>
     *
     * @param properties the ObjectMapper configuration properties
     * @return a fully configured {@link ObjectMapper} instance
     */
    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper(ObjectMapperProperties properties) {
        JsonMapper.Builder builder = JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .findAndAddModules();

        if (properties.isSerializeLongAsString()) {
            builder.addModule(longToStringModule());
        }

        return builder.build();
    }

    /**
     * Creates a Jackson {@link SimpleModule} that serializes {@code Long}, {@code long},
     * and {@code BigInteger} values as JSON strings.
     *
     * <p>
     * This module is registered into the {@link ObjectMapper} when
     * {@code common.object-mapper.serialize-long-as-string} is {@code true}.
     * It is also exposed as a standalone bean so other components can reuse it.
     * </p>
     *
     * @return the {@code LongToStringModule}
     */
    @Bean
    @ConditionalOnMissingBean(name = "longToStringModule")
    public SimpleModule longToStringModule() {
        SimpleModule module = new SimpleModule("LongToStringModule");
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        module.addSerializer(BigInteger.class, ToStringSerializer.instance);
        return module;
    }

}
