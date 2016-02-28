package com.hd123.oauth2.service;

import javax.servlet.http.HttpServletRequest;

import com.hd123.oauth2.entity.User;
import com.hd123.oauth2.exception.AuthServiceException;
import com.hd123.oauth2.rest.AccessToken;
import com.hd123.oauth2.rest.AccessTokenCheckRequest;
import com.hd123.oauth2.rest.RsResponse;

/**
 * 授权服务接口
 *
 * @author liyue
 */
public interface OAuthService {

  /**
   * 客户端模式获取access_token
   * 
   * @param request
   *          access_token请求
   * @return AccessToken
   * @throws AuthServiceException
   *           授权业务异常
   */
  AccessToken fetchTokenWithClientMode(HttpServletRequest request) throws AuthServiceException;

  /**
   * 授权码方式获取access_token
   *
   * @param request
   *          access_token请求
   * @return AccessToken
   * @throws AuthServiceException
   *           授权业务异常
   */
  AccessToken fetchTokenWithAuthCodeMode(HttpServletRequest request) throws AuthServiceException;

  /**
   * 获取携带授权码的回调地址
   *
   * @param user
   *          用户
   * @param request
   *          授权码请求
   * @return 回调地址
   * @throws AuthServiceException
   *           授权业务异常
   */
  String authorize(User user, HttpServletRequest request) throws AuthServiceException;

  /**
   * 验证access token是否有效
   *
   * @param request
   *          验证请求
   * @return RsResponse
   */
  RsResponse checkAccessToken(AccessTokenCheckRequest request) ;

}