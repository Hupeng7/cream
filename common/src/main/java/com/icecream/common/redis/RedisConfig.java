package com.icecream.common.redis;

import com.alibaba.fastjson.parser.ParserConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Resource;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/6/19 0019
 */
@Configuration
public class RedisConfig {

    private static final String host = "10.40.254.59";
    //端口
    private static final int port =6379 ;
    //密码没有不填写
    private static final String password="";
    // Redis数据库索引（默认为0）
    private static final int database =1;
    //连接池最大阻塞等待时间（使用负值表示没有限制）
    private static final int maxWait=-1;
    //连接池中的最大空闲连接
    private static final int maxIdle=1000;
    //连接池中的最小空闲连接
    private static final int minIdle=1;
    //连接超时时间（毫秒）
    private static final int timeOut=3000;

    @Bean
    public RedisSerializer fastJson2JsonRedisSerializer() {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        return new FastJson2JsonRedisSerializer<Object>(Object.class);
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        //最大空闲接连
        jedisPoolConfig.setMaxIdle(maxIdle);
        //最小空闲连接
        jedisPoolConfig.setMinIdle(minIdle);
        //连接池最大阻塞等待时间
        jedisPoolConfig.setMaxWaitMillis(maxWait);
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        //主机地址
        jedisConnectionFactory.setHostName(host);
        //端口
        jedisConnectionFactory.setPort(port);
        //密码
        jedisConnectionFactory.setPassword(password);
        //索引
        jedisConnectionFactory.setDatabase(database);
        //超时时间
        jedisConnectionFactory.setTimeout(timeOut);
        jedisConnectionFactory.setUsePool(true);
        jedisConnectionFactory.setPoolConfig(jedisPoolConfig);
        return jedisConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory, RedisSerializer fastJson2JsonRedisSerializer) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate(factory);
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        //redis   开启事务
        redisTemplate.setEnableTransactionSupport(true);
        //hash  使用jdk  的序列化
        redisTemplate.setHashValueSerializer(fastJson2JsonRedisSerializer/*new JdkSerializationRedisSerializer()*/);
        //StringRedisSerializer  key  序列化
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        //keySerializer  对key的默认序列化器。默认值是StringSerializer
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //  valueSerializer
        redisTemplate.setValueSerializer(fastJson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
