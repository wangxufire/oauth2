package com.hd123.oauth2.entity;

import static com.alibaba.fastjson.serializer.SerializerFeature.WriteMapNullValue;
import static com.alibaba.fastjson.serializer.SerializerFeature.WriteNullNumberAsZero;

import org.springframework.data.annotation.Version;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 带版本控制的文档基类，自动生成_id
 *
 * @author liyue
 */
public abstract class PVersionEntity extends PEntity {
  private static final long serialVersionUID = -8561825676613808604L;

  @Version
  @JSONField(serialzeFeatures = {
      WriteMapNullValue, WriteNullNumberAsZero })
  private Integer version;

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

}