package com.xyl.plugin.hook.ams;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.xyl.plugin.PluginManager;
import com.xyl.plugin.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.List;

public class HCallback implements Handler.Callback {

    public static final int LAUNCH_ACTIVITY = 100;//8.0及8.0以前,使用此消息创建页面
    public static final int EXECUTE_TRANSACTION = 159;//10.0+,页面处理统一使用此消息

    private Handler mHandler;
    private Class launchClazz;//包装启动activity的类

    public HCallback(Handler handler) {
        mHandler = handler;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == LAUNCH_ACTIVITY) {
            Object r = msg.obj;//ActivityClientRecord
            try {
                //得到消息中的Intent(启动PluginActivity的Intent)
                handleLaunch(r, "intent");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (msg.what == EXECUTE_TRANSACTION) {
            Object transaction = msg.obj;//实际类:ClientTransaction
            List<?> callbacks = null;
            try {
                callbacks = (List<?>) ReflectionUtil.getField(transaction.getClass(), transaction,
                        "mActivityCallbacks");
                Object callback = callbacks.size() > 0 ? callbacks.get(0) : null;
                Class launchItem = getLaunchClazz();//启动activity
                if (launchItem.isInstance(callback)) {//替换mIntent对象
                    handleLaunch(callback, "mIntent");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mHandler.handleMessage(msg);
        return true;
    }


    private void handleLaunch(Object pipe, String name) throws Exception {
        Field intentFld = ReflectionUtil.getField(pipe.getClass(), name);
        Intent intent = (Intent) intentFld.get(pipe);
        Intent target = replacePluginIntent(intent);
        intentFld.set(pipe, target);
    }

    /**
     * 替换plugin为target
     *
     * @param intent
     * @return
     */
    private Intent replacePluginIntent(Intent intent) {
        //得到此前保存起来的Intent(启动TargetActivity的Intent)
        Intent target = intent.getParcelableExtra(PluginManager.TARGET_INTENT);
        //将启动SubActivity的Intent替换为启动TargetActivity的Intent
        if (target != null) {
            intent = target;
        }
        return intent;
    }

    private Class<?> getLaunchClazz() throws ClassNotFoundException {
        if (launchClazz == null) {
            launchClazz = Class.forName("android.app.servertransaction.LaunchActivityItem");
        }
        return launchClazz;
    }
}
