package com.xyl.plugin.hook;

import android.app.Instrumentation;
import android.content.Context;

import com.xyl.plugin.ReflectionUtil;

import java.lang.reflect.Proxy;

public class BaseHookDelegate implements IPluginHook {

    protected Object currentThread;//当前ActivityThread对象
    protected Instrumentation mInstrumentation;

    @Override
    public void hook(Context context) throws Exception {//hook pms
        //获取当前ActivityThread对象
        Object currentThread = getCurrentThread(context);
        Object pm = ReflectionUtil.with(currentThread).invokeMethod("getPackageManager");
        //获取activityThread的pmg
        //getPackageManager
        Class ipm = ReflectionUtil.loadClass("android.content.pm.IPackageManager");
        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new
                Class[]{ipm}, new IPMProxy(pm));
        ReflectionUtil.with(currentThread).field("sPackageManager").set(proxy);
    }

    @Override
    public Instrumentation getInstrumentation() throws Exception {
        if (mInstrumentation == null) {
            mInstrumentation = (Instrumentation) ReflectionUtil.with(currentThread).field
                    ("mInstrumentation").get();
        }
        return mInstrumentation;
    }

    protected Object getCurrentThread(Context context) throws Exception {
        if (currentThread == null) {
            return currentThread = ReflectionUtil.with(context).field("mMainThread").get();
        }
        return currentThread;
    }
}
