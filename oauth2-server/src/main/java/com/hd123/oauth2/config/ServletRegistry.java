package com.hd123.oauth2.config;

import static org.springframework.beans.factory.config.BeanDefinition.ROLE_SUPPORT;

import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Role;

import com.hd123.oauth2.geetest.StartCaptchaServlet;
import com.hd123.oauth2.geetest.VerifyServlet;

/**
 * 自定义servlet
 *
 * @author liyue
 * @since 0.0.1
 */
@Configuration
public class ServletRegistry {

  @Role(ROLE_SUPPORT)
  @Description("geetest验证登陆")
  @Bean(name = "startCaptchaServletRegistration")
  public ServletRegistrationBean startCaptchaServletRegistration() {
    ServletRegistrationBean registration = new ServletRegistrationBean();
    registration.setServlet(new StartCaptchaServlet());
    registration.setName("startCaptchaServlet");
    registration.addUrlMappings("/startCaptchaServlet/*");
    registration.setOrder(2);
    registration.setEnabled(true);
    registration.setAsyncSupported(true);

    return registration;
  }

  @Role(ROLE_SUPPORT)
  @Description("geetest登陆验证")
  @Bean(name = "startVerifyServletRegistration")
  public ServletRegistrationBean startVerifyServletRegistration() {
    ServletRegistrationBean registration = new ServletRegistrationBean();
    registration.setServlet(new VerifyServlet());
    registration.setName("verifyServlet");
    registration.addUrlMappings("/verifyServlet/*");
    registration.setOrder(3);
    registration.setEnabled(true);
    registration.setAsyncSupported(true);

    return registration;
  }

}