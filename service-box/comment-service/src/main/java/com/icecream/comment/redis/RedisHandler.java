package com.icecream.comment.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author Mr_h
 * @version 1.0
 * description: redis工具类
 * create by Mr_h on 2018/6/19 0019
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class RedisHandler {

    @Autowired
    private  RedisTemplate redisTemplate;

    public RedisConnection getConnection(){
        RedisConnectionFactory connectionFactory =redisTemplate.getConnectionFactory();
        RedisConnection connection = connectionFactory.getConnection();
        return connection;
    }

    /**
     * 出异常，重复操作的次数
     */
    private  Integer times = 5;


    public  double getCreateTimeScore(long date) {
        return date / 100000.0;
    }


    public  Set<String> getAllKeys() {
        return redisTemplate.keys("*");
    }


    public  Map<String, Object> getAllString() {
        Set<String> stringSet = getAllKeys();
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.STRING) {
                map.put(k, get(k));
            }
        }
        return map;
    }


    public  Map<String, Set<Object>> getAllSet() {
        Set<String> stringSet = getAllKeys();
        Map<String, Set<Object>> map = new HashMap<String, Set<Object>>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.SET) {
                map.put(k, getSet(k));
            }
        }
        return map;
    }


    public  Map<String, Set<Object>> getAllZSetRange() {
        Set<String> stringSet = getAllKeys();
        Map<String, Set<Object>> map = new HashMap<String, Set<Object>>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.ZSET) {
                log.debug("k:" + k);
                map.put(k, getZSetRange(k));
            }
        }
        return map;
    }


    public  Map<String, Set<Object>> getAllZSetReverseRange() {
        Set<String> stringSet = getAllKeys();
        Map<String, Set<Object>> map = new HashMap<String, Set<Object>>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.ZSET) {
                map.put(k, getZSetReverseRange(k));
            }
        }
        return map;
    }


    public  Map<String, List<Object>> getAllList() {
        Set<String> stringSet = getAllKeys();
        Map<String, List<Object>> map = new HashMap<String, List<Object>>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.LIST) {
                map.put(k, getList(k));
            }
        }
        return map;
    }


    public  Map<String, Map<String, Object>> getAllMap() {
        Set<String> stringSet = getAllKeys();
        Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.HASH) {
                map.put(k, getMap(k));
            }
        }
        return map;
    }


    public  void addList(String key, List<Object> objectList) {
        for (Object obj : objectList) {
            addList(key, obj);
        }
    }


    public  long addList(String key, Object obj) {
        return redisTemplate.boundListOps(key).rightPush(obj);
    }


    public  long addList(String key, Object... obj) {
        return redisTemplate.boundListOps(key).rightPushAll(obj);
    }


    public  List<Object> getList(String key, long s, long e) {
        return redisTemplate.boundListOps(key).range(s, e);
    }


    public  List<Object> getList(String key) {
        return redisTemplate.boundListOps(key).range(0, getListSize(key));
    }


    public  long getListSize(String key) {
        return redisTemplate.boundListOps(key).size();
    }


    public  long removeListValue(String key, Object object) {
        return redisTemplate.boundListOps(key).remove(0, object);
    }


    public  long removeListValue(String key, Object... objects) {
        long r = 0;
        for (Object object : objects) {
            r += removeListValue(key, object);
        }
        return r;
    }


    public  void remove(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                remove(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }


    public  void removeBlear(String... blears) {
        for (String blear : blears) {
            removeBlear(blear);
        }
    }


    public  Boolean renameIfAbsent(String oldKey, String newKey) {
        return redisTemplate.renameIfAbsent(oldKey, newKey);
    }


    public  void removeBlear(String blear) {
        redisTemplate.delete(redisTemplate.keys(blear));
    }


    public  void removeByRegular(String... blears) {
        for (String blear : blears) {
            removeBlear(blear);
        }
    }


    public  void removeByRegular(String blear) {
        Set<String> stringSet = getAllKeys();
        for (String s : stringSet) {
            if (Pattern.compile(blear).matcher(s).matches()) {
                redisTemplate.delete(s);
            }
        }
    }


    public  void removeMapFieldByRegular(String key, String... blears) {
        for (String blear : blears) {
            removeMapFieldByRegular(key, blear);
        }
    }


    public  void removeMapFieldByRegular(String key, String blear) {
        Map<String, Object> map = getMap(key);
        Set<String> stringSet = map.keySet();
        for (String s : stringSet) {
            if (Pattern.compile(blear).matcher(s).matches()) {
                redisTemplate.boundHashOps(key).delete(s);
            }
        }
    }


    public  Long removeZSetValue(String key, Object... value) {
        return redisTemplate.boundZSetOps(key).remove(value);
    }


    public  void removeZSet(String key) {
        removeZSetRange(key, 0L, getZSetSize(key));
    }


    public  void removeZSetRange(String key, Long start, Long end) {
        redisTemplate.boundZSetOps(key).removeRange(start, end);
    }


    public  void setZSetUnionAndStore(String key, String key1, String key2) {
        redisTemplate.boundZSetOps(key).unionAndStore(key1, key2);
    }


    public  Set<Object> getZSetRange(String key) {
        return getZSetRange(key, 0, getZSetSize(key));
    }


    public  Set<Object> getZSetRange(String key, long s, long e) {
        return redisTemplate.boundZSetOps(key).range(s, e);
    }


    public  Set<Object> getZSetReverseRange(String key) {
        return getZSetReverseRange(key, 0, getZSetSize(key));
    }


    public  Set<Object> getZSetReverseRange(String key, long start, long end) {
        return redisTemplate.boundZSetOps(key).reverseRange(start, end);
    }


    public  Set<Object> getZSetRangeByScore(String key, double start, double end) {
        return redisTemplate.boundZSetOps(key).rangeByScore(start, end);
    }


    public  Set<Object> getZSetReverseRangeByScore(String key, double start, double end) {
        return redisTemplate.boundZSetOps(key).reverseRangeByScore(start, end);
    }


    public  Set<ZSetOperations.TypedTuple<Object>> getZSetRangeWithScores(String key, long start, long end) {
        return redisTemplate.boundZSetOps(key).rangeWithScores(start, end);
    }


    public  Set<ZSetOperations.TypedTuple<Object>> getZSetReverseRangeWithScores(String key, long start, long end) {
        return redisTemplate.boundZSetOps(key).reverseRangeWithScores(start, end);
    }


    public  Set<ZSetOperations.TypedTuple<Object>> getZSetRangeWithScores(String key) {
        return getZSetRangeWithScores(key, 0, getZSetSize(key));
    }


    public  Set<ZSetOperations.TypedTuple<Object>> getZSetReverseRangeWithScores(String key) {
        return getZSetReverseRangeWithScores(key, 0, getZSetSize(key));
    }


    public  long getZSetCountSize(String key, double sMin, double sMax) {
        return redisTemplate.boundZSetOps(key).count(sMin, sMax);
    }


    public  long getZSetSize(String key) {
        return redisTemplate.boundZSetOps(key).size();
    }


    public  double getZSetScore(String key, Object value) {
        return redisTemplate.boundZSetOps(key).score(value);
    }


    public  double incrementZSetScore(String key, Object value, double delta) {
        return redisTemplate.boundZSetOps(key).incrementScore(value, delta);
    }


    public  Boolean addZSet(String key, double score, Object value) {
        return redisTemplate.boundZSetOps(key).add(value, score);
    }


    public  Long addZSet(String key, TreeSet<Object> value) {
        return redisTemplate.boundZSetOps(key).add(value);
    }


    public  Boolean addZSet(String key, double[] score, Object[] value) {
        if (score.length != value.length) {
            return false;
        }
        for (int i = 0; i < score.length; i++) {
            if (addZSet(key, score[i], value[i]) == false) {
                return false;
            }
        }
        return true;
    }


    public  void remove(String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }


    public  void removeZSetRangeByScore(String key, double s, double e) {
        redisTemplate.boundZSetOps(key).removeRangeByScore(s, e);
    }


    public  Boolean setSetExpireTime(String key, Long time) {
        return redisTemplate.boundSetOps(key).expire(time, TimeUnit.SECONDS);
    }


    public  Boolean setZSetExpireTime(String key, Long time) {
        return redisTemplate.boundZSetOps(key).expire(time, TimeUnit.SECONDS);
    }


    public  boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    public  Object get(int key) {
        return get(String.valueOf(key));
    }

    public  Object get(long key) {
        return get(String.valueOf(key));
    }

    public  Object get(String key) {
        return redisTemplate.boundValueOps(key).get();
    }


    public  List<Object> get(String... keys) {
        List<Object> list = new ArrayList<Object>();
        for (String key : keys) {
            list.add(get(key));
        }
        return list;
    }


    public  List<Object> getByRegular(String regKey) {
        Set<String> stringSet = getAllKeys();
        List<Object> objectList = new ArrayList<Object>();
        for (String s : stringSet) {
            if (Pattern.compile(regKey).matcher(s).matches() && getType(s) == DataType.STRING) {
                objectList.add(get(s));
            }
        }
        return objectList;
    }


    public  void set(long key, Object value) {
        set(String.valueOf(key), value);
    }

    public  void set(int key, Object value) {
        set(String.valueOf(key), value);
    }


    public  void set(String key, Object value) {
        redisTemplate.boundValueOps(key).set(value);
    }


    public  void set(String key, Object value, Long expireTime) {
        redisTemplate.boundValueOps(key).set(value, expireTime, TimeUnit.SECONDS);
    }


    public  boolean setExpireTime(String key, long expireTime,TimeUnit timeUnit) {
        return redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }


    public  DataType getType(String key) {
        return redisTemplate.type(key);
    }


    public  void removeMapField(String key, Object... field) {
        redisTemplate.boundHashOps(key).delete(field);
    }


    public  Long getMapSize(String key) {
        return redisTemplate.boundHashOps(key).size();
    }


    public  Map<String, Object> getMap(String key) {
        return redisTemplate.boundHashOps(key).entries();
    }


    public  <T> T getMapField(String key, String field) {
        return (T) redisTemplate.boundHashOps(key).get(field);
    }

    public  <K,V> void set(K key,V value){
        JSONObject jsonObject = (JSONObject) JSON.toJSON(value);
        set(String.valueOf(key), value);
    }


    public  Boolean hasMapKey(String key, String field) {
        return redisTemplate.boundHashOps(key).hasKey(field);
    }


    public  List<Object> getMapFieldValue(String key) {
        return redisTemplate.boundHashOps(key).values();
    }


    public  Set<Object> getMapFieldKey(String key) {
        return redisTemplate.boundHashOps(key).keys();
    }


    public  void addMap(String key, Map<String, Object> map) {
        redisTemplate.boundHashOps(key).putAll(map);
    }


    public  void addMap(String key, String field, Object value) {
        redisTemplate.boundHashOps(key).put(field, value);
    }


    public  void addMap(String key, String field, Object value, long time) {
        redisTemplate.boundHashOps(key).put(field, value);
        redisTemplate.boundHashOps(key).expire(time, TimeUnit.SECONDS);
    }


    public  void watch(String key) {
        redisTemplate.watch(key);
    }


    public  void addSet(String key, Object... obj) {
        redisTemplate.boundSetOps(key).add(obj);
    }


    public  long removeSetValue(String key, Object obj) {
        return redisTemplate.boundSetOps(key).remove(obj);
    }


    public  long removeSetValue(String key, Object... obj) {
        if (obj != null && obj.length > 0) {
            return redisTemplate.boundSetOps(key).remove(obj);
        }
        return 0L;
    }


    public  long getSetSize(String key) {
        return redisTemplate.boundSetOps(key).size();
    }


    public  Boolean hasSetValue(String key, Object obj) {
        Boolean boo = null;
        int t = 0;
        while (true) {
            try {
                boo = redisTemplate.boundSetOps(key).isMember(obj);
                break;
            } catch (Exception e) {
                log.error("key[" + key + "],obj[" + obj + "]判断Set中的值是否存在失败,异常信息:" + e.getMessage());
                t++;
            }
            if (t > times) {
                break;
            }
        }
        log.info("key[" + key + "],obj[" + obj + "]是否存在,boo:" + boo);
        return boo;
    }


    public  Set<Object> getSet(String key) {
        return redisTemplate.boundSetOps(key).members();
    }


    public  Set<Object> getSetUnion(String key, String otherKey) {
        return redisTemplate.boundSetOps(key).union(otherKey);
    }


    public  Set<Object> getSetUnion(String key, Set<Object> set) {
        return redisTemplate.boundSetOps(key).union(set);
    }


    public  Set<Object> getSetIntersect(String key, String otherKey) {
        return redisTemplate.boundSetOps(key).intersect(otherKey);
    }


    public  Set<Object> getSetIntersect(String key, Set<Object> set) {
        return redisTemplate.boundSetOps(key).intersect(set);
    }


    //递减
    public  Long decr(String key,long reduce) {
        if(reduce<0){
            throw  new RuntimeException("递减因子不能为0");
        }
        return redisTemplate.opsForValue().increment(key,-reduce);
    }

    public  Long incr(String key,long increment) {
        if(increment<0){
            throw  new RuntimeException("递增因子不能为0");
        }

        return redisTemplate.opsForValue().increment(key,increment);
    }

    /**
     * 加锁
     * @param lockName       锁的key
     * @param acquireTimeout 获取超时时间(s)
     * @param timeout        锁的超时时间(ms)
     * @return 锁标识
     */

    public  String lockWithTimeout(String lockName, long acquireTimeout, long timeout) {
        String retIdentifier = null;
        try {
            // 获取连接
            // 随机生成一个value
            String identifier = UUID.randomUUID().toString();
            // 锁名，即key值
            String lockKey = "lock:" + lockName;
            // 超时时间，上锁后超过此时间则自动释放锁
            int lockExpire = (int) (timeout/1000);
            // 获取锁的超时时间，超过这个时间则放弃获取锁
            while (0 < acquireTimeout) {
                if (redisTemplate.opsForValue().setIfAbsent(lockKey,identifier)) {
                    redisTemplate.expire(lockKey, lockExpire,TimeUnit.SECONDS);
                    // 返回value值，用于释放锁时间确认
                    retIdentifier = identifier;
                    return retIdentifier;
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (JedisException e) {
            e.printStackTrace();
        }
        return retIdentifier;
    }

    /**
     * 释放锁
     *
     * @param lockName   锁的key
     * @param identifier 释放锁的标识
     * @return
     */

    public  void releaseLock(String lockName, String identifier) {
        String lockKey = "lock:" + lockName;
        String value = get(lockKey).toString();
        if(value.equals(identifier)){
            remove(lockKey);
        }
    }

    /**
     * 获取jedis对象
     * @return
     */
    public  Jedis getJedis(){
        Field jedisField  = ReflectionUtils.findField(JedisConnection.class, "jedis");
        ReflectionUtils.makeAccessible(jedisField );
        Jedis jedis = (Jedis) ReflectionUtils.getField(jedisField, redisTemplate.getConnectionFactory().getConnection());
        return jedis;
    }


}


