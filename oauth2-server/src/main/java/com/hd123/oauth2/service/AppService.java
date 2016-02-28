package com.hd123.oauth2.service;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hd123.oauth2.entity.App;
import com.hd123.oauth2.exception.AuthServiceException;

/**
 * 应用服务接口
 * 
 * @author liyue
 */
public interface AppService {

  String APPID_PREFIX = "hd";
  String SEARCH_FIELD_USERID = "user";

  /**
   * 新增
   * 
   * @param app
   *          app
   * @return app
   * @throws AuthServiceException
   *           授权业务异常
   */
  App create(App app) throws AuthServiceException;

  /**
   * 审核
   *
   * @param id
   *          id
   * @throws AuthServiceException
   *           授权业务异常
   */
  void audit(ObjectId id) throws AuthServiceException;

  /**
   * 分页关键字查询
   *
   * @param keyword
   *          关键字
   * @param user
   *          用户
   * @param pageRequest
   *          分页请求
   * @return Page<App>
   */
  Page<App> page(String keyword, String user, Pageable pageRequest);

  /**
   * 更新
   * 
   * @param app
   *          app
   * @throws AuthServiceException
   *           授权业务异常
   */
  void update(App app) throws AuthServiceException;

  /**
   * 更新,不校验用户
   *
   * @param app
   *          app
   * @throws AuthServiceException
   *           授权业务异常
   */
  void updateWithNoCheck(App app) throws AuthServiceException;

  /**
   * 删除
   * 
   * @param appId
   *          appId
   * @throws AuthServiceException
   *           授权业务异常
   */
  void delete(String appId) throws AuthServiceException;

  /**
   * 删除
   *
   * @param id
   *          id
   * @throws AuthServiceException
   *           授权业务异常
   */
  void delete(ObjectId id) throws AuthServiceException;

  /**
   * 根据id查找
   *
   * @param id
   *          id
   * @return app
   * @throws AuthServiceException
   *           授权业务异常
   */
  App findOne(ObjectId id) throws AuthServiceException;

  /**
   * 根据app_id查找
   * 
   * @param appId
   *          appId
   * @return app
   * @throws AuthServiceException
   *           授权业务异常
   */
  App findByAppId(String appId) throws AuthServiceException;

  /**
   * 根据app_secret查找
   * 
   * @param appSecret
   *          appSecret
   * @return app
   * @throws AuthServiceException
   *           授权业务异常
   */
  App findByAppSecret(String appSecret) throws AuthServiceException;

  /**
   * 根据appName查找
   *
   * @param appName
   *          appName
   * @return app
   * @throws AuthServiceException
   *           授权业务异常
   */
  App findByName(String appName) throws AuthServiceException;

}