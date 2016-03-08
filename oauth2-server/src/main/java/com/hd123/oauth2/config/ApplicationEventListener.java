package com.hd123.oauth2.config;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;

/**
 * 应用事件监听
 *
 * @author liyue
 */
public class ApplicationEventListener implements ApplicationListener<ContextStartedEvent> {

  @Override
  public void onApplicationEvent(ContextStartedEvent event) {
    System.out.println("started====================");
  }

  public static final class A implements ApplicationListener<ContextStartedEvent> {
    @Override
    public void onApplicationEvent(ContextStartedEvent event) {
      System.out.println("started====================");
    }

  }

  public static final class B implements ApplicationListener<ContextClosedEvent> {
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
      System.out.println("Closed====================");
    }

  }

  public static final class C implements ApplicationListener<ContextStoppedEvent> {
    @Override
    public void onApplicationEvent(ContextStoppedEvent event) {
      System.out.println("Stopped====================");
    }

  }

  // @Bean
  // public ApplicationListener<ContextClosedEvent> as() {
  // return event -> {
  // System.out.println("Closed====================");
  // };
  // }
  //
  // @Bean
  // public ApplicationListener<ContextStoppedEvent> ass() {
  // return event -> {
  // System.out.println("Stoppe====================");
  // };
  // }

}
