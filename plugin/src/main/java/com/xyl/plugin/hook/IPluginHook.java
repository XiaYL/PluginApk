package com.xyl.plugin.hook;

import android.app.Instrumentation;
import android.content.Context;

import androidx.annotation.IntDef;

public interface IPluginHook {

    String TAG = "IPluginHook";

    int AMS = 0x01;
    int INS = 0x02;

    @IntDef({AMS, INS})
    @interface HookMode {

    }

    void hook(Context context) throws Exception;

    Instrumentation getInstrumentation() throws Exception;
}
