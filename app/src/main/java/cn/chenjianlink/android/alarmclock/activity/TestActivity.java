package cn.chenjianlink.android.alarmclock.activity;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.thorqin.reader.activities.book.BookActivity;
import com.google.gson.Gson;

import cn.chenjianlink.android.alarmclock.R;
import cn.chenjianlink.android.alarmclock.databinding.ActivityAlarmClockRingBinding;
import cn.chenjianlink.android.alarmclock.databinding.ActivityTestBinding;
import cn.chenjianlink.android.alarmclock.databinding.NotificationMonitorBinding;
import cn.chenjianlink.android.alarmclock.model.AlarmClock;
import cn.chenjianlink.android.alarmclock.service.AlarmClockService;
import cn.chenjianlink.android.alarmclock.service.AlarmClockServiceProvider;
import cn.chenjianlink.android.alarmclock.service.NotificationMonitorService;
import cn.chenjianlink.android.alarmclock.utils.ActivityUtil;
import cn.chenjianlink.android.alarmclock.utils.AnnotationUtil;
import cn.chenjianlink.android.alarmclock.utils.CommonValue;
import cn.chenjianlink.android.alarmclock.utils.LogUtil;
import cn.chenjianlink.android.alarmclock.utils.TimeUtil;

/**
 * @author chenjian
 * 响铃界面Activity
 */
public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AlarmClockRingActivity";

    private TextView nowTime;

    private TextView alarmClockLabel;

    private Intent serviceIntent;

    private AlarmClockServiceProvider serviceProvider;

    private RingServiceConnection connection;

    /**
     * 判断用户是否按下界面按钮
     */
    private boolean pauseOrNot;

    /**
     * 计时器，在界面无操作响应一分钟之后进行
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!pauseOrNot) {
                        serviceProvider.oneMinClose();
                        TestActivity.this.finish();
                        LogUtil.i(TAG, "--------响铃一分钟无反应。。--成功调用服务里五分钟后再响的方法!----------------");
                    } else {
                        LogUtil.i(TAG, "--------点击了暂停或关闭闹钟。--一分钟失效！---------------");
                    }
                }
            }, 60 * 1000);
        }
    };

    ActivityTestBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        findViewById(R.id.btn_to_ring).setOnClickListener(this);

        findViewById(R.id.btn_to_noti).setOnClickListener(this);
        binding.btnToShowLog.setOnClickListener(view1 -> {
            ActivityUtil.startActivity(this,HolderActivity.class);
        });
        binding.btnToInitLogFile.setOnClickListener(view1 -> {
            //initLogFile();
            Log.i(TAG, "onCreate: btnToInitLogFile");
        });
        //java 打开 kotlin的 activity
        binding.btnToBookMain.setOnClickListener(view1 -> {
            //ActivityUtil.startActivity(this, com.github.thorqin.reader.activities.main.MainActivity.class);
            Log.i(TAG, "onCreate:  btnToBookMain ");
            ActivityUtil.startActivity(this, BookActivity.class);
        });
        binding.btnToLogTable.setOnClickListener(view1 -> {
            //ActivityUtil.startActivity(this, com.github.thorqin.reader.activities.main.MainActivity.class);
            Log.i(TAG, "onCreate:  btnToBookMain ");
            ActivityUtil.startActivity(this, BookActivity.class);
        });
        binding.btnStartNoti.setOnClickListener(view1 -> {
            //ActivityUtil.startActivity(this, com.github.thorqin.reader.activities.main.MainActivity.class);
            Log.i(TAG, "onCreate:  btnToBookMain ");
            //ActivityUtil.startActivity(this, BookActivity.class);
            ActivityUtil.startService(this, NotificationMonitorService.class);
            //startService()
        });
        //try{
        //    AnnotationUtil.test();
        //
        //}catch (Exception e){
        //    e.printStackTrace();
        //}
        //binding.btnt
    }


//    闹钟不是在这里响起来的 这里是 界面的变化
    @Override
    protected void onStart() {
        super.onStart();
        //闹钟来时绑定对应的服务
        //connection = new RingServiceConnection();
        //serviceIntent = new Intent(this, AlarmClockService.class);
        //bindService(serviceIntent, connection, 0);
        //AlarmClock alarmClock = new Gson().fromJson(getIntent().getStringExtra(CommonValue.ALARM_CLOCK_INTENT), AlarmClock.class);
        //Calendar calendar = Calendar.getInstance();
        //int hour = calendar.get(Calendar.HOUR_OF_DAY);
        //int minute = calendar.get(Calendar.MINUTE);
        //nowTime.setText(TimeUtil.getShowTime(hour, minute));
        //assert alarmClock != null;
        //if (alarmClock.getRemark() != null) {
        //    alarmClockLabel.setText(alarmClock.getRemark());
        //}
    }

    @Override
    protected void onResume() {
        super.onResume();
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_to_ring:
                Intent intent=new Intent(this, SoundActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_to_noti:
                intent=new Intent(this, NotificationMonitorActivity.class);
                startActivity(intent);
                break;

        }
    }


    /**
     * 中间人接口连接类
     */
    private class RingServiceConnection implements ServiceConnection {
        //当服务被连接的时候调用 服务识别成功 绑定的时候调用
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            serviceProvider = (AlarmClockServiceProvider) service;
            LogUtil.i(TAG, "-------在activity里面得到了服务的中间人对象-------------");
        }

        //当服务失去连接的时候调用（一般进程挂了，服务被异常杀死）
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    }

    @Override
    protected void onDestroy() {
        //unbindService(connection);
        //stopService(serviceIntent);
        super.onDestroy();
    }
}