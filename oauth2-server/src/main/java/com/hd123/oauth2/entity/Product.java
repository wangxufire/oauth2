package com.hd123.oauth2.entity;

import static com.google.common.collect.Lists.newArrayList;
import static com.hd123.oauth2.util.DateUtil.now;
import static org.springframework.data.mongodb.core.index.IndexDirection.DESCENDING;

import java.util.List;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.hd123.oauth2.common.tuple.MutablePair;

/**
 * 产品
 *
 * @author liyue
 */
@Document(collection = "product", language = "simplified chinese")
public class Product extends PVersionEntity {
  private static final long serialVersionUID = -910647022026023688L;

  @NotBlank
  @Indexed(name = "product_name_idx", unique = true, direction = DESCENDING, background = true)
  private String name;
  /* 最后修改时间 */
  private String modify = now();
  /* 接口地址,接口名 */
  private List<MutablePair<String, String>> scopes = newArrayList();

  public Product() {
    super();
  }

  public Product(String name) {
    this.name = name;
  }

  @PersistenceConstructor
  public Product(String name, String modify, List<MutablePair<String, String>> scopes) {
    this.name = name;
    this.modify = modify;
    this.scopes = scopes;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getModify() {
    return modify;
  }

  public void setModify(String modify) {
    this.modify = modify;
  }

  public List<MutablePair<String, String>> getScopes() {
    return scopes;
  }

  public void setScopes(List<MutablePair<String, String>> scopes) {
    this.scopes = scopes;
  }

}