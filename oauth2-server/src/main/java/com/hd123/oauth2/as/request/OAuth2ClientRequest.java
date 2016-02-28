package com.hd123.oauth2.as.request;

import static org.apache.oltu.oauth2.common.message.types.GrantType.AUTHORIZATION_CODE;
import static org.apache.oltu.oauth2.common.message.types.GrantType.CLIENT_CREDENTIALS;
import static org.apache.oltu.oauth2.common.message.types.GrantType.PASSWORD;
import static org.apache.oltu.oauth2.common.message.types.GrantType.REFRESH_TOKEN;

import javax.servlet.http.HttpServletRequest;

import org.apache.oltu.oauth2.as.validator.AuthorizationCodeValidator;
import org.apache.oltu.oauth2.as.validator.ClientCredentialValidator;
import org.apache.oltu.oauth2.as.validator.PasswordValidator;
import org.apache.oltu.oauth2.as.validator.RefreshTokenValidator;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.validators.OAuthValidator;

/**
 * The Default OAuth Authorization Server class that validates whether a given
 * HttpServletRequest is a valid OAuth Token request.
 * <p/>
 * IMPORTANT: This OAuthTokenRequest assumes that a token request requires
 * client authentication. Please see section 3.2.1 of the OAuth Specification:
 * http://tools.ietf.org/html/rfc6749#section-3.2.1
 * 
 * @author liyue
 */
public class OAuth2ClientRequest extends AbstractOAuth2Request {

  /**
   * Create an OAuth Token request from a given HttpSerlvetRequest
   *
   * @param request
   *          the httpservletrequest that is validated and transformed into the
   *          OAuth Token Request
   * @throws OAuthSystemException
   *           if an unexpected exception was thrown
   * @throws OAuthProblemException
   *           if the request was not a valid Token request this exception is
   *           thrown.
   */
  public OAuth2ClientRequest(HttpServletRequest request) throws OAuthSystemException,
      OAuthProblemException {
    super(request);
  }

  @Override
  protected OAuthValidator<HttpServletRequest> initValidator() throws OAuthProblemException,
      OAuthSystemException {
    validators.put(PASSWORD.toString(), PasswordValidator.class);
    validators.put(CLIENT_CREDENTIALS.toString(), ClientCredentialValidator.class);
    validators.put(AUTHORIZATION_CODE.toString(), AuthorizationCodeValidator.class);
    validators.put(REFRESH_TOKEN.toString(), RefreshTokenValidator.class);
    return super.initValidator();
  }

}