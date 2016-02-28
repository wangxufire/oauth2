package com.hd123.oauth2.util;

import static com.google.common.collect.Sets.newHashSet;
import static com.hd123.oauth2.util.StringUtil.generateUuid;
import static org.apache.commons.lang3.time.DateUtils.addSeconds;
import static org.springframework.security.oauth2.common.OAuth2AccessToken.BEARER_TYPE;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.stereotype.Component;

/**
 * Jwt 工具类
 * 
 * @author liyue
 */
@Component
public class JwtTokenUtil {

  @Autowired
  private JwtTokenStore tokenStore;
  @Autowired
  private JwtAccessTokenConverter jwtConverter;

  /**
   * 生成OAuth2授权token
   *
   * @param appid
   *          应用id
   * @param appSecret
   *          应用密钥
   * @param expiresIn
   *          有效期(秒)
   * @param scopes
   *          scopes
   * @return token
   */
  public OAuth2AccessToken generateToken(final String appid, final String appSecret,
      final int expiresIn, final Collection<String> scopes) {
    final DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(generateUuid());
    token.setExpiration(addSeconds(new Date(), expiresIn + 1));
    token.setScope(newHashSet(scopes));
    token.setTokenType(BEARER_TYPE);
    final Authentication userAuthentication = new UsernamePasswordAuthenticationToken(appid,
        appSecret);
    final OAuth2Request oAuth2Request = new OAuth2Request(null, appid, null, true,
        token.getScope(), null, null, null, null);
    final OAuth2Authentication authentication = new OAuth2Authentication(oAuth2Request,
        userAuthentication);
    return jwtConverter.enhance(token, authentication);
  }

  /**
   * 解析token
   *
   * @param tokenValue
   *          tokenValue
   * @return token
   */
  public OAuth2AccessToken parseToken(String tokenValue) {
    return tokenStore.readAccessToken(tokenValue);
  }

}