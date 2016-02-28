package com.hd123.oauth2.rest;

import static com.hd123.oauth2.support.ExceptionCode.ok;

import java.beans.Transient;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlTransient;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.hd123.oauth2.support.ExceptionCode;

/**
 * Restful Service Response base
 * 
 * @since 0.0.1
 * @author liyue
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RsResponse implements Serializable {
  private static final long serialVersionUID = -7541098774144937639L;

  private int errorCode = ok.getCode();
  private String message = ok.getMessage();
  private String stackTrace;

  /**
   * 构造函数，初始化正常返回。
   */
  public RsResponse() {
    this(ok);
  }

  /**
   * 构造函数，初始化为指定异常代码。
   * 
   * @param code
   *          异常代码
   */
  public RsResponse(ExceptionCode code) {
    if (code != null) {
      setErrorCode(code.getCode());
      setMessage(code.getMessage());
    }
  }

  /**
   * 异常代码
   * 
   * {@link com.hd123.oauth2.support.ExceptionCode}
   * 
   * @return 0:正常， 非0异常。
   */
  public int getErrorCode() {
    return errorCode;
  }

  /**
   * 设置异常代码
   * 
   * @param errorCode
   *          异常代码
   */
  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }

  /**
   * 异常描述
   * 
   * @return 异常描述
   */
  public String getMessage() {
    return message;
  }

  /**
   * 设置异常信息
   * 
   * @param message
   *          异常信息
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * 异常堆栈
   * 
   * @return 异常堆栈
   */
  public String getStackTrace() {
    return stackTrace;
  }

  public void setStackTrace(String stackTrace) {
    this.stackTrace = stackTrace;
  }

  /**
   * @return 是否正常返回。
   */
  @Transient
  @JsonIgnore
  @XmlTransient
  @JSONField(serialize = false)
  public boolean isOk() {
    return errorCode == ok.getCode();
  }

}