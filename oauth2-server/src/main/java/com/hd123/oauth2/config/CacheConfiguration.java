package com.hd123.oauth2.config;

import static com.hd123.oauth2.common.Constants.COLON;
import static java.util.stream.Stream.of;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCachePrefix;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 缓存配置(Redis)
 *
 * @author liyue
 * @since 0.3.1
 */
@Configuration
public class CacheConfiguration {

  @Bean
  @Autowired
  @Role(ROLE_INFRASTRUCTURE)
  public Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer(ObjectMapper objectMapper) {
    final Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(
        Object.class);
    jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
    return jackson2JsonRedisSerializer;
  }

  @Bean
  @Autowired
  @Role(ROLE_INFRASTRUCTURE)
  public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory,
      Jackson2JsonRedisSerializer jackson2JsonRedisSerializer) {
    final RedisTemplate<Object, Object> template = new RedisTemplate<Object, Object>();
    template.setConnectionFactory(redisConnectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(jackson2JsonRedisSerializer);
    template.setHashValueSerializer(jackson2JsonRedisSerializer);
    template.setDefaultSerializer(jackson2JsonRedisSerializer);
    template.setEnableTransactionSupport(true);
    return template;
  }

  @Bean
  @Role(ROLE_INFRASTRUCTURE)
  public KeyGenerator redisKeyGenerator() {
    return (target, method, params) -> {
      final StringBuilder sb = new StringBuilder();
      sb.append(target.getClass().getSimpleName()).append(COLON).append(method.getName())
          .append(COLON);
      of(params).parallel().forEach(sb::append);
      return sb.append(COLON).toString();
    };
  }

  @Bean
  @Autowired
  @Role(ROLE_INFRASTRUCTURE)
  public CacheManager cacheManager(RedisTemplate redisTemplate) {
    final RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate);
    redisCacheManager.setUsePrefix(true);
    redisCacheManager.setCachePrefix(new Oauth2RedisCachePrefix("OAUTH2"));
    redisCacheManager.setTransactionAware(true);
    redisCacheManager.setLoadRemoteCachesOnStartup(true);
    return redisCacheManager;
  }

  /**
   * 缓存前缀
   *
   * @author liyue
   */
  static final class Oauth2RedisCachePrefix implements RedisCachePrefix {

    private final RedisSerializer serializer = new StringRedisSerializer();
    private final String prefix;

    public Oauth2RedisCachePrefix() {
      this(null);
    }

    public Oauth2RedisCachePrefix(String prefix) {
      this.prefix = prefix.concat(COLON);
    }

    public byte[] prefix(String cacheName) {
      return serializer.serialize((prefix != null ? prefix.concat(cacheName.concat(COLON))
          : cacheName.concat(COLON)));
    }

  }

}