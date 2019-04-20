package com.wangliang.musicplayer.presenter;

import android.media.MediaPlayer;
import android.os.Binder;
import android.util.Log;

import com.wangliang.musicplayer.data.ListAdapter;
import com.wangliang.musicplayer.interfaces.MusicPlayControl;
import com.wangliang.musicplayer.interfaces.MusicViewControl;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static com.wangliang.musicplayer.utils.Constants.PLAY_STATE_PAUSE;
import static com.wangliang.musicplayer.utils.Constants.PLAY_STATE_PLAY;
import static com.wangliang.musicplayer.utils.Constants.PLAY_STATE_STOP;


public class PlayerPresenter extends Binder implements MusicPlayControl {
    private MusicViewControl mViewController;
    private String TAG ="PlayerPresenter";
    private int mCurrentState =PLAY_STATE_STOP;
    private MediaPlayer mediaPlayer;
    private Timer mTimer;
    private seekTimeTask mSeekTask;
    private ListAdapter mListControler;
    private int currectListPosition;

    @Override
    public void registerViewControler(MusicViewControl viewControler) {
        this.mViewController = viewControler;
    }

    @Override
    public void registerListControler(ListAdapter listControler) {
        this.mListControler =listControler;
    }

    @Override
    public void unRegisterViewControler() {
        this.mViewController = null;
    }

    @Override
    public void unRegisterListControler() {
        this.mListControler = null;
    }

    @Override
    public void playOrPause(String URL) {
        if (URL != null) {
            initPlayer();
        }
        if (mediaPlayer == null) {
            mListControler.setCurrectPosition(0);
            playOrPause(mListControler.list.get(mListControler.getCurrectPosition()).path);
            return;
        }
        Log.d(TAG, "playOrPause: " + mCurrentState);
        if (mCurrentState==PLAY_STATE_STOP) {
            try {
                mediaPlayer.setDataSource(URL);
                mediaPlayer.prepare();
                mViewController.onTotalTimeChange(mediaPlayer.getDuration());
                Log.d(TAG, "playOrPauseTIME: "+ mediaPlayer.getDuration());
                startTimer();
                mediaPlayer.start();
                mCurrentState=PLAY_STATE_PLAY;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (mCurrentState==PLAY_STATE_PAUSE) {
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
            mListControler.onPlayerStateChange(mCurrentState);
        }
    }

    @Override
    public void playLast() {
        playOrPause(mListControler.list.get(mListControler.getLastPosition()).path);
        mListControler.setCurrectPosition(mListControler.getLastPosition());
    }

    @Override
    public void playNext() {
        playOrPause(mListControler.list.get(mListControler.getNextPosition()).path);
        mListControler.setCurrectPosition(mListControler.getNextPosition());
    }

    private void initPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        if(mCurrentState != PLAY_STATE_STOP){
            stopTimer();
            mediaPlayer.stop();
            mediaPlayer.reset();
            mCurrentState = PLAY_STATE_STOP;
        }
        //mediaPlayer.setAudioSessionId(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.seekTo(0);
                mViewController.onSeekChange(0);
                mViewController.onCurrectTimeChange(0);
                mCurrentState=PLAY_STATE_PAUSE;
                mViewController.onPlayerStateChange(mCurrentState);
                mListControler.onPlayerStateChange(mCurrentState);
                stopTimer();
//                mp.release();

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

    @Override
    public void updateUIrequest() {

        if (mediaPlayer != null) {

            mViewController.onSeekChange(
                    (int) (mediaPlayer.getCurrentPosition()* 1000.0f /mediaPlayer.getDuration()));
            mViewController.onCurrectTimeChange(mediaPlayer.getCurrentPosition());
            mViewController.onTotalTimeChange(mediaPlayer.getDuration());
            mListControler.setCurrectPosition(currectListPosition);
            mViewController.onPlayerStateChange(mCurrentState);
            mListControler.onPlayerStateChange(mCurrentState);

        }
    }

    @Override
    public void setCurrectListPosition(int position) {
        this.currectListPosition = position;
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
            }
        }
    }
}
