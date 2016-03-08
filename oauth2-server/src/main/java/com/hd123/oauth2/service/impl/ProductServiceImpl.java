package com.hd123.oauth2.service.impl;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.hd123.oauth2.support.ExceptionCode.productNameExist;
import static com.hd123.oauth2.support.ExceptionCode.productNotExist;
import static com.hd123.oauth2.util.DateUtil.now;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_APPLICATION;
import static org.springframework.util.Assert.hasLength;
import static org.springframework.util.Assert.notNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.context.annotation.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hd123.oauth2.common.tuple.MutablePair;
import com.hd123.oauth2.entity.Product;
import com.hd123.oauth2.exception.AuthServiceException;
import com.hd123.oauth2.logger.ServiceLogger;
import com.hd123.oauth2.service.ProductService;

/**
 * 产品服务接口实现
 *
 * @author liyue
 */
@Role(ROLE_APPLICATION)
@Service(value = "productService")
public class ProductServiceImpl extends AbstractService implements ProductService {

  @Override
  @ServiceLogger("新增产品信息")
  public Product create(Product product) throws AuthServiceException {
    notNull(product, "product");
    final String name = product.getName();
    hasLength(name, "product.getName");
    if (findByName(name) != null) {
      throw new AuthServiceException(productNameExist.messageOf(name));
    }
    return productRepository.save(product);
  }

  @Override
  @ServiceLogger("分页查询产品信息")
  public Page<Product> page(String keyword, Pageable pageRequest) {
    return specificationQuery.query(keyword, Product.class, pageRequest);
  }

  @Override
  @ServiceLogger("查看所有产品信息")
  public List<Product> findAll() {
    return productRepository.findAll();
  }

  @Override
  @ServiceLogger("更新产品信息")
  public void update(Product product) {
    notNull(product, "product");
    product.setModify(now());
    product.setScopes(reBuildScopes(product.getScopes()));
    specificationModify.update(product.getId(), product, Product.class);
  }

  @Override
  @ServiceLogger("根据id删除产品信息")
  public void delete(ObjectId id) {
    notNull(id, "id");
    productRepository.delete(id);
  }

  @Override
  @ServiceLogger("根据id查询产品信息")
  public Product findOne(ObjectId id) throws AuthServiceException {
    notNull(id, "id");
    final Product product = productRepository.findOne(id);
    if (product == null) {
      throw new AuthServiceException(productNotExist.messageOf(id.toString()));
    }
    return product;
  }

  @Override
  @ServiceLogger("根据名称查询产品信息")
  public Product findByName(String name) throws AuthServiceException {
    hasLength(name, "name");

    final Optional<Product> opProduct = productRepository.findDistinctByName(name);
    if (!opProduct.isPresent()) {
      return null;
    }
    return opProduct.get();
  }

  /**
   * 重构scopes,去除键值相同的
   *
   * @param scopes
   *          scopes
   * @return newScopes
   */
  private List<MutablePair<String, String>> reBuildScopes(List<MutablePair<String, String>> scopes) {
    final Map<String, MutablePair<String, String>> maps = newHashMap();
    scopes.parallelStream().forEach(pair -> {
      maps.put(pair.getLeft(), pair);
    });
    final List<MutablePair<String, String>> newScopes = newArrayList();
    maps.entrySet().parallelStream().forEach(entry -> {
      newScopes.add(maps.get(entry.getKey()));
    });
    return newScopes;
  }

}