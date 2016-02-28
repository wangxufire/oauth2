package com.hd123.oauth2.rest;

import static com.hd123.oauth2.support.ExceptionCode.ok;

/**
 * Rest String response
 *
 * @author liyue
 */
public class RsStringResponse extends RsResponse {
  private static final long serialVersionUID = 9153177309442248320L;

  private String data;

  public RsStringResponse() {
    super();
  }

  public RsStringResponse(String data) {
    super(ok);
    this.data = data;
  }

  /**
   * 请求结果
   *
   * @return data
   */
  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

}