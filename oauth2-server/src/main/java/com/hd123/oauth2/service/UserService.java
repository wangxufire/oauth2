package com.hd123.oauth2.service;

import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hd123.oauth2.entity.User;
import com.hd123.oauth2.exception.AuthServiceException;

/**
 * 用户服务接口
 * 
 * @author liyue
 */
public interface UserService {

  /**
   * 登录
   *
   * @param user
   *          user basic info
   * @param response
   *          response
   * @return userdetails
   * @throws AuthServiceException
   *           授权业务异常
   */
  org.springframework.security.core.userdetails.User login(User user, HttpServletResponse response)
      throws AuthServiceException;

  /**
   * 用户登出
   *
   * @throws AuthServiceException
   *           授权业务异常
   */
  void logout() throws AuthServiceException;

  /**
   * 创建用户
   * 
   * @param user
   *          user
   * @return User
   * @throws AuthServiceException
   *           授权业务异常
   */
  User create(User user) throws AuthServiceException;

  /**
   * 分页关键字查询
   *
   * @param keyword
   *          关键字
   * @param pageRequest
   *          分页请求
   * @return Page<User>
   */
  Page<User> page(String keyword, Pageable pageRequest);

  /**
   * 更新
   * 
   * @param user
   *          user
   * @throws AuthServiceException
   *           授权业务异常
   */
  void update(User user) throws AuthServiceException;

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
   * 根据id查询
   *
   * @param id
   *          id
   * @return User
   * @throws AuthServiceException
   *           授权业务异常
   */
  User findOne(ObjectId id) throws AuthServiceException;

  /**
   * 根据用户名查找用户
   *
   * @param username
   *          username
   * @return User
   * @throws AuthServiceException
   *           授权业务异常
   */
  User findByUsername(String username) throws AuthServiceException;

  /**
   * 根据邮箱查找用户
   *
   * @param email
   *          email
   * @return User
   * @throws AuthServiceException
   *           授权业务异常
   */
  User findByEmail(String email) throws AuthServiceException;

  /**
   * 修改密码
   * 
   * @param id
   *          id
   * @param newPassword
   *          newPassword
   * @throws AuthServiceException
   *           授权业务异常
   */
  void modifyPassword(ObjectId id, String newPassword) throws AuthServiceException;

  /**
   * 验证用户密码
   *
   * @param rawPwd
   *          密码
   * @param encryPtpwd
   *          加密后的密码
   * @return isValid
   * @throws AuthServiceException
   *           授权业务异常
   */
  Boolean checkPassword(String rawPwd, String encryPtpwd) throws AuthServiceException;

}