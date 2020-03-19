package com.xyl.plugin.hook.ams;

import android.content.Intent;

import com.xyl.plugin.PluginManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class IActivityManagerProxy implements InvocationHandler {

    private Object am;//activity manager
    private String packageName;

    public IActivityManagerProxy(Object am, String packageName) {
        this.am = am;
        this.packageName = packageName;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if ("startActivity".equals(method.getName())) {//1
            Intent intent = null;
            int index = 0;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    index = i;
                    break;
                }
            }
            intent = (Intent) args[index];
            Intent subIntent = new Intent();//2
            subIntent.setClassName(packageName, PluginManager.PLUGIN_PROXY_ACTIVITY);//3 替换为插件activity
            subIntent.putExtra(PluginManager.TARGET_INTENT, intent);//4
            args[index] = subIntent;//5
        }
        return method.invoke(am, args);
    }
}
