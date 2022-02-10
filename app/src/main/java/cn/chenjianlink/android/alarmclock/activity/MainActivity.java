package cn.chenjianlink.android.alarmclock.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.thorqin.reader.activities.book.BookActivity;
import com.google.gson.Gson;

import java.io.File;
import java.util.List;

import cn.chenjianlink.android.alarmclock.R;
import cn.chenjianlink.android.alarmclock.adapter.AlarmClockAdapter;
import cn.chenjianlink.android.alarmclock.adapter.AlarmClockItemTouchCallback;
import cn.chenjianlink.android.alarmclock.databinding.ActivityMainBinding;
import cn.chenjianlink.android.alarmclock.model.AlarmClock;
import cn.chenjianlink.android.alarmclock.receiver.AlarmClockReceiver;
import cn.chenjianlink.android.alarmclock.service.NotificationMonitorService;
import cn.chenjianlink.android.alarmclock.utils.ActivityUtil;
import cn.chenjianlink.android.alarmclock.utils.CommonValue;
import cn.chenjianlink.android.alarmclock.utils.LogUtil;
import cn.chenjianlink.android.alarmclock.utils.LogcatHelper;
import cn.chenjianlink.android.alarmclock.utils.ServiceRunStateUtils;
import cn.chenjianlink.android.alarmclock.utils.TimeUtil;
import cn.chenjianlink.android.alarmclock.viewmodel.AlarmClockViewModel;
import lombok.SneakyThrows;

/**
 * @author chenjian
 * 闹钟主界面，包含闹钟列表和添加闹钟按钮
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 创建MainActivity,将ToolBar控件显示
     *
     * @param savedInstanceState 保存的数据
     */
    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ActivityMainBinding 根据这个名字就可以绑定吗
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        AlarmClockViewModel alarmClockViewModel = new ViewModelProvider(this).get(AlarmClockViewModel.class);
        setSupportActionBar(binding.mainToolbar);

        //配置RecyclerView
        RecyclerView alarmClockList = binding.alarmClockList;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        alarmClockList.setLayoutManager(layoutManager);
        final AlarmClockAdapter adapter = new AlarmClockAdapter(alarmClockViewModel);
        //设置监听事件
        // 什么时候才会 被observe，应该说什么时候onChanged
        alarmClockViewModel.getAlarmClockList().observe(this, new Observer<List<AlarmClock>>() {
            @Override
            public void onChanged(List<AlarmClock> alarmClockList) {
                // 所有的闹钟开启
                LogUtil.i("Main", "data changed");
                adapter.updateAlarmClockList(alarmClockList);
                //发送闹钟广播
                for (AlarmClock alarmClock : alarmClockList) {
                    LogUtil.i("Intent", "send message");
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(MainActivity.this, AlarmClockReceiver.class);
                    intent.putExtra(CommonValue.ALARM_CLOCK_INTENT, new Gson().toJson(alarmClock));
                    int nextRingTime = TimeUtil.nextRingTime(alarmClock.getRingCycle(), alarmClock.getHour(), alarmClock.getMinute());
                    LogUtil.i("nextTime", "" + nextRingTime);
                    PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this, alarmClock.getClockId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    if (alarmClock.isStarted()) {
                        //闹钟启用则发送PendingIntent,否则取消发送
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + nextRingTime, pi);
                    } else {
                        alarmManager.cancel(pi);
                    }
                }
            }
        });
        alarmClockList.setAdapter(adapter);

        //设置滑动删除
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new AlarmClockItemTouchCallback(adapter));
        itemTouchHelper.attachToRecyclerView(alarmClockList);

//        这是后来加的
//        find 的还有一个坏处是他不知道自己是没有的 binding是知道的
//        findViewById(R.id.btn_to_ring).setOnClickListener(this);
//
//        findViewById(R.id.btn_to_noti).setOnClickListener(this);
//        getAppInfo(this);
//        binding.btnToShowLog.setOnClickListener(view1 -> {
//            ActivityUtil.startActivity(this,HolderActivity.class);
//        });
//        binding.btnToInitLogFile.setOnClickListener(view1 -> {
//            initLogFile();
//        });
//        binding.btnToBookMain.setOnClickListener(view1 -> {
//            //ActivityUtil.startActivity(this, com.github.thorqin.reader.activities.main.MainActivity.class);
//            ActivityUtil.startActivity(this, BookActivity.class);
//        });

        binding.btnToTest.setOnClickListener(view1 -> {
            //ActivityUtil.startActivity(this, com.github.thorqin.reader.activities.main.MainActivity.class);
            ActivityUtil.startActivity(this, TestActivity.class);
        });
        //initLogFile();
        //LogcatHelper.getInstance(this).start();
       startSomeService();
    }

    void startSomeService(){
        boolean serviceRunning = ServiceRunStateUtils.
                isServiceRunning(this, NotificationMonitorService.serviceName);
        if(!serviceRunning){
            //D/not: onCreate: serviceRunning not
            //D/NotificationMonitorService: onStartCommand: startId 1 flags 0
            Log.d("not", "onCreate: serviceRunning not");
            //这里启动貌似也就收到一条
            ActivityUtil.startService(this, NotificationMonitorService.class);
        }

    }

    //    https://blog.csdn.net/u010885095/article/details/78291541
    private void getAppInfo(Context context) throws Exception {
        PackageManager packageManager = context.getPackageManager();
        //获取所有安装的app
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for (PackageInfo info : installedPackages) {
            String packageName = info.packageName;//app包名
            ApplicationInfo ai = packageManager.getApplicationInfo(packageName, 0);
            String appName = (String) packageManager.getApplicationLabel(ai);//获取应用名称
            LogUtil.i("packageName", packageName);
            LogUtil.i("appName", appName);
        }
    }

    public static void openApp(Context context, String packageName) {
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(intent);
    }

    private static final int WRITE_EXTERNAL_STORAGE_REQUIRE = 1;

    //int
    void initLogFile() {
        Log.i("initLogFile", "initLogFile: ");
        //这个  权限已经给了 所以不用再次申请了是吗
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android
                .Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUIRE);
    }

    //static   void initPath(Context context){
    //    if (Environment.getExternalStorageState().equals(
    //            Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
    //        PATH_LOGCAT = Environment.getExternalStorageDirectory()
    //                .getAbsolutePath() + File.separator + "bsyqLog";
    //    } else {// 如果SD卡不存在，就保存到本应用的目录下
    //        PATH_LOGCAT = context.getFilesDir().getAbsolutePath()
    //                + File.separator + "llvisionLog";
    //    }
    //    File file = new File(PATH_LOGCAT);
    //    if(!file.exists()){
    //        boolean mkdirs = file.mkdirs();
    //        Log.i("mkdirs", "initPath: "+mkdirs);
    //    }
    //    //if()
    //}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_REQUIRE:
                Log.i("LogcatHelper", "initLogFile: start ");
                LogcatHelper.getInstance(this).start();
                //if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //    //创建文件夹
                //    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                //        File file = new File(Environment.getExternalStorageDirectory() + "/aa/bb/");
                //        if (!file.exists()) {
                //            Log.d("jim", "path1 create:" + file.mkdirs());
                //        }
                //    }
                //    break;
                //}
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_to_ring:
                Intent intent = new Intent(MainActivity.this, SoundActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_to_noti:
                intent = new Intent(MainActivity.this, NotificationMonitorActivity.class);
                startActivity(intent);
                break;

        }
    }

    /**
     * 创建Toolbar上的menu
     *
     * @param menu menu对象
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_tool_bar_menu, menu);
        return true;
    }

    /**
     * 设置menu点击事件
     *
     * @param item item对象
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(MainActivity.this, AlarmClockManageActivity.class);
        startActivityForResult(intent, CommonValue.SETTING_CLOCK_CODE);
        return true;
    }

    /**
     * 处理返回信息（返回闹钟下一次响铃时间信息）
     *
     * @param requestCode 请求码
     * @param resultCode  请求结果码
     * @param data        包含处理结果的信息
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.i("ActivityResult", "is used");
        if (requestCode == CommonValue.SETTING_CLOCK_CODE) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                String nextRingTime = data.getStringExtra(CommonValue.NEXT_RING_TIME);
                LogUtil.i("NextRingTime:", nextRingTime);
                Toast.makeText(this, nextRingTime, Toast.LENGTH_SHORT).show();
            }
        }
    }
}