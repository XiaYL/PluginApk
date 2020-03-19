package net.luculent.plugindemo;

import android.app.Application;
import android.content.Context;

import com.xyl.plugin.PluginManager;
import com.xyl.plugin.hook.IPluginHook;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
            PluginManager.getInstance().attach(base, IPluginHook.INS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
