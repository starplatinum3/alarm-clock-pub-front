package cn.chenjianlink.android.alarmclock.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import cn.chenjianlink.android.alarmclock.activity.AlarmClockOntimeActivity;
import cn.chenjianlink.android.alarmclock.activity.AlarmClockRingActivity;
import cn.chenjianlink.android.alarmclock.R;
import cn.chenjianlink.android.alarmclock.activity.SoundActivity;
import cn.chenjianlink.android.alarmclock.model.AlarmClock;
import cn.chenjianlink.android.alarmclock.receiver.AlarmClockReceiver;
import cn.chenjianlink.android.alarmclock.repository.AlarmClockRepository;
import cn.chenjianlink.android.alarmclock.utils.CommonValue;
import cn.chenjianlink.android.alarmclock.utils.LogUtil;
import cn.chenjianlink.android.alarmclock.utils.TimeUtil;

/**
 * @author chenjian
 * 响铃服务
 */
public class AlarmClockService extends Service {

    private static final String TAG = "AlarmClockService";

    private AlarmManager alarmManager;

    private AlarmClock alarmClock;

    private Intent toReceiverIntent;

    private Vibrator vibrator;

    private Ringtone ringtone;

    private PowerManager powerManager;

    private static final String CLOSE = "close";

    private static final String SNOOZE = "snooze";

    private static final String DELETE = "delete";

    private static final int FOREGROUND_SERVICE = 0b1010101;

    private static final String NOTIFICATION = "notification";

    private NotificationManager notificationManager;

    //    没有log 怎么办
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "-----------onCreate---------");
        alarmManager = getSystemService(AlarmManager.class);
        vibrator = getSystemService(Vibrator.class);
        powerManager = getSystemService(PowerManager.class);
        notificationManager = getSystemService(NotificationManager.class);
        createNotificationChannel();
    }

    // 请注意getRunningServices 已经标记为deprecated, 如果要达到类似效果，请使用isMyServiceRunning来代替isRunning()
    public boolean isRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService =
                (ArrayList<ActivityManager.RunningServiceInfo>) activityManager.getRunningServices(40);
        for(int i=0; i<runningService.size();i++){
            String name = runningService.get(i).service.getClassName().toString();
            if(name.equals("com.spacesoftwares.servicedemo.MyService")){
                return true;
            }
        }
        return  false;
    }
//
//————————————————
//    版权声明：本文为CSDN博主「高精度计算机视觉」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//    原文链接：https://blog.csdn.net/tanmx219/article/details/81239651
//
//    onStartCommand  startId
    @SuppressLint({"NewApi", "WrongConstant"})
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //isRun
        //Service isRun
        LogUtil.i(TAG, "--------------onStartCommand----------");
        LogUtil.i(TAG, "flag:" + flags + ",startId:" + startId);
        //判断是否是前台服务，若是根据Intent则执行关闭或者贪睡(二进宫)
        if (intent.getIntExtra(NOTIFICATION, 0) == FOREGROUND_SERVICE) {
            String action = intent.getAction();
            //关闭闹钟
            notificationManager.cancel(alarmClock.getClockId());
            if (Objects.equals(action, CLOSE)) {
                dismiss();
            } else if (Objects.equals(action, SNOOZE) || Objects.equals(action, DELETE)) {
                //贪睡模式
                snooze();
            }
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }


        String clockJson = intent.getStringExtra(CommonValue.ALARM_CLOCK_INTENT);
        //若是service还未执行过，则执行相应的操作，若已经执行过了，则将重复的闹钟往后延
        alarmClock = new Gson().fromJson(clockJson, AlarmClock.class);
        LogUtil.i(TAG, "alarmClock " + alarmClock);
//        这里的json是不是要变一下 因为 alarm 下次不能不响起来

        //在闹铃界面点击 暂停再响 或 关闭 时 发送的Intent
        toReceiverIntent = new Intent(this, AlarmClockReceiver.class);
//        toReceiverIntent.putExtra(CommonValue.ALARM_CLOCK_INTENT, clockJson);
//        flag:0,startId:3
        if (startId == 1) {
//            看看他有没有 这次不响的flag 有的话，就下次再响起
//            alarmClock = new Gson().fromJson(clockJson, AlarmClock.class);

            if (alarmClock.isStopOnce()) {
                LogUtil.i(TAG, "推迟到下次");
                pushToNextTime(alarmClock);
            } else {
                LogUtil.i(TAG, "开启闹钟");
//                startClock(clockJson);
                LogUtil.i(TAG, "开启活动");
                Intent it = new Intent(this, AlarmClockOntimeActivity.class);
                it.putExtra(CommonValue.ALARM_CLOCK_INTENT, clockJson);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(it);
            }
//
//            //在闹铃界面点击 暂停再响 或 关闭 时 发送的Intent
//            toReceiverIntent = new Intent(this, AlarmClockReceiver.class);
//            toReceiverIntent.putExtra(CommonValue.ALARM_CLOCK_INTENT, clockJson);
//
//            //若用户正在和设备交互，则弹出通知，反之弹出Activity
//            if (powerManager.isInteractive()) {
//                buildNotification(alarmClock);
//            } else {
//                callActivity(clockJson);
//            }
//
//            //设置响铃音乐以及震动
//            if (alarmClock.getRingMusicUri() != null) {
//                ringtone = RingtoneManager.getRingtone(this, Uri.parse(alarmClock.getRingMusicUri()));
//                ringtone.setLooping(true);
////                ringtone.setVolume(0.3f);
////                不用音量 主要是这里需要铃声开启 而不是媒体音量
////                https://developer.android.google.cn/reference/kotlin/android/media/Ringtone
////                Float: a raw scalar in range 0.0 to 1.0, where 0.0 mutes this player,
////                and 1.0 corresponds to no attenuation being applied.
////                浮动：0.0到1.0范围内的原始标量，其中0.0使该播放器静音，1.0对应于未应用衰减。
////                ringtone 没有声音
////                https://blog.csdn.net/jhope/article/details/81018312
//                ringtone.play();
//            }
//            if (alarmClock.isVibrated()) {
//                vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0, 1000, 1000, 1000}, 0));
//            }

        } else {
            LogUtil.i(TAG, "因为startId 不是1 ， 推迟到下次");
            pushToNextTime(alarmClock);
//            AlarmClock clock = new Gson().fromJson(clockJson, AlarmClock.class);
//            PendingIntent pi = PendingIntent.getBroadcast(AlarmClockService.this,
//                    clock.getClockId(), toReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            int nextRingTime = TimeUtil.nextRingTime(clock.getRingCycle(), clock.getHour(), clock.getMinute());
//            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
//                    System.currentTimeMillis() + nextRingTime, pi);
//            LogUtil.i(TAG, "时间冲突，该闹钟延后" + TimeUtil.dateFormat(nextRingTime) + "执行");
        }


        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * init type of  MediaPlayer
     *
     * @param context Activity      不能是 service吗,不能
     * @param rawId   the id of raw
     * @return MediaPlayer
     */
    public MediaPlayer initMediaPlayer(Service context, int rawId) {
//        context.setVolumeControlStream(AudioManager.STREAM_MUSIC);
//        Service service=new Service() {
//            @Nullable
//            @Override
//            public IBinder onBind(Intent intent) {
//                return null;
//            }
//        };
//        service.setVolumeControlStream()
//        不能是 service吗
        final MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        /**
         * When the beep has finished playing, rewind to queue up another one.
         */
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.seekTo(0);
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mediaPlayer.release();
                return true;
            }
        });
        AssetFileDescriptor file;
        if (rawId == 0) {
            file = context.getResources().openRawResourceFd(R.raw.beep);
        } else {
            file = context.getResources().openRawResourceFd(rawId);
        }
        try {
            mediaPlayer.setDataSource(file.getFileDescriptor(),
                    file.getStartOffset(), file.getLength());
            file.close();
            float BEEP_VOLUME = 1.0f;
            mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("initSpecifiedSound", "what happened to init sound? you need to deal it .");
            return null;
        }
        return mediaPlayer;
    }


    /**
     * init type of  MediaPlayer
     *
     * @param context Activity      不能是 service吗,不能
     * @param rawId   the id of raw
     * @return MediaPlayer
     */
//    public MediaPlayer initMediaPlayer(Activity context, int rawId) {
//        context.setVolumeControlStream(AudioManager.STREAM_MUSIC);
////        Service service=new Service() {
////            @Nullable
////            @Override
////            public IBinder onBind(Intent intent) {
////                return null;
////            }
////        };
////        service.setVolumeControlStream()
////        不能是 service吗
//        final MediaPlayer mediaPlayer = new MediaPlayer();
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        /**
//         * When the beep has finished playing, rewind to queue up another one.
//         */
//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                mediaPlayer.seekTo(0);
//            }
//        });
//        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer mp, int what, int extra) {
//                mediaPlayer.release();
//                return true;
//            }
//        });
//        AssetFileDescriptor file;
//        if (rawId == 0) {
//            file = context.getResources().openRawResourceFd(R.raw.beep);
//        } else {
//            file = context.getResources().openRawResourceFd(rawId);
//        }
//        try {
//            mediaPlayer.setDataSource(file.getFileDescriptor(),
//                    file.getStartOffset(), file.getLength());
//            file.close();
//            float BEEP_VOLUME = 1.0f;
//            mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
//            mediaPlayer.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.i("initSpecifiedSound", "what happened to init sound? you need to deal it .");
//            return null;
//        }
//        return mediaPlayer;
//    }


    //    @SuppressLint({"NewApi", "WrongConstant"})
    @RequiresApi(api = Build.VERSION_CODES.P)
    void startClock(String clockJson) {
//    void startClock(AlarmClock alarmClock ){
        //在闹铃界面点击 暂停再响 或 关闭 时 发送的Intent
//        toReceiverIntent = new Intent(this, AlarmClockReceiver.class);
//        toReceiverIntent.putExtra(CommonValue.ALARM_CLOCK_INTENT, clockJson);
        toReceiverIntent.putExtra(CommonValue.ALARM_CLOCK_INTENT, clockJson);
        //若用户正在和设备交互，则弹出通知，反之弹出Activity
        if (powerManager.isInteractive()) {
            buildNotification(alarmClock);
        } else {
            callActivity(clockJson);
        }



//        I/AlarmClockService: alarmClock AlarmClock{clockId=1, hour=21, minute=39, ringCycle=0,
//        isStarted=true, remark='', ringMusicUri='null', isVibrated=false, stopOnce=false}
//        开启闹钟
//        他是字符串的 null
        //设置响铃音乐以及震动
        if (alarmClock.getRingMusicUri() != null) {
            LogUtil.i(TAG, "发出铃声");
            playRingtone();
//            这里有play ring ，那前面的 callActivity 是什么
//            服务 播放 MediaPlayer 安卓

//            MediaPlayer mediaPlayer = initMediaPlayer(AlarmClockService.this, 0);
//            mediaPlayer.stop();

//            MediaPlayer 30秒之后关掉
//        mediaPlayer 可以播放
//            MediaPlayer me
//            如果打开一个 activity 就好了
//            需要传递一个 clock啊

//            Intent intent = new Intent(this, SoundActivity.class);
//            intent.putExtra(CommonValue.ALARM_CLOCK_INTENT, clockJson);
//            startActivity(intent);

            LogUtil.i(TAG, "开启活动");
            Intent it = new Intent(this, AlarmClockOntimeActivity.class);
            it.putExtra(CommonValue.ALARM_CLOCK_INTENT, clockJson);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(it);

        }
        if (alarmClock.isVibrated()) {
            LogUtil.i(TAG, "开始振动");
            vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0, 1000, 1000, 1000}, 0));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    void playRingtone() {
        //            但是他还是有铃声 的 uri
        ringtone = RingtoneManager.getRingtone(this, Uri.parse(alarmClock.getRingMusicUri()));
        ringtone.setLooping(true);
//                ringtone.setVolume(0.3f);
//                不用音量 主要是这里需要铃声开启 而不是媒体音量
//                https://developer.android.google.cn/reference/kotlin/android/media/Ringtone
//                Float: a raw scalar in range 0.0 to 1.0, where 0.0 mutes this player,
//                and 1.0 corresponds to no attenuation being applied.
//                浮动：0.0到1.0范围内的原始标量，其中0.0使该播放器静音，1.0对应于未应用衰减。
//                ringtone 没有声音
//                https://blog.csdn.net/jhope/article/details/81018312
        ringtone.play();

    }


    //    这个clock 也是class 里面的clock啊
    void pushToNextTime(AlarmClock clock) {
//        AlarmClock clock = new Gson().fromJson(clockJson, AlarmClock.class);
        //在闹铃界面点击 暂停再响 或 关闭 时 发送的Intent
//        这里怎么判断是 冲突 还是因为这一次让他不响起来呢 或者不用判断 直接 设置为 下次会响起来了

        //在闹铃界面点击 暂停再响 或 关闭 时 发送的Intent
//        PendingIntent toReceiverIntent = new Intent(this, AlarmClockReceiver.class);
        clock.setStopOnce(false);

        AlarmClockRepository repository = new AlarmClockRepository(AlarmClockService.this);
//        alarmClock.setStarted(false);
        repository.update(clock);
//        要更新数据库 因为 下次已经不停止了，这回改变ui吗， 可以改变哦

//        toReceiverIntent.putExtra(CommonValue.ALARM_CLOCK_INTENT, clockJson);
        toReceiverIntent.putExtra(CommonValue.ALARM_CLOCK_INTENT, new Gson().toJson(clock));

        PendingIntent pi = PendingIntent.getBroadcast(AlarmClockService.this,
                clock.getClockId(), toReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        int nextRingTime = TimeUtil.nextRingTime(clock.getRingCycle(), clock.getHour(), clock.getMinute());
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + nextRingTime, pi);
        LogUtil.i(TAG, "时间冲突，该闹钟延后" + TimeUtil.dateFormat(nextRingTime) + "执行");
    }

    @Override
    public IBinder onBind(@NonNull Intent intent) {
        return new AlarmClockRingServiceProvider();
    }

    /**
     * 对外暴露接口
     */
    private class AlarmClockRingServiceProvider extends Binder implements AlarmClockServiceProvider {
        /**
         * 5分钟贪睡功能
         */
        @Override
        public void pauseFiveMinRing() {
            snooze();
        }

        /**
         * 关闭闹钟
         */
        @Override
        public void close() {
            dismiss();
        }

        /**
         * 响铃后如果没有在一分钟内做出操作，默认直接进入贪睡模式
         */
        @Override
        public void oneMinClose() {
            pauseFiveMinRing();
        }
    }

    /**
     * 贪睡模式真实实现
     */
    private void snooze() {
        stopMusic();
        stopVibrator();
        int alarmClockId = alarmClock.getClockId();
        PendingIntent pi = PendingIntent.getBroadcast(AlarmClockService.this, alarmClockId, toReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5 * 60 * 1000, pi);
        LogUtil.i(TAG, "----------5分钟后响铃的广播发送完毕！-----------");
    }

    /**
     * 关闭闹钟真实实现
     */
    private void dismiss() {
        stopMusic();
        stopVibrator();
        int alarmClockId = alarmClock.getClockId();
        int ringCycle = alarmClock.getRingCycle();
        int hour = alarmClock.getHour();
        int minute = alarmClock.getMinute();
        PendingIntent pi = PendingIntent.getBroadcast(AlarmClockService.this, alarmClockId, toReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (ringCycle == 0) {
            //如果闹钟只响一次，则更改数据库将闹钟启动选项改成false
            alarmManager.cancel(pi);
            AlarmClockRepository repository = new AlarmClockRepository(AlarmClockService.this);
            alarmClock.setStarted(false);
            repository.update(alarmClock);
            LogUtil.i(TAG, "--只响一次的闹钟广播已取消，数据库更新完毕！！-----");
        } else {
            int nextRingTime = TimeUtil.nextRingTime(ringCycle, hour, minute);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + nextRingTime, pi);
            LogUtil.i(TAG, "--距离下一次闹钟的剩余时间为" + TimeUtil.dateFormat(nextRingTime) + "-----");
        }
    }

    /**
     * 关闭铃声
     */
    private void stopMusic() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
            ringtone = null;
        }
    }

    /**
     * 停止震动
     */
    private void stopVibrator() {
        if (vibrator != null) {
            vibrator.cancel();
            vibrator = null;
        }
    }

    /**
     * 创建通知渠道
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            @SuppressLint("WrongConstant")
            NotificationChannel channel = new NotificationChannel(CommonValue.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * 创建通知
     *
     * @param alarmClock 响铃对应的闹钟对象
     */
    @SuppressLint("WrongConstant")
    private void buildNotification(AlarmClock alarmClock) {
        PendingIntent piClose = buildPendingIntent(CLOSE);
        PendingIntent piSnooze = buildPendingIntent(SNOOZE);
        PendingIntent piDelete = buildPendingIntent(DELETE);

        Notification notification = new NotificationCompat.Builder(this, CommonValue.CHANNEL_ID)
                .setContentTitle(TimeUtil.getShowTime(alarmClock.getHour(), alarmClock.getMinute()))
                .setContentText(alarmClock.getRemark())
                .setWhen(System.currentTimeMillis())
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setSmallIcon(R.mipmap.app_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon))
                .addAction(R.drawable.ic_launcher_foreground, getString(R.string.close_clock), piClose)
                .addAction(R.drawable.ic_launcher_foreground, getString(R.string.ring_later), piSnooze)
                .setDeleteIntent(piDelete)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        notificationManager.notify(alarmClock.getClockId(), notification);
    }

    /**
     * 启动响铃界面
     *
     * @param clockJson 闹铃数据
     */
    private void callActivity(String clockJson) {
        //发送到 闹铃界面的意图
        Intent alarmClockRingIntent = new Intent(this, AlarmClockRingActivity.class);
        alarmClockRingIntent.putExtra(CommonValue.ALARM_CLOCK_INTENT, clockJson);
        alarmClockRingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(alarmClockRingIntent);
    }

    /**
     * 根据action构建对应的PendingIntent
     *
     * @param action 对应的action
     * @return PendingIntent
     */
    private PendingIntent buildPendingIntent(String action) {
        Intent intent = new Intent(this, AlarmClockService.class);
        intent.setAction(action);
        intent.putExtra(NOTIFICATION, FOREGROUND_SERVICE);
        return PendingIntent.getService(this, 0, intent, 0);
    }

}
