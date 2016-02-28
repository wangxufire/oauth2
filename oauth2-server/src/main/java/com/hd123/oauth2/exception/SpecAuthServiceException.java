package com.hd123.oauth2.exception;

import static com.hd123.oauth2.support.ExceptionCode.failed;
import static com.hd123.oauth2.support.ExceptionCode.valueOf;

import javax.naming.OperationNotSupportedException;

import com.hd123.oauth2.support.ExceptionCode;

/**
 * 授权服务异常,可以不显示抛出
 *
 * @author liyue
 * @since 0.1.0
 */
public class SpecAuthServiceException extends RuntimeException {

  private static final long serialVersionUID = -4937171361493289613L;

  private int errorCode = failed.getCode();

  @Deprecated
  public SpecAuthServiceException() throws OperationNotSupportedException {
    throw new OperationNotSupportedException();
  }

  public SpecAuthServiceException(ExceptionCode code) {
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
   * <p>
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