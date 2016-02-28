package com.hd123.oauth2.service.impl;

import static com.google.common.base.CharMatcher.JAVA_LETTER_OR_DIGIT;
import static com.google.common.base.Objects.equal;
import static com.hd123.oauth2.common.tuple.MutablePair.of;
import static com.hd123.oauth2.entity.App.State.audited;
import static com.hd123.oauth2.entity.App.State.auditing;
import static com.hd123.oauth2.support.ExceptionCode.appAlreadyAudited;
import static com.hd123.oauth2.support.ExceptionCode.appNameExist;
import static com.hd123.oauth2.support.ExceptionCode.appNotExist;
import static com.hd123.oauth2.util.DateUtil.now;
import static com.hd123.oauth2.util.StringUtil.generateUuid;
import static org.apache.commons.lang3.RandomStringUtils.randomAscii;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_APPLICATION;
import static org.springframework.util.Assert.hasLength;
import static org.springframework.util.Assert.notNull;

import java.util.Date;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.context.annotation.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hd123.oauth2.entity.App;
import com.hd123.oauth2.exception.AuthServiceException;
import com.hd123.oauth2.exception.OptionalException;
import com.hd123.oauth2.logger.ServiceLogger;
import com.hd123.oauth2.service.AppService;

/**
 * 应用服务接口实现
 *
 * @author liyue
 */
@Role(ROLE_APPLICATION)
@Service(value = "appService")
public class AppServiceImpl extends BaseService implements AppService {

  @Override
  @ServiceLogger("注册应用")
  public App create(App app) throws AuthServiceException {
    notNull(app, "app");
    final String appName = app.getAppName();
    hasLength(appName, "app.getAppName");
    if (findByName(appName) != null) {
      throw new AuthServiceException(appNameExist.messageOf(appName));
    }
    final String appId = APPID_PREFIX + generateUuid();
    final String appSecret = passwordEncoder.encode(new StringBuilder(new ObjectId(new Date())
        .toHexString()).append(randomAscii(24)).append(generateUuid()).toString());
    app.setAppId(appId);
    app.setAppSecret(JAVA_LETTER_OR_DIGIT.retainFrom(appSecret));
    return appRepository.save(app);
  }

  @Override
  @ServiceLogger("审核应用")
  public void audit(ObjectId id) throws AuthServiceException {
    notNull(id, "id");
    final App app = findOne(id);
    if (equal(app.getState(), audited)) {
      throw new AuthServiceException(appAlreadyAudited.messageOf(app.getAppName()));
    }
    app.setState(audited);
    update(app);
  }

  @Override
  @ServiceLogger("应用分页查询")
  public Page<App> page(String keyword, String user, Pageable pageRequest) {
    if (isBlank(user)) {
      return specificationQuery.query(keyword, App.class, pageRequest);
    } else {
      return specificationQuery.query(keyword, of(SEARCH_FIELD_USERID, user), App.class,
          pageRequest);
    }
  }

  @Override
  @ServiceLogger("更新应用")
  public void update(App app) throws AuthServiceException {
    notNull(app, "app");
    final App oldApp = findOne(app.getId());
    checkUserOperation(app.getUser());
    // 更新权限需审核
    if (equal(oldApp.getState(), audited) && !equal(oldApp.getScopes(), app.getScopes())) {
      app.setState(auditing);
    }
    app.setModify(now());
    specificationModify.update(app.getId(), app, App.class);
  }

  @Override
  public void updateWithNoCheck(App app) throws AuthServiceException {
    notNull(app, "app");
    app.setModify(now());
    specificationModify.update(app.getId(), app, App.class);
  }

  @Override
  @ServiceLogger("根据appid删除应用")
  public void delete(String appId) throws AuthServiceException {
    hasLength(appId, "appId");

    final App app = findByAppId(appId);
    checkUserOperation(app.getUser());
    appRepository.delete(app);
  }

  @Override
  @ServiceLogger("根据id删除应用")
  public void delete(ObjectId id) throws AuthServiceException {
    notNull(id, "id");
    final App app = findOne(id);
    checkUserOperation(app.getUser());
    appRepository.delete(id);
  }

  @Override
  @ServiceLogger("根据id查询应用")
  public App findOne(ObjectId id) throws AuthServiceException {
    notNull(id, "id");
    final App app = appRepository.findOne(id);
    if (app == null) {
      throw new AuthServiceException(appNotExist.messageOf(id.toString()));
    }
    return app;
  }

  @Override
  @ServiceLogger("根据appid查询应用")
  public App findByAppId(String appId) throws AuthServiceException {
    hasLength(appId, "appId");

    final Optional<App> opApp = appRepository.findDistinctByAppId(appId);
    return opApp.orElseThrow(OptionalException::new);
  }

  @Override
  @ServiceLogger("根据appSecret查询应用")
  public App findByAppSecret(String appSecret) throws AuthServiceException {
    hasLength(appSecret, "appSecret");

    final Optional<App> opApp = appRepository.findDistinctByAppSecret(appSecret);
    return opApp.orElseThrow(OptionalException::new);
  }

  @Override
  @ServiceLogger("根据应用名查询应用")
  public App findByName(String appName) throws AuthServiceException {
    hasLength(appName, "appName");

    final Optional<App> opApp = appRepository.findDistinctByAppName(appName);
    if (!opApp.isPresent()) {
      return null;
    }
    return opApp.get();
  }

}