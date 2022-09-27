package com.zijun.reggie.common;

import com.zijun.reggie.entity.Dish;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestDemo {

  @Autowired
  private RedisTemplate<String, Object> redisTemplate ;

  @GetMapping("/demo")
  public R<String> demo() {

    Dish dish = new Dish();
    dish.setCreateTime(LocalDateTime.now());
    dish.setUpdateTime(LocalDateTime.now());

    redisTemplate.opsForValue().set("test", dish, 10, TimeUnit.MINUTES);


    return R.success("success");
  }

  @GetMapping("/demo2")
  public R<Dish> demo2 () {
    Dish test = (Dish) redisTemplate.opsForValue().get("test");

    return R.success(test);
  }
}
