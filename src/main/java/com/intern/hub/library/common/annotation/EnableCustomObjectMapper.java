package com.intern.hub.library.common.annotation;

import com.intern.hub.library.common.autoconfig.objectmapper.ObjectMapperAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ObjectMapperAutoConfiguration.class)
public @interface EnableCustomObjectMapper {
}
