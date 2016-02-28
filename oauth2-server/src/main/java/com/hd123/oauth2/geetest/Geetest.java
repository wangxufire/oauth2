package com.hd123.oauth2.geetest;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author liyue
 */
public class Geetest implements Serializable {
  private static final long serialVersionUID = -973072446591400271L;

  @JSONField(name = "geetest_challenge")
  private String geetestChallenge;
  @JSONField(name = "geetest_validate")
  private String geetestValidate;
  @JSONField(name = "geetest_seccode")
  private String geetestSeccode;

  public String getGeetestChallenge() {
    return geetestChallenge;
  }

  public void setGeetestChallenge(String geetestChallenge) {
    this.geetestChallenge = geetestChallenge;
  }

  public String getGeetestValidate() {
    return geetestValidate;
  }

  public void setGeetestValidate(String geetestValidate) {
    this.geetestValidate = geetestValidate;
  }

  public String getGeetestSeccode() {
    return geetestSeccode;
  }

  public void setGeetestSeccode(String geetestSeccode) {
    this.geetestSeccode = geetestSeccode;
  }

}