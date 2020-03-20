package com.xyl.plugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 反射处理类
 */
public class ReflectionUtil {

    private Class mClazz;
    private Object mTarget;
    private Field mField;

    private ReflectionUtil(Class clazz) {
        this.mClazz = clazz;
    }

    public static ReflectionUtil with(Object object) {
        return with(object.getClass()).target(object);
    }

    public static ReflectionUtil with(Class clazz) {
        return new ReflectionUtil(clazz);
    }

    public static ReflectionUtil with(String classname) throws Exception {
        return with(loadClass(classname));
    }

    public static Class<?> loadClass(String classname) throws ClassNotFoundException {
        return Class.forName(classname);
    }

    public static <T> T newInstance(Class<T> tClass) throws Exception {
        return tClass.newInstance();
    }

    public ReflectionUtil field(String name) {
        try {
            mField = getField(mClazz, name);
            mField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public ReflectionUtil target(Object target) {
        this.mTarget = target;
        return this;
    }

    public Object get() throws Exception {
        return get(mTarget);
    }

    public Object get(Object target) throws Exception {
        return mField.get(target);
    }

    public void set(Object value) throws Exception {
        mField.set(mTarget, value);
    }

    public Object invokeMethod(String name, Object... objects) throws Exception {
        Class[] parameterTypes = new Class[objects.length];
        for (int i = 0; i < objects.length; i++) {
            parameterTypes[i] = objects[i].getClass();
        }
        Method method = mClazz.getDeclaredMethod(name, parameterTypes);
        return method.invoke(mTarget, objects);
    }

    private Field getField(Class clazz, String name) throws Exception {
        try {
            return clazz.getDeclaredField(name);
        } catch (Exception e) {
            while ((clazz = clazz.getSuperclass()) != null) {
                return getField(clazz, name);
            }
            throw e;
        }
    }
}
