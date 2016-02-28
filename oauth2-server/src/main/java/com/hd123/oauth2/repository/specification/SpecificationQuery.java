package com.hd123.oauth2.repository.specification;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Stream.of;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.util.Assert.hasLength;
import static org.springframework.util.Assert.notNull;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.hd123.oauth2.common.tuple.Pair;
import com.hd123.oauth2.entity.PEntity;

/**
 * 通用查询操作
 *
 * @author liyue
 */
@Component
@SuppressWarnings({"rawtypes", "unchecked"})
public class SpecificationQuery {

  private static final String UID_FIELD = "serialVersionUID";

  @Autowired protected MongoTemplate mongoTemplate;

  /**
   * 全文检索分页查询
   *
   * @param keyword       关键词（正则）,传空("")不限制条件
   * @param clazz         文档实体类型
   * @param excludeFields 排除字段,将不被检索
   * @param <T>           文档
   * @return Page<T>
   */
  public <T extends PEntity> Page<T> query(String keyword, Class<T> clazz, String... excludeFields) {
    return query(keyword, clazz, null, null, excludeFields);
  }

  /**
   * 全文检索分页查询
   *
   * @param keyword       关键词（正则）,传空("")不限制条件
   * @param clazz         文档实体类型
   * @param pageReq       分页信息
   * @param excludeFields 排除字段,将不被检索
   * @param <T>           文档
   * @return Page<T>
   */
  public <T extends PEntity> Page<T> query(String keyword, Class<T> clazz, Pageable pageReq, String... excludeFields) {
    return query(keyword, clazz, pageReq, null, excludeFields);
  }

  /**
   * 全文检索分页查询
   *
   * @param keyword       关键词（正则）,传空("")不限制条件
   * @param clazz         文档实体类型
   * @param sort          排序信息
   * @param excludeFields 排除字段,将不被检索
   * @param <T>           文档
   * @return Page<T>
   */
  public <T extends PEntity> Page<T> query(String keyword, Class<T> clazz, Sort sort, String... excludeFields) {
    return query(keyword, clazz, null, sort, excludeFields);
  }

  /**
   * 全文检索分页查询
   *
   * @param keyword       关键词（正则）,传空("")不限制条件
   * @param clazz         文档实体类型
   * @param pageReq       分页信息
   * @param sort          排序信息
   * @param excludeFields 排除字段,将不被检索
   * @param <T>           文档
   * @return Page<T>
   */
  public <T extends PEntity> Page<T> query(String keyword, Class<T> clazz, Pageable pageReq, Sort sort, String... excludeFields) {
    return query(keyword, null, clazz, pageReq, sort, excludeFields);
  }

  /**
   * 全文检索分页查询
   *
   * @param keyword       关键词（正则）,传空("")不限制条件
   * @param pair          精确条件键值对
   * @param clazz         文档实体类型
   * @param excludeFields 排除字段,将不被检索
   * @param <T>           文档
   * @return Page<T>
   */
  public <T extends PEntity> Page<T> query(String keyword, Pair<String, Object> pair,
      Class<T> clazz, String... excludeFields) {
    return query(keyword, pair, clazz, null, null, excludeFields);
  }

  /**
   * 全文检索分页查询
   *
   * @param keyword       关键词（正则）,传空("")不限制条件
   * @param pair          精确条件键值对
   * @param clazz         文档实体类型
   * @param pageReq       分页信息
   * @param excludeFields 排除字段,将不被检索
   * @param <T>           文档
   * @return Page<T>
   */
  public <T extends PEntity> Page<T> query(String keyword, Pair<String, Object> pair,
      Class<T> clazz, Pageable pageReq, String... excludeFields) {
    return query(keyword, pair, clazz, pageReq, null, excludeFields);
  }

  /**
   * 全文检索分页查询
   *
   * @param keyword       关键词（正则）,传空不限制条件
   * @param pair          精确条件键值对
   * @param clazz         文档实体类型
   * @param pageReq       分页信息
   * @param sort          排序信息
   * @param excludeFields 排除字段,将不被检索
   * @param <T>           文档
   * @return Page<T>
   */
  public <T extends PEntity> Page<T> query(String keyword, Pair<String, Object> pair,
      Class<T> clazz, Pageable pageReq, Sort sort, String... excludeFields) {
    notNull(clazz, "claz");

    Query query = null;
    if (isBlank(keyword)) {
      query = new Query();
      if (pair != null) {
        final String key = pair.getLeft();
        hasLength(key, "pair.getLeft");
        final Object value = pair.getRight();
        notNull(value, "pair.getRight");
        query.addCriteria(new Criteria(key).is(value));
      }
    } else {
      final Field[] fields = clazz.getDeclaredFields();
      final List<Criteria> criterias = newArrayList();
      final Predicate<Field> id = field -> !field.isAnnotationPresent(Id.class);
      final Predicate<Field> uid = field -> !equal(field.getName(), UID_FIELD);
      of(fields).parallel().filter(id.and(uid)).forEach(field -> {
        final String key = field.getName();
        for (String excludeField : excludeFields) {
          if (key.equalsIgnoreCase(excludeField)) {
            return;
          }
        }
        criterias.add(where(key).regex(keyword));
      });
      final Criteria[] queryCriterias = criterias.toArray(new Criteria[criterias.size()]);
      final Criteria criteria = new Criteria().orOperator(queryCriterias);
      if (pair != null) {
        final String key = pair.getLeft();
        hasLength(key, "pair.getLeft");
        final Object value = pair.getRight();
        notNull(value, "pair.getRight");
        criteria.andOperator(where(key).is(value));
      }

      query = new Query(criteria);
    }

    if (pageReq == null) {
      pageReq = new PageRequest(0, 2000);
    }
    query.with(pageReq);

    if (sort != null) {
      query.with(sort);
    }

    return new PageImpl(mongoTemplate.find(query, clazz), pageReq, mongoTemplate.count(query, clazz));
  }

}