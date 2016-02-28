package com.hd123.oauth2.common;

/**
 * Constants for Spring Security authorities.
 * 
 * @author liyue
 * @since 0.1.0
 * @see com.hd123.oauth2.entity.User.Role
 */
public final class AuthoritiesConstants {

  /**
   * @see org.springframework.security.access.annotation.Secured
   * @see javax.annotation.security.RolesAllowed
   */

  public static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";

  public static final String ROLE_ADMIN = "ROLE_ADMIN";

  public static final String ROLE_USER = "ROLE_USER";

  /**
   * @see org.springframework.security.access.prepost.PreAuthorize
   */

  public static final String USER = "hasAnyRole('ADMIN','USER')";

  public static final String ADMIN = "hasRole('ADMIN')";

  // ~~ OAuth2 Scope
  // ===================================================

  public static final String USER_SCOPE = "user";

  public static final String ADMIN_SCOPE = "admin";

  public static final String HAS_USER_SCOPE = "#oauth2.hasScope('user')";

  public static final String HAS_ADMIN_SCOPE = "#oauth2.hasScope('admin')";

}