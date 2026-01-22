package com.intern.hub.library.common.annotation;

import com.intern.hub.library.common.autoconfig.exadvice.GlobalExceptionAdviceConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable global exception handling in a Spring Boot application.
 * <p>
 * When applied to a configuration class or the main application class, this annotation
 * imports the {@link GlobalExceptionAdviceConfiguration} which registers the
 * {@link com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice}
 * as a bean, providing centralized exception handling for REST controllers.
 * </p>
 *
 * <p><b>Usage:</b></p>
 * <pre>{@code
 * @SpringBootApplication
 * @EnableGlobalExceptionHandler
 * public class MyApplication {
 *     public static void main(String[] args) {
 *         SpringApplication.run(MyApplication.class, args);
 *     }
 * }
 * }</pre>
 *
 * @see GlobalExceptionAdviceConfiguration
 * @see com.intern.hub.library.common.autoconfig.exadvice.DefaultGlobalExceptionAdvice
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(GlobalExceptionAdviceConfiguration.class)
public @interface EnableGlobalExceptionHandler {
}
