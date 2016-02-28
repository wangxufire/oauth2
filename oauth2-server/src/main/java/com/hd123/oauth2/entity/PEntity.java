package com.hd123.oauth2.entity;

import static com.google.common.base.Objects.equal;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.google.common.base.Objects;

/**
 * 文档基类，自动生成_id
 *
 * @author liyue
 */
public abstract class PEntity implements Serializable {
  private static final long serialVersionUID = -3200234117346961714L;

  @Id
  private ObjectId id; // = new ObjectId(new Date())

  /**
   * 主键，自增长
   *
   * @return id
   */
  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId uuid) {
    this.id = uuid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PEntity)) {
      return false;
    }
    final PEntity pEntity = (PEntity) o;
    return equal(id, pEntity.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

}