package com.hd123.oauth2.controller.service;

import static com.hd123.oauth2.common.AuthoritiesConstants.ADMIN;
import static com.hd123.oauth2.common.AuthoritiesConstants.USER;
import static com.hd123.oauth2.common.HttpMediaType.APPLICATION_JSON_VALUE_UTF_8;
import static com.hd123.oauth2.controller.service.ProductController.SERVICE_PATH;
import static com.hd123.oauth2.support.ExceptionCode.ok;
import static com.hd123.oauth2.support.ExceptionCode.productNameNotExist;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

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

import com.hd123.oauth2.controller.BaseController;
import com.hd123.oauth2.entity.Product;
import com.hd123.oauth2.exception.AuthServiceException;
import com.hd123.oauth2.logger.ControllerLogger;
import com.hd123.oauth2.rest.RsResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 产品业务转发
 *
 * @author liyue
 */
@RestController
@Api(tags = "产品信息接口")
@RequestMapping(path = SERVICE_PATH, consumes = ALL_VALUE, produces = APPLICATION_JSON_VALUE_UTF_8)
public class ProductController extends BaseController {

  protected static final String SERVICE_PATH = PATH + "product";

  @PreAuthorize(ADMIN)
  @ControllerLogger("获取产品信息列表")
  @ApiOperation(value = "获取产品信息列表")
  @RequestMapping(path = "/list", method = GET)
  public Page<Product> list(@RequestParam("keyword") String keyword, @PageableDefault(page = 0,
      size = 10) Pageable page) {
    return productService.page(keyword, page);
  }

  @PreAuthorize(USER)
  @ControllerLogger("获取所有产品信息")
  @ApiOperation(value = "获取所有产品信息")
  @RequestMapping(path = "/listAll", method = GET)
  public List<Product> listAll() {
    return productService.findAll();
  }

  @PreAuthorize(ADMIN)
  @ControllerLogger("新增产品信息")
  @ApiOperation(value = "新增产品信息")
  @RequestMapping(path = "/create", method = POST)
  public RsResponse create(@Valid @RequestBody Product product) throws AuthServiceException {
    productService.create(product);
    return new RsResponse(ok);
  }

  @PreAuthorize(ADMIN)
  @ControllerLogger("根据id查询产品信息")
  @ApiOperation(value = "根据id查询产品信息")
  @RequestMapping(path = "/{id}", method = GET)
  public Product detail(@PathVariable("id") ObjectId id) throws AuthServiceException {
    return productService.findOne(id);
  }

  @PreAuthorize(USER)
  @ControllerLogger("根据名称查询产品信息")
  @ApiOperation(value = "根据名称查询产品信息")
  @RequestMapping(path = "/getByName/{name}", method = GET)
  public Product getByName(@PathVariable("name") String name) throws AuthServiceException {
    final Product product = productService.findByName(name);
    if (product == null) {
      throw new AuthServiceException(productNameNotExist.messageOf(name));
    }
    return product;
  }

  @PreAuthorize(ADMIN)
  @ControllerLogger("更新产品信息")
  @ApiOperation(value = "更新产品信息")
  @RequestMapping(path = "/update", method = POST)
  public RsResponse update(@Valid @RequestBody Product product) {
    productService.update(product);
    return new RsResponse(ok);
  }

  @PreAuthorize(ADMIN)
  @ControllerLogger("删除产品信息")
  @ApiOperation(value = "删除产品信息")
  @RequestMapping(path = "/delete/{id}", method = DELETE)
  public RsResponse delete(@PathVariable("id") ObjectId id) {
    productService.delete(id);
    return new RsResponse(ok);
  }

}