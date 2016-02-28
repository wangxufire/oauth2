package com.hd123.oauth2.exception;

import static com.hd123.oauth2.support.ExceptionCode.failed;
import static com.hd123.oauth2.support.ExceptionCode.valueOf;

import javax.naming.OperationNotSupportedException;

import com.hd123.oauth2.support.ExceptionCode;

/**
 * 授权服务异常
 * 
 * @author liyue
 * @since 0.0.1
 */
public class AuthServiceException extends Exception {
  private static final long serialVersionUID = -4937171361493289613L;

  private int errorCode = failed.getCode();

  @Deprecated
  public AuthServiceException() throws OperationNotSupportedException {
    throw new OperationNotSupportedException();
  }

  public AuthServiceException(ExceptionCode code) {
    super(code.getMessage());
    this.errorCode = code.getCode();
  }

  /**
   * 获取异常定义
   * 
   * @return ExceptionCode
   */
  public ExceptionCode getExceptionCode() {
    return valueOf(getErrorCode());
  }

  /**
   * 获取异常代码
   *
   * @return 异常代码
   */
  public int getErrorCode() {
    return errorCode;
  }

  /**
   * 返回 className: code, message，如果message为null，则返回className: code
   * 
   * {@link Throwable#toString()}
   * 
   * @return 异常信息
   */
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder(getClass().getName()).append(":").append(
        getErrorCode());

    final String message = getLocalizedMessage();
    if (message != null) {
      builder.append(", ").append(message);
    }

    return builder.toString();
  }

}