package com.intern.hub.library.common.id;

import com.intern.hub.library.common.annotation.SnowflakeGeneratedId;
import com.intern.hub.library.common.utils.Snowflake;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;
import org.hibernate.generator.GeneratorCreationContext;

import java.lang.reflect.Member;
import java.util.EnumSet;

/**
 * Hibernate {@link BeforeExecutionGenerator} that produces Snowflake IDs.
 * <p>
 * This generator is linked to the {@link SnowflakeGeneratedId} annotation via
 * Hibernate's {@code @IdGeneratorType} meta-annotation. It delegates ID
 * generation to the {@link Snowflake} bean registered in the Spring context,
 * which is injected via {@link #configure(Snowflake)} at application startup.
 * </p>
 *
 * <p><b>Usage on a JPA entity:</b></p>
 * <pre>{@code
 * @Entity
 * public class MyEntity {
 *
 *     @Id
 *     @SnowflakeGeneratedId
 *     private Long id;
 * }
 * }</pre>
 *
 * @see SnowflakeGeneratedId
 * @see Snowflake
 */
public class SnowflakeIdGenerator implements BeforeExecutionGenerator {

  private static volatile Snowflake snowflake;

  /**
   * Called by {@link com.intern.hub.library.common.autoconfig.snowflake.SnowflakeAutoConfiguration}
   * at startup to provide the shared {@link Snowflake} instance.
   *
   * @param instance the configured {@link Snowflake} bean
   */
  public static void configure(Snowflake instance) {
    snowflake = instance;
  }

  /**
   * Required constructor for Hibernate's {@code @IdGeneratorType} contract.
   *
   * @param annotation the {@link SnowflakeGeneratedId} annotation on the field
   * @param member     the annotated field or method
   * @param context    the generator creation context provided by Hibernate
   */
  public SnowflakeIdGenerator(
      SnowflakeGeneratedId annotation,
      Member member,
      GeneratorCreationContext context) {
    // no per-annotation configuration needed
  }

  /**
   * Returns the set of events for which this generator should run.
   * Only {@link EventType#INSERT} is needed since IDs are assigned once.
   */
  @Override
  public EnumSet<EventType> getEventTypes() {
    return EnumSet.of(EventType.INSERT);
  }

  /**
   * Generates the next Snowflake ID.
   *
   * @throws IllegalStateException if {@link #configure(Snowflake)} has not been called
   */
  @Override
  public Object generate(
      SharedSessionContractImplementor session,
      Object owner,
      Object currentValue,
      EventType eventType) {
    if (snowflake == null) {
      throw new IllegalStateException(
          "SnowflakeIdGenerator has not been configured. "
              + "Make sure SnowflakeAutoConfiguration is active or call SnowflakeIdGenerator.configure(snowflake).");
    }
    return snowflake.next();
  }
}

