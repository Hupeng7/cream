package com.icecream.comment.redis;

import com.alibaba.fastjson.parser.ParserConfig;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCachePrefix;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/6/19 0019
 */
@Configuration
public class RedisConfig extends CachingConfigurerSupport {


    /**
     * redis key生成规则
     * @return
     * className:method:params
     */
    @Bean("KeyGenerator")
    public KeyGenerator KeyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }

    /**
     * 缓存容器
     * @param redisTemplate redis操作模板
     * @return
     */
    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager manager = new RedisCacheManager(redisTemplate);
        manager.setUsePrefix(true);
        RedisCachePrefix cachePrefix = new RedisPrefix("comment");
        manager.setCachePrefix(cachePrefix);
        // 整体缓存过期时间
        manager.setDefaultExpiration(3600L);
        // 设置缓存过期时间。key和缓存过期时间，单位秒
        Map<String, Long> expiresMap = new HashMap<>();
        manager.setExpires(expiresMap);
        return manager;
    }


    /**
     * 自定义序列器
     * @return
     */
    @Bean
    public RedisSerializer CustomRedisSerializer() {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        return new CustomRedisSerializer<>(Object.class);
    }


    /**
     * redisTemplate 配置
     * @param factory jedis连接工厂对象
     * @return
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory,RedisSerializer customRedisSerializer) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate(factory);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setConnectionFactory(factory);
        //redis   开启事务
        redisTemplate.setEnableTransactionSupport(true);

        //StringRedisSerializer  key  序列化
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        //keySerializer  对key的默认序列化器。默认值是StringSerializer
        redisTemplate.setKeySerializer(stringRedisSerializer);
        //  valueSerializer
        redisTemplate.setValueSerializer(customRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
