package com.icecream.common.util.bean;

import com.icecream.common.model.pojo.Good;
import com.icecream.common.model.pojo.User;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr_h
 * @version 1.0
 * description: 注入bean属性
 * create by Mr_h on 2018/7/12 0012
 */

@Slf4j
public class BeanPropertyFactory{

    public static<T> T createSqlBean(Class<T> consumer, Object...objects) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        T value = consumer.newInstance();
        Map<String,String> map = new HashMap<>();
        for (int i=0;i<objects.length;i++){
            if(i%2==0){
                map.put(""+objects[i],"");
            }else {
                map.put(""+objects[i-1],""+objects[i]);
            }
        }
        Method[] methods = value.getClass().getMethods();
        for(Map.Entry<String, String> vo : map.entrySet()){
            for (Method method:methods) {
                if(("set"+vo.getKey()).toLowerCase().equals(method.getName().toLowerCase())){
                    method.invoke(value,vo.getValue());
                }
            }
        }
        return value;
    }

}
