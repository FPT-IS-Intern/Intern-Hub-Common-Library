package com.intern.hub.library.common.autoconfig.exadvice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class that registers the global exception advice bean.
 * <p>
 * This configuration is imported when using the
 * {@link com.intern.hub.library.common.annotation.EnableGlobalExceptionHandler} annotation.
 * </p>
 *
 * @see com.intern.hub.library.common.annotation.EnableGlobalExceptionHandler
 * @see DefaultGlobalExceptionAdvice
 */
@Configuration
public class GlobalExceptionAdviceConfiguration {

  /**
   * Creates and registers the {@link DefaultGlobalExceptionAdvice} bean.
   *
   * @return a new instance of DefaultGlobalExceptionAdvice
   */
  @Bean
  public DefaultGlobalExceptionAdvice globalExceptionAdvice() {
    return new DefaultGlobalExceptionAdvice();
  }

}
