package com.hd123.oauth2.support;

import static com.hd123.oauth2.util.TokenUtil.BEARER;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * Spring Security logout handler, specialized for Ajax requests.
 * 
 * @author liyue
 */
public class AjaxLogoutSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler
    implements LogoutSuccessHandler {

  @Inject
  private TokenStore tokenStore;

  @Override
  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    final String token = request.getHeader(AUTHORIZATION);
    if (isNotBlank(token) && token.startsWith(BEARER)) {
      final OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(StringUtils
          .substringAfter(token, BEARER));

      if (oAuth2AccessToken != null) {
        tokenStore.removeAccessToken(oAuth2AccessToken);
      }
    }

    response.setStatus(SC_OK);
  }

}