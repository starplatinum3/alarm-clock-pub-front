package cn.chenjianlink.android.alarmclock.activity;
//package org.tuzhao.demo.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

//import org.tuzhao.sqlite.R;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import cn.chenjianlink.android.alarmclock.R;

/**
 * https://www.cnblogs.com/Free-Thinker/p/6704932.html
 * three types to play sound
 * @author tuzhao
 */
public class SoundActivity extends AppCompatActivity implements View.OnClickListener {

    private Ringtone ringtone;

    private SoundPool soundPool;
    private int loadId;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);
        //初始化这三个方法
        ringtone = initRingtone(this);
//没有声音  是因为音乐太大吗？ 并不是 这里是设置了一个默认铃声，所以说铃声是要开启的呀，
// 不是闹钟音量  而是铃声啊
        soundPool = initSoundPool();
//        https://blog.csdn.net/bxdzyhx/article/details/107418897
        loadId = soundPool.load(this, R.raw.beep, 1);
//没声音
        mediaPlayer = initMediaPlayer(this, 0);
//        mediaPlayer 可以播放

        findViewById(R.id.bt_ringtone).setOnClickListener(this);
        findViewById(R.id.bt_sound_pool).setOnClickListener(this);
        findViewById(R.id.bt_media).setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_ringtone:
                ringtone.play();
                break;
            case R.id.bt_sound_pool:
                soundPool.play(loadId, 1.0f, 1.0f, 0, 0, 1.0f);
                break;
            case R.id.bt_media:
                mediaPlayer.start();
//                mediaPlayer.stop();
                break;
            case R.id.btn_back:

                Intent intent=new Intent(SoundActivity.this, MainActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //release resource of this two types.
        soundPool.release();
        mediaPlayer.release();
    }

    /**
     * init type of Ringtone
     * @param context Activity
     * @return Ringtone
     */
    private Ringtone initRingtone(Activity context) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        return RingtoneManager.getRingtone(context, notification);
    }

    /**
     * init type of  SoundPool
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
     * @param context Activity
     * @param rawId   the id of raw
     * @return MediaPlayer
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

}