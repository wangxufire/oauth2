package com.hd123.oauth2.logger;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 拦截service日志
 * 
 * @author liyue
 * @since 0.0.1
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface ServiceLogger {

  String value() default "";

}