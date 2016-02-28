package com.hd123.oauth2.rest;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 授权凭证
 *
 * @author liyue
 */
public class AccessToken implements Serializable {
  private static final long serialVersionUID = -4620231389744927411L;

  @JSONField(name = "access_token")
  @JsonProperty(value = "access_token")
  private String accessToken;
  @JSONField(name = "refresh_token")
  @JsonProperty(value = "refresh_token")
  private String refreshToken;
  @JSONField(name = "expires_in")
  @JsonProperty(value = "expires_in")
  private Long expiresIn;
  @JSONField(name = "token_type")
  @JsonProperty(value = "token_type")
  private String type;
  private String state;

  public AccessToken() {
  }

  public AccessToken(String accessToken, String refreshToken, Long expireIn, String state) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.expiresIn = expireIn;
    this.state = state;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public Long getExpiresIn() {
    return expiresIn;
  }

  public void setExpiresIn(Long expiresIn) {
    this.expiresIn = expiresIn;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

}