package com.xyl.plugin.hook.instrumentation;

import android.app.Instrumentation;
import android.content.Context;

import com.xyl.plugin.hook.BaseHookDelegate;
import com.xyl.plugin.ReflectionUtil;

import java.lang.reflect.Field;

public class INSHookDelegate extends BaseHookDelegate {

    @Override
    public void hook(Context context) throws Exception {
        super.hook(context);
        hookInstrumentation(context);
    }

    private static void hookInstrumentation(Context context) throws Exception {
        //获取当前ActivityThread对象
        Object currentThread = ReflectionUtil.getField(context.getClass(), context, "mMainThread");
        //mInstrumentation
        Field instrumentFiled = ReflectionUtil.getField(currentThread.getClass(), "mInstrumentation");
        Instrumentation instrumentation = (Instrumentation) instrumentFiled.get(currentThread);
        InstrumentationProxy proxy = new InstrumentationProxy(instrumentation);
        instrumentFiled.set(currentThread, proxy);
    }
}
