package com.xyl.plugin;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.File;

public class ApkUtils {

    private static final String TAG = "ApkUtils";

    public static void analyze(Context context, File apk) {
        if (apk == null || !apk.exists()) {
            Log.e(TAG, "invalid apk");
            return;
        }
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apk.getAbsolutePath(),
                PackageManager.GET_ACTIVITIES);
        if (packageInfo != null) {
            Log.i(TAG, "analyze: " + packageInfo);
        }
    }
}
