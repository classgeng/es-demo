/*
 * Copyright(C) 2019 FUYUN DATA SERVICES CO.,LTD. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 该源代码版权归属福韵数据服务有限公司所有
 * 未经授权，任何人不得复制、泄露、转载、使用，否则将视为侵权
 */

package com.fydata.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TypeUtil {
    public static Type getSuperclassTypeParameter(Class<?> clazz) {
        return getSuperclassTypeParameter(clazz, clazz);
    }

    private static Type getSuperclassTypeParameter(Class<?> oriClazz, Class<?> clazz) {
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass == null) {
            return null;
        }
        if (genericSuperclass instanceof Class) {
            if (oriClazz != genericSuperclass) {
                return getSuperclassTypeParameter(oriClazz, clazz.getSuperclass());
            }
            throw new RuntimeException("'" + oriClazz + "' misses the type parameter. "
                    + "Remove the extension or add a type parameter to it.");
        }

        Type rawType = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
        if (rawType instanceof ParameterizedType) {
            rawType = ((ParameterizedType) rawType).getRawType();
        }
        return rawType;
    }

    public static Class<?> extractTypeParameter(Class<?> clazz) {
        return extractTypeParameter(clazz, clazz);
    }

    private static Class<?> extractTypeParameter(Class<?> c, Class<?> oriClass) {
        if(c == null){
            String message = "Can not find parametirized type in {}...";
            throw new IllegalArgumentException(String.format(message, oriClass));
        }
        for (Type t : c.getGenericInterfaces()){
            if(t instanceof ParameterizedType){
                ParameterizedType type = (ParameterizedType) t;
                if(oriClass.equals(type.getRawType())){
                    return  (Class<?>) type.getActualTypeArguments()[0];
                }
            }
        }
        return extractTypeParameter(c.getSuperclass(), oriClass);
    }

    public static void main(String[] args) {
        ConcurrentHashMap<String,String> concurrentHashMap = new ConcurrentHashMap<>();
        concurrentHashMap.put("aa","11");
        concurrentHashMap.put("bb","22");
        concurrentHashMap.put("cc","33");

        Iterator<Map.Entry<String, String>> iterator = concurrentHashMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, String> item = iterator.next();
            System.out.println(item);
            concurrentHashMap.put("dd","444");
            concurrentHashMap.remove("bb");
        }

    }

}
