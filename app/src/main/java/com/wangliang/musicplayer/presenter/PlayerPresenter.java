package com.wangliang.musicplayer.presenter;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.wangliang.musicplayer.R;
import com.wangliang.musicplayer.interfaces.MusicPlayControl;
import com.wangliang.musicplayer.interfaces.MusicViewControl;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class PlayerPresenter extends Binder implements MusicPlayControl {
    private MusicViewControl mViewController;
    private String TAG ="PlayerPresenter";
    private int mCurrentState =PLAY_STATE_STOP;
    private MediaPlayer mediaPlayer;
    private Timer mTimer;
    private seekTimeTask mSeekTask;

    @Override
    public void registerViewControler(MusicViewControl viewControler) {
        this.mViewController = viewControler;
    }

    @Override
    public void unRegisterViewControler() {
        this.mViewController = null;
    }

    @Override
    public void playOrPause() {
        Log.d(TAG, "playOrPause: " + mCurrentState);
        if (mCurrentState==PLAY_STATE_STOP) {
            initPlayer();
            try {
                mediaPlayer.setDataSource("/mnt/sdcard/music.mp3");
                mediaPlayer.prepare();
                mViewController.onTotalTimeChange(mediaPlayer.getDuration());
                Log.d(TAG, "playOrPauseTIME: "+ mediaPlayer.getDuration());
                startTimer();
                mediaPlayer.start();
                mCurrentState=PLAY_STATE_PLAY;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (mCurrentState==PLAY_STATE_PAUSE) {
            startTimer();
            mediaPlayer.start();
            mCurrentState=PLAY_STATE_PLAY;
        }else if(mCurrentState==PLAY_STATE_PLAY) {
            mediaPlayer.pause();
            stopTimer();
            mCurrentState=PLAY_STATE_PAUSE;
        }else{
            Log.d(TAG, "playOrPause: ERROR");
        }
        if (mViewController != null) {
            mViewController.onPlayerStateChange(mCurrentState);
        }
    }

    private void initPlayer() {
        mediaPlayer = new MediaPlayer();
        //mediaPlayer.setAudioSessionId(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.seekTo(0);
                mCurrentState=PLAY_STATE_PAUSE;
                mViewController.onPlayerStateChange(mCurrentState);
            }
        });
    }

    @Override
    public void seekTo(int seek) {
        Log.d(TAG, "seekTo: " + seek);
        if (mediaPlayer != null) {
            seek = (int) (seek * 1.0f / 1000.0 * mediaPlayer.getDuration());
            mediaPlayer.seekTo(seek);
        }

    }

    private void startTimer(){
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mSeekTask == null) {
            mSeekTask = new seekTimeTask();
        }
        mTimer.schedule(mSeekTask,0,100);
    }
    private void stopTimer(){
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
            mSeekTask = null;
        }
    }
    private class seekTimeTask extends TimerTask {

        @Override
        public void run() {
            if (mediaPlayer != null) {
                mViewController.onSeekChange(
                        (int) (mediaPlayer.getCurrentPosition()* 1000.0f /mediaPlayer.getDuration()));
                mViewController.onCurrectTimeChange(mediaPlayer.getCurrentPosition());
                mViewController.onPlayerStateChange(mCurrentState);
            }
        }
    }
}
