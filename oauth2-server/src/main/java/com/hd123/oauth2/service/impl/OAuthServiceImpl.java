package com.hd123.oauth2.service.impl;

import static com.alibaba.fastjson.JSON.parseObject;
import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Sets.newHashSet;
import static com.hd123.oauth2.entity.App.State.audited;
import static com.hd123.oauth2.entity.App.State.auditing;
import static com.hd123.oauth2.support.ExceptionCode.accessTokenOutOfDate;
import static com.hd123.oauth2.support.ExceptionCode.appNotApplayAnyAuth;
import static com.hd123.oauth2.support.ExceptionCode.appRegisterIncorrect;
import static com.hd123.oauth2.support.ExceptionCode.fetchAccessTokenFailed;
import static com.hd123.oauth2.support.ExceptionCode.fetchAuthorizationCodeFailed;
import static com.hd123.oauth2.support.ExceptionCode.interfaceAddressUnauthorized;
import static com.hd123.oauth2.support.ExceptionCode.invalidAccessToken;
import static com.hd123.oauth2.support.ExceptionCode.invalidAuthorizationCode;
import static com.hd123.oauth2.support.ExceptionCode.invalidGrantType;
import static com.hd123.oauth2.support.ExceptionCode.invalidRedirectUrl;
import static com.hd123.oauth2.support.ExceptionCode.invalidResponseType;
import static com.hd123.oauth2.support.ExceptionCode.ok;
import static com.hd123.oauth2.support.ExceptionCode.passwordIncorrect;
import static com.hd123.oauth2.support.ExceptionCode.unauthorizedAppId;
import static com.hd123.oauth2.support.ExceptionCode.unauthorizedAppSecret;
import static com.hd123.oauth2.support.ExceptionCode.usernameNotExist;
import static com.hd123.oauth2.util.DateUtil.nowTimestamp;
import static com.hd123.oauth2.util.TokenUtil.BEARER;
import static com.hd123.oauth2.util.TokenUtil.generateOauth2Token;
import static com.hd123.oauth2.util.TokenUtil.parseOauth2Token;
import static java.util.stream.Collectors.toList;
import static javax.servlet.http.HttpServletResponse.SC_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.oltu.oauth2.as.response.OAuthASResponse.authorizationResponse;
import static org.apache.oltu.oauth2.as.response.OAuthASResponse.tokenResponse;
import static org.apache.oltu.oauth2.common.OAuth.OAUTH_CODE;
import static org.apache.oltu.oauth2.common.OAuth.OAUTH_GRANT_TYPE;
import static org.apache.oltu.oauth2.common.OAuth.OAUTH_REDIRECT_URI;
import static org.apache.oltu.oauth2.common.OAuth.OAUTH_RESPONSE_TYPE;
import static org.apache.oltu.oauth2.common.OAuth.OAUTH_STATE;
import static org.apache.oltu.oauth2.common.message.types.GrantType.AUTHORIZATION_CODE;
import static org.apache.oltu.oauth2.common.message.types.GrantType.CLIENT_CREDENTIALS;
import static org.apache.oltu.oauth2.common.message.types.ResponseType.CODE;
import static org.apache.oltu.oauth2.common.message.types.ResponseType.TOKEN;
import static org.apache.oltu.oauth2.common.utils.OAuthUtils.isEmpty;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_APPLICATION;
import static org.springframework.util.Assert.hasLength;
import static org.springframework.util.Assert.notNull;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.issuer.UUIDValueGenerator;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse.OAuthAuthorizationResponseBuilder;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.context.annotation.Role;
import org.springframework.stereotype.Service;

import com.hd123.oauth2.as.request.OAuth2ClientRequest;
import com.hd123.oauth2.entity.App;
import com.hd123.oauth2.entity.User;
import com.hd123.oauth2.exception.AuthServiceException;
import com.hd123.oauth2.logger.ServiceLogger;
import com.hd123.oauth2.rest.AccessToken;
import com.hd123.oauth2.rest.AccessTokenCheckRequest;
import com.hd123.oauth2.rest.RsResponse;
import com.hd123.oauth2.service.AppService;
import com.hd123.oauth2.service.OAuthService;
import com.hd123.oauth2.service.UserService;
import com.hd123.oauth2.util.TokenUtil.Jwt;

@Role(ROLE_APPLICATION)
@Service(value = "oAuthService")
public class OAuthServiceImpl extends AbstractService implements OAuthService {

  @Autowired
  private AppService appService;
  @Autowired
  private UserService userService;

  @Override
  public AccessToken fetchTokenWithClientMode(HttpServletRequest request)
      throws AuthServiceException {
    try {
      final OAuth2ClientRequest oauthRequest = new OAuth2ClientRequest(request);

      final String appid = oauthRequest.getAppId();
      final String appSecret = oauthRequest.getAppSecret();
      // 校验app
      final App app = checkApp(appid, appSecret);
      // 检查授权类型，此处只检查CLIENT_CREDENTIALS类型
      final String grantType = oauthRequest.getParam(OAUTH_GRANT_TYPE);
      if (!equal(grantType, CLIENT_CREDENTIALS.toString())) {
        throw new AuthServiceException(invalidGrantType.messageOf(grantType));
      }

      final String responseType = oauthRequest.getParam(OAUTH_RESPONSE_TYPE);
      if (!equal(responseType, TOKEN.toString())) {
        throw new AuthServiceException(invalidResponseType.messageOf(responseType));
      }

      // 生成Access Token
      Collection<String> scopes = null;
      final Cache cache = cacheManager.getCache(SCOPE_CACHE);
      // 因申请新的权限导致审核中，则采用原有权限生成token
      if (equal(app.getState(), auditing)) {
        final ValueWrapper valueWrapper = cache.get(appid);
        if (valueWrapper == null) {
          scopes = newHashSet();
        } else {
          scopes = (Set<String>) valueWrapper.get();
        }
      } else {
        scopes = app.getScopes();
      }

      final int expireIn = appProperties.getOauth2().getAccessTokenExpireIn();
      final Jwt jetToken = generateOauth2Token(expireIn, appid, appSecret, scopes);
      final String accessToken = jetToken.getToken();
      app.setAccessToken(accessToken);
      app.setExpired(jetToken.getExp());
      appService.updateWithNoCheck(app);
      cache.put(appid, scopes);

      final OAuthResponse response = tokenResponse(SC_OK).setAccessToken(accessToken)
          .setTokenType(BEARER.trim()).setExpiresIn(String.valueOf(expireIn))
          .setParam(OAUTH_STATE, oauthRequest.getParam(OAUTH_STATE)).buildJSONMessage();
      return parseObject(response.getBody(), AccessToken.class);
    } catch (OAuthProblemException | OAuthSystemException ex) {
      if (logger.isDebugEnabled()) {
        logger.debug("fetchTokenWithClientMode", ex);
      }
      throw new AuthServiceException(fetchAccessTokenFailed);
    }
  }

  @Override
  public AccessToken fetchTokenWithAuthCodeMode(HttpServletRequest request)
      throws AuthServiceException {
    try {
      final OAuthTokenRequest oauthRequest = new OAuthTokenRequest(request);

      // 校验app
      checkApp(oauthRequest.getClientId(), oauthRequest.getClientSecret());
      final String authCode = oauthRequest.getParam(OAUTH_CODE);
      // 检查授权类型，此处只检查AUTHORIZATION_CODE类型
      if (equal(oauthRequest.getParam(OAUTH_GRANT_TYPE), AUTHORIZATION_CODE.toString())) {
        if (!checkAuthCode(authCode)) {
          throw new AuthServiceException(invalidAuthorizationCode.messageOf(authCode));
        }
      }

      // 生成Access Token
      final OAuthIssuer oauthIssuer = new OAuthIssuerImpl(new UUIDValueGenerator());
      final String accessToken = oauthIssuer.accessToken();
      final String refreshToken = oauthIssuer.refreshToken();
      addAccessToken(accessToken, getUsernameByAuthCode(authCode));

      final OAuthResponse response = tokenResponse(SC_OK).setAccessToken(accessToken)
          .setRefreshToken(refreshToken)
          .setExpiresIn(String.valueOf(appProperties.getOauth2().getAccessTokenExpireIn()))
          .setParam(OAUTH_STATE, oauthRequest.getParam(OAUTH_STATE)).buildJSONMessage();
      return parseObject(response.getBody(), AccessToken.class);
    } catch (OAuthProblemException | OAuthSystemException ex) {
      if (logger.isDebugEnabled()) {
        logger.debug("fetchTokenWithAuthCodeMode", ex);
      }
      throw new AuthServiceException(fetchAccessTokenFailed);
    }
  }

  @Override
  @ServiceLogger("校验access_token")
  public RsResponse checkAccessToken(AccessTokenCheckRequest request) {
    final String uri = request.getUri();
    final String accessToken = request.getUri();
    final Jwt jwt = parseOauth2Token(accessToken);
    final Optional<App> opApp = appRepository.findDistinctByAppId(jwt.getAppid());
    if (!opApp.isPresent()) {
      return new RsResponse(invalidAccessToken.messageOf(accessToken));
    }

    final App app = opApp.get();

    if (app.getExpired() < nowTimestamp()) {
      return new RsResponse(accessTokenOutOfDate);
    }

    if (!(equal(app.getState(), audited) || equal(app.getState(), auditing))) {
      return new RsResponse(appRegisterIncorrect.messageOf(app.getAppId()));
    }

    if (jwt.getScopes().parallelStream().filter(uri::startsWith).collect(toList()).isEmpty()) {
      return new RsResponse(interfaceAddressUnauthorized.messageOf(uri));
    }

    return new RsResponse(ok);
  }

  @Override
  @ServiceLogger("获取携带授权码的回调地址")
  public String authorize(User user, HttpServletRequest request) throws AuthServiceException {
    notNull(user, "user");
    try {
      final OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
      final String username = loginCheck(user);

      // 校验app
      checkApp(oauthRequest.getClientId(), oauthRequest.getClientSecret());

      // 生成授权码
      String authorizationCode = EMPTY;
      // responseType目前仅支持CODE，另外还有TOKEN
      final String responseType = oauthRequest.getParam(OAUTH_RESPONSE_TYPE);
      if (equal(responseType, CODE.toString())) {
        final OAuthIssuerImpl oauthIssuer = new OAuthIssuerImpl(new UUIDValueGenerator());
        authorizationCode = oauthIssuer.authorizationCode();
        addAuthCode(authorizationCode, username);
      } else {
        throw new AuthServiceException(invalidResponseType.messageOf(responseType));
      }

      final OAuthAuthorizationResponseBuilder builder = authorizationResponse(request, SC_FOUND);
      builder.setCode(authorizationCode);
      // 得到到客户端重定向地址
      final String redirectUrl = oauthRequest.getParam(OAUTH_REDIRECT_URI);
      final OAuthResponse response = builder.location(redirectUrl).buildQueryMessage();
      return response.getLocationUri();
    } catch (OAuthProblemException | OAuthSystemException ex) {
      if (ex instanceof OAuthProblemException) {
        final String redirectUrl = ((OAuthProblemException) ex).getRedirectUri();
        if (isEmpty(redirectUrl)) {
          throw new AuthServiceException(invalidRedirectUrl.messageOf(EMPTY));
        }
      }

      throw new AuthServiceException(fetchAuthorizationCodeFailed);
    }
  }

  /**
   * 校验应用
   *
   * @param appid
   *          appid
   * @param appSecret
   *          appSecret
   * @return App
   * @throws AuthServiceException
   *           授权业务异常
   */
  private App checkApp(String appid, String appSecret) throws AuthServiceException {
    hasLength(appid, "appid");
    hasLength(appSecret, "appSecret");

    final Optional<App> opApp = appRepository.findDistinctByAppId(appid);
    if (!opApp.isPresent()) {
      throw new AuthServiceException(unauthorizedAppId.messageOf(appid));
    }

    final App app = opApp.get();

    if (!equal(appSecret, app.getAppSecret())) {
      throw new AuthServiceException(unauthorizedAppSecret.messageOf(appSecret));
    }

    if (app.getScopes().isEmpty()) {
      throw new AuthServiceException(appNotApplayAnyAuth.messageOf(appid));
    }

    return app;
  }

  /**
   * 验证登录
   *
   * @param user
   *          登录表单
   * @return username 用户名
   * @throws AuthServiceException
   *           授权业务异常
   */
  private String loginCheck(User userForm) throws AuthServiceException {
    final String username = userForm.getUsername();
    final String password = userForm.getPassword();
    hasLength(username, "username");
    hasLength(password, "password");

    final User user = userService.findByUsername(username);
    if (user != null) {
      if (!userService.checkPassword(password, user.getPassword())) {
        throw new AuthServiceException(passwordIncorrect.messageOf(username));
      }
    } else {
      throw new AuthServiceException(usernameNotExist.messageOf(username));
    }

    return username;
  }

  /**
   * 添加 auth code
   *
   * @param authCode
   *          authCode
   * @param username
   *          username
   */
  private void addAuthCode(String authCode, String username) {
    // Not Used
  }

  /**
   * 添加 access token
   *
   * @param accessToken
   *          accessToken
   * @param username
   *          username
   */
  private void addAccessToken(String accessToken, String username) {
    // Not Used
  }

  /**
   * 根据authCode获取用户名
   *
   * @param authCode
   *          authCode
   * @return username
   */
  private String getUsernameByAuthCode(String authCode) {
    // Not Used
    return EMPTY;
  }

  /**
   * 根据accessToken获取用户名
   *
   * @param accessToken
   *          accessToken
   * @return username
   */
  private String getUsernameByAccessToken(String accessToken) {
    // Not Used
    return EMPTY;
  }

  /**
   * 验证auth code是否有效
   *
   * @param authCode
   *          authCode
   * @return isValid
   */
  private Boolean checkAuthCode(String authCode) {
    // Not Used
    return false;
  }

}