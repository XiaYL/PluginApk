package com.xyl.plugin.core;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;

import com.xyl.plugin.PluginManager;

/**
 * author xiayanlei
 * date 2020/3/19
 */
public class IntentHandler {

    private PluginManager mPluginManager;
    private Context mHostContext;

    public IntentHandler(PluginManager pluginManager) {
        this.mPluginManager = pluginManager;
        mHostContext = pluginManager.getHostContext();
    }


    /**
     * 替换成已经在宿主apk中注册的PluginProxyActivity
     *
     * @param intent
     * @return
     */
    public Intent transformPluginIntentIfNeed(Intent intent) {
        if (!isFromHostIntent(intent)) {//在插件apk中
            LoadedPlugin plugin = mPluginManager.getPlugin(intent.getComponent().getPackageName());
            if (plugin != null) {//找到对应的插件，在插件apk中查找匹配的活动
            }
        }
        return intent;
    }

    public boolean isFromHostIntent(Intent intent) {
        ComponentName component = intent.getComponent();
        if (component != null && component.getPackageName().equals(mHostContext.getPackageName())) {
            ResolveInfo resolveInfo = mHostContext.getPackageManager().resolveActivity(intent, 0);
            if (resolveInfo != null && resolveInfo.activityInfo != null) {
                return true;
            }
        }
        return false;
    }
}
