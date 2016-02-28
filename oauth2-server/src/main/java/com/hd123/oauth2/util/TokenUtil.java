package com.hd123.oauth2.util;

import static com.alibaba.fastjson.JSON.parseObject;
import static com.alibaba.fastjson.JSON.toJSONString;
import static com.google.common.collect.Lists.newArrayList;
import static com.hd123.oauth2.common.Constants.ALGORITHM;
import static com.hd123.oauth2.common.Constants.ALGORITHM_PROVIDER;
import static com.hd123.oauth2.common.Constants.DOLLAR;
import static com.hd123.oauth2.common.Constants.HEADING;
import static com.hd123.oauth2.common.Constants.STOP;
import static com.hd123.oauth2.util.StringUtil.COMMA_JOINER;
import static com.hd123.oauth2.util.StringUtil.COMMA_SPLITTER;
import static com.hd123.oauth2.util.StringUtil.generateUuid;
import static io.jsonwebtoken.Jwts.builder;
import static io.jsonwebtoken.Jwts.claims;
import static io.jsonwebtoken.Jwts.parser;
import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.impl.crypto.MacProvider.generateKey;
import static java.security.SecureRandom.getInstance;
import static org.apache.commons.lang3.RandomStringUtils.randomAscii;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.time.DateUtils.addHours;
import static org.apache.commons.lang3.time.DateUtils.addSeconds;
import static org.springframework.util.Assert.hasLength;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import com.hd123.oauth2.entity.User;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.impl.compression.GzipCompressionCodec;

/**
 * token工具类
 *
 * @author liyue
 */
// http://www.toptal.com/web/cookie-free-authentication-with-json-web-tokens-an-example-in-laravel-and-angularjs
public final class TokenUtil {

  public static final String SUBJECT = "sub";
  public static final String EXPIRED = "exp";
  public static final String TOKEN = "token";
  public static final String BEARER = "Bearer ";
  public static final String USER = "user";

  private TokenUtil() {
  }

  private static SecureRandom RANDOM;
  static {
    try {
      RANDOM = getInstance(ALGORITHM, ALGORITHM_PROVIDER);
      RANDOM.setSeed(RANDOM.generateSeed(128));
    } catch (NoSuchAlgorithmException | NoSuchProviderException ignored) {
    }
  }

  public static final Key KEY = generateKey(HS256, RANDOM);

  /**
   * 生成OAuth2授权token
   *
   * @param param
   *          param
   * @param scopes
   *          scopes
   * @return token
   */
  public static String generateOAuth2Token(String param, Collection<String> scopes) {
    final JwtBuilder jwtBuilder = builder()
        .setClaims(claims().setSubject(COMMA_JOINER.join(scopes)))
        .setHeaderParam(randomAscii(10), generateUuid()).compressWith(new GzipCompressionCodec());
    if (isNotBlank(param)) {
      jwtBuilder.setHeaderParam(generateUuid(), param);
    }
    return jwtBuilder.compact().replace(STOP, DOLLAR);
  }

  /**
   * 生成前端请求token
   *
   * @param user
   *          user
   * @return token
   */
  public static String generateHttpToken(User user) {
    final Date now = new Date();
    return builder().setClaims(claims().setSubject(user.getUsername())).signWith(HS256, KEY)
        .setIssuer(HEADING).setIssuedAt(now).setExpiration(addHours(now, 1))
        .compressWith(new GzipCompressionCodec()).compact();
  }

  /**
   * 解析OAuth2Token
   * 
   * @param token
   *          token
   * @return scopes (保存在body sub)
   */
  public static List<String> parseOAuth2Token(String token) {
    hasLength(token, "token");
    return newArrayList(COMMA_SPLITTER.split((String) parseObject(
        toJSONString(parser().parse(token.replace(DOLLAR, STOP)).getBody())).get(SUBJECT)));
  }

  /**
   * 解析HttpToken
   *
   * @param token
   *          token
   * @return body
   */
  public static JSONObject parseHttpToken(String token) {
    hasLength(token, "token");
    return parseObject(toJSONString(parser().setSigningKey(KEY).parse(token).getBody()));
  }

  // ~ test
  // ==========================================================

  public static void main(String[] args) {
    System.out.println(addSeconds(new Date(), 7200));
  }

}