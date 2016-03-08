package com.hd123.oauth2.util;

import static com.google.common.base.Objects.equal;
import static com.hd123.oauth2.common.AuthoritiesConstants.ROLE_ANONYMOUS;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Utility class for Spring Security.
 *
 * @author liyue
 */
public final class SecurityUtil {

  private SecurityUtil() {
  }

  /**
   * Get the login of the current user.
   *
   * @return userName
   */
  public static String getCurrentUserLogin() {
    final Authentication authentication = getContext().getAuthentication();
    return getCurrentUserLogin(authentication);
  }

  /**
   * Check if a user is authenticated.
   *
   * @return true if the user is authenticated, false otherwise
   */
  public static boolean isAuthenticated() {
    final Authentication authentication = getContext().getAuthentication();
    if (authentication == null) {
      return false;
    }
    final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    return authorities != null
        && !authorities.isEmpty()
        && authorities.parallelStream()
            .map(authority -> equal(authority.getAuthority(), ROLE_ANONYMOUS)).collect(toList())
            .size() == 0;
  }

  /**
   * Return the current user, or throws an exception, if the user is not
   * authenticated yet.
   *
   * @return the current user
   */
  public static User getCurrentUser() {
    final Authentication authentication = getContext().getAuthentication();
    return new User(getCurrentUserLogin(authentication), EMPTY, authentication.getAuthorities());
  }

  /**
   * If the current user has a specific authority (security role).
   *
   * <p>
   * The name of this method comes from the isUserInRole() method in the Servlet
   * API
   * </p>
   *
   * @param authority
   * @return isCurrentUserInRole
   */
  public static boolean isCurrentUserInRole(String authority) {
    final Authentication authentication = getContext().getAuthentication();
    if (authentication != null) {
      if (authentication.getPrincipal() instanceof UserDetails) {
        return ((UserDetails) authentication.getPrincipal()).getAuthorities().contains(
            new SimpleGrantedAuthority(authority));
      }
    }

    return false;
  }

  /**
   * 根据authentication获取用户标识
   *
   * @param authentication
   *          authentication
   * @return userName
   */
  private static String getCurrentUserLogin(Authentication authentication) {
    String userName = EMPTY;
    if (authentication != null) {
      if (authentication.getPrincipal() instanceof UserDetails) {
        userName = ((UserDetails) authentication.getPrincipal()).getUsername();
      } else if (authentication.getPrincipal() instanceof String) {
        userName = (String) authentication.getPrincipal();
      }
    }

    return userName;
  }

}