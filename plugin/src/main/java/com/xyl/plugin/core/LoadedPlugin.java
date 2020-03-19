package com.xyl.plugin.core;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.xyl.plugin.ApkUtils;
import com.xyl.plugin.PluginManager;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class LoadedPlugin {

    private PluginManager mPluginManager;
    private Context mContext;//插件上下文环境
    private PackageManager mPackageManager;
    private ClassLoader mClassLoader;//对应插件的类加载器
    private Resources mResources;//资源管理类
    private PackageInfo mPackageInfo;

    private LoadedPlugin(PluginManager pluginManager, Context context, File file) throws Exception {
        PackageInfo info = ApkUtils.analyze(context, file);
        if (info == null) {
            throw new IllegalArgumentException("invalid apk file");
        }
        this.mPluginManager = pluginManager;
        mPackageInfo = info;
        mContext = new PluginContext(this);
        mPackageManager = new PluginPackageManager(this);
        mClassLoader = createClassLoader(file, context.getClassLoader());
        mResources = createResources(context, file.getAbsolutePath());
    }


    public static LoadedPlugin loadPlugin(PluginManager pluginManager, Context context, File file) throws Exception {
        return new LoadedPlugin(pluginManager, context, file);
    }

    /**
     * 插件apk的类加载器
     *
     * @param dexFile
     * @param parent
     * @return
     */
    private DexClassLoader createClassLoader(File dexFile, ClassLoader parent) {
        PluginConfiguration configuration = mPluginManager.getConfiguration();
        DexClassLoader loader = new DexClassLoader(dexFile.getAbsolutePath(), configuration.getOutputDir(),
                configuration.getLibDir(), parent);
        return loader;
    }

    /**
     * 插件apk的资源管理器
     *
     * @param hostContext
     * @param apk
     * @return
     * @throws Exception
     */
    private Resources createResources(Context hostContext, String apk) throws Exception {
        Resources hostResources = hostContext.getResources();//宿主apk的资源加载器
        AssetManager assetManager = AssetManager.class.newInstance();
        Method method = AssetManager.class.getMethod("addAssetPath", String.class);
        method.setAccessible(true);
        method.invoke(assetManager, apk);
        Resources resources = new Resources(assetManager, hostResources.getDisplayMetrics(),
                hostResources.getConfiguration());
        return resources;
    }

    /**
     * 宿主apk的运行环境
     *
     * @return
     */
    public Context getHostContext() {
        return mPluginManager.getHostContext();
    }

    public ClassLoader getClassLoader() {
        return mClassLoader;
    }

    public PackageManager getPackageManager() {
        return mPackageManager;
    }

    public Resources getResources() {
        return mResources;
    }

    public PackageInfo getPackageInfo() {
        return mPackageInfo;
    }
}
