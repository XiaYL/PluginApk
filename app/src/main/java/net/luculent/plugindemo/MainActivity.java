package net.luculent.plugindemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.xyl.plugin.ApkUtils;
import com.xyl.plugin.PluginManager;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_CODE_STORAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        findViewById(R.id.analyze_apk).setOnClickListener(this);
        findViewById(R.id.plugin_install).setOnClickListener(this);
        findViewById(R.id.plugin_uninstall).setOnClickListener(this);
        findViewById(R.id.host_test).setOnClickListener(this);
        findViewById(R.id.plugin_test).setOnClickListener(this);
        findViewById(R.id.fake_test).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.analyze_apk:
                ApkUtils.analyze(this, new File(getApkFile()));
                break;
            case R.id.plugin_install:
                try {
                    PluginManager.getInstance().loadPlugin(new File(getApkFile()));
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "插件加载失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.plugin_uninstall:
                break;
            case R.id.host_test:
                intent.setClass(this, TargetActivity.class);
                intent.putExtra("source", getClass().getSimpleName());
                startActivity(intent);
                break;
            case R.id.plugin_test:
                intent.setClassName("net.luculent.sxcoal", "net.luculent.mobile65.ui.entry" +
                        ".EntrySplashActivity");
                intent.putExtra("source", getClass().getSimpleName());
                startActivity(intent);
                break;
            case R.id.fake_test:
                break;
        }
    }

    private String getApkFile() {
        return Environment.getExternalStorageDirectory() + "/山西焦煤/downloads/sxcoal.apk";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (PERMISSION_REQUEST_CODE_STORAGE == requestCode) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                requestPermission();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean hasPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void requestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE_STORAGE);
        }
    }
}
