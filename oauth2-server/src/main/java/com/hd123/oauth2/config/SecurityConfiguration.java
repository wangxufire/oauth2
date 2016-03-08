package com.hd123.oauth2.config;

import static com.hd123.oauth2.common.HttpParams.XAUTH_TOKEN_HEADER_NAME;
import static com.hd123.oauth2.support.ExceptionCode.userNotEnabled;
import static com.hd123.oauth2.support.ExceptionCode.usernameNotExist;
import static com.hd123.oauth2.util.PasswordUtil.getPasswordEncoder;
import static com.hd123.oauth2.util.TokenUtil.parseHttpToken;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.logging.log4j.LogManager.getLogger;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.GenericFilterBean;

import com.hd123.oauth2.entity.User;
import com.hd123.oauth2.exception.SpecAuthServiceException;
import com.hd123.oauth2.repository.UserRepository;
import com.hd123.oauth2.support.Http401UnauthorizedEntryPoint;
import com.hd123.oauth2.util.ProfileUtil;

/**
 * Web 权限配置
 *
 * @author liyue
 * @since 0.1.0
 */
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final Logger logger = getLogger(SecurityConfiguration.class);

  @Autowired
  private ProfileUtil profileUtil;
  @Autowired
  private UserRepository userRepository;

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.debug(profileUtil.isUnSafe())
        .ignoring()
        .antMatchers("/**/*.{html,js,css,ico,properties,jpg,jpeg,png,gif}", "/bower_components/**",
            "/i18n/**", "/dist/**", "/", "/index.html", "/appinfo", "/api/user/login",
            "/api/user/logout", "/api/user/create", "/api/token", "/api/check",
            "/startCaptchaServlet", "/verifyServlet", "/swagger-ui/**", "/v2/**");
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http.sessionManagement().maximumSessions(32).sessionRegistry(sessionRegistry()).and().and()
        .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint()).and()
        .addFilterBefore(new XAuthTokenFilter(), UsernamePasswordAuthenticationFilter.class).csrf()
        .disable().headers().frameOptions().disable().and().sessionManagement()
        .sessionCreationPolicy(STATELESS).and().antMatcher("/api/**").authorizeRequests()
        .anyRequest().authenticated();
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
  }

  @Bean
  @Role(ROLE_INFRASTRUCTURE)
  public PasswordEncoder passwordEncoder() {
    return getPasswordEncoder();
  }

  @Bean
  @Override
  @Role(ROLE_INFRASTRUCTURE)
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  @Role(ROLE_INFRASTRUCTURE)
  public AuthenticationEntryPoint authenticationEntryPoint() {
    return new Http401UnauthorizedEntryPoint();
  }

  @Bean
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

  @Bean
  public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
    return new SecurityEvaluationContextExtension();
  }

  @Bean
  @Role(ROLE_INFRASTRUCTURE)
  public UserDetailsService userDetailsService() {
    return username -> {
      if (logger.isDebugEnabled()) {
        logger.debug("Authenticating User: {}", username);
      }
      final Optional<User> userFromDatabase = userRepository.findDistinctByUsername(username);
      return userFromDatabase.map(
          user -> {
            if (!user.getEnabled()) {
              throw new SpecAuthServiceException(userNotEnabled.messageOf(username));
            }
            final List<GrantedAuthority> grantedAuthorities = user.getRoles().parallelStream()
                .map(authority -> new SimpleGrantedAuthority(authority.name())).collect(toList());
            return new org.springframework.security.core.userdetails.User(username, user
                .getPassword(), grantedAuthorities);
          }).orElseThrow(() -> new SpecAuthServiceException(usernameNotExist.messageOf(username)));
    };
  }

  /**
   * Filters incoming requests and installs a Spring Security principal if a
   * header corresponding to a valid user is found.
   */
  final class XAuthTokenFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
        FilterChain filterChain) throws IOException, ServletException {
      final HttpServletRequest req = (HttpServletRequest) servletRequest;
      final String authToken = req.getHeader(XAUTH_TOKEN_HEADER_NAME);
      if (isNotBlank(authToken)) {
        final UserDetails details = userDetailsService().loadUserByUsername(
            parseHttpToken(authToken).getUsername());
        final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
            details, details.getPassword(), details.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);
      }
      filterChain.doFilter(servletRequest, servletResponse);
    }
  }

}