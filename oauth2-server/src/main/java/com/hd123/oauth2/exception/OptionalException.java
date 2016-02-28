package com.hd123.oauth2.exception;

import static com.hd123.oauth2.support.ExceptionCode.optionalReturnNull;

import java.util.function.Supplier;

import javax.naming.OperationNotSupportedException;

import com.hd123.oauth2.support.ExceptionCode;

/**
 * OptionalException 用于Optional的value为null时抛出
 * 
 * @see java.util.Optional
 *
 * @author liyue
 * @since 0.0.1
 */
public class OptionalException extends AuthServiceException implements
    Supplier<AuthServiceException> {

  private static final long serialVersionUID = 3697019050244987681L;

  public OptionalException() {
    super(optionalReturnNull);
  }

  @SuppressWarnings("unused")
  private OptionalException(ExceptionCode code) throws OperationNotSupportedException {
    super(code);
    throw new OperationNotSupportedException();
  }

  @Override
  public AuthServiceException get() {
    return this;
  }

}