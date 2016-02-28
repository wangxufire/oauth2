package com.hd123.oauth2.support;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 标记参数可为NULL或返回值可为NULL
 *
 * @author liyue
 * @since 0.0.1
 */
@Documented
@Retention(RUNTIME)
@Target({
    PARAMETER, METHOD })
public @interface Nullable {
}