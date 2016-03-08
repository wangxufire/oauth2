package com.hd123.oauth2.config;

import static org.apache.logging.log4j.LogManager.getLogger;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;
import static reactor.bus.EventBus.create;
import static reactor.jarjar.com.lmax.disruptor.dsl.ProducerType.SINGLE;

import java.lang.reflect.Field;

import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import reactor.Environment;
import reactor.bus.EventBus;
import reactor.core.dispatch.RingBufferDispatcher;
import reactor.core.dispatch.wait.AgileWaitingStrategy;

/**
 * Reactor基于RingBuffer异步配置
 *
 * @author liyue
 * @since 0.3.1
 */
@Configuration
public class ReactorConfiguration {

  private final Logger logger = getLogger(ReactorConfiguration.class);

  @Bean
  @Role(ROLE_INFRASTRUCTURE)
  public EventBus eventBus(Environment environment) {
    final String dispatcherName = "ringBufferDispatcher";
    environment.setDispatcher(dispatcherName, new RingBufferDispatcher("reactorDispatcher", 2048,
        throwable -> {
          if (logger.isErrorEnabled()) {
            logger.error("EventBus Dispatcher Failed", throwable);
          }
        }, SINGLE, new AgileWaitingStrategy()));
    try {
      final Field defaultDispatcher = environment.getClass().getDeclaredField("defaultDispatcher");
      defaultDispatcher.setAccessible(true);
      defaultDispatcher.set(environment, dispatcherName);
    } catch (Exception ex) {
      if (logger.isWarnEnabled()) {
        logger.warn("Modify Default Dispatcher Failed, Caused By: {}", ex.getMessage());
      }
    }

    return create(environment);
  }

}