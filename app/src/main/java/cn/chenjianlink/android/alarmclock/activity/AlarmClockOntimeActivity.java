/*
 * Copyright (c) 2016 咖枯 <kaku201313@163.com | 3772304@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package cn.chenjianlink.android.alarmclock.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

//import com.kaku.weac.fragment.AlarmClockOntimeFragment;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Optional;

import cn.chenjianlink.android.alarmclock.R;
import cn.chenjianlink.android.alarmclock.common.Commons;
import cn.chenjianlink.android.alarmclock.model.AlarmClock;
import cn.chenjianlink.android.alarmclock.utils.AudioPlayer;
import cn.chenjianlink.android.alarmclock.utils.CommonValue;
import cn.chenjianlink.android.alarmclock.utils.LogUtil;

/**
 * 闹钟响起画面Activity
 *
 * @author 咖枯
 * @version 1.0 2015/06
 */
public class AlarmClockOntimeActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "AlarmClockOntimeActivity";
    private MediaPlayer mediaPlayer;
    private AudioPlayer audioPlayer;

//    @Override
//    protected Fragment createFragment() {
//        return new AlarmClockOntimeFragment();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sound);
        LogUtil.i(TAG, "onCreate");
        setContentView(R.layout.alarm_clock_ontime_activity);
        //初始化这三个方法
//        ringtone = initRingtone(this);
////没有声音  是因为音乐太大吗？ 并不是 这里是设置了一个默认铃声，所以说铃声是要开启的呀，
//// 不是闹钟音量  而是铃声啊
//        soundPool = initSoundPool();
////        https://blog.csdn.net/bxdzyhx/article/details/107418897
//        loadId = soundPool.load(this, R.raw.beep, 1);
////没声音
        LogUtil.i(TAG, "初始化音乐");
//        mediaPlayer = initMediaPlayer(this, 0);
//        mediaPlayer 选择本地音乐打开


        playMusic();
//        playMusicRaw();

//        mediaPlayer = initMediaPlayer(this, R.raw.beep);
////        mediaPlayer 可以播放
//        if(mediaPlayer!=null){
//            mediaPlayer.start();
//        }else{
//            LogUtil.i(TAG, "音乐初始化失败");
//        }

        findViewById(R.id.btn_stop).setOnClickListener(this);
//        findViewById(R.id.bt_sound_pool).setOnClickListener(this);
//        findViewById(R.id.bt_media).setOnClickListener(this);
//        findViewById(R.id.btn_back).setOnClickListener(this);
    }

    void playMusic() {
        Intent intent = getIntent();
        String clockJson = intent.getStringExtra(CommonValue.ALARM_CLOCK_INTENT);
        //若是service还未执行过，则执行相应的操作，若已经执行过了，则将重复的闹钟往后延
        AlarmClock alarmClock = new Gson().fromJson(clockJson, AlarmClock.class);
        LogUtil.i(TAG, "alarmClock " + alarmClock);
        playRing(alarmClock);

    }

    void playMusicRaw() {
        mediaPlayer = initMediaPlayer(this, R.raw.beep);
//        mediaPlayer 可以播放
        if (mediaPlayer != null) {
            mediaPlayer.start();
        } else {
            LogUtil.i(TAG, "音乐初始化失败");
        }


    }


    @Override
    public void onBackPressed() {
        // 禁用back键
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //release resource of this two types.
//        soundPool.release();
//        if(mediaPlayer!=null){
//            mediaPlayer.release();
//        }
        if(audioPlayer!=null){
//            audioPlayer.release();
            audioPlayer.stop();
        }
//        Optional.ofNullable()
//        Optional.empty().
//        Optional.ofNullable(audioPlayer).isPresent().get().stop();
    }

    /**
     * init type of Ringtone
     *
     * @param context Activity
     * @return Ringtone
     */
    private Ringtone initRingtone(Activity context) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        return RingtoneManager.getRingtone(context, notification);
    }

    /**
     * init type of  SoundPool
     *
     * @return SoundPool
     */
    @SuppressWarnings("deprecation")
    private SoundPool initSoundPool() {

//      The content of the comments need API-23
//      SoundPool.Builder builder=new SoundPool.Builder();
//      builder.setMaxStreams(10);
//
//      AudioAttributes.Builder audioBuilder=new AudioAttributes.Builder();
//      audioBuilder.setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN);
//      audioBuilder.setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED);
//      audioBuilder.setLegacyStreamType(AudioManager.STREAM_SYSTEM);
//      audioBuilder.setUsage(AudioAttributes.USAGE_NOTIFICATION);
//      AudioAttributes attributes = audioBuilder.build();
//
//      builder.setAudioAttributes(attributes);
//      SoundPool soundPool = builder.build();
        return new SoundPool(10, AudioManager.STREAM_NOTIFICATION, 5);
    }

    /**
     * init type of  MediaPlayer
     *
     * @param context Activity
     * @param rawId   the id of raw
     * @return MediaPlayer
     * 貌似没有发出声音啊
     */
    public MediaPlayer initMediaPlayer(Activity context, int rawId) {
        context.setVolumeControlStream(AudioManager.STREAM_MUSIC);
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
     * 声音管理
     */
    private AudioManager mAudioManager;
    /**
     * 当前音量
     */
    private int mCurrentVolume;

    /**
     * 播放铃声
     * 如果没有设置闹钟 就来个默认的
     */
    private void playRing(AlarmClock mAlarmClock) {
        Activity activity = this;
        mAudioManager = (AudioManager) activity.getSystemService(
                Context.AUDIO_SERVICE);
//        mCurrentVolume = mAudioManager
//                .getStreamVolume(AudioManager.STREAM_MUSIC);
//        https://blog.csdn.net/tuke_tuke/article/details/50739150
        mCurrentVolume = mAudioManager
                .getStreamVolume(AudioManager.STREAM_ALARM);
//        AudioManager.STREAM_ALARM
//        挑选铃声的时候 可以响起来吗
        if (mAlarmClock != null) {
            // 设置铃声音量
//            int volume=mAlarmClock.getVolume();
//            int volume=1;
            int volume = 6;
//            6, 是一个可能的音量
//            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
//                    volume, AudioManager.ADJUST_SAME);
            mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,
                    mCurrentVolume, AudioManager.ADJUST_SAME);
//            这个 有什么用处，也没有调用，是一个线程吗，也就是现在播放的音乐都是这个设置？

            doPlay(mAlarmClock,activity);
//
//            String ringMusicUri = mAlarmClock.getRingMusicUri();
//            boolean isVibrated = mAlarmClock.isVibrated();
//            // 默认铃声
////            AudioPlayer audioPlayer = AudioPlayer.getInstance(activity);
////            audioPlayer.play("/mnt/sdcard/3/tv.danmaku.bili/download/28525359/1/16/video.m4s",
////                    false,false);
////            url 怎么写
////            安卓 文件 url
//            AudioPlayer audioPlayer = AudioPlayer.getInstance(activity);
//
//            if (ringMusicUri == null) {
//                audioPlayer.playRaw(R.raw.ring_weac_alarm_clock_default, true, true);
//                return;
//            }
//            if (ringMusicUri.equals(Commons.DEFAULT_RING_URL)
//                    || TextUtils.isEmpty(ringMusicUri)) {
//                // 振动模式
////                if (isVibrated) {
////                    // 播放
////                    AudioPlayer.getInstance(activity).playRaw(
////                            R.raw.ring_weac_alarm_clock_default, true, true);
////                } else {
////                    AudioPlayer.getInstance(activity).playRaw(
////                            R.raw.ring_weac_alarm_clock_default, true, false);
////                }
//                playDefault(audioPlayer, isVibrated);
//                // 无铃声
//            } else if (ringMusicUri.equals(Commons.NO_RING_URL)) {
//                // 振动模式
//                if (isVibrated) {
//                    AudioPlayer.getInstance(activity).stop();
//                    AudioPlayer.getInstance(activity).vibrate();
//                } else {
//                    AudioPlayer.getInstance(activity).stop();
//                }
//            } else {
//                // 振动模式
//                if (isVibrated) {
//                    AudioPlayer.getInstance(activity).play(
//                            ringMusicUri, true, true);
//                } else {
//                    AudioPlayer.getInstance(activity).play(
//                            ringMusicUri, true, false);
//                }
//            }
        } else {
            AudioPlayer.getInstance(activity).playRaw(
                    R.raw.ring_weac_alarm_clock_default, true, true);
        }
    }

    void doPlay(AlarmClock mAlarmClock,  Activity activity){
        String ringMusicUri = mAlarmClock.getRingMusicUri();
        boolean isVibrated = mAlarmClock.isVibrated();
        // 默认铃声
//            AudioPlayer audioPlayer = AudioPlayer.getInstance(activity);
//            audioPlayer.play("/mnt/sdcard/3/tv.danmaku.bili/download/28525359/1/16/video.m4s",
//                    false,false);
//            url 怎么写
//            安卓 文件 url
        audioPlayer     = AudioPlayer.getInstance(activity);
//        AudioPlayer audioPlayer = AudioPlayer.getInstance(activity);

        if (ringMusicUri == null) {
            audioPlayer.playRaw(R.raw.ring_weac_alarm_clock_default, true, true);
            return;
        }
        if (ringMusicUri.equals(Commons.DEFAULT_RING_URL)
                || TextUtils.isEmpty(ringMusicUri)) {
            // 振动模式
//                if (isVibrated) {
//                    // 播放
//                    AudioPlayer.getInstance(activity).playRaw(
//                            R.raw.ring_weac_alarm_clock_default, true, true);
//                } else {
//                    AudioPlayer.getInstance(activity).playRaw(
//                            R.raw.ring_weac_alarm_clock_default, true, false);
//                }
            playDefault(audioPlayer, isVibrated);
            // 无铃声
        } else if (ringMusicUri.equals(Commons.NO_RING_URL)) {
            // 振动模式
            if (isVibrated) {
                AudioPlayer.getInstance(activity).stop();
                AudioPlayer.getInstance(activity).vibrate();
            } else {
                AudioPlayer.getInstance(activity).stop();
            }
        } else {
            // 振动模式 是不是
            AudioPlayer.getInstance(activity).play(
                    ringMusicUri, true, isVibrated);
        }
    }
    void playDefault(AudioPlayer audioPlayer, boolean isVibrated) {
        // 振动模式
        // 播放
        audioPlayer.playRaw(
                R.raw.ring_weac_alarm_clock_default, true, isVibrated);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_stop:
//                audioPlayer
                if(audioPlayer!=null){
                    audioPlayer.stop();
                }
//                if(mediaPlayer!=null){
//                    mediaPlayer.stop();
//                }

//                this.
//                activity destroy
//                https://blog.csdn.net/Listening_music/article/details/6892730?locationNum=9
                finish();
                break;
//            case R.id.btn_back:
//
//                Intent intent = new Intent(this, MainActivity.class);
//                startActivity(intent);
//                break;
        }
    }


}
