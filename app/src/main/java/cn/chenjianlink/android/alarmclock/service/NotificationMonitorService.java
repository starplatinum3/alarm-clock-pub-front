package cn.chenjianlink.android.alarmclock.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Process;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

//import com.example.myapplication.util.DateUtil;
//import com.example.myapplication.util.EmailSendThread;
//import com.example.myapplication.util.ToastUtil;

import com.starp.roomUtil.BaseDao;
import com.starp.roomUtil.RoomUtilAppDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.chenjianlink.android.alarmclock.R;
import cn.chenjianlink.android.alarmclock.activity.MainActivity;
import cn.chenjianlink.android.alarmclock.db.AppDatabase;
import cn.chenjianlink.android.alarmclock.model.LogInfo;
import cn.chenjianlink.android.alarmclock.utils.ActivityUtil;
import cn.chenjianlink.android.alarmclock.utils.DateUtil;
import cn.chenjianlink.android.alarmclock.utils.EmailSendThread;
import cn.chenjianlink.android.alarmclock.utils.ServiceRunStateUtils;
import cn.chenjianlink.android.alarmclock.utils.ThreadPoolFactory;
import cn.chenjianlink.android.alarmclock.utils.ToastUtil;

//service 在 onpause 也可以执行吗
//https://www.jianshu.com/p/82713b43b59e?appinstall=0
//NotificationListener Observer
//app usb 断开之后 重新连接，adb还是可以看到log
//NotificationListenerService 过段时间没了
@SuppressLint("NewApi")
//安卓 检查一个service 是否在运行
public class NotificationMonitorService extends NotificationListenerService {
    private static final String TAG = "NotificationMonitorService";
    //因为继承了 所以会去做这些事情吗
    // 在收到消息时触发
//    为什么会有两个
//    onNotificationPosted 会收到两个
//    http://www.voidcn.com/article/p-vvlmysoa-bvw.html
    private String mPreviousNotificationKey;
    //    https://zhidao.baidu.com/question/1754773056201412428.html
    private long previousTime;
    private String lastTitle = "";

    //https://blog.csdn.net/zrj244265428/article/details/68958358
    //确认NotificationMonitor是否开启
    private void ensureCollectorRunning() {
        //run
        //ComponentName collectorComponent = new ComponentName(this, NotificationMonitor.class);
        ComponentName collectorComponent = new ComponentName(this, NotificationMonitorService.class);
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        boolean collectorRunning = false;
        List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);
        if (runningServices == null) {
            return;
        }
        //Process.myPid()
        for (ActivityManager.RunningServiceInfo service : runningServices) {
            if (service.service.equals(collectorComponent)) {
                //Process.
                if (service.pid == Process.myPid()) {
                    collectorRunning = true;
                }
            }
        }
        if (collectorRunning) {
            return;
        }
        toggleNotificationListenerService();
    }

    //重新开启NotificationMonitor
    private void toggleNotificationListenerService() {
        //ComponentName thisComponent = new ComponentName(this,  NotificationMonitor.class);
        ComponentName thisComponent = new ComponentName(this, NotificationMonitorService.class);
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

    }
//————————————————
//    版权声明：本文为CSDN博主「zrj244265428」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//    原文链接：https://blog.csdn.net/zrj244265428/article/details/68958358

    //重新启动就可以 收到了
//    I/XSL_Test: Notification posted 哔哩哔哩验证码 & 895481，请勿泄露给他人，点击查看详情
//    但是这样有问题诶 要重新启动 代码，是代码启动 不是程序启动啊
//    而且他有没有用 也不知道怎么 判断啊 是后台的程序啊
//    回到手机桌面 也没有失效

    void saveLog(LogInfo logInfo) {
        ThreadPoolFactory.getExecutorService().execute(() -> {
            //AppDatabase database = AppDatabase.getDatabase(this);
            try {
                Log.i(TAG, "onNotificationPosted: insert(buil");
                database.logInfoDao().insert(logInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    // 请注意getRunningServices 已经标记为deprecated, 如果要达到类似效果，请使用isMyServiceRunning来代替isRunning()
    public boolean isRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService =
                (ArrayList<ActivityManager.RunningServiceInfo>) activityManager.getRunningServices(40);
        for (int i = 0; i < runningService.size(); i++) {
            String name = runningService.get(i).service.getClassName().toString();
            if (name.equals("com.spacesoftwares.servicedemo.MyService")) {
                return true;
            }
        }
        return false;
    }
    //
//————————————————
//    版权声明：本文为CSDN博主「高精度计算机视觉」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//    原文链接：https://blog.csdn.net/tanmx219/article/details/81239651
//

    public static final int notifId = 110;

    public static final String serviceName = "cn.chenjianlink.android.alarmclock.service.NotificationMonitorService";
   public    void startForegroundService(){
        //https://www.cnblogs.com/renhui/p/8575299.html
        // 在API11之后构建Notification的方式
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel(manager);
        //要先 create Channel
        Notification.Builder builder = new Notification.Builder
                (this.getApplicationContext(),defaultChannelId); //获取一个Notification构造器
        Intent nfIntent = new Intent(this, MainActivity.class);
        //点击了会去那个 activity
        builder
                .setContentIntent(PendingIntent.
                getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                        R.mipmap.app_icon)) // 设置下拉列表中的图标(大图标)
                //R.mipmap.ic_large)) // 设置下拉列表中的图标(大图标)
                .setContentTitle("下拉列表中的Title") // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                .setContentText("通知获取服务") // 设置上下文内容
                //.setChannelId(defaultChannelId)
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        Notification notification = builder.build(); // 获取构建好的Notification
        //notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        // 参数一：唯一的通知标识；参数二：通知消息。
        //startForeground(110, notification);// 开始前台服务
        startForeground(notifId, notification);// 开始前台服务
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //这个是什么时候开启的

        Log.d(TAG, "onStartCommand: startId " + startId + " flags " + flags);

        startForegroundService();
        //https://www.cnblogs.com/renhui/p/8575299.html
        // 在API11之后构建Notification的方式
        //NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //createNotificationChannel(manager);
        ////要先 create Channel
        //Notification.Builder builder = new Notification.Builder
        //        (this.getApplicationContext(),defaultChannelId); //获取一个Notification构造器
        //Intent nfIntent = new Intent(this, MainActivity.class);
        ////点击了会去那个 activity
        //builder.setContentIntent(PendingIntent.
        //        getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
        //        .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
        //                R.mipmap.app_icon)) // 设置下拉列表中的图标(大图标)
        //        //R.mipmap.ic_large)) // 设置下拉列表中的图标(大图标)
        //        .setContentTitle("下拉列表中的Title") // 设置下拉列表里的标题
        //        .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
        //        .setContentText("通知获取服务") // 设置上下文内容
        //        //.setChannelId(defaultChannelId)
        //        .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
        //
        //Notification notification = builder.build(); // 获取构建好的Notification
        ////notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        //// 参数一：唯一的通知标识；参数二：通知消息。
        ////startForeground(110, notification);// 开始前台服务
        //startForeground(notifId, notification);// 开始前台服务
        //https://www.cnblogs.com/renhui/p/8575299.html
        ThreadPoolFactory.getExecutorService().execute(() -> {
            int i = 0;
            //isMyServiceRunning()

            //String serviceName="cn.chenjianlink.android.alarmclock.service.NotificationMonitorService";
            //boolean serviceRunning = ServiceRunStateUtils.
            //        isServiceRunning(this, "cn.chenjianlink.android.alarmclock.service.NotificationMonitorService");
            //while(isRunning()) // or, you can use while(isMyServiceRunning) here instead of isRunning
            //ServiceRunStateUtils.
            //isServiceRunning(this,serviceName)
            while (true) // or, you can use while(isMyServiceRunning) here instead of isRunning
            {
                //Log.i(TAG, "SERVICE IS RUNNING for " + String.valueOf(i++) + " seconds");
                Log.i(TAG, "SERVICE IS RUNNING for " + String.valueOf(i++) + " 10 mins ");
                try {
                    //Thread.sleep(1000);
                    Thread.sleep(600000);
                    //10分钟检查一次吗
                    //1000*60*10
                    boolean serviceRunning = ServiceRunStateUtils.
                            isServiceRunning(NotificationMonitorService.this, serviceName);
                    if (!serviceRunning) {
                        //startSe
                        ActivityUtil.startService(NotificationMonitorService.this,
                                NotificationMonitorService.class);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
        //new Thread(new Runnable() {
        //    @Override
        //    public void run() {
        //        int i = 0;
        //        //isMyServiceRunning()
        //
        //        String serviceName="cn.chenjianlink.android.alarmclock.service.NotificationMonitorService";
        //        //boolean serviceRunning = ServiceRunStateUtils.
        //        //        isServiceRunning(this, "cn.chenjianlink.android.alarmclock.service.NotificationMonitorService");
        //        //while(isRunning()) // or, you can use while(isMyServiceRunning) here instead of isRunning
        //        //ServiceRunStateUtils.
        //                //isServiceRunning(this,serviceName)
        //        while(true) // or, you can use while(isMyServiceRunning) here instead of isRunning
        //        {
        //            //Log.i(TAG, "SERVICE IS RUNNING for " + String.valueOf(i++) + " seconds");
        //            Log.i(TAG, "SERVICE IS RUNNING for " + String.valueOf(i++) + " 10 mins ");
        //            try {
        //                //Thread.sleep(1000);
        //                Thread.sleep(600000);
        //                //10分钟检查一次吗
        //                //1000*60*10
        //                boolean serviceRunning = ServiceRunStateUtils.
        //                        isServiceRunning(this, serviceName);
        //                if(!serviceRunning){
        //                    //startSe
        //                    ActivityUtil.startService(NotificationMonitorService.this,
        //                            NotificationMonitorService.class);
        //                }
        //            }catch (InterruptedException e){
        //                e.printStackTrace();
        //            }
        //        }
        //    }
        //}).start();
//————————————————
//        版权声明：本文为CSDN博主「高精度计算机视觉」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//        原文链接：https://blog.csdn.net/tanmx219/article/details/81239651
        return super.onStartCommand(intent, flags, startId);
    }

    void onNotificationPostedDo(StatusBarNotification sbn) {
        mPreviousNotificationKey = sbn.getKey();
        Log.i("NotifyService", "got notification");
        // TODO Auto-generated method stub
        Bundle extras = sbn.getNotification().extras;
        // 获取接收消息APP的包名
        String notificationPkg = sbn.getPackageName();
        // 获取接收消息的抬头
        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
        // 获取接收消息的内容
        if (notificationTitle == null) {
            Log.d(TAG, "onNotificationPosted: 标题是空的 ");
            return;
        }
        if (notificationTitle.equals(lastTitle)) {
            Log.d(TAG, "onNotificationPosted: 不要重复发 " + lastTitle);
            return;
        }
        lastTitle = notificationTitle;
        String notificationText = extras.getString(Notification.EXTRA_TEXT);
        Log.i("XSL_Test", "Notification posted " + notificationTitle + " & " + notificationText);
//            java  字符串 查找
//            I/XSL_Test: Notification posted 哔哩哔哩验证码 & 311362，请勿泄露给他人，点击查看详情
//            可以 在后台，在桌面很久了 好几分钟，他还是可以收到 但是没有发邮件啊
//            因为他的 conten 没有 验证码的字样啊
//            两者都不包括 验证码就不发了
        Date date = new Date();
        LogInfo build = new LogInfo(null);
        build.setDate(date);
        build.setContent(notificationText);
        //build.se
        build.setKey(sbn.getKey());
        build.setTitle(notificationTitle);
        //LogInfo build = LogInfo.builder().date(date).
        //        content(notificationText).title(notificationTitle).build();

        boolean containsVerificationCode = notificationText != null &&
                notificationText.contains("验证码") ||
                notificationTitle != null && notificationTitle.contains("验证码");
//            notificationText==null||!notificationText.contains("验证码")
//                    &&!notificationTitle.contains("验证码")
        if (!containsVerificationCode) {
            Log.i(TAG, "onNotificationPosted: 不是验证码就不发了");
//                Context applicationContext = getApplicationContext();
            ToastUtil.toastToMain(getApplicationContext(), "onNotificationPosted: 不是验证码就不发了");
//                Handler     handler=new Handler(Looper.getMainLooper());
//                handler.post(new Runnable(){
//                    public void run(){
//                        Toast.makeText(getApplicationContext(),
//                                "Service is created!", Toast.LENGTH_LONG).show();
//                    }
//                });
//————————————————
//                版权声明：本文为CSDN博主「pku_android」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//                原文链接：https://blog.csdn.net/pku_android/article/details/7438901
//                build.setIsVerificationCode(1);
            build.setVerificationCode(false);
            saveLog(build);
            return;
        }

        build.setVerificationCode(true);
        String nowDateStr = DateUtil.dateToString(date, DateUtil.FORMAT_ONE);
        saveLog(build);
        //RoomUtil.
        //RoomUtilAppDatabase database = AppDatabase.getDatabase(this);

        //ThreadPoolFactory.getExecutorService().execute(()->{
        //    //AppDatabase database = AppDatabase.getDatabase(this);
        //    try{
        //        Log.i(TAG, "onNotificationPosted: insert(buil");
        //        database.logInfoDao().insert(build);
        //    }catch (Exception e){
        //        e.printStackTrace();
        //    }
        //
        //});

        //BaseDao<LogInfo>logInfoBaseDao=
//            怎么不发邮箱
        Log.i(TAG, "onNotificationPosted: 发邮件");
        ToastUtil.toastToMain(getApplicationContext(), "onNotificationPosted: 发邮件");

        EmailSendThread emailSendThread = new EmailSendThread();
        emailSendThread.setDatabase(database);
        //emailSendThread.setTitle(notificationTitle + DateUtil.dateToString(new Date(), DateUtil.FORMAT_ONE));
        emailSendThread.setTitle(notificationTitle + nowDateStr);
        emailSendThread.setContent(notificationText);
//        emailSendThread.set(notificationText);
        emailSendThread.start();
//            不会报错吗
        Toast.makeText(getApplicationContext(), "发送邮件", Toast.LENGTH_SHORT).show();
//            toast.show();
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
//        NotificationListenerService
//        失效了
//        开启 是 开关开启了就是开启啊
//        onNotificationPosted 防止消息重复
//        https://www.jianshu.com/p/82713b43b59e
        Log.i(TAG, "onNotificationPosted: 通知信息到达");
        Log.i(TAG, "onNotificationPosted: sbn " + sbn);
//        long nowLong = System.currentTimeMillis();
//        if (nowLong - previousTime < 1000 * 30) {
////            30秒之内的数据就不要重复弄了
//            return;
//        }
//        //万一有一条是qq消息 然后之后1秒来了个 验证码 这里就收不到了呀
//        previousTime = System.currentTimeMillis();

//        如果没有key 或者 key 不重复

        String key = sbn.getKey();
        //D/NotificationMonitorService: onNotificationPosted: key 0|com.tencent.mobileqq|515|null|10355
        //D/NotificationMonitorService: onNotificationPosted: key 0|com.tencent.mobileqq|515|null|10355
        //两个key 是一样的 发的不是同一个消息 key 代表的是他们是同一个框吗

        Log.d(TAG, "onNotificationPosted: key " + key);
        onNotificationPostedDo(sbn);
        //连续发 qq 貌似不行

//        if (TextUtils.isEmpty(mPreviousNotificationKey) ||
//                !TextUtils.isEmpty(mPreviousNotificationKey)
//                        && !sbn.getKey().equals(mPreviousNotificationKey)) {
//            onNotificationPostedDo(sbn);
////            mPreviousNotificationKey = sbn.getKey();
////            Log.i("NotifyService", "got notification");
////            // TODO Auto-generated method stub
////            Bundle extras = sbn.getNotification().extras;
////            // 获取接收消息APP的包名
////            String notificationPkg = sbn.getPackageName();
////            // 获取接收消息的抬头
////            String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
////            // 获取接收消息的内容
////            if(notificationTitle==null){
////                Log.d(TAG, "onNotificationPosted: 标题是空的 ");
////                return;
////            }
////            if(notificationTitle.equals(lastTitle)){
////                Log.d(TAG, "onNotificationPosted: 不要重复发 "+lastTitle);
////                return;
////            }
////            lastTitle = notificationTitle;
////            String notificationText = extras.getString(Notification.EXTRA_TEXT);
////            Log.i("XSL_Test", "Notification posted " + notificationTitle + " & " + notificationText);
//////            java  字符串 查找
//////            I/XSL_Test: Notification posted 哔哩哔哩验证码 & 311362，请勿泄露给他人，点击查看详情
//////            可以 在后台，在桌面很久了 好几分钟，他还是可以收到 但是没有发邮件啊
//////            因为他的 conten 没有 验证码的字样啊
//////            两者都不包括 验证码就不发了
////            Date date = new Date();
////            LogInfo build = new LogInfo(null);
////            build.setDate(date);
////            build.setContent(notificationText);
////            build.setTitle(notificationTitle);
////            //LogInfo build = LogInfo.builder().date(date).
////            //        content(notificationText).title(notificationTitle).build();
////
////            boolean containsVerificationCode = notificationText != null &&
////                    notificationText.contains("验证码") ||
////                    notificationTitle != null && notificationTitle.contains("验证码");
//////            notificationText==null||!notificationText.contains("验证码")
//////                    &&!notificationTitle.contains("验证码")
////            if (!containsVerificationCode) {
////                Log.i(TAG, "onNotificationPosted: 不是验证码就不发了");
//////                Context applicationContext = getApplicationContext();
////                ToastUtil.toastToMain(getApplicationContext(), "onNotificationPosted: 不是验证码就不发了");
//////                Handler     handler=new Handler(Looper.getMainLooper());
//////                handler.post(new Runnable(){
//////                    public void run(){
//////                        Toast.makeText(getApplicationContext(),
//////                                "Service is created!", Toast.LENGTH_LONG).show();
//////                    }
//////                });
//////————————————————
//////                版权声明：本文为CSDN博主「pku_android」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//////                原文链接：https://blog.csdn.net/pku_android/article/details/7438901
//////                build.setIsVerificationCode(1);
////                build.setVerificationCode(false);
////                saveLog(build);
////                return;
////            }
////
////            build.setVerificationCode(true);
////            String nowDateStr = DateUtil.dateToString(date, DateUtil.FORMAT_ONE);
////            saveLog(build);
////            //RoomUtil.
////            //RoomUtilAppDatabase database = AppDatabase.getDatabase(this);
////
////            //ThreadPoolFactory.getExecutorService().execute(()->{
////            //    //AppDatabase database = AppDatabase.getDatabase(this);
////            //    try{
////            //        Log.i(TAG, "onNotificationPosted: insert(buil");
////            //        database.logInfoDao().insert(build);
////            //    }catch (Exception e){
////            //        e.printStackTrace();
////            //    }
////            //
////            //});
////
////            //BaseDao<LogInfo>logInfoBaseDao=
//////            怎么不发邮箱
////            Log.i(TAG, "onNotificationPosted: 发邮件");
////            ToastUtil.toastToMain(getApplicationContext(), "onNotificationPosted: 发邮件");
////
////            EmailSendThread emailSendThread = new EmailSendThread();
////            emailSendThread.setDatabase(database);
////            //emailSendThread.setTitle(notificationTitle + DateUtil.dateToString(new Date(), DateUtil.FORMAT_ONE));
////            emailSendThread.setTitle(notificationTitle + nowDateStr);
////            emailSendThread.setContent(notificationText);
//////        emailSendThread.set(notificationText);
////            emailSendThread.start();
//////            不会报错吗
////            Toast.makeText(getApplicationContext(), "发送邮件", Toast.LENGTH_SHORT).show();
////            toast.show();
//        }


    }

    AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        database = AppDatabase.getDatabase(this);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel(manager);
    }

    // 在删除消息时触发
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // TODO Auto-generated method stub
        Bundle extras = sbn.getNotification().extras;
        // 获取接收消息APP的包名
        String notificationPkg = sbn.getPackageName();
        // 获取接收消息的抬头
        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
        // 获取接收消息的内容
        String notificationText = extras.getString(Notification.EXTRA_TEXT);
        Log.i("XSL_Test", "Notification removed " + notificationTitle + " & " + notificationText);
    }

    public static final String defaultChannelId = "default";

    //https://blog.csdn.net/qq_15527709/article/details/78853048
    private void createNotificationChannel(NotificationManager manager) {
        //String defaultChannelId="default";
        if (manager.getNotificationChannel("default") == null) {
            NotificationChannel notificationChannel =
                    new NotificationChannel("default",
                            "notification_channel",
                            NotificationManager.IMPORTANCE_LOW);

            notificationChannel.setDescription(
                    "notification_channel_description");

            manager.createNotificationChannel(notificationChannel);
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        stopForeground(true);// 停止前台服务--参数：表示是否移除之前的通知


        Log.d(TAG, "onDestroy: ");
        LogInfo logInfo = new LogInfo(null);
        logInfo.setTitle("onDestroy");
        logInfo.setContent("重新启动");
        logInfo.setDate(new Date());
        saveLog(logInfo);
        //连续启动Service

        //Intent intentOne = new Intent(this, NotificationMonitorService.class);
        //startService(intentOne);

        super.onDestroy();
    }
}