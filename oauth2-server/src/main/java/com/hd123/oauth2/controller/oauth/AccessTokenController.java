package com.hd123.oauth2.controller.oauth;

import static com.hd123.oauth2.as.Common.OAuth2.CACHE_CONTROL;
import static com.hd123.oauth2.as.Common.OAuth2.NO_STORE;
import static com.hd123.oauth2.as.Common.OAuth2.PRAGMA;
import static com.hd123.oauth2.common.HttpMediaType.APPLICATION_JSON_VALUE_UTF_8;
import static com.hd123.oauth2.common.HttpMediaType.APPLICATION_URLENCODED_VALUE_UTF_8;
import static com.hd123.oauth2.controller.oauth.AccessTokenController.SERVICE_PATH;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hd123.oauth2.controller.BaseController;
import com.hd123.oauth2.exception.AuthServiceException;
import com.hd123.oauth2.logger.ControllerLogger;
import com.hd123.oauth2.rest.AccessToken;
import com.hd123.oauth2.rest.AccessTokenCheckRequest;
import com.hd123.oauth2.rest.RsResponse;
import com.hd123.oauth2.service.OAuthService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * access_token转发
 *
 * @author liyue
 */
@RestController
@Api(tags = "access_token相关接口")
@RequestMapping(path = SERVICE_PATH, consumes = ALL_VALUE, produces = APPLICATION_JSON_VALUE_UTF_8)
public class AccessTokenController extends BaseController {

  protected static final String SERVICE_PATH = PATH;

  @Autowired
  private OAuthService oAuthService;

  /**
   * 客户端模式获取access_token
   *
   * @param request
   *          request
   * @return access_token JSON
   * @throws AuthServiceException
   *           授权业务异常
   */
  @PermitAll
  @ControllerLogger("通过appid及appSecret获取access_token")
  @ApiOperation(value = "通过appid及appSecret获取access_token")
  @RequestMapping(path = "/token", method = {
      GET, POST }, consumes = ALL_VALUE, produces = APPLICATION_JSON_VALUE_UTF_8)
  public AccessToken fetchTokenByClientMode(HttpServletRequest request) throws AuthServiceException {
    return oAuthService.fetchTokenWithClientMode(request);
  }

  /**
   * 授权码方式生成access_token
   *
   * @param request
   *          request
   * @return access_token JSON
   * @throws AuthServiceException
   *           授权业务异常
   */
  @PermitAll
  @ControllerLogger("通过授权码获取access_token")
  @ApiOperation(value = "通过授权码获取access_token")
  @RequestMapping(path = "/accessToken", consumes = APPLICATION_URLENCODED_VALUE_UTF_8,
      produces = APPLICATION_JSON_VALUE_UTF_8, method = POST)
  public AccessToken fetchTokenByAuthCodeMode(HttpServletRequest request)
      throws AuthServiceException {
    return oAuthService.fetchTokenWithClientMode(request);
  }


  /**
   * 验证accessToken
   *
   * @param request
   *          验证请求
   * @return RsResponse
   * @throws AuthServiceException
   *           授权业务异常
   */
  @PermitAll
  @ControllerLogger("验证access_token有效性")
  @ApiOperation(value = "验证access_token有效性")
  @RequestMapping(path = "/check", method = POST)
  public RsResponse check(@Valid @RequestBody AccessTokenCheckRequest request)
      throws AuthServiceException {
    return oAuthService.checkAccessToken(request);
  }


  /**
   * 禁止缓存
   *
   * @param response
   *          response
   */
  @ModelAttribute
  private void noCache(HttpServletResponse response) {
    response.addHeader(PRAGMA, NO_STORE);
    response.addHeader(CACHE_CONTROL, NO_STORE);
  }

}