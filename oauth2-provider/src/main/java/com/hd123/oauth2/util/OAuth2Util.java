package com.hd123.oauth2.util;

import static com.hd123.oauth2.common.OAuth2Constant.ACCESS_TOKEN;
import static com.hd123.oauth2.common.OAuth2Constant.COLON;
import static com.hd123.oauth2.common.OAuth2Constant.COMMA;
import static com.hd123.oauth2.common.OAuth2Constant.CONTENT_TYPE;
import static com.hd123.oauth2.common.OAuth2Constant.EMPTY;
import static com.hd123.oauth2.common.OAuth2Constant.ENCODING;
import static com.hd123.oauth2.common.OAuth2Constant.HTTPS_PROTOCOL;
import static com.hd123.oauth2.common.OAuth2Constant.INTERFACE_URI;
import static com.hd123.oauth2.common.OAuth2Constant.LEFT_BRACKET;
import static com.hd123.oauth2.common.OAuth2Constant.MEDIA_TYPE;
import static com.hd123.oauth2.common.OAuth2Constant.QUOTES;
import static com.hd123.oauth2.common.OAuth2Constant.REQUEST_METHOD;
import static com.hd123.oauth2.common.OAuth2Constant.RIGHT_BRACKET;
import static com.hd123.oauth2.common.OAuth2Constant.SPACE;
import static com.hd123.oauth2.common.OAuth2Constant.TLS;
import static java.lang.Character.isWhitespace;
import static javax.net.ssl.SSLContext.getInstance;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * OAuth2-Provider 工具类
 *
 * @author liyue
 * @since 0.1.0
 */
public final class OAuth2Util {

  private static final X509TrustManager trustManager = new DefaultTrustManager();
  private static final HostnameVerifier hostnameVerifier = new DefaultHostnameVerifier();

  /**
   * 判断对象是否相等
   *
   * @param a
   *          a
   * @param b
   *          b
   * @return isEqual
   */
  public static boolean equal(Object a, Object b) {
    return Objects.equals(a, b);
  }

  /**
   * 校验字符串是否为空
   *
   * @param cs
   *          cs
   * @return isBlank
   */
  public static boolean isBlank(final CharSequence cs) {
    int strLen;
    if (cs == null || (strLen = cs.length()) == 0) {
      return true;
    }
    for (int i = 0; i < strLen; i++) {
      if (!isWhitespace(cs.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * 验证access_token
   *
   * @param uri
   *          验证接口uri
   * @param accessToken
   *          access_token
   * @param validateUrl
   *          验证地址
   * @return check result
   * @throws IOException
   *           IOException
   */
  public static Map<String, String> checkAccessToken(final String uri, final String accessToken,
      final String validateUrl) throws IOException {
    HttpURLConnection conn = null;
    InputStream in = null;
    BufferedReader br = null;
    DataOutputStream dos = null;
    try {
      final URL url = new URL(validateUrl);

      if (HTTPS_PROTOCOL.equals(url.getProtocol())) {
        final HttpsURLConnection connHttps = (HttpsURLConnection) url.openConnection();
        try {
          final SSLContext ctx = getInstance(TLS);
          ctx.init(new KeyManager[0], new TrustManager[] {
            trustManager }, new SecureRandom());
          connHttps.setSSLSocketFactory(ctx.getSocketFactory());
          connHttps.setHostnameVerifier(hostnameVerifier);
        } catch (Exception e) {
          throw new IOException(e);
        }
        conn = connHttps;
      } else {
        conn = (HttpURLConnection) url.openConnection();
      }

      conn.setRequestMethod(REQUEST_METHOD);
      conn.setRequestProperty(CONTENT_TYPE, MEDIA_TYPE);
      conn.setDoInput(true);
      conn.setDoOutput(true);
      conn.connect();

      final StringBuilder jsonBuilder = new StringBuilder(LEFT_BRACKET).append(QUOTES);
      jsonBuilder.append(INTERFACE_URI).append(QUOTES).append(COLON).append(QUOTES).append(uri)
          .append(QUOTES).append(COMMA).append(QUOTES).append(ACCESS_TOKEN).append(QUOTES)
          .append(COLON).append(QUOTES).append(accessToken).append(QUOTES).append(RIGHT_BRACKET);
      dos = new DataOutputStream(conn.getOutputStream());
      dos.writeBytes(jsonBuilder.toString());

      in = conn.getInputStream();
      br = new BufferedReader(new InputStreamReader(in, ENCODING));
      final StringBuilder resp = new StringBuilder();
      String line = null;
      while ((line = br.readLine()) != null) {
        resp.append(line);
      }
      final String response = resp.toString();
      final String[] entries = response.substring(1, response.length() - 1).replace(SPACE, EMPTY)
          .replace(QUOTES, EMPTY).split(COMMA);
      final Map<String, String> result = new HashMap<String, String>();
      for (final String entry : entries) {
        final String[] pair = entry.split(COLON);
        result.put(pair[0], pair[1]);
      }

      return result;
    } catch (Exception ex) {
      throw new IOException(ex);
    } finally { // 兼容java6,不采用try-with-resource
      if (conn != null) {
        conn.disconnect();
      }
      if (dos != null) {
        dos.flush();
        dos.close();
      }
      if (in != null) {
        in.close();
      }
      if (br != null) {
        br.close();
      }
    }
  }

  private static class DefaultTrustManager implements X509TrustManager {

    @Override
    public X509Certificate[] getAcceptedIssuers() {
      return null;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {
    }

  }

  private static class DefaultHostnameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String s, SSLSession sslSession) {
      return false; // 默认认证不通过，进行证书校验。
    }

  }

}