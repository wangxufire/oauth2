package com.hd123.oauth2.as.request;

import static org.apache.oltu.oauth2.common.OAuth.OAUTH_CODE;
import static org.apache.oltu.oauth2.common.OAuth.OAUTH_GRANT_TYPE;
import static org.apache.oltu.oauth2.common.OAuth.OAUTH_PASSWORD;
import static org.apache.oltu.oauth2.common.OAuth.OAUTH_REFRESH_TOKEN;
import static org.apache.oltu.oauth2.common.OAuth.OAUTH_USERNAME;
import static org.apache.oltu.oauth2.common.utils.OAuthUtils.handleOAuthProblemException;
import static org.apache.oltu.oauth2.common.utils.OAuthUtils.instantiateClass;
import static org.apache.oltu.oauth2.common.utils.OAuthUtils.isEmpty;

import javax.servlet.http.HttpServletRequest;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.validators.OAuthValidator;

/**
 * Abstract OAuth Token request class
 * 
 * @author liyue
 */
abstract class AbstractOAuth2Request extends OAuth2Request {

  protected AbstractOAuth2Request(HttpServletRequest request) throws OAuthSystemException,
      OAuthProblemException {
    super(request);
  }

  protected OAuthValidator<HttpServletRequest> initValidator() throws OAuthProblemException,
      OAuthSystemException {
    final String requestTypeValue = getParam(OAUTH_GRANT_TYPE);
    if (isEmpty(requestTypeValue)) {
      throw handleOAuthProblemException("Missing grant_type parameter value");
    }
    final Class<? extends OAuthValidator<HttpServletRequest>> clazz = validators
        .get(requestTypeValue);
    if (clazz == null) {
      throw handleOAuthProblemException("Invalid grant_type parameter value");
    }
    return instantiateClass(clazz);
  }

  public String getPassword() {
    return getParam(OAUTH_PASSWORD);
  }

  public String getUsername() {
    return getParam(OAUTH_USERNAME);
  }

  public String getRefreshToken() {
    return getParam(OAUTH_REFRESH_TOKEN);
  }

  public String getCode() {
    return getParam(OAUTH_CODE);
  }

  public String getGrantType() {
    return getParam(OAUTH_GRANT_TYPE);
  }

}