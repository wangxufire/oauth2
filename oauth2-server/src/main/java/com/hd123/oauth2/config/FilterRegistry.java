package com.hd123.oauth2.config;

import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 * 自定义filter
 *
 * @author liyue
 * @since 0.0.1
 */
@Configuration
public class FilterRegistry {

  @Autowired
  @Role(ROLE_INFRASTRUCTURE)
  @Bean(name = "characterEncodingFilterRegistration")
  public FilterRegistrationBean characterEncodingFilterRegistration(
      CharacterEncodingFilter characterEncodingFilter) {
    FilterRegistrationBean registration = new FilterRegistrationBean();
    registration.setFilter(characterEncodingFilter);
    registration.addUrlPatterns("/*");
    registration.addInitParameter("encoding", "UTF-8");
    registration.addInitParameter("forceEncoding", "true");
    registration.setName("characterEncodingFilter");
    registration.setOrder(1);
    registration.setEnabled(true);
    registration.setAsyncSupported(true);

    return registration;
  }

  // @Role(ROLE_INFRASTRUCTURE)
  // @Bean(name = "authorizationFilterRegistration")
  // public FilterRegistrationBean authorizationFilterRegistration() {
  // FilterRegistrationBean registration = new FilterRegistrationBean();
  // registration
  // // 主页、登录、注册页面不拦截
  // .setFilter(new AuthorizationFilter(newArrayList("/", "/api/user/login",
  // "/api/user/create")));
  // registration.addUrlPatterns("/api/*");
  // registration.setName("authorizationFilter");
  // registration.setOrder(2);
  // registration.setEnabled(true);
  // registration.setAsyncSupported(true);
  //
  // return registration;
  // }

  // @Bean(name = "oauth2ValidateFilterRegistration")
  // public FilterRegistrationBean oauth2FilterRegistration() {
  // FilterRegistrationBean registration = new FilterRegistrationBean();
  // registration.setFilter(new OAuth2ValidateFilter());
  // registration.addUrlPatterns("/token");
  // registration.setName("oauth2ValidateFilter");
  // registration.addInitParameter("oauth2ServerUrl",
  // "http://172.17.2.143:8180");
  // registration.setOrder(1);
  //
  // return registration;
  // }

}