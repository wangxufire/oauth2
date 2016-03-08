package com.hd123.oauth2.support;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.http.HttpHeaders.WWW_AUTHENTICATE;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * Returns a 401 error code (Unauthorized) to the client.
 *
 * @author liyue
 */
public class Http401UnauthorizedEntryPoint implements AuthenticationEntryPoint {

  /**
   * Always returns a 401 error code to the client.
   */
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException arg2) throws IOException, ServletException {
    final StringBuilder errorBuilder = new StringBuilder("Token-Based realm=Heading OAuth2 Server")
        .append(", error=unauthorized, error_description=").append(arg2.getMessage());
    response.setStatus(SC_UNAUTHORIZED);
    response.addHeader(WWW_AUTHENTICATE, errorBuilder.toString());
  }

}