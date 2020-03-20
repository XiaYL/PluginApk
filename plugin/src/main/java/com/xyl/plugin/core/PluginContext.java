package com.xyl.plugin.core;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

public class PluginContext extends ContextWrapper {

    private LoadedPlugin mPlugin;

    public PluginContext(LoadedPlugin plugin) {
        this(plugin, plugin.getHostContext());
    }

    public PluginContext(LoadedPlugin plugin, Context base) {
        super(base);
        this.mPlugin = plugin;
    }

    @Override
    public Resources getResources() {
        return mPlugin.getResources();
    }

    @Override
    public AssetManager getAssets() {
        return getResources().getAssets();
    }

    @Override
    public ClassLoader getClassLoader() {
        return mPlugin.getClassLoader();
    }

    @Override
    public PackageManager getPackageManager() {
        return mPlugin.getPackageManager();
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }
}
