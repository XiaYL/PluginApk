package com.xyl.plugin.core;

import android.content.ComponentName;

import com.xyl.plugin.PluginManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * packagemanager代理类
 */
public class IPMProxy implements InvocationHandler {

    private static final String TAG = "IPMProxy";

    private Object ipm;//包管理类

    public IPMProxy(Object ipm) {
        this.ipm = ipm;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = method.invoke(ipm, args);
        if ("getActivityInfo".equals(method.getName()) && result == null) {//目标activity没有注册
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof ComponentName) {
                    ComponentName oldComponent = (ComponentName) args[i];
                    ComponentName targetComponent = new ComponentName(oldComponent.getPackageName(),
                            PluginManager.PLUGIN_PROXY_ACTIVITY);
                    if (targetComponent.equals(oldComponent)) {//插件activity没有注册
                        return null;
                    } else {
                        args[i] = targetComponent;
                    }
                }
            }
            return method.invoke(ipm, args);
        }
        return result;
    }
}
