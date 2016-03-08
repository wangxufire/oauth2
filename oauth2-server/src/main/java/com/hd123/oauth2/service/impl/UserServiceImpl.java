package com.hd123.oauth2.service.impl;

import static com.hd123.oauth2.entity.User.Role.ROLE_ADMIN;
import static com.hd123.oauth2.support.ExceptionCode.canNotDeleteAdminUser;
import static com.hd123.oauth2.support.ExceptionCode.emailExist;
import static com.hd123.oauth2.support.ExceptionCode.passwordIncorrect;
import static com.hd123.oauth2.support.ExceptionCode.userIdNotExist;
import static com.hd123.oauth2.support.ExceptionCode.usernameExist;
import static com.hd123.oauth2.util.DateUtil.now;
import static com.hd123.oauth2.util.TokenUtil.TOKEN;
import static com.hd123.oauth2.util.TokenUtil.generateHttpToken;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_APPLICATION;
import static org.springframework.util.Assert.hasLength;
import static org.springframework.util.Assert.notNull;
import static reactor.bus.Event.wrap;

import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import reactor.spring.context.annotation.Consumer;
import reactor.spring.context.annotation.Selector;

import com.hd123.oauth2.entity.User;
import com.hd123.oauth2.exception.AuthServiceException;
import com.hd123.oauth2.logger.ServiceLogger;
import com.hd123.oauth2.service.UserService;

/**
 * 用户服务接口实现
 *
 * @author liyue
 */
@Consumer
@Role(ROLE_APPLICATION)
@Service(value = "userService")
public class UserServiceImpl extends AbstractService implements UserService {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Override
  @ServiceLogger("用户登录")
  public org.springframework.security.core.userdetails.User login(User user,
      HttpServletResponse response) throws AuthServiceException {
    notNull(user, "user");
    final String username = user.getUsername();
    hasLength(username, "user.getUsername");
    final String password = user.getPassword();
    hasLength(password, "user.getPassword");

    try {
      final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
          username, password);
      final Authentication authentication = authenticationManager.authenticate(token);

      response.addHeader(TOKEN, generateHttpToken(appProperties.getJwt().getExpireIn(), username));

      eventBus.notify(UPDATE_LOGIN_EVENT, wrap(username));

      return new org.springframework.security.core.userdetails.User(username, EMPTY,
          authentication.getAuthorities());
    } catch (AuthenticationException ex) {
      throw new AuthServiceException(passwordIncorrect.messageOf(username));
    }
  }

  @Selector(value = UPDATE_LOGIN_EVENT, eventBus = REACTOR_EVENT_BUS)
  public void updateLogin(String username) throws AuthServiceException {
    hasLength(username, "username");
    final User user = findByUsername(username);
    if (user != null) {
      user.setLastLogin(now());
      updateWithNoCheck(user);
    }
  }

  @Override
  @ServiceLogger("用户登出")
  public void logout() throws AuthServiceException {
  }

  @Override
  @ServiceLogger("注册用户")
  public User create(User user) throws AuthServiceException {
    notNull(user, "user");
    final String userName = user.getUsername();
    hasLength(userName, "user.getUsername");
    if (findByUsername(userName) != null) {
      throw new AuthServiceException(usernameExist.messageOf(userName));
    }
    final String email = user.getEmail();
    if (isNotBlank(email)) {
      if (findByEmail(email) != null) {
        throw new AuthServiceException(emailExist.messageOf(email));
      }
    }
    final String pwd = user.getPassword();
    hasLength(pwd, "user.getPassword");
    // 加密密码
    user.setPassword(passwordEncoder.encode(pwd));
    return userRepository.save(user);
  }

  @Override
  @ServiceLogger("分页查询用户")
  public Page<User> page(String keyword, Pageable pageRequest) {
    return specificationQuery.query(keyword, User.class, pageRequest);
  }

  @Override
  @ServiceLogger("更新用户信息")
  public void update(User user) throws AuthServiceException {
    notNull(user, "user");
    checkUserOperation(user.getUsername());
    user.setModify(now());
    specificationModify.update(user.getId(), user, User.class);
  }

  @Override
  @ServiceLogger("不校验更新用户信息")
  public void updateWithNoCheck(User user) throws AuthServiceException {
    notNull(user, "user");
    user.setModify(now());
    specificationModify.update(user.getId(), user, User.class);
  }

  @Override
  @ServiceLogger("删除用户")
  public void delete(ObjectId id) throws AuthServiceException {
    notNull(id, "id");
    final User user = findOne(id);
    checkUserOperation(user.getUsername());
    if (user.getRoles().contains(ROLE_ADMIN)) {
      throw new AuthServiceException(canNotDeleteAdminUser);
    }
    userRepository.delete(id);
  }

  @Override
  @ServiceLogger("根据id查询用户")
  public User findOne(ObjectId id) throws AuthServiceException {
    notNull(id, "id");
    final User user = userRepository.findOne(id);
    if (user == null) {
      throw new AuthServiceException(userIdNotExist.messageOf(id.toString()));
    }
    return user;
  }

  @Override
  @ServiceLogger("根据用户名用户")
  public User findByUsername(String username) throws AuthServiceException {
    hasLength(username, "username");
    final Optional<User> opUser = userRepository.findDistinctByUsername(username);
    return opUser.orElse(null);
  }

  @Override
  @ServiceLogger("根据邮箱查询用户")
  public User findByEmail(String email) throws AuthServiceException {
    hasLength(email, "email");
    final Optional<User> opUser = userRepository.findDistinctByEmail(email);
    if (!opUser.isPresent()) {
      return null;
    }
    return opUser.get();
  }

  @Override
  @ServiceLogger("修改用户密码")
  public void modifyPassword(ObjectId id, String newPassword) throws AuthServiceException {
    notNull(id, "id");
    hasLength(newPassword, "newPassword");
    final User user = findOne(id);
    checkUserOperation(user.getUsername());
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }

  @Override
  @ServiceLogger("校验用户密码")
  public Boolean checkPassword(String rawPwd, String encryptPwd) throws AuthServiceException {
    hasLength(encryptPwd, "encryptPwd");

    return passwordEncoder.matches(rawPwd, encryptPwd);
  }

}