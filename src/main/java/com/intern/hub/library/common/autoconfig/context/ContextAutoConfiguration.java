package com.intern.hub.library.common.autoconfig.context;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ContextAutoConfiguration {

  @Bean
  public FilterRegistrationBean<ContextFilter> contextFilter() {
    FilterRegistrationBean<ContextFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new ContextFilter());
    registrationBean.addUrlPatterns("/*");
    registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return registrationBean;
  }

}
