package com.intern.hub.library.common.annotation;
import com.intern.hub.library.common.id.SnowflakeIdGenerator;
import org.hibernate.annotations.IdGeneratorType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Marks a JPA entity field or property as an ID that should be generated
 * using the Snowflake algorithm.
 *
 * <p>
 * Apply this annotation together with {@code @Id} on a {@code Long} field
 * (or any numeric type that fits a 64-bit value). The ID is produced by the
 * {@link SnowflakeIdGenerator}, which delegates to the
 * {@link com.intern.hub.library.common.utils.Snowflake} bean registered in
 * the Spring application context.
 * </p>
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * @Entity
 * public class Order {
 *
 *     @Id
 *     @SnowflakeGeneratedId
 *     private Long id;
 * }
 * }</pre>
 *
 * <p>
 * Make sure {@code SnowflakeAutoConfiguration} is active (or provide a
 * {@code Snowflake} bean manually) so that the generator is configured before
 * any entity is persisted.
 * </p>
 *
 * @see SnowflakeIdGenerator
 * @see com.intern.hub.library.common.utils.Snowflake
 */
@IdGeneratorType(SnowflakeIdGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface SnowflakeGeneratedId {
}
