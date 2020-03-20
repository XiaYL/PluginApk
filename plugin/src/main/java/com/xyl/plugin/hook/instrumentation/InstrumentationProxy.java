package com.xyl.plugin.hook.instrumentation;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;

import com.xyl.plugin.PluginManager;
import com.xyl.plugin.ReflectionUtil;
import com.xyl.plugin.core.LoadedPlugin;

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
    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token,
                                            Activity target,
                                            Intent intent, int requestCode, Bundle options) {

        Intent proxyIntent = new Intent(intent);
        proxyIntent.putExtra(PluginManager.TARGET_INTENT, intent);
        proxyIntent.setClassName(who, PluginManager.PLUGIN_PROXY_ACTIVITY);//2替换为PluginActivity
        try {
            Method execMethod = getStartMethod();
            return (ActivityResult) execMethod.invoke(mInstrumentation, who, contextThread,
                    token, target, proxyIntent,
                    requestCode, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws
            ClassNotFoundException, IllegalAccessException, InstantiationException {
        Intent targetIntent = intent.getParcelableExtra(PluginManager.TARGET_INTENT);
        //真正需要跳转的activity
        if (targetIntent != null) {
            ComponentName targetComponent = targetIntent.getComponent();
            if (targetComponent != null) {
                String targetClass = targetComponent.getClassName();
                try {
                    return mInstrumentation.newActivity(cl, targetClass, targetIntent);
                } catch (Exception e) {
                    Activity pluginActivity = newActivityFromPlugin(targetIntent);
                    if (pluginActivity != null) {
                        return pluginActivity;
                    }
                }
            }
        }
        return mInstrumentation.newActivity(cl, className, intent);//都没有找到，直接跳转到插件PluginActivity
    }


    @Override
    public Application newApplication(ClassLoader cl, String className, Context context) throws
            ClassNotFoundException, IllegalAccessException, InstantiationException {
        return mInstrumentation.newApplication(cl, className, context);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        hookActivity(activity);
        mInstrumentation.callActivityOnCreate(activity, icicle);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle, PersistableBundle
            persistentState) {
        hookActivity(activity);
        mInstrumentation.callActivityOnCreate(activity, icicle, persistentState);
    }

    /**
     * 加载插件activity
     *
     * @param targetIntent
     * @return
     */
    private Activity newActivityFromPlugin(Intent targetIntent) {
        ComponentName targetComponent = targetIntent.getComponent();
        String targetClass = targetComponent.getClassName();
        LoadedPlugin plugin = PluginManager.getInstance().getPlugin(targetComponent
                .getPackageName());
        if (plugin != null) {
            try {
                Activity activity = mInstrumentation.newActivity(plugin.getClassLoader(),
                        targetClass, targetIntent);
                if (activity != null) {//hook插件中的mResources
                    targetIntent.putExtra(PluginManager.PLUGIN_FLAG, true);
                }
                return activity;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 替换插件activity中的context，resources，application
     *
     * @param activity
     */
    private void hookActivity(Activity activity) {
        Intent intent = activity.getIntent();
        Intent targetIntent = intent.getParcelableExtra(PluginManager.TARGET_INTENT);
        if (targetIntent != null && targetIntent.getBooleanExtra(PluginManager.PLUGIN_FLAG,
                false)) {
            try {
                LoadedPlugin plugin = PluginManager.getInstance().getPlugin(targetIntent
                        .getComponent().getPackageName());
                ReflectionUtil reflect = ReflectionUtil.with(activity);
                reflect.field("mResources").set(plugin.getResources());
                reflect.field("mBase").set(plugin.createPluginContext(activity.getBaseContext()));
                reflect.field("mApplication").set(plugin.getApplication());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Method getStartMethod() throws NoSuchMethodException {
        if (startMethod == null) {
            startMethod = Instrumentation.class.getDeclaredMethod("execStartActivity",
                    Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class,
                    int.class, Bundle.class);
        }
        return startMethod;
    }
}
