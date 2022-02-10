package cn.chenjianlink.android.alarmclock.utils;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

import cn.chenjianlink.android.alarmclock.R;

public class MyMediaPlayer {

    private MediaPlayer mediaPlayer;
    MyMediaPlayer(){

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
