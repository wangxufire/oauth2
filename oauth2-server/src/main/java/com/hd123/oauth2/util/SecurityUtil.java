package com.hd123.oauth2.util;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import org.springframework.security.core.Authentication;
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

  /**
   * Check if a user is authenticated.
   *
   * @return true if the user is authenticated, false otherwise
   */
  // public static boolean isAuthenticated() {
  // final Collection<? extends GrantedAuthority> authorities =
  // getContext().getAuthentication()
  // .getAuthorities();
  // return authorities == null
  // || authorities.parallelStream()
  // .filter(authority -> equal(ROLE_ANONYMOUS.name(),
  // authority.getAuthority()))
  // .collect(toList()).isEmpty();
  // }

  /**
   * Return the current user, or throws an exception, if the user is not
   * authenticated yet.
   *
   * @return the current user
   */
  public static User getCurrentUser() {
    final Authentication authentication = getContext().getAuthentication();
    String userName = EMPTY;
    if (authentication != null) {
      if (authentication.getPrincipal() instanceof UserDetails) {
        userName = ((UserDetails) authentication.getPrincipal()).getUsername();
      } else if (authentication.getPrincipal() instanceof String) {
        userName = (String) authentication.getPrincipal();
      }
    }

    return new User(userName, EMPTY, authentication.getAuthorities());
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

}