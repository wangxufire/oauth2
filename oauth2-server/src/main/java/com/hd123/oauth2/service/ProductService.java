package com.hd123.oauth2.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hd123.oauth2.entity.Product;
import com.hd123.oauth2.exception.AuthServiceException;

/**
 * 产品服务接口
 *
 * @author liyue
 */
public interface ProductService {

  /**
   * 新增
   *
   * @param product
   *          product
   * @return product
   * @throws AuthServiceException
   *           授权业务异常
   */
  Product create(Product product) throws AuthServiceException;

  /**
   * 分页关键字查询
   *
   * @param keyword
   *          关键字
   * @param pageRequest
   *          分页请求
   * @return Page<App>
   */
  Page<Product> page(String keyword, Pageable pageRequest);

  /**
   * 获取全部产品
   *
   * @return Products
   */
  List<Product> findAll();

  /**
   * 更新
   *
   * @param product
   *          product
   */
  void update(Product product);

  /**
   * 删除
   *
   * @param id
   *          id
   */
  void delete(ObjectId id);

  /**
   * 根据id查找
   *
   * @param id
   *          id
   * @return product
   * @throws AuthServiceException
   *           授权业务异常
   */
  Product findOne(ObjectId id) throws AuthServiceException;

  /**
   * 根据name查找
   *
   * @param name
   *          name
   * @return product
   * @throws AuthServiceException
   *           授权业务异常
   */
  Product findByName(String name) throws AuthServiceException;

}