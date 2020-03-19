package com.xyl.plugin;

import android.content.Context;
import android.text.TextUtils;

import com.xyl.plugin.core.IntentHandler;
import com.xyl.plugin.core.LoadedPlugin;
import com.xyl.plugin.core.PluginConfiguration;
import com.xyl.plugin.hook.IPluginHook;
import com.xyl.plugin.hook.ams.AMSHookDelegate;
import com.xyl.plugin.hook.instrumentation.INSHookDelegate;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * 插件hook类
 */
public class PluginManager {

    private static final String TAG = "PluginManager";
    public static final String PLUGIN_PROXY_ACTIVITY = PluginProxyActivity.class.getName();
    public static final String TARGET_INTENT = "target_intent";
    public static final String TARGET_ACTIVITY_NAME = "target_activity_name";

    private static volatile PluginManager instance;
    private Context mContext;
    private IPluginHook pluginHook;
    private IntentHandler mIntentHandler;
    private PluginConfiguration mConfiguration;

    private Map<String, LoadedPlugin> loadedPluginCaches = new HashMap<>();//已经加载过的插件缓存

    private PluginManager() {
        mConfiguration = new PluginConfiguration.Builder()
                .outputDir("")
                .libDir("")
                .build();
    }

    public static PluginManager getInstance() {
        if (instance == null) {
            synchronized (PluginManager.class) {
                if (instance == null) {
                    instance = new PluginManager();
                }
            }
        }
        return instance;
    }

    public void attach(Context context, @IPluginHook.HookMode int mode) throws Exception {
        this.mContext = context;
        if (mode == IPluginHook.AMS) {
            pluginHook = new AMSHookDelegate();
        } else if (mode == IPluginHook.INS) {
            pluginHook = new INSHookDelegate();
        } else {
            throw new IllegalArgumentException("hook mode invalid");
        }
        pluginHook.hook(context);
        mIntentHandler = new IntentHandler(this);
    }

    public PluginManager setConfiguration(PluginConfiguration configuration) {
        mConfiguration = configuration;
        return this;
    }

    public PluginConfiguration getConfiguration() {
        return mConfiguration;
    }

    /**
     * @return 宿主apk的上下文环境
     */
    public Context getHostContext() {
        return mContext;
    }

    /**
     * 加载插件文件
     *
     * @param file
     */
    public void loadPlugin(File file) throws Exception {
        if (file == null) {
            throw new IllegalArgumentException("plugin file is null");
        }
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        LoadedPlugin loadedPlugin = LoadedPlugin.loadPlugin(this, mContext, file);
        loadedPluginCaches.put(file.getAbsolutePath(), loadedPlugin);//直接替换插件
    }

    public LoadedPlugin getPlugin(String packageName) {
        for (LoadedPlugin loadedPlugin : loadedPluginCaches.values()) {
            if (TextUtils.equals(packageName, loadedPlugin.getPackageInfo().packageName)) {
                return loadedPlugin;
            }
        }
        return null;
    }

    public IntentHandler getIntentHandler() {
        return mIntentHandler;
    }
}
