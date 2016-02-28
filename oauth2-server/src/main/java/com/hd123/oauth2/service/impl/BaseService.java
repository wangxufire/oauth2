package com.hd123.oauth2.service.impl;

import static com.google.common.base.Objects.equal;
import static com.hd123.oauth2.entity.User.Role.ROLE_ADMIN;
import static com.hd123.oauth2.support.ExceptionCode.currentUserCanNotOperate;
import static com.hd123.oauth2.util.SecurityUtil.getCurrentUser;
import static com.hd123.oauth2.util.SecurityUtil.getCurrentUserLogin;
import static org.apache.logging.log4j.LogManager.getLogger;

import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.hd123.oauth2.config.AppProperties;
import com.hd123.oauth2.exception.AuthServiceException;
import com.hd123.oauth2.repository.AppRepository;
import com.hd123.oauth2.repository.ProductRepository;
import com.hd123.oauth2.repository.UserRepository;
import com.hd123.oauth2.repository.specification.SpecificationModify;
import com.hd123.oauth2.repository.specification.SpecificationQuery;
import com.hd123.oauth2.util.JwtTokenUtil;

/**
 * 服务基类
 *
 * @author liyue
 */
@Component(value = "oauth2BaseService")
public class BaseService {
  protected final Logger logger = getLogger(getClass());

  @Autowired
  protected AppRepository appRepository;
  @Autowired
  protected UserRepository userRepository;
  @Autowired
  protected ProductRepository productRepository;

  @Autowired
  protected SpecificationQuery specificationQuery;
  @Autowired
  protected SpecificationModify specificationModify;

  @Autowired
  protected JwtTokenUtil tokenUtil;
  @Inject
  protected PasswordEncoder passwordEncoder;
  @Autowired
  protected AppProperties appProperties;

  /**
   * 获取当前登录用户
   *
   * @return
   */
  protected String getCurrentLogin() {
    return getCurrentUserLogin();
  }

  /**
   * 校验当前用户是否具有操作权
   *
   * @param username
   *          用户名
   */
  protected void checkUserOperation(final String username) throws AuthServiceException {
    final User userDetail = getCurrentUser();
    if (!userDetail.getAuthorities().contains(new SimpleGrantedAuthority(ROLE_ADMIN.name()))) {
      final String login = userDetail.getUsername();
      if (!equal(username, login)) {
        throw new AuthServiceException(currentUserCanNotOperate.messageOf(login));
      }
    }
  }

}