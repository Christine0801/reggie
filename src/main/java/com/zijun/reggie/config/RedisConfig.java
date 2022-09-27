package com.zijun.reggie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    // 注入连接工厂
    redisTemplate.setConnectionFactory(connectionFactory);

//    // 设置 key 序列化方式
//    redisTemplate.setKeySerializer(new StringRedisSerializer());
//    // 设置 value 序列化方式
//    redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//    // 设置 Hash key 序列化方式
//    redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//    // 设置 Hash value 序列化方式
//    redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
//
//    return redisTemplate;

    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setConnectionFactory(connectionFactory);
    return redisTemplate;
  }
}
