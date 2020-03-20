package com.xyl.plugin.core;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.xyl.plugin.ApkUtils;
import com.xyl.plugin.PluginManager;
import com.xyl.plugin.ReflectionUtil;

import java.io.File;

import dalvik.system.DexClassLoader;

public class LoadedPlugin {

    private PluginManager mPluginManager;
    private Context mPluginContext;//插件上下文环境
    private PackageManager mPackageManager;
    private PackageInfo mPackageInfo;
    private ClassLoader mClassLoader;//对应插件的类加载器
    private Resources mResources;//资源管理类
    private Application mApplication;

    private LoadedPlugin(PluginManager pluginManager, Context context, File file) throws Exception {
        PackageInfo info = ApkUtils.analyze(context, file);
        if (info == null) {
            throw new IllegalArgumentException("invalid apk file");
        }
        this.mPluginManager = pluginManager;
        mPackageInfo = info;
        mPluginContext = createPluginContext(null);
        mPackageManager = new PluginPackageManager(this);
        mClassLoader = createClassLoader(file, context.getClassLoader());
        mResources = createResources(context, file.getAbsolutePath());
        copyNativeLibs();
        mApplication = createApplication(mPackageInfo.applicationInfo.className);
    }


    public static LoadedPlugin loadPlugin(PluginManager pluginManager, Context context, File
            file) throws Exception {
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
        DexClassLoader loader = new DexClassLoader(dexFile.getAbsolutePath(), configuration
                .getOutputDir(),
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
        AssetManager assetManager = ReflectionUtil.newInstance(AssetManager.class);
        ReflectionUtil.with(assetManager).invokeMethod("addAssetPath", apk);
        Resources resources = new Resources(assetManager, hostResources.getDisplayMetrics(),
                hostResources.getConfiguration());
        return resources;
    }

    /**
     * 拷贝so包
     */
    private void copyNativeLibs() {

    }

    private Application createApplication(String appClass) throws Exception {
        if (appClass == null) {
            appClass = "android.app.Application";
        }
        Instrumentation instrumentation = mPluginManager.getInstrumentation();
        Application application = instrumentation.newApplication(mClassLoader, appClass,
                getPluginContext());
//        instrumentation.callApplicationOnCreate(application);
        return application;
    }

    /**
     * 宿主apk的运行环境
     *
     * @return
     */
    public Context getHostContext() {
        return mPluginManager.getHostContext();
    }

    public PluginContext createPluginContext(Context base) {
        if (base == null) {
            return new PluginContext(this);
        }
        return new PluginContext(this, base);
    }

    public Context getPluginContext() {
        return mPluginContext;
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

    public Application getApplication() {
        return mApplication;
    }
}
