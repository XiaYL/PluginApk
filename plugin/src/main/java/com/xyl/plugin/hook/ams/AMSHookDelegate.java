package com.xyl.plugin.hook.ams;

import android.content.Context;
import android.os.Build;
import android.os.Handler;

import com.xyl.plugin.ReflectionUtil;
import com.xyl.plugin.hook.BaseHookDelegate;

import java.lang.reflect.Proxy;

public class AMSHookDelegate extends BaseHookDelegate {

    @Override
    public void hook(Context context) throws Exception {
        super.hook(context);
        hookAMS(context);
        hookHandler(context);
    }

    /**
     * 10.0开始,ams通过 ActivityTaskManager#IActivityTaskManagerSingleton.get(),ActivityTaskManagerService;
     * 8.0以后,ams通过 AvtivityManager#IActivityManagerSingleton.get();
     * 8.0之前,ams通过 ActivityManagerNative#gDefault.get();
     *
     * @param context
     */
    private void hookAMS(Context context) throws Exception {
        Object amSingleton;
        Class targetClazz;
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.Q) {
            amSingleton = ReflectionUtil.with("android.app.ActivityTaskManager").field
                    ("IActivityTaskManagerSingleton").get();
            targetClazz = ReflectionUtil.loadClass("android.app.IActivityTaskManager");
        } else if (sdkInt >= Build.VERSION_CODES.O) {
            amSingleton = ReflectionUtil.with("android.app.ActivityManager").field("IActivityManagerSingleton").get();
            targetClazz = ReflectionUtil.loadClass("android.app.IActivityManager");
        } else {
            amSingleton = ReflectionUtil.with("android.app.ActivityManagerNative").field("gDefault").get();
            targetClazz = ReflectionUtil.loadClass("android.app.IActivityManager");
        }

        //获取mInstance对象
        Object target = ReflectionUtil.with(amSingleton).invokeMethod("get");

        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{targetClazz}, new IActivityManagerProxy(target, context.getPackageName()));
        ReflectionUtil.with(amSingleton).field("mInstance").set(proxy);
    }

    private void hookHandler(Context context) throws Exception {
        //获取当前ActivityThread对象
        Object currentThread = getCurrentThread(context);
        Handler mH = (Handler) ReflectionUtil.with(currentThread).field("mH").get();//获取handler对象
        ReflectionUtil.with(mH).field("mCallback").set(new HCallback(mH));
    }
}
