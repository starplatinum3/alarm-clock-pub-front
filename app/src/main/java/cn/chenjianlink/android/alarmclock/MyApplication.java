package cn.chenjianlink.android.alarmclock;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import cn.chenjianlink.android.alarmclock.service.JobWakeUpService;
import cn.chenjianlink.android.alarmclock.utils.LogcatHelper;
//Application 安卓 注册
public class MyApplication extends Application {
  
    @Override  
    public void onCreate() {
        // TODO Auto-generated method stub  
        super.onCreate();
        Log.i("MyApplication", "onCreate: ");
        //LogcatHelper.getInstance(this).start();
        //Graph.provide(this);
        //调用kotlin要这样 上面这样没有的
        Graph.INSTANCE.provide(this);
        //startAllServices();
    }

    /**
     * 开启所有Service
     */
    private void startAllServices()
    {
        //startService(new Intent(this, StepService.class));
        //startService(new Intent(this, GuardService.class));
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP) {
            Log.d("startAllServices", "startAllServices: ");
            //版本必须大于5.0
            startService(new Intent(this, JobWakeUpService.class));
        }
    }
} 