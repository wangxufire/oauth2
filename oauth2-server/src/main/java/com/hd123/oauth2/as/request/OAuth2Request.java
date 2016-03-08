package com.hd123.oauth2.as.request;

import static com.google.common.collect.Maps.newHashMap;
import static com.hd123.oauth2.as.common.OAuth2.OAUTH_APP_ID;
import static com.hd123.oauth2.as.common.OAuth2.OAUTH_APP_SECRET;
import static com.hd123.oauth2.as.common.OAuth2.OAUTH_REDIRECT_URL;
import static org.apache.logging.log4j.LogManager.getLogger;
import static org.apache.oltu.oauth2.common.OAuth.HeaderType.AUTHORIZATION;
import static org.apache.oltu.oauth2.common.OAuth.OAUTH_CLIENT_ID;
import static org.apache.oltu.oauth2.common.OAuth.OAUTH_CLIENT_SECRET;
import static org.apache.oltu.oauth2.common.OAuth.OAUTH_REDIRECT_URI;
import static org.apache.oltu.oauth2.common.OAuth.OAUTH_SCOPE;
import static org.apache.oltu.oauth2.common.utils.OAuthUtils.decodeClientAuthenticationHeader;
import static org.apache.oltu.oauth2.common.utils.OAuthUtils.decodeScopes;
import static org.apache.oltu.oauth2.common.utils.OAuthUtils.isEmpty;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.apache.oltu.oauth2.as.request.OAuthRequest;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.validators.OAuthValidator;

/**
 * The Abstract OAuth request for the Authorization server.
 *
 * @author liyue
 */
abstract class OAuth2Request {

  private final Logger logger = getLogger(OAuthRequest.class);

  protected HttpServletRequest request;
  protected OAuthValidator<HttpServletRequest> validator;
  protected Map<String, Class<? extends OAuthValidator<HttpServletRequest>>> validators = newHashMap();
  private String[] creds = null;

  private OAuth2Request() {
  }

  public OAuth2Request(HttpServletRequest request) throws OAuthProblemException,
      OAuthSystemException {
    this.request = request;
    validate();
    creds = decodeClientAuthenticationHeader(request.getHeader(AUTHORIZATION));
  }

  protected void validate() throws OAuthProblemException, OAuthSystemException {
    try {
      validator = initValidator();
      validator.validateRequiredParameters(request);
    } catch (OAuthProblemException e) {
      try {
        final String redirectUri = request.getParameter(OAUTH_REDIRECT_URL);
        if (!isEmpty(redirectUri)) {
          e.setRedirectUri(redirectUri);
        }
      } catch (Exception ex) {
        if (logger.isDebugEnabled()) {
          logger.debug("Cannot read redirect_url from the request: {0}", ex.getMessage());
        }
      }

      throw e;
    }

  }

  protected abstract OAuthValidator<HttpServletRequest> initValidator()
      throws OAuthProblemException, OAuthSystemException;

  public String getParam(String name) {
    return request.getParameter(name);
  }

  public String getAppId() {
    if (isClientAuthHeaderUsed()) {
      return creds[0];
    }

    String appId = getParam(OAUTH_APP_ID);
    if (isEmpty(appId)) {
      appId = getParam(OAUTH_CLIENT_ID);
    }

    return appId;
  }

  public String getRedirectURL() {
    String redirectUrl = getParam(OAUTH_APP_ID);
    if (isEmpty(redirectUrl)) {
      redirectUrl = getParam(OAUTH_REDIRECT_URI);
    }

    return redirectUrl;
  }

  public String getAppSecret() {
    if (isClientAuthHeaderUsed()) {
      return creds[1];
    }

    String appSecret = getParam(OAUTH_APP_SECRET);
    if (isEmpty(appSecret)) {
      appSecret = getParam(OAUTH_CLIENT_SECRET);
    }

    return appSecret;
  }

  /**
   * @return
   */
  public boolean isClientAuthHeaderUsed() {
    return creds != null && creds.length >= 1;
  }

  public Set<String> getScopes() {
    return decodeScopes(getParam(OAUTH_SCOPE));
  }

}