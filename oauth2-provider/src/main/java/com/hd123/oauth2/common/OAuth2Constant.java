package com.hd123.oauth2.common;

/**
 * 常量
 *
 * @author liyue
 * @since 0.1.0
 */
public final class OAuth2Constant {

  // Constant
  public static final String ACCESS_TOKEN = "access_token";
  public static final String AUTHORIZATION = "Authorization";
  public static final String BEARER = "Bearer ";
  public static final String WWW_AUTHENTICATE = "WWW-Authenticate";
  public static final String URL_PATTERN = "^((https|http)?://)+(([0-9a-z_!~*'().&=+$%-]+:)"
      + "?[0-9a-z_!~*'().&=+$%-]+@)?(([0-9]{1,3}\\.){3}[0-9]{1,3}|([0-9a-z_!~*'()-]+\\.) *"
      + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\.[a-z]{2,6})(:[0-9]{1,4})?((/?)|(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
  public static final String REQUEST_METHOD = "POST";
  public static final String ENCODING = "UTF-8";
  public static final String INTERFACE_URI = "uri";
  public static final String TLS = "TLS";
  public static final String HTTPS_PROTOCOL = "https";
  public static final String CONTENT_TYPE = "Content-Type";
  public static final String MEDIA_TYPE = "application/json; charset=UTF-8";
  public static final String CODE = "errorCode";
  public static final String MESSAGE = "message";
  public static final String DEFAULT_REALM = "heading";
  public static final String VALIDATE_URI = "/api/check";

  // Special Symbol
  public static final String EMPTY = "";
  public static final String COLON = ":";
  public static final String SPACE = " ";
  public static final String COMMA = ",";
  public static final String QUOTES = "\"";
  public static final String LEFT_BRACKET = "{";
  public static final String RIGHT_BRACKET = "}";

}
