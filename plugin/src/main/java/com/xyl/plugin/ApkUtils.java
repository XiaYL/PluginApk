package com.xyl.plugin;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.File;

public class ApkUtils {

    private static final String TAG = "ApkUtils";

    public static PackageInfo analyze(Context context, File apk) {
        if (apk == null || !apk.exists()) {
            return null;
        }
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apk.getAbsolutePath(),
                PackageManager.GET_ACTIVITIES);
        Log.i(TAG, "analyze: " + packageInfo);
        return packageInfo;
    }
}
