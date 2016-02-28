package com.hd123.oauth2.geetest;

import static com.hd123.oauth2.common.Constants.CAPTCHAID;
import static com.hd123.oauth2.common.Constants.GEETEST_PRIVATEKEY;
import static java.lang.System.getProperty;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 使用Get的方式返回：challenge和capthca_id 此方式以实现前后端完全分离的开发模式 专门实现failback
 * 
 * @author liyue
 */
public final class StartCaptchaServlet extends HttpServlet {
  private static final long serialVersionUID = -706946390783144934L;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Conifg the parameter of the geetest object
    final GeetestLib gtSdk = new GeetestLib();
    gtSdk.setCaptchaId(getProperty(CAPTCHAID));
    gtSdk.setPrivateKey(getProperty(GEETEST_PRIVATEKEY));
    // 如果是同一会话多实例，可以使用此函数的另一重载实现式，设置不同的key即可
    gtSdk.setGtSession(request);

    String resStr = EMPTY;
    if (gtSdk.preProcess() == 1) {
      // gt server is in use
      resStr = gtSdk.getSuccessPreProcessRes();
      gtSdk.setGtServerStatusSession(request, 1);
    } else {
      // gt server is down
      resStr = gtSdk.getFailPreProcessRes();
      gtSdk.setGtServerStatusSession(request, 0);
    }

    try (final PrintWriter out = response.getWriter()) {
      out.println(resStr);
    }
  }

}