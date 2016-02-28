package com.hd123.oauth2.config;

import static com.google.common.base.Objects.equal;
import static com.hd123.oauth2.entity.User.Role.ROLE_ADMIN;
import static com.hd123.oauth2.util.DateUtil.now;

import javax.inject.Inject;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.hd123.oauth2.entity.User;
import com.hd123.oauth2.repository.specification.SpecificationModify;

/**
 * 管理员用户信息
 *
 * @author liyue
 * @since 0.0.1
 */
@Component
public class AdminUserInitializer implements InitializingBean {

  @Autowired
  private SpecificationModify specificationModify;
  @Autowired
  private MongoOperations operations;
  @Autowired
  private AppProperties appProperties;
  @Inject
  private PasswordEncoder passwordEncoder;


  @Override
  public void afterPropertiesSet() throws Exception {
    modifyAdminUser();
  }

  /**
   * 修改管理员用户
   */
  private void modifyAdminUser() {
    final String username = appProperties.getAdmin().getName();
    final String passwd = appProperties.getAdmin().getPasswd();
    final String email = appProperties.getAdmin().getEmail();
    final Query query = new Query(new Criteria("username").is(username));
    User adminUser = operations.findOne(query, User.class);
    if (adminUser == null) {
      adminUser = new User(username);
      adminUser.setEmail(email);
      adminUser.getRoles().add(ROLE_ADMIN);
      adminUser.setPassword(passwordEncoder.encode(passwd));
      operations.insert(adminUser);
    } else {
      boolean modify = false;
      if (!passwordEncoder.matches(passwd, adminUser.getPassword())) {
        modify = true;
        adminUser.setPassword(passwordEncoder.encode(passwd));
      }
      if (!equal(email, adminUser.getEmail())) {
        modify = true;
        adminUser.setEmail(email);
      }
      if (modify) {
        adminUser.setModify(now());
        specificationModify.update(adminUser.getId(), adminUser, User.class);
      }
    }
  }

}