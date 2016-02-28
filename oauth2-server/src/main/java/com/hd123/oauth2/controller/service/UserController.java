package com.hd123.oauth2.controller.service;

import static com.hd123.oauth2.common.AuthoritiesConstants.ADMIN;
import static com.hd123.oauth2.common.AuthoritiesConstants.USER;
import static com.hd123.oauth2.common.HttpMediaType.APPLICATION_JSON_VALUE_UTF_8;
import static com.hd123.oauth2.controller.service.UserController.SERVICE_PATH;
import static com.hd123.oauth2.support.ExceptionCode.ok;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hd123.oauth2.controller.BaseController;
import com.hd123.oauth2.entity.User;
import com.hd123.oauth2.exception.AuthServiceException;
import com.hd123.oauth2.logger.ControllerLogger;
import com.hd123.oauth2.rest.RsResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 用户业务转发
 * 
 * @author liyue
 */
@RestController
@Api(tags = "用户信息接口")
@RequestMapping(path = SERVICE_PATH, consumes = ALL_VALUE, produces = APPLICATION_JSON_VALUE_UTF_8)
public class UserController extends BaseController {

  protected static final String SERVICE_PATH = PATH + "user";

  @PermitAll
  @ControllerLogger("用户登录")
  @ApiOperation(value = "用户登录")
  @RequestMapping(path = "/login", method = POST)
  public org.springframework.security.core.userdetails.User login(@Valid @RequestBody User user, HttpServletResponse response)
      throws AuthServiceException {
    return userService.login(user, response);
  }

  @PreAuthorize(USER)
  @ControllerLogger("用户登出")
  @ApiOperation(value = "用户登出")
  @RequestMapping(path = "/logout", method = GET)
  public RsResponse logout() throws AuthServiceException {
    userService.logout();
    return new RsResponse(ok);
  }

  @PreAuthorize(ADMIN)
  @ControllerLogger("获取用户列表")
  @ApiOperation(value = "获取用户列表")
  @RequestMapping(path = "/list", method = GET)
  public Page<User> list(@RequestParam("keyword") String keyword, @PageableDefault(page = 0,
      size = 10) Pageable page) {
    return userService.page(keyword, page);
  }

  @PermitAll
  @ControllerLogger("新增用户")
  @ApiOperation(value = "新增用户")
  @RequestMapping(path = "/create", method = POST)
  public RsResponse create(@Valid @RequestBody User user) throws AuthServiceException {
    userService.create(user);
    return new RsResponse(ok);
  }

  @PreAuthorize(USER)
  @ControllerLogger("根据id查询用户")
  @ApiOperation(value = "根据id查询用户")
  @RequestMapping(path = "/{id}", method = GET)
  public User detail(@PathVariable("id") ObjectId id) throws AuthServiceException {
    return userService.findOne(id);
  }

  @PreAuthorize(USER)
  @ControllerLogger("更新用户信息")
  @ApiOperation(value = "更新用户信息")
  @RequestMapping(path = "/update", method = POST)
  public RsResponse update(@Valid @RequestBody User user) throws AuthServiceException {
    userService.update(user);
    return new RsResponse(ok);
  }

  @PreAuthorize(USER)
  @ControllerLogger("删除用户")
  @ApiOperation(value = "删除用户")
  @RequestMapping(path = "/delete/{id}", method = DELETE)
  public RsResponse delete(@PathVariable("id") ObjectId id) throws AuthServiceException {
    userService.delete(id);
    return new RsResponse(ok);
  }

  @PreAuthorize(USER)
  @ControllerLogger("修改用户密码")
  @ApiOperation(value = "修改用户密码")
  @RequestMapping(path = "/{id}/modifyPassword", method = POST)
  public String modifyPassword(@PathVariable("id") ObjectId id, String newPassword,
      RedirectAttributes redirectAttributes) throws AuthServiceException {
    userService.modifyPassword(id, newPassword);
    redirectAttributes.addFlashAttribute("msg", "修改密码成功");
    return "redirect:/user";
  }

}