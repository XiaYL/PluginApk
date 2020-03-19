package com.xyl.plugin;

import android.content.Context;

import com.xyl.plugin.ams.AMSHookDelegate;
import com.xyl.plugin.core.IPluginHook;
import com.xyl.plugin.core.LoadedPlugin;
import com.xyl.plugin.core.PluginConfiguration;
import com.xyl.plugin.instrumentation.INSHookDelegate;

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
    private PluginConfiguration mConfiguration;

    private Map<String, LoadedPlugin> loadedPluginCaches = new HashMap<>();//已经加载过的插件缓存

    private PluginManager(int mode) {
        if (mode == IPluginHook.AMS) {
            pluginHook = new AMSHookDelegate();
        } else if (mode == IPluginHook.INS) {
            pluginHook = new INSHookDelegate();
        } else {
            throw new IllegalArgumentException("hook mode invalid");
        }
        mConfiguration = new PluginConfiguration.Builder()
                .outputDir("")
                .libDir("")
                .build();
    }

    public static PluginManager getInstance(@IPluginHook.HookMode int mode) {
        if (instance == null) {
            synchronized (PluginManager.class) {
                if (instance == null) {
                    instance = new PluginManager(mode);
                }
            }
        }
        return instance;
    }

    public void attach(Context context) throws Exception {
        this.mContext = context;
        pluginHook.hook(context);
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
        LoadedPlugin loadedPlugin = loadedPluginCaches.get(file.getAbsolutePath());
        if (loadedPlugin == null) {
            loadedPlugin = LoadedPlugin.loadPlugin(this, mContext, file);
            loadedPluginCaches.put(file.getAbsolutePath(), loadedPlugin);
        }
    }
}
