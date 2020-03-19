package com.xyl.plugin.hook.ams;

import android.content.Context;
import android.os.Build;
import android.os.Handler;

import com.xyl.plugin.ReflectionUtil;
import com.xyl.plugin.hook.BaseHookDelegate;

import java.lang.reflect.Method;
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
    private static void hookAMS(Context context) throws Exception {
        Object amSingleton;
        Class targetClazz;
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.Q) {
            Class atmClazz = Class.forName("android.app.ActivityTaskManager");
            amSingleton = ReflectionUtil.getField(atmClazz, null, "IActivityTaskManagerSingleton");
            targetClazz = Class.forName("android.app.IActivityTaskManager");
        } else if (sdkInt >= Build.VERSION_CODES.O) {
            Class amClazz = Class.forName("android.app.ActivityManager");
            amSingleton = ReflectionUtil.getField(amClazz, null, "IActivityManagerSingleton");
            targetClazz = Class.forName("android.app.IActivityManager");
        } else {
            Class amnClazz = Class.forName("android.app.ActivityManagerNative");
            amSingleton = ReflectionUtil.getField(amnClazz, null, "gDefault");
            targetClazz = Class.forName("android.app.IActivityManager");
        }

        Class singleton = Class.forName("android.util.Singleton");
        Method gMethod = singleton.getDeclaredMethod("get");
        Object target = gMethod.invoke(amSingleton);//通过get方法获取mInstance对象

        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{targetClazz}, new IActivityManagerProxy(target, context.getPackageName()));
        ReflectionUtil.setField(singleton, amSingleton, "mInstance", proxy);
    }

    private static void hookHandler(Context context) throws Exception {
        //获取当前ActivityThread对象
        Object currentThread = ReflectionUtil.getField(context.getClass(), context, "mMainThread");
        Handler mH = (Handler) ReflectionUtil.getField(currentThread.getClass(), currentThread, "mH");//获取handler对象
        ReflectionUtil.setField(Handler.class, mH, "mCallback", new HCallback(mH));
    }
}
