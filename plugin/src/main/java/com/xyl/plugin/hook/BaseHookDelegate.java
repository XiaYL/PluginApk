package com.xyl.plugin.hook;

import android.content.Context;

import com.xyl.plugin.ReflectionUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class BaseHookDelegate implements IPluginHook {

    @Override
    public void hook(Context context) throws Exception {//hook pms
        //获取当前ActivityThread对象
        Object currentThread = ReflectionUtil.getField(context.getClass(), context, "mMainThread");
        Method method = currentThread.getClass().getMethod("getPackageManager");
        Object pm = method.invoke(currentThread);//获取activityThread的pmg
        //getPackageManager
        Class ipm = Class.forName("android.content.pm.IPackageManager");
        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{ipm},
                new IPMProxy(pm));

        ReflectionUtil.setField(currentThread.getClass(), currentThread, "sPackageManager", proxy);
    }
}
