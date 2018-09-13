package com.icecream.zuul.redis;

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

import javax.annotation.PostConstruct;
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
    private RedisTemplate redisTemplate;

    private static RedisHandler redisHandler;

    @PostConstruct
    public void init() {
        redisHandler = this;
        redisHandler.redisTemplate = this.redisTemplate;
    }


    public RedisConnection getConnection() {
        RedisConnectionFactory connectionFactory = redisHandler.redisTemplate.getConnectionFactory();
        RedisConnection connection = connectionFactory.getConnection();
        return connection;
    }

    /**
     * 出异常，重复操作的次数
     */
    private static Integer times = 5;


    public static double getCreateTimeScore(long date) {
        return date / 100000.0;
    }


    public static Set<String> getAllKeys() {
        return redisHandler.redisTemplate.keys("*");
    }


    public static Map<String, Object> getAllString() {
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


    public static Map<String, Set<Object>> getAllSet() {
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


    public static Map<String, Set<Object>> getAllZSetRange() {
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


    public static Map<String, Set<Object>> getAllZSetReverseRange() {
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


    public static Map<String, List<Object>> getAllList() {
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


    public static Map<String, Map<String, Object>> getAllMap() {
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


    public static void addList(String key, List<Object> objectList) {
        for (Object obj : objectList) {
            addList(key, obj);
        }
    }


    public static long addList(String key, Object obj) {
        return redisHandler.redisTemplate.boundListOps(key).rightPush(obj);
    }


    public static long addList(String key, Object... obj) {
        return redisHandler.redisTemplate.boundListOps(key).rightPushAll(obj);
    }


    public static List<Object> getList(String key, long s, long e) {
        return redisHandler.redisTemplate.boundListOps(key).range(s, e);
    }


    public static List<Object> getList(String key) {
        return redisHandler.redisTemplate.boundListOps(key).range(0, getListSize(key));
    }


    public static long getListSize(String key) {
        return redisHandler.redisTemplate.boundListOps(key).size();
    }


    public static long removeListValue(String key, Object object) {
        return redisHandler.redisTemplate.boundListOps(key).remove(0, object);
    }


    public static long removeListValue(String key, Object... objects) {
        long r = 0;
        for (Object object : objects) {
            r += removeListValue(key, object);
        }
        return r;
    }


    public static void remove(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                remove(key[0]);
            } else {
                redisHandler.redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }


    public static void removeBlear(String... blears) {
        for (String blear : blears) {
            removeBlear(blear);
        }
    }


    public static Boolean renameIfAbsent(String oldKey, String newKey) {
        return redisHandler.redisTemplate.renameIfAbsent(oldKey, newKey);
    }


    public static void removeBlear(String blear) {
        redisHandler.redisTemplate.delete(redisHandler.redisTemplate.keys(blear));
    }


    public static void removeByRegular(String... blears) {
        for (String blear : blears) {
            removeBlear(blear);
        }
    }


    public static void removeByRegular(String blear) {
        Set<String> stringSet = getAllKeys();
        for (String s : stringSet) {
            if (Pattern.compile(blear).matcher(s).matches()) {
                redisHandler.redisTemplate.delete(s);
            }
        }
    }


    public static void removeMapFieldByRegular(String key, String... blears) {
        for (String blear : blears) {
            removeMapFieldByRegular(key, blear);
        }
    }


    public static void removeMapFieldByRegular(String key, String blear) {
        Map<String, Object> map = getMap(key);
        Set<String> stringSet = map.keySet();
        for (String s : stringSet) {
            if (Pattern.compile(blear).matcher(s).matches()) {
                redisHandler.redisTemplate.boundHashOps(key).delete(s);
            }
        }
    }


    public static Long removeZSetValue(String key, Object... value) {
        return redisHandler.redisTemplate.boundZSetOps(key).remove(value);
    }


    public static void removeZSet(String key) {
        removeZSetRange(key, 0L, getZSetSize(key));
    }


    public static void removeZSetRange(String key, Long start, Long end) {
        redisHandler.redisTemplate.boundZSetOps(key).removeRange(start, end);
    }


    public static void setZSetUnionAndStore(String key, String key1, String key2) {
        redisHandler.redisTemplate.boundZSetOps(key).unionAndStore(key1, key2);
    }


    public static Set<Object> getZSetRange(String key) {
        return getZSetRange(key, 0, getZSetSize(key));
    }


    public static Set<Object> getZSetRange(String key, long s, long e) {
        return redisHandler.redisTemplate.boundZSetOps(key).range(s, e);
    }


    public static Set<Object> getZSetReverseRange(String key) {
        return getZSetReverseRange(key, 0, getZSetSize(key));
    }


    public static Set<Object> getZSetReverseRange(String key, long start, long end) {
        return redisHandler.redisTemplate.boundZSetOps(key).reverseRange(start, end);
    }


    public static Set<Object> getZSetRangeByScore(String key, double start, double end) {
        return redisHandler.redisTemplate.boundZSetOps(key).rangeByScore(start, end);
    }


    public static Set<Object> getZSetReverseRangeByScore(String key, double start, double end) {
        return redisHandler.redisTemplate.boundZSetOps(key).reverseRangeByScore(start, end);
    }


    public static Set<ZSetOperations.TypedTuple<Object>> getZSetRangeWithScores(String key, long start, long end) {
        return redisHandler.redisTemplate.boundZSetOps(key).rangeWithScores(start, end);
    }


    public static Set<ZSetOperations.TypedTuple<Object>> getZSetReverseRangeWithScores(String key, long start, long end) {
        return redisHandler.redisTemplate.boundZSetOps(key).reverseRangeWithScores(start, end);
    }


    public static Set<ZSetOperations.TypedTuple<Object>> getZSetRangeWithScores(String key) {
        return getZSetRangeWithScores(key, 0, getZSetSize(key));
    }


    public static Set<ZSetOperations.TypedTuple<Object>> getZSetReverseRangeWithScores(String key) {
        return getZSetReverseRangeWithScores(key, 0, getZSetSize(key));
    }


    public static long getZSetCountSize(String key, double sMin, double sMax) {
        return redisHandler.redisTemplate.boundZSetOps(key).count(sMin, sMax);
    }


    public static long getZSetSize(String key) {
        return redisHandler.redisTemplate.boundZSetOps(key).size();
    }


    public static double getZSetScore(String key, Object value) {
        return redisHandler.redisTemplate.boundZSetOps(key).score(value);
    }


    public static double incrementZSetScore(String key, Object value, double delta) {
        return redisHandler.redisTemplate.boundZSetOps(key).incrementScore(value, delta);
    }


    public static Boolean addZSet(String key, double score, Object value) {
        return redisHandler.redisTemplate.boundZSetOps(key).add(value, score);
    }


    public static Long addZSet(String key, TreeSet<Object> value) {
        return redisHandler.redisTemplate.boundZSetOps(key).add(value);
    }


    public static Boolean addZSet(String key, double[] score, Object[] value) {
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


    public static void remove(String key) {
        if (exists(key)) {
            redisHandler.redisTemplate.delete(key);
        }
    }


    public static void removeZSetRangeByScore(String key, double s, double e) {
        redisHandler.redisTemplate.boundZSetOps(key).removeRangeByScore(s, e);
    }


    public static Boolean setSetExpireTime(String key, Long time) {
        return redisHandler.redisTemplate.boundSetOps(key).expire(time, TimeUnit.SECONDS);
    }


    public static Boolean setZSetExpireTime(String key, Long time) {
        return redisHandler.redisTemplate.boundZSetOps(key).expire(time, TimeUnit.SECONDS);
    }


    public static boolean exists(String key) {
        return redisHandler.redisTemplate.hasKey(key);
    }

    public static Object get(int key) {
        return get(String.valueOf(key));
    }

    public static Object get(long key) {
        return get(String.valueOf(key));
    }

    public static Object get(String key) {
        return redisHandler.redisTemplate.boundValueOps(key).get();
    }


    public static List<Object> get(String... keys) {
        List<Object> list = new ArrayList<Object>();
        for (String key : keys) {
            list.add(get(key));
        }
        return list;
    }


    public static List<Object> getByRegular(String regKey) {
        Set<String> stringSet = getAllKeys();
        List<Object> objectList = new ArrayList<Object>();
        for (String s : stringSet) {
            if (Pattern.compile(regKey).matcher(s).matches() && getType(s) == DataType.STRING) {
                objectList.add(get(s));
            }
        }
        return objectList;
    }


    public static void set(long key, Object value) {
        set(String.valueOf(key), value);
    }

    public static void set(int key, Object value) {
        set(String.valueOf(key), value);
    }


    public static void set(String key, Object value) {
        redisHandler.redisTemplate.boundValueOps(key).set(value);
    }


    public static void set(String key, Object value, Long expireTime) {
        redisHandler.redisTemplate.boundValueOps(key).set(value, expireTime, TimeUnit.SECONDS);
    }


    public static boolean setExpireTime(String key, long expireTime, TimeUnit timeUnit) {
        return redisHandler.redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }


    public static DataType getType(String key) {
        return redisHandler.redisTemplate.type(key);
    }


    public static void removeMapField(String key, Object... field) {
        redisHandler.redisTemplate.boundHashOps(key).delete(field);
    }


    public static Long getMapSize(String key) {
        return redisHandler.redisTemplate.boundHashOps(key).size();
    }


    public static Map<String, Object> getMap(String key) {
        return redisHandler.redisTemplate.boundHashOps(key).entries();
    }


    public static <T> T getMapField(String key, String field) {
        return (T) redisHandler.redisTemplate.boundHashOps(key).get(field);
    }

    public static <K, V> void set(K key, V value) {
        JSONObject jsonObject = (JSONObject) JSON.toJSON(value);
        set(String.valueOf(key), value);
    }


    public static Boolean hasMapKey(String key, String field) {
        return redisHandler.redisTemplate.boundHashOps(key).hasKey(field);
    }


    public static List<Object> getMapFieldValue(String key) {
        return redisHandler.redisTemplate.boundHashOps(key).values();
    }


    public static Set<Object> getMapFieldKey(String key) {
        return redisHandler.redisTemplate.boundHashOps(key).keys();
    }


    public static void addMap(String key, Map<String, Object> map) {
        redisHandler.redisTemplate.boundHashOps(key).putAll(map);
    }


    public static void addMap(String key, String field, Object value) {
        redisHandler.redisTemplate.boundHashOps(key).put(field, value);
    }


    public static void addMap(String key, String field, Object value, long time) {
        redisHandler.redisTemplate.boundHashOps(key).put(field, value);
        redisHandler.redisTemplate.boundHashOps(key).expire(time, TimeUnit.SECONDS);
    }


    public static void watch(String key) {
        redisHandler.redisTemplate.watch(key);
    }


    public static void addSet(String key, Object... obj) {
        redisHandler.redisTemplate.boundSetOps(key).add(obj);
    }


    public static long removeSetValue(String key, Object obj) {
        return redisHandler.redisTemplate.boundSetOps(key).remove(obj);
    }


    public static long removeSetValue(String key, Object... obj) {
        if (obj != null && obj.length > 0) {
            return redisHandler.redisTemplate.boundSetOps(key).remove(obj);
        }
        return 0L;
    }


    public static long getSetSize(String key) {
        return redisHandler.redisTemplate.boundSetOps(key).size();
    }


    public static Boolean hasSetValue(String key, Object obj) {
        Boolean boo = null;
        int t = 0;
        while (true) {
            try {
                boo = redisHandler.redisTemplate.boundSetOps(key).isMember(obj);
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


    public static Set<Object> getSet(String key) {
        return redisHandler.redisTemplate.boundSetOps(key).members();
    }


    public static Set<Object> getSetUnion(String key, String otherKey) {
        return redisHandler.redisTemplate.boundSetOps(key).union(otherKey);
    }


    public static Set<Object> getSetUnion(String key, Set<Object> set) {
        return redisHandler.redisTemplate.boundSetOps(key).union(set);
    }


    public static Set<Object> getSetIntersect(String key, String otherKey) {
        return redisHandler.redisTemplate.boundSetOps(key).intersect(otherKey);
    }


    public static Set<Object> getSetIntersect(String key, Set<Object> set) {
        return redisHandler.redisTemplate.boundSetOps(key).intersect(set);
    }


    //递减
    public static Long decr(String key, long reduce) {
        if (reduce < 0) {
            throw new RuntimeException("递减因子不能为0");
        }
        return redisHandler.redisTemplate.opsForValue().increment(key, -reduce);
    }

    public static Long incr(String key, long increment) {
        if (increment < 0) {
            throw new RuntimeException("递增因子不能为0");
        }

        return redisHandler.redisTemplate.opsForValue().increment(key, increment);
    }

    /**
     * 加锁
     *
     * @param lockName       锁的key
     * @param acquireTimeout 获取超时时间(s)
     * @param timeout        锁的超时时间(ms)
     * @return 锁标识
     */

    public static String lockWithTimeout(String lockName, long acquireTimeout, long timeout) {
        String retIdentifier = null;
        try {
            // 获取连接
            // 随机生成一个value
            String identifier = UUID.randomUUID().toString();
            // 锁名，即key值
            String lockKey = "lock:" + lockName;
            // 超时时间，上锁后超过此时间则自动释放锁
            int lockExpire = (int) (timeout / 1000);
            // 获取锁的超时时间，超过这个时间则放弃获取锁
            while (0 < acquireTimeout) {
                if (redisHandler.redisTemplate.opsForValue().setIfAbsent(lockKey, identifier)) {
                    redisHandler.redisTemplate.expire(lockKey, lockExpire, TimeUnit.SECONDS);
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

    public static void releaseLock(String lockName, String identifier) {
        String lockKey = "lock:" + lockName;
        String value = RedisHandler.get(lockKey).toString();
        if (value.equals(identifier)) {
            RedisHandler.remove(lockKey);
        }
    }

    /**
     * 获取jedis对象
     *
     * @return
     */
    public static Jedis getJedis() {
        Field jedisField = ReflectionUtils.findField(JedisConnection.class, "jedis");
        ReflectionUtils.makeAccessible(jedisField);
        Jedis jedis = (Jedis) ReflectionUtils.getField(jedisField, redisHandler.redisTemplate.getConnectionFactory().getConnection());
        return jedis;
    }



}

