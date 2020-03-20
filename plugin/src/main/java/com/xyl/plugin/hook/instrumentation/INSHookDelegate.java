package com.xyl.plugin.hook.instrumentation;

import android.content.Context;

import com.xyl.plugin.ReflectionUtil;
import com.xyl.plugin.hook.BaseHookDelegate;

public class INSHookDelegate extends BaseHookDelegate {

    @Override
    public void hook(Context context) throws Exception {
        super.hook(context);
        hookInstrumentation(context);
    }

    private void hookInstrumentation(Context context) throws Exception {
        //获取当前ActivityThread对象
        Object currentThread = getCurrentThread(context);
        //mInstrumentation
        InstrumentationProxy proxy = new InstrumentationProxy(getInstrumentation());
        ReflectionUtil.with(currentThread).field("mInstrumentation").set(proxy);
    }
}
