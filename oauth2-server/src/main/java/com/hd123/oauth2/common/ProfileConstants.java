package com.hd123.oauth2.common;

/**
 * 运行模式常量
 *
 * @author liyue
 * @since 0.0.1
 */
public final class ProfileConstants {

  /** 开发环境 */
  public static final String DEVELOPMENT = "dev";
  /** 生产环境 */
  public static final String PRODUCTION = "prod";
  /** 非安全环境 */
  public static final String UNSAFE = "unsafe";

  /** 非生产环境 */
  public static final String UN_PRODUCTION = "!prod";

  /** 打印详细日志 */
  public static final String LOGGING = "log";

  private ProfileConstants() {
  }

}