package com.hd123.oauth2.controller.oauth;

import static com.hd123.oauth2.common.HttpMediaType.APPLICATION_JSON_VALUE_UTF_8;
import static com.hd123.oauth2.controller.oauth.AuthorizeController.SERVICE_PATH;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hd123.oauth2.controller.BaseController;
import com.hd123.oauth2.entity.User;
import com.hd123.oauth2.exception.AuthServiceException;
import com.hd123.oauth2.logger.ControllerLogger;
import com.hd123.oauth2.service.OAuthService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 授权转发
 *
 * @author liyue
 */
@RestController
@Api(tags = "授权码相关接口")
@RequestMapping(path = SERVICE_PATH, consumes = ALL_VALUE, produces = APPLICATION_JSON_VALUE_UTF_8)
public class AuthorizeController extends BaseController {

  protected static final String SERVICE_PATH = PATH;

  @Autowired
  private OAuthService oAuthService;

  /**
   * 获取授权码
   *
   * @param request
   *          request
   * @return 授权码回调响应
   * @throws AuthServiceException
   *           授权业务异常
   */
  @PermitAll
  @RequestMapping(path = "/authorize", method = POST)
  @ControllerLogger("获取授权码并返回携带授权码的回调地址")
  @ApiOperation(value = "获取授权码并返回携带授权码的回调地址")
  public String authorize(@RequestBody @Valid User user, HttpServletRequest request)
      throws AuthServiceException {
    return oAuthService.authorize(user, request);
  }

}