package com.hd123.oauth2.controller.service;

import static com.hd123.oauth2.common.AuthoritiesConstants.ADMIN;
import static com.hd123.oauth2.common.AuthoritiesConstants.ROLE_ADMIN;
import static com.hd123.oauth2.common.AuthoritiesConstants.USER;
import static com.hd123.oauth2.common.HttpMediaType.APPLICATION_JSON_VALUE_UTF_8;
import static com.hd123.oauth2.controller.service.AppController.SERVICE_PATH;
import static com.hd123.oauth2.support.ExceptionCode.ok;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import javax.validation.Valid;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hd123.oauth2.controller.BaseController;
import com.hd123.oauth2.entity.App;
import com.hd123.oauth2.exception.AuthServiceException;
import com.hd123.oauth2.logger.ControllerLogger;
import com.hd123.oauth2.rest.RsResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 应用业务转发
 *
 * @author liyue
 */
@RestController
@Api(tags = "应用信息接口")
@RequestMapping(path = SERVICE_PATH, consumes = ALL_VALUE, produces = APPLICATION_JSON_VALUE_UTF_8)
public class AppController extends BaseController {

  protected static final String SERVICE_PATH = PATH + "app";

  @Secured(ROLE_ADMIN)
  @ControllerLogger("获取应用列表")
  @ApiOperation(value = "获取应用列表")
  @RequestMapping(path = "/list", method = GET)
  public Page<App> list(@RequestParam("keyword") String keyword, @PageableDefault(page = 0,
      size = 10) Pageable page) {
    return appService.page(keyword, EMPTY, page);
  }

  @PreAuthorize(USER)
  @ControllerLogger("根据用户获取应用列表")
  @ApiOperation(value = "根据用户获取应用列表")
  @RequestMapping(path = "/listUserApps", method = GET)
  public Page<App> list(@RequestParam("user") String user, @RequestParam("keyword") String keyword,
      @PageableDefault(page = 0, size = 10) Pageable page) {
    return appService.page(keyword, user, page);
  }

  @PreAuthorize(USER)
  @ControllerLogger("新增应用")
  @ApiOperation(value = "新增应用")
  @RequestMapping(path = "/create", method = POST)
  public RsResponse create(@Valid @RequestBody App app) throws AuthServiceException {
    appService.create(app);
    return new RsResponse(ok);
  }

  @PreAuthorize(ADMIN)
  @ControllerLogger("审核应用")
  @ApiOperation(value = "审核应用")
  @RequestMapping(path = "/audit/{id}", method = POST)
  public RsResponse audit(@PathVariable("id") ObjectId id) throws AuthServiceException {
    appService.audit(id);
    return new RsResponse(ok);
  }

  @PreAuthorize(USER)
  @ControllerLogger("根据id查询应用")
  @ApiOperation(value = "根据id查询应用")
  @RequestMapping(path = "/{id}", method = GET)
  public App detail(@PathVariable("id") ObjectId id) throws AuthServiceException {
    return appService.findOne(id);
  }

  @PreAuthorize(USER)
  @ControllerLogger("根据appId查询应用")
  @ApiOperation(value = "根据appId查询应用")
  @RequestMapping(path = "/query/{appId}", method = GET)
  public App detail(@PathVariable("appId") String appId) throws AuthServiceException {
    return appService.findByAppId(appId);
  }

  @PreAuthorize(USER)
  @ControllerLogger("更新应用")
  @ApiOperation(value = "更新应用")
  @RequestMapping(path = "/update", method = POST)
  public RsResponse update(@Valid @RequestBody App app) throws AuthServiceException {
    appService.update(app);
    return new RsResponse(ok);
  }

  @PreAuthorize(USER)
  @ControllerLogger("删除应用")
  @ApiOperation(value = "删除应用")
  @RequestMapping(path = "/delete/{id}", method = DELETE)
  public RsResponse delete(@PathVariable("id") ObjectId id) throws AuthServiceException {
    appService.delete(id);
    return new RsResponse(ok);
  }

}