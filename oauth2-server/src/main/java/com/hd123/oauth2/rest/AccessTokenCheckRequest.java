package com.hd123.oauth2.rest;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * accessToken校验请求
 *
 * @author liyue
 */
public class AccessTokenCheckRequest implements Serializable {
  private static final long serialVersionUID = 6248134579905776856L;

  @NotBlank
  private String uri;
  @NotBlank
  @JsonProperty(value = "access_token")
  private String accessToken;

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

}
