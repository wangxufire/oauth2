package com.hd123.oauth2.geetest;

import static com.hd123.oauth2.common.Constants.GEETEST_PRIVATEKEY;
import static com.hd123.oauth2.geetest.GeetestLib.FAIL_RES;
import static com.hd123.oauth2.geetest.GeetestLib.FORBIDDEN_RES;
import static com.hd123.oauth2.geetest.GeetestLib.SUCCESS_RES;
import static com.hd123.oauth2.geetest.GeetestLib.getGtServerStatusSession;
import static java.lang.System.getProperty;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 使用post方式，返回验证结果, request表单中必须包含challenge, validate, seccode
 * 
 * @author liyue
 */
public class VerifyServlet extends HttpServlet {
  private static final long serialVersionUID = 244554953219893949L;

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    final GeetestLib gtSdk = new GeetestLib();
    gtSdk.setPrivateKey(getProperty(GEETEST_PRIVATEKEY));

    // 从session中获取gt-server状态
    int gtServerStatusCode = getGtServerStatusSession(request);
    // 从session中获取challenge
    gtSdk.getChallengeSession(request);

    String gtResult = FAIL_RES;
    if (gtServerStatusCode == 1) {
      // gt-server正常，向gt-server进行二次验证
      gtResult = gtSdk.enhencedValidateRequest(request);
    } else {
      // gt-server非正常情况下，进行failback模式验证
      gtResult = gtSdk.failbackValidateRequest(request);
    }

    if (gtResult.equals(SUCCESS_RES)) {
      // 验证成功
      try (final PrintWriter out = response.getWriter()) {
        out.print(SUCCESS_RES);
      }
    } else if (gtResult.equals(FORBIDDEN_RES)) {
      // 验证被判为机器人
      try (final PrintWriter out = response.getWriter()) {
        out.print(FORBIDDEN_RES);
      }
    } else {
      // 验证失败
      try (final PrintWriter out = response.getWriter()) {
        out.print(FAIL_RES);
      }
    }
  }

}