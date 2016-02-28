package com.hd123.oauth2.support;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Sets.newHashSet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Service参数校验切面
 *
 * @author liyue
 * @since 0.0.1
 */
@Aspect
@Component
public class ValidatorAspect {

  @Autowired(required = true)
  @Qualifier(value = "localValidatorFactoryBean")
  private Validator validator;

  @Primary
  @Bean(name = "localValidatorFactoryBean")
  public Validator localValidatorFactoryBean() {
    return new LocalValidatorFactoryBean();
  }

  /**
   * 校验参数合法性
   *
   * @param jp
   *          切点
   * @throws NoSuchMethodException
   *           NoSuchMethodException
   */
  @Before("execution(* com.hd123.oauth2.service..*.*(..))")
  public void valid(JoinPoint jp) throws NoSuchMethodException {
    final Set<ConstraintViolation<?>> violations = newHashSet();

    final Method interfaceMethod = ((MethodSignature) jp.getSignature()).getMethod();
    final Method implementationMethod = jp.getTarget().getClass()
        .getMethod(interfaceMethod.getName(), interfaceMethod.getParameterTypes());

    // annotation
    final Annotation[][] annotationParameters = implementationMethod.getParameterAnnotations();
    for (int i = 0, max = annotationParameters.length; i < max; i++) {
      final Annotation[] annotations = annotationParameters[i];
      for (Annotation annotation : annotations) {
        if (equal(annotation.annotationType(), Valid.class)) {
          final Valid valid = (Valid) annotation;
          final Object arg = jp.getArgs()[i];
          violations.addAll(validator.validate(arg));
        }
      }
    }

    if (!violations.isEmpty()) {
      final StringBuilder msgBuilder = new StringBuilder();
      violations.forEach(violation -> {
        msgBuilder.append(violation.getPropertyPath()).append(":").append(violation.getMessage())
            .append(";");
      });

      throw new ConstraintViolationException(msgBuilder.toString(), violations);
    }
  }

}