package com.hd123.oauth2.config;

import static com.hd123.oauth2.util.PasswordUtil.getPasswordEncoder;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import java.security.KeyPair;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.hd123.oauth2.support.AjaxLogoutSuccessHandler;
import com.hd123.oauth2.support.Http401UnauthorizedEntryPoint;
import com.hd123.oauth2.util.ProfileUtil;

/**
 * Web 权限配置
 * 
 * @author liyue
 * @since 0.1.0
 */
@Configuration
public class SecurityConfiguration {

  @Configuration
  protected static class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private AppProperties appProperties;
    @Autowired
    private ProfileUtil profileUtil;

    @Override
    public void configure(WebSecurity web) throws Exception {
      web.debug(profileUtil.isUnSafe())
          .ignoring()
          .antMatchers("/**/*.{html,js,css,ico,properties,jpg,jpeg,png,gif}",
              "/bower_components/**", "/i18n/**", "/dist/**", "/", "/info", "/api/user/login",
              "/api/user/create", "/api/token", "/api/check", "/startCaptchaServlet",
              "/verifyServlet", "/doc/**", "/v2/**");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
      http.sessionManagement().sessionCreationPolicy(STATELESS).and().antMatcher("/api/*")
          .authorizeRequests().anyRequest().authenticated();
    }

    @Bean
    @Override
    @Role(ROLE_INFRASTRUCTURE)
    public AuthenticationManager authenticationManagerBean() throws Exception {
      return super.authenticationManagerBean();
    }

    @Bean
    @Role(ROLE_INFRASTRUCTURE)
    public PasswordEncoder passwordEncoder() {
      return getPasswordEncoder();
    }

    @Bean
    @Role(ROLE_INFRASTRUCTURE)
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
      return new SecurityEvaluationContextExtension();
    }

    @Bean
    @Role(ROLE_INFRASTRUCTURE)
    public JwtTokenStore tokenStore() {
      return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    @Role(ROLE_INFRASTRUCTURE)
    public JwtAccessTokenConverter accessTokenConverter() {
      final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
      final KeyPair keyPair = new KeyStoreKeyFactory(new ClassPathResource(appProperties.getJwt()
          .getKeyPath()), appProperties.getJwt().getKeyPassword().toCharArray())
          .getKeyPair(appProperties.getJwt().getAlias());
      converter.setKeyPair(keyPair);
      return converter;
    }

  }

  @Configuration
  protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
      http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint()).and().logout()
          .logoutUrl("/api/user/logout").logoutSuccessHandler(logoutSuccessHandler()).and()
          .authorizeRequests().anyRequest().authenticated();
    }

    @Bean
    @Role(ROLE_INFRASTRUCTURE)
    public AuthenticationEntryPoint authenticationEntryPoint() {
      return new Http401UnauthorizedEntryPoint();
    }

    @Bean
    @Role(ROLE_INFRASTRUCTURE)
    public LogoutSuccessHandler logoutSuccessHandler() {
      return new AjaxLogoutSuccessHandler();
    }

  }

  // @Configuration
  // @EnableAuthorizationServer
  // protected static class AuthorizationServerConfiguration extends
  // AuthorizationServerConfigurerAdapter {
  //
  // @Inject
  // private OAuth2AccessTokenRepository oAuth2AccessTokenRepository;
  //
  // @Inject
  // private OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository;
  //
  // @Inject
  // private JHipsterProperties jHipsterProperties;
  //
  // @Bean
  // public TokenStore tokenStore() {
  // return new MongoDBTokenStore(oAuth2AccessTokenRepository,
  // oAuth2RefreshTokenRepository);
  // }
  //
  // @Inject
  // @Qualifier("authenticationManagerBean")
  // private AuthenticationManager authenticationManager;
  //
  // @Override
  // public void configure(AuthorizationServerEndpointsConfigurer endpoints)
  // throws Exception {
  //
  // endpoints.tokenStore(tokenStore()).authenticationManager(authenticationManager);
  // }
  //
  // @Override
  // public void configure(AuthorizationServerSecurityConfigurer oauthServer)
  // throws Exception {
  // oauthServer.allowFormAuthenticationForClients();
  // }
  //
  // @Override
  // public void configure(ClientDetailsServiceConfigurer clients) throws
  // Exception {
  // clients
  // .inMemory()
  // .withClient(jHipsterProperties.getSecurity().getAuthentication().getOauth().getClientid())
  // .scopes("read", "write")
  // .authorities(AuthoritiesConstants.ROLE_ADMIN,
  // AuthoritiesConstants.ROLE_USER)
  // .authorizedGrantTypes("password", "refresh_token", "authorization_code",
  // "implicit")
  // .secret(jHipsterProperties.getSecurity().getAuthentication().getOauth().getSecret())
  // .accessTokenValiditySeconds(
  // jHipsterProperties.getSecurity().getAuthentication().getOauth()
  // .getTokenValidityInSeconds());
  // }
  // }

}