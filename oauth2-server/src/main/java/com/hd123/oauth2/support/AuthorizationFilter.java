package com.hd123.oauth2.support;

import static com.google.common.collect.Lists.newArrayList;
import static com.hd123.oauth2.common.Constants.CURRENT_USER;
import static com.hd123.oauth2.util.TokenUtil.BEARER;
import static com.hd123.oauth2.util.TokenUtil.SUBJECT;
import static com.hd123.oauth2.util.TokenUtil.parseHttpToken;
import static java.lang.String.format;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.logging.log4j.LogManager.getLogger;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.WWW_AUTHENTICATE;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

/**
 * 权限过滤器
 *
 * @author liyue
 */
@Deprecated
public class AuthorizationFilter implements Filter {
  private static final Logger logger = getLogger(AuthorizationFilter.class);

  private List<String> excludeUris = newArrayList();

  public AuthorizationFilter() {
    super();
  }

  public AuthorizationFilter(List<String> excludedUris) {
    this.excludeUris = excludedUris;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    final HttpServletRequest req = (HttpServletRequest) request;
    final HttpServletResponse res = (HttpServletResponse) response;

    final String uri = req.getRequestURI();
    if (excludeUris.contains(uri)) {
      chain.doFilter(request, response);
    } else {
      final String header = req.getHeader(AUTHORIZATION);
      if (isNotBlank(header) && header.startsWith(BEARER)) {
        final String token = header.substring(7);
        try {
          final String username = (String) parseHttpToken(token).get(SUBJECT);
          req.setAttribute(CURRENT_USER, username);
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException ex) {
          if (logger.isDebugEnabled()) {
            logger.debug(format("请求%s被拦截，原因:token已失效", uri));
          }
          res.setStatus(SC_UNAUTHORIZED);
          res.addHeader(WWW_AUTHENTICATE, ex.getMessage());
          return;
        }
        chain.doFilter(request, response);
      } else {
        if (logger.isDebugEnabled()) {
          logger.debug(format("请求%s被拦截，原因:缺失token", uri));
        }
        res.setStatus(SC_UNAUTHORIZED);
        res.addHeader(WWW_AUTHENTICATE, "Missing Bearer Token");
      }
    }
  }

  @Override
  public void destroy() {
  }

}