package com.hd123.oauth2.config;

import static com.hd123.oauth2.common.Constants.CAPTCHAID;
import static com.hd123.oauth2.common.Constants.GEETEST_PRIVATEKEY;
import static com.hd123.oauth2.config.AppProperties.APP_PROPERTIES;
import static java.lang.System.setProperty;

import java.beans.Transient;

import javax.xml.bind.annotation.XmlTransient;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 应用配置
 *
 * @author liyue
 * @0.1.0
 */
@Component
@ConfigurationProperties(prefix = APP_PROPERTIES, ignoreUnknownFields = false)
public class AppProperties implements InitializingBean {
  protected static final String APP_PROPERTIES = "app";

  private final Admin admin = new Admin();
  private final Oauth2 oauth2 = new Oauth2();
  private final Tls tls = new Tls();
  private final Jwt jwt = new Jwt();
  private final Geetest geetest = new Geetest();
  private final Swagger swagger = new Swagger();
  private final Async async = new Async();
  private final Info info = new Info();

  public Admin getAdmin() {
    return admin;
  }

  public Oauth2 getOauth2() {
    return oauth2;
  }

  public Tls getTls() {
    return tls;
  }

  public Jwt getJwt() {
    return jwt;
  }

  public Geetest getGeetest() {
    return geetest;
  }

  public Swagger getSwagger() {
    return swagger;
  }

  public Async getAsync() {
    return async;
  }

  public Info getInfo() {
    return info;
  }

  public static class Admin {

    private String name;
    private String passwd;
    private String email;

    public String getPasswd() {
      return passwd;
    }

    public void setPasswd(String passwd) {
      this.passwd = passwd;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }
  }

  public static class Oauth2 {

    private int authCodeExpireIn;
    private int accessTokenExpireIn;

    public int getAuthCodeExpireIn() {
      return authCodeExpireIn;
    }

    public void setAuthCodeExpireIn(int authCodeExpireIn) {
      this.authCodeExpireIn = authCodeExpireIn;
    }

    public int getAccessTokenExpireIn() {
      return accessTokenExpireIn;
    }

    public void setAccessTokenExpireIn(int accessTokenExpireIn) {
      this.accessTokenExpireIn = accessTokenExpireIn;
    }

  }

  public static class Tls {

    private int port;
    private String keyStorePassword;
    private String keyStorePath;

    public int getPort() {
      return port;
    }

    public void setPort(int port) {
      this.port = port;
    }

    public String getKeyStorePassword() {
      return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
      this.keyStorePassword = keyStorePassword;
    }

    public String getKeyStorePath() {
      return keyStorePath;
    }

    public void setKeyStorePath(String keyStorePath) {
      this.keyStorePath = keyStorePath;
    }

  }

  public static class Jwt {

    private int expireIn;

    public int getExpireIn() {
      return expireIn;
    }

    public void setExpireIn(int expireIn) {
      this.expireIn = expireIn;
    }

  }

  public static class Swagger {

    private String apiPattern = "/api/.*";
    private String title = "Heading OAuth2 API";
    private String description = "Heading OAuth2 API Documentation";
    private String version = "0.0.1";
    private String termsOfServiceUrl;
    private String contact;
    private String license;
    private String licenseUrl;

    public String getApiPattern() {
      return apiPattern;
    }

    public void setApiPattern(String apiPattern) {
      this.apiPattern = apiPattern;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public String getVersion() {
      return version;
    }

    public void setVersion(String version) {
      this.version = version;
    }

    public String getTermsOfServiceUrl() {
      return termsOfServiceUrl;
    }

    public void setTermsOfServiceUrl(String termsOfServiceUrl) {
      this.termsOfServiceUrl = termsOfServiceUrl;
    }

    public String getContact() {
      return contact;
    }

    public void setContact(String contact) {
      this.contact = contact;
    }

    public String getLicense() {
      return license;
    }

    public void setLicense(String license) {
      this.license = license;
    }

    public String getLicenseUrl() {
      return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
      this.licenseUrl = licenseUrl;
    }

  }

  public static class Async {

    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
    private String namePrefix;

    public int getCorePoolSize() {
      return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
      this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
      return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
      this.maxPoolSize = maxPoolSize;
    }

    public int getQueueCapacity() {
      return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
      this.queueCapacity = queueCapacity;
    }

    public String getNamePrefix() {
      return namePrefix;
    }

    public void setNamePrefix(String namePrefix) {
      this.namePrefix = namePrefix;
    }

  }

  public static class Info {

    private String name = "Merchant Services";
    private String version;
    private String stage = "test";
    private String developer = "liyue";
    private String mail = "liyue@hd123.com";
    private String organization = "heading";

    /**
     * 应用名称
     *
     * @return 应用名称
     */
    public String getName() {
      return name;
    }

    /**
     * 应用版本号
     *
     * @return 版本号
     */
    public String getVersion() {
      return version;
    }

    public void setName(String name) {
      this.name = name;
    }

    public void setVersion(String version) {
      this.version = version;
    }

    public String getStage() {
      return stage;
    }

    public void setStage(String stage) {
      this.stage = stage;
    }

    public String getDeveloper() {
      return developer;
    }

    public void setDeveloper(String developer) {
      this.developer = developer;
    }

    public String getMail() {
      return mail;
    }

    public void setMail(String mail) {
      this.mail = mail;
    }

    public String getOrganization() {
      return organization;
    }

    public void setOrganization(String organization) {
      this.organization = organization;
    }

    /**
     * 获取全部应用信息
     *
     * @return 全部应用信息
     */
    @Transient
    @JsonIgnore
    @XmlTransient
    public String getSumInfo() {
      return name + ":" + version;
    }

  }

  public static class Geetest {

    private String id;
    private String key;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getKey() {
      return key;
    }

    public void setKey(String key) {
      this.key = key;
    }

  }

  @Override
  public void afterPropertiesSet() throws Exception {
    setProperty(CAPTCHAID, geetest.getId());
    setProperty(GEETEST_PRIVATEKEY, geetest.getKey());
  }

}