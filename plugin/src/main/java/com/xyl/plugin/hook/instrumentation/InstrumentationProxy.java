package com.xyl.plugin.hook.instrumentation;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

import com.xyl.plugin.PluginManager;

import java.lang.reflect.Method;

/**
 * Instrumentation 代理类,此类的调用时机在ams调用start方法之前
 */
public class InstrumentationProxy extends Instrumentation {

    private Instrumentation mInstrumentation;
    private Method startMethod;//获取启动activity的方法

    public InstrumentationProxy(Instrumentation instrumentation) {
        this.mInstrumentation = instrumentation;
    }

    /**
     * 重写instrument的此方法
     *
     * @param who
     * @param contextThread
     * @param token
     * @param target
     * @param intent
     * @param requestCode
     * @param options
     * @return
     */
    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target,
                                            Intent intent, int requestCode, Bundle options) {

        intent.putExtra(PluginManager.TARGET_ACTIVITY_NAME, intent.getComponent().getClassName());//1
        intent.setClassName(who, PluginManager.PLUGIN_PROXY_ACTIVITY);//2
        try {
            Method execMethod = getStartMethod();
            return (ActivityResult) execMethod.invoke(mInstrumentation, who, contextThread, token, target, intent,
                    requestCode, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws ClassNotFoundException,
            IllegalAccessException, InstantiationException {
        String targetClassname = intent.getStringExtra(PluginManager.TARGET_ACTIVITY_NAME);
        className = TextUtils.isEmpty(targetClassname) ? className : targetClassname;
        return mInstrumentation.newActivity(cl, className, intent);
    }

    private Method getStartMethod() throws NoSuchMethodException {
        if (startMethod == null) {
            startMethod = Instrumentation.class.getDeclaredMethod("execStartActivity",
                    Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, int.class, Bundle.class);
        }
        return startMethod;
    }

    private void injectPlugin() {

    }
}
