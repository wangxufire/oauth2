package com.hd123.oauth2.util;

import static com.alibaba.fastjson.JSON.parseObject;
import static com.alibaba.fastjson.JSON.toJSONString;
import static com.hd123.oauth2.common.Constants.ALGORITHM;
import static com.hd123.oauth2.common.Constants.ALGORITHM_PROVIDER;
import static com.hd123.oauth2.common.Constants.DOLLAR;
import static com.hd123.oauth2.common.Constants.HEADING;
import static com.hd123.oauth2.common.Constants.STOP;
import static com.hd123.oauth2.support.ExceptionCode.invalidAccessToken;
import static com.hd123.oauth2.support.ExceptionCode.unauthorizedCurrentUser;
import static com.hd123.oauth2.util.DateUtil.DATE_TIME_FORMAT;
import static com.hd123.oauth2.util.DateUtil.date2Timestamp;
import static com.hd123.oauth2.util.DateUtil.timeStamp2Date;
import static com.hd123.oauth2.util.StringUtil.COMMA_JOINER;
import static com.hd123.oauth2.util.StringUtil.COMMA_SPLITTER;
import static com.hd123.oauth2.util.StringUtil.generateUuid;
import static io.jsonwebtoken.Jwts.builder;
import static io.jsonwebtoken.Jwts.claims;
import static io.jsonwebtoken.Jwts.parser;
import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.impl.crypto.MacProvider.generateKey;
import static java.security.SecureRandom.getInstance;
import static org.apache.commons.lang3.time.DateUtils.addSeconds;
import static org.springframework.util.Assert.hasLength;
import static org.springframework.util.Assert.notNull;

import java.io.Serializable;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Date;

import com.alibaba.fastjson.JSON;

import com.hd123.oauth2.exception.SpecAuthServiceException;

import io.jsonwebtoken.JwtException;
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
   * @param expiresIn
   *          有效期(秒)
   * @param appid
   *          应用id
   * @param appSecret
   *          应用密钥
   * @param scopes
   *          scopes
   * @return Jwt
   */
  public static Jwt generateOauth2Token(int expiresIn, String appid, String appSecret,
      Collection<String> scopes) {
    hasLength(appid, "appid");
    hasLength(appSecret, "appSecret");
    notNull(scopes, "scopes");

    final Date now = new Date();
    final Date expied = addSeconds(now, expiresIn);
    final String token = builder()
        .setClaims(claims().setSubject(COMMA_JOINER.join(scopes)).setId(appid))
        .setHeaderParam(generateUuid(), appSecret).setIssuer(HEADING).setIssuedAt(now)
        .setExpiration(expied).compressWith(new GzipCompressionCodec()).compact()
        .replace(STOP, DOLLAR);
    final Jwt jwt = new Jwt();
    jwt.setToken(token);
    jwt.setExp(date2Timestamp(expied));
    return jwt;
  }

  /**
   * 解析OAuth2Token
   *
   * @param token
   *          token
   * @return Jwt
   */
  public static Jwt parseOauth2Token(String token) throws SpecAuthServiceException {
    hasLength(token, "token");
    try {
      final String body = toJSONString(parser().parse(token.replace(DOLLAR, STOP)).getBody());
      return parseObject(body, Jwt.class);
    } catch (JwtException ex) {
      throw new SpecAuthServiceException(invalidAccessToken.messageOf(token));
    }
  }

  /**
   * 生成前端请求token
   *
   * @param expiresIn
   *          有效期
   * @param username
   *          用户名
   * @return token
   */
  public static String generateHttpToken(int expiresIn, String username) {
    hasLength(username, "username");
    final Date now = new Date();
    return builder().setClaims(claims().setId(username)).signWith(HS256, KEY).setIssuer(HEADING)
        .setIssuedAt(now).setExpiration(addSeconds(now, expiresIn))
        .compressWith(new GzipCompressionCodec()).compact();
  }

  /**
   * 解析HttpToken
   *
   * @param token
   *          token
   * @return Jwt
   */
  public static Jwt parseHttpToken(String token) throws SpecAuthServiceException {
    hasLength(token, "token");
    try {
      final String body = toJSONString(parser().setSigningKey(KEY).parse(token).getBody());
      return parseObject(body, Jwt.class);
    } catch (JwtException ex) {
      throw new SpecAuthServiceException(unauthorizedCurrentUser);
    }
  }

  /**
   * token内容
   *
   * @author liyue
   */
  public static final class Jwt implements Serializable {
    private static final long serialVersionUID = 9171907542880271422L;

    private String jti; // 存放身份标识
    private String sub; // 存放scopes
    private long exp; // 有效期
    private String iss; // 颁发人
    private long iat; // 颁发时间

    private transient String token;
    private transient String appid;
    private transient String username;
    private transient Collection<String> scopes;

    public String getJti() {
      return jti;
    }

    public void setJti(String jti) {
      this.jti = jti;
    }

    public String getSub() {
      return sub;
    }

    public void setSub(String sub) {
      this.sub = sub;
    }

    public long getExp() {
      return exp;
    }

    public void setExp(long exp) {
      this.exp = exp;
    }

    public String getToken() {
      return token;
    }

    public void setToken(String token) {
      this.token = token;
    }

    public String getIss() {
      return iss;
    }

    public void setIss(String iss) {
      this.iss = iss;
    }

    public long getIat() {
      return iat;
    }

    public void setIat(long iat) {
      this.iat = iat;
    }

    public String getAppid() {
      return jti;
    }

    public String getUsername() {
      return jti;
    }

    public Collection<String> getScopes() {
      return COMMA_SPLITTER.splitToList(sub);
    }

  }

  // ~ test
  // ==========================================================

  public static void main(String[] args) throws InterruptedException {
    // String t = generateOauth2Token(12, "123", "456", newArrayList("7", "8",
    // "9")).getToken();
    String t = generateHttpToken(12, "123");
    System.out.println(t);
    Thread.sleep(1000L);
    String b = toJSONString(parser().setSigningKey(KEY).parse(t).getBody());
    Jwt j = JSON.parseObject(b, Jwt.class);
    // Map<String, String> j = JSON.parseObject(b, Map.class);
    System.out.println(j.toString());
    System.out.println(timeStamp2Date(j.getIat(), DATE_TIME_FORMAT));
    System.out.println(timeStamp2Date(j.getExp(), DATE_TIME_FORMAT));
  }

}