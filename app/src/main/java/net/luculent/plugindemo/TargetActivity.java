package net.luculent.plugindemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


/**
 * 未在manifest里面注册的activity
 */
public class TargetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        Log.i("abc", "initData: " + intent.getStringExtra("source"));
    }
}
