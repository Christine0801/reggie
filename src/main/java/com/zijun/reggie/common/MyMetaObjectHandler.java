package com.zijun.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

  @Autowired
  private RedisTemplate<String, Object> redisTemplate ;

  @Override
  public void insertFill(MetaObject metaObject) {
    Integer empId = (Integer) redisTemplate.opsForValue().get("employee");
    if (metaObject.hasSetter("createUser"))
      metaObject.setValue("createUser", empId.longValue());
    if (metaObject.hasSetter("updateUser"))
      metaObject.setValue("updateUser", empId.longValue());
    if (metaObject.hasSetter("createTime"))
      metaObject.setValue("createTime", LocalDateTime.now());
    if (metaObject.hasSetter("updateTime"))
      metaObject.setValue("updateTime", LocalDateTime.now());
  }

  @Override
  public void updateFill(MetaObject metaObject) {
    Integer empId = (Integer) redisTemplate.opsForValue().get("employee");
    if (metaObject.hasSetter("updateUser"))
      metaObject.setValue("updateUser", empId.longValue());
    if (metaObject.hasSetter("updateTime"))
      metaObject.setValue("updateTime", LocalDateTime.now());
  }
}
