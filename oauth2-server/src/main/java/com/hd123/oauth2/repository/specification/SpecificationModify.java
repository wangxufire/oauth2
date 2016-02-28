package com.hd123.oauth2.repository.specification;

import static com.alibaba.fastjson.JSON.parseObject;
import static com.alibaba.fastjson.JSON.toJSONString;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Stream.of;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Update.fromDBObject;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.hd123.oauth2.entity.PEntity;

import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;

/**
 * 通用更新操作
 *
 * @author liyue
 */
@Component
@SuppressWarnings({
    "rawtypes", "unchecked" })
public class SpecificationModify {

  private static final String IDENTIFIER = "id";
  private static final String DB_IDENTIFIER = "_id";
  private static final String VERSION = "version";
  private static List<String> excludes = newArrayList(IDENTIFIER, DB_IDENTIFIER);

  @Autowired
  protected MongoTemplate mongoTemplate;

  /**
   * 更新文档
   *
   * @param id
   *          文档id
   * @param entity
   *          文档实体
   * @param clazz
   *          文档类型
   * @param excludeFields
   *          排除字段,将不被更新
   * @param <T>
   *          文档
   * @return WriteResult更新结果
   */
  public <T extends PEntity> WriteResult update(ObjectId id, PEntity entity, Class<T> clazz,
      String... excludeFields) {
    final Query query = new Query(where(DB_IDENTIFIER).is(id));
    of(excludeFields).parallel().forEach(field -> {
      excludes.add(field);
    });

    final BasicDBObject dbObject = new BasicDBObject(parseObject(toJSONString(entity), Map.class));
    if (dbObject.containsField(VERSION)) {
      dbObject.replace(VERSION, ((Integer) dbObject.get(VERSION)) + 1);
    }

    final Update update = fromDBObject(dbObject, excludes.toArray(new String[excludes.size()]));
    return mongoTemplate.upsert(query, update, clazz);
  }

}