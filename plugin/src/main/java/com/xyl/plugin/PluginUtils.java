package com.xyl.plugin;

import android.content.ComponentName;
import android.content.Intent;

import com.xyl.plugin.core.LoadedPlugin;

/**
 * author xiayanlei
 * date 2020/3/19
 */
public class PluginUtils {

    public static void isPluginIntent(Intent intent) {
        ComponentName component = intent.getComponent();
        LoadedPlugin plugin = PluginManager.getInstance().getPlugin(component.getPackageName());
        if (plugin == null) {

        }
    }
}
