package com.hd123.oauth2.util;

import static com.hd123.oauth2.common.ProfileConstants.DEVELOPMENT;
import static com.hd123.oauth2.common.ProfileConstants.LOGGING;
import static com.hd123.oauth2.common.ProfileConstants.PRODUCTION;
import static com.hd123.oauth2.common.ProfileConstants.UNSAFE;
import static java.util.Arrays.asList;

import java.util.Collection;

import javax.inject.Inject;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 运行模式工具类
 *
 * @author liyue
 * @since 0.0.1
 */
@Component
public class ProfileUtil implements InitializingBean {

  @Inject
  private Environment env;

  private Collection<String> profiles;

  /**
   * @return 当前模式
   */
  public Collection<String> getProfiles() {
    return profiles;
  }

  /**
   * @return 是否日志模式
   */
  public boolean isLog() {
    return profiles.contains(LOGGING);
  }

  /**
   * @return 是否开发模式
   */
  public boolean isDev() {
    return profiles.contains(DEVELOPMENT);
  }

  /**
   * @return 是否生产模式
   */
  public boolean isProd() {
    return profiles.contains(PRODUCTION);
  }

  /**
   * @return 是否开发或日志模式
   */
  public boolean isDevOrLog() {
    return (profiles.contains(DEVELOPMENT) || profiles.contains(LOGGING));
  }

  /**
   * @return 是否开发且日志模式
   */
  public boolean isDevAndLog() {
    return (profiles.contains(DEVELOPMENT) && profiles.contains(LOGGING));
  }

  /**
   * @return 是否开发且生产模式
   */
  public boolean isDevAndProd() {
    return (profiles.contains(DEVELOPMENT) && profiles.contains(PRODUCTION));
  }

  /**
   * @return 是否非安全模式
   */
  public boolean isUnSafe() {
    return profiles.contains(UNSAFE);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    profiles = asList(env.getActiveProfiles());
  }

}