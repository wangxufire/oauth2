package com.hd123.oauth2.entity;

import static com.google.common.collect.Sets.newHashSet;
import static com.hd123.oauth2.entity.App.State.initial;
import static com.hd123.oauth2.util.DateUtil.now;
import static org.springframework.data.mongodb.core.index.IndexDirection.DESCENDING;

import java.util.Set;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 应用
 * 
 * @author liyue
 */
// http://docs.mongodb.org/manual/reference/text-search-languages
@Document(collection = "app", language = "simplified chinese")
@CompoundIndexes({
  @CompoundIndex(name = "app_token_idx", unique = true, def = "{'appId': -1, 'appSecret': -1}",
      background = true) })
public class App extends PVersionEntity {
  private static final long serialVersionUID = -442259840161885377L;

  @NotBlank
  @Indexed(name = "app_name_idx", unique = true, direction = DESCENDING, background = true)
  private String appName;
  @Indexed(name = "app_appId_idx", unique = true, direction = DESCENDING, background = true)
  private String appId;
  @Indexed(name = "app_appSecret_idx", unique = true, direction = DESCENDING, background = true)
  private String appSecret;
  @Indexed(name = "app_accessToken_idx", direction = DESCENDING, background = true)
  private String accessToken;
  @Indexed(name = "app_refreshToken_idx", direction = DESCENDING, background = true)
  private String refreshToken;
  @Indexed(name = "app_userId_idx", direction = DESCENDING, background = true)
  private String user;
  private Long expired;
  private String create = now();
  private String modify = now();
  private State state = initial;
  private String bindProduct;
  private Set<String> scopes = newHashSet();

  public enum State {
    // 初始，新增应用
    initial,
    // 审核中，申请新的接口调用权限
    auditing,
    // 已审核，审核应用或接口调用权限申请
    audited
  }

  public App() {
    super();
  }

  public App(String appName) {
    this.appName = appName;
  }

  public App(String appName, String appId, String appSecret) {
    super();
    this.appName = appName;
    this.appId = appId;
    this.appSecret = appSecret;
  }

  @PersistenceConstructor
  public App(String appName, String appId, String appSecret, String accessToken,
      String refreshToken, Long expired, String create, String modify, State state,
      String bindProduct, String user, Set<String> scopes) {
    super();
    this.appName = appName;
    this.appId = appId;
    this.appSecret = appSecret;
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.expired = expired;
    this.create = create;
    this.modify = modify;
    this.state = state;
    this.bindProduct = bindProduct;
    this.user = user;
    this.scopes = scopes;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getAppSecret() {
    return appSecret;
  }

  public void setAppSecret(String appSecret) {
    this.appSecret = appSecret;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public Long getExpired() {
    return expired;
  }

  public void setExpired(Long expired) {
    this.expired = expired;
  }

  public String getCreate() {
    return create;
  }

  public void setCreate(String create) {
    this.create = create;
  }

  public String getModify() {
    return modify;
  }

  public void setModify(String modify) {
    this.modify = modify;
  }

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

  public String getBindProduct() {
    return bindProduct;
  }

  public void setBindProduct(String bindProduct) {
    this.bindProduct = bindProduct;
  }

  public Set<String> getScopes() {
    return scopes;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public void setScopes(Set<String> scopes) {
    this.scopes = scopes;
  }

}