package com.hd123.oauth2.entity;

import static com.google.common.collect.Sets.newHashSet;
import static com.hd123.oauth2.common.Constants.LETTER_OR_DIGIT_PATTERN;
import static com.hd123.oauth2.entity.User.Role.ROLE_USER;
import static com.hd123.oauth2.util.DateUtil.now;
import static java.lang.Boolean.TRUE;
import static org.springframework.data.mongodb.core.index.IndexDirection.DESCENDING;

import java.util.Set;

import javax.naming.OperationNotSupportedException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.hd123.oauth2.common.AuthoritiesConstants;

/**
 * 用户
 *
 * @author liyue
 */
@Document(collection = "user", language = "simplified chinese")
public class User extends PVersionEntity {
  private static final long serialVersionUID = -5034703386109066152L;

  @NotNull(message = "用户名不能为空")
  @Pattern(regexp = LETTER_OR_DIGIT_PATTERN)
  @Size(min = 2, max = 20, message = "用户名不合法")
  @Indexed(name = "user_name_idx", unique = true, direction = DESCENDING, background = true)
  private String username;
  @NotNull(message = "用户密码不能为空")
  @Size(min = 5, max = 50, message = "用户密码不合法")
  private String password;
  @Email
  private String email;
  /* 注册时间 */
  private String register = now();
  /* 最后修改时间 */
  private String modify = now();
  /* 最后登陆时间 */
  private String lastLogin = now();
  /* 是否启用状态 */
  private Boolean enabled = TRUE;
  /* 拥有的角色 */
  private Set<Role> roles = newHashSet(ROLE_USER);

  /**
   * 用户角色
   * 
   * @author liyue
   */
  public enum Role {
    /* 匿名用户 */
    ROLE_ANONYMOUS(AuthoritiesConstants.ROLE_ANONYMOUS),
    /* 用户 */
    ROLE_USER(AuthoritiesConstants.ROLE_USER),
    /* 管理员 */
    ROLE_ADMIN(AuthoritiesConstants.ROLE_ADMIN),

    ;

    private String role;

    private Role() throws OperationNotSupportedException {
      throw new OperationNotSupportedException();
    }

    private Role(String role) {
      this.role = role;
    }

  }

  public User() {
    super();
  }

  public User(String username) {
    this.username = username;
  }

  public User(String username, String email) {
    this.username = username;
    this.email = email;
  }

  public User(String username, String password, String email) {
    this.username = username;
    this.password = password;
    this.email = email;
  }

  @PersistenceConstructor
  public User(String username, String password, String email, String lastLogin, String register,
      String modify, Boolean enabled, Set<Role> roles) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.lastLogin = lastLogin;
    this.register = register;
    this.modify = modify;
    this.enabled = enabled;
    this.roles = roles;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getLastLogin() {
    return lastLogin;
  }

  public void setLastLogin(String lastLogin) {
    this.lastLogin = lastLogin;
  }

  public String getRegister() {
    return register;
  }

  public void setRegister(String register) {
    this.register = register;
  }

  public String getModify() {
    return modify;
  }

  public void setModify(String modify) {
    this.modify = modify;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

}