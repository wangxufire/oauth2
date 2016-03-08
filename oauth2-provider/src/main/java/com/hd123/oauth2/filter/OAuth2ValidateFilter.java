package com.hd123.oauth2.filter;

import static com.hd123.oauth2.common.OAuth2Constant.ACCESS_TOKEN;
import static com.hd123.oauth2.common.OAuth2Constant.AUTHORIZATION;
import static com.hd123.oauth2.common.OAuth2Constant.BEARER;
import static com.hd123.oauth2.common.OAuth2Constant.CODE;
import static com.hd123.oauth2.common.OAuth2Constant.COMMA;
import static com.hd123.oauth2.common.OAuth2Constant.DEFAULT_REALM;
import static com.hd123.oauth2.common.OAuth2Constant.EMPTY;
import static com.hd123.oauth2.common.OAuth2Constant.MESSAGE;
import static com.hd123.oauth2.common.OAuth2Constant.URL_PATTERN;
import static com.hd123.oauth2.common.OAuth2Constant.VALIDATE_URI;
import static com.hd123.oauth2.common.OAuth2Constant.WWW_AUTHENTICATE;
import static com.hd123.oauth2.util.OAuth2Util.checkAccessToken;
import static com.hd123.oauth2.util.OAuth2Util.isBlank;
import static java.util.Arrays.asList;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * OAuth2认证过滤器
 *
 * <pre>
 *    也可以不用此filter, resource server方可按照类似逻辑实现filter
 *    其他语言可自行添加http中间件
 * </pre>
 *
 * <pre>
 *    Spring4.0或以上版本可用java config方式添加该filter,
 *    servlet3.0以上版本可继承该filter并使用{@link javax.servlet.annotation.WebFilter}注解添加filter,
 *    安全性较暴露在web.xml要高
 * </pre>
 *
 * @author liyue
 * @since 0.0.5
 */
public class OAuth2ValidateFilter implements Filter {

  // ~ Init Parameter
  // ================================================================================================

  /* required 验证地址,只到端口号 */
  private static final String VALIDATE_URL_INIT_PARAMETER = "oauth2ServerUrl";
  /* optional 域 */
  private static final String REALM_INIT_PARAMETER = "realm";
  /* optional 不校验的uri,相对于Servlet ContextPath */
  private static final String EXCLUDED_URIS_INIT_PARAMETER = "excludeUris";

  // ~ Method
  // ================================================================================================

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    final String oauth2ServerUrl = filterConfig.getInitParameter(VALIDATE_URL_INIT_PARAMETER);
    if (isBlank(oauth2ServerUrl)) {
      throw new IllegalArgumentException("OAuth2验证服务器地址不能为空");
    }
    final Pattern pattern = compile(URL_PATTERN, CASE_INSENSITIVE);
    if (!pattern.matcher(oauth2ServerUrl).matches()) {
      throw new IllegalArgumentException("OAuth2验证服务器地址不合法");
    }
    setOauth2ValidateUrl(oauth2ServerUrl);
    final String currentRealm = filterConfig.getInitParameter(REALM_INIT_PARAMETER);
    setRealm(currentRealm);
    final String excludesStr = filterConfig.getInitParameter(EXCLUDED_URIS_INIT_PARAMETER);
    final String[] excludes = isBlank(excludesStr) ? null : excludesStr.split(COMMA);
    setExcludeUris(excludes);
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    final HttpServletRequest req = (HttpServletRequest) request;
    final HttpServletResponse res = (HttpServletResponse) response;

    final String uri = req.getRequestURI().replace(req.getContextPath(), EMPTY);

    if (excludeUris.contains(uri)) {
      chain.doFilter(request, response);
    } else {
      String accessToken = req.getParameter(ACCESS_TOKEN);
      if (isBlank(accessToken)) {
        final String header = req.getHeader(AUTHORIZATION);
        if (!isBlank(header) && header.startsWith(BEARER)) {
          accessToken = header.substring(7);
        }
        if (isBlank(accessToken)) {
          final StringBuilder errorBuilder = new StringBuilder("Bearer realm=")
              .append(realm)
              .append(
                  ", error=unauthorized, error_description=Authentication is required to obtain an access token (anonymous not allowed)");
          res.setStatus(SC_UNAUTHORIZED);
          res.addHeader(WWW_AUTHENTICATE, errorBuilder.toString());
          return;
        }
      }

      final Map<String, String> validate = checkAccessToken(uri, accessToken, oauth2ValidateUrl);
      final String code = validate.get(CODE);
      if (isBlank(code) || Integer.valueOf(code).intValue() != SC_OK) {
        final StringBuilder errorBuilder = new StringBuilder("Bearer realm=").append(realm)
            .append(", error=unauthorized, error_description=").append(validate.get(MESSAGE));
        res.setStatus(SC_UNAUTHORIZED);
        res.addHeader(WWW_AUTHENTICATE, errorBuilder.toString());
      } else {
        chain.doFilter(request, response);
      }
    }
  }

  @Override
  public void destroy() {
    // Nothing to destroy
  }

  private static String oauth2ValidateUrl;
  private static String realm;
  private static Collection<String> excludeUris;

  public static void setRealm(String currentRealm) {
    realm = isBlank(currentRealm) ? DEFAULT_REALM : currentRealm;
  }

  private void setOauth2ValidateUrl(String oauth2ServerUrl) {
    oauth2ValidateUrl = oauth2ServerUrl + VALIDATE_URI;
  }

  private void setExcludeUris(String[] excludes) {
    excludeUris = excludes == null ? new ArrayList<String>() : asList(excludes);
  }

}