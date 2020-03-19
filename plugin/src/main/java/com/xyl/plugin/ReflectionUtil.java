package com.xyl.plugin;

import java.lang.reflect.Field;

/**
 * 反射处理类
 */
public class ReflectionUtil {

    public static Object getField(Class clazz, Object target, String name) throws Exception {
        Field field = getField(clazz, name);
        return field.get(target);
    }

    public static Field getField(Class clazz, String name) throws Exception {
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    public static void setField(Class clazz, Object target, String name, Object value) throws Exception {
        Field field = getField(clazz, name);
        field.set(target, value);
    }
}
