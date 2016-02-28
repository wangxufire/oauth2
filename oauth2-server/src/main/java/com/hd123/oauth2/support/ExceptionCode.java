package com.hd123.oauth2.support;

import static com.google.common.base.Objects.equal;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * 全局异常代码(没有请自定义)
 * 
 * @author liyue
 * @since 0.0.1
 */
public enum ExceptionCode {

  // rest 响应成功
  ok(200, "操作成功"),

  // 系统内部错误
  failed(-1, "操作失败"),

  notSupported(-2, "不支持"),

  illegalArgument(-3, "请求参数非法"),

  optionalReturnNull(-4, "Optional返回空值,请检查所传参数"),

  httpClientFailed(-6, "构造httpClient失败"),

  dateFormatTransError(-9, "日期格式转换出错"),

  @Deprecated
  unknown(-999999, "未知异常"),

  // http错误
  unauthorizedCurrentUser(401, "当前用户未授权"),

  unauthorizedCurrentUserToResource(403, "当前用户未授权访问该资源"),

  // oauth
  unauthorizedAppId(100000, "未授权的app_id[%s]"),

  unauthorizedAppSecret(100001, "未授权的app_secret[%s]"),

  invalidAuthorizationCode(100002, "无效的授权码[%s]"),

  invalidResponseType(100003, "无效的响应类型[%s]"),

  invalidRedirectUrl(100004, "无效的回调地址[%s]"),

  fetchAuthorizationCodeFailed(100005, "获取授权码失败"),

  fetchAccessTokenFailed(100006, "获取access_token失败"),

  accessTokenOutOfDate(100007, "access_token已失效"),

  invalidGrantType(100008, "无效的授权类型[%s]"),

  invalidAccessToken(100009, "无效的access_token[%s]"),

  appRegisterIncorrect(100010, "应用[%s]未能正确接入"),

  interfaceAddressUnauthorized(100011, "接口地址[%s]未授权"),

  // product
  productNameExist(200000, "产品名[%s]已存在"),

  productNotExist(200001, "id为[%s]的产品不存在"),

  productNameNotExist(200002, "产品名为[%s]的产品已存在"),

  // user
  usernameExist(300000, "用户名[%s]已存在"),

  usernameNotExist(300001, "用户名[%s]不存在"),

  passwordIncorrect(300002, "用户[%s]密码不正确"),

  checkCodeIncorrect(300003, "验证码不正确"),

  canNotDeleteAdminUser(300004, "不能删除管理员用户"),

  userIdNotExist(300005, "id为[%s]的用户不存在"),

  userNotEnabled(300006, "用户[%s]不是启用状态"),

  emailExist(300007, "邮箱[%s]已存在"),

  currentUserCanNotOperate(300008, "当前用户[%s]无权执行此操作"),

  // app
  appNameExist(400000, "应用名[%s]已存在"),

  appNotExist(400001, "id为[%s]的应用不存在"),

  appNotApplayAnyAuth(400002, "应用[%s]未申请任何接口调用权限"),

  appAlreadyAudited(400003, "应用[%s]已审核，不能重复审核"),

  ;

  private int code;
  private String message;

  private ExceptionCode() {
  }

  private ExceptionCode(int code, String message) {
    setCode(code);
    setMessage(message);
  }

  /**
   * 异常代码
   * 
   * @return 异常代码
   */
  public int getCode() {
    return code;
  }

  private void setCode(int code) {
    this.code = code;
  }

  /**
   * 异常信息
   * 
   * @return 异常信息
   */
  public String getMessage() {
    return message;
  }

  private void setMessage(String message) {
    this.message = message;
  }

  /**
   * Rebuild Error Message by placeholders
   *
   * @param placeholders
   *          占位符信息
   * @return ExceptionCode
   */
  public ExceptionCode messageOf(Object... placeholders) {
    final String message = getMessage();
    if (placeholders.length == 0 || placeholders[0] == null || equal(placeholders[0], EMPTY)) {
      setMessage(message.replace("[%s]", EMPTY));
    } else {
      setMessage(format(message, placeholders));
    }

    new Thread(() -> {
      rebuidMessage(this);
    }).start();

    return this;
  }

  /**
   * Return the enum constant of this type with the specified numeric value.
   * 
   * @param code
   *          the numeric value of the enum to be returned
   * @return the enum constant with the specified numeric value
   * @throws IllegalArgumentException
   *           if this enum has no constant for the specified numeric value
   */
  public static ExceptionCode valueOf(int code) {
    for (ExceptionCode ec : values()) {
      if (ec.code == code) {
        return ec;
      }
    }
    throw new IllegalArgumentException(format("No matching constant for [%d]", code));
  }

  /**
   * Reset Error Message
   *
   * @param ec
   *          ExceptionCode
   */
  private void rebuidMessage(ExceptionCode ec) {
    final String message = ec.getMessage();
    if (message.contains("[")) {
      final String left = message.substring(0, message.indexOf("["));
      final String right = message.substring(message.indexOf("]"), message.length());
      ec.setMessage(new StringBuffer(left).append("[%s").append(right).toString());
    }
  }

}