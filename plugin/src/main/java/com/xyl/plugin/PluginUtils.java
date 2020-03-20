package com.xyl.plugin;

import android.content.Context;

import java.io.File;

/**
 * author xiayanlei
 * date 2020/3/19
 */
public class PluginUtils {

    public static File getDir(Context context, String name) {
        return context.getDir(name, Context.MODE_PRIVATE);
    }
}
