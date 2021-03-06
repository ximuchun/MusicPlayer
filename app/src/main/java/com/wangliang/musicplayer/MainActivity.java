package com.wangliang.musicplayer;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wangliang.musicplayer.data.GetMusicList;
import com.wangliang.musicplayer.data.SongInfo;
import com.wangliang.musicplayer.data.ListAdapter;
import com.wangliang.musicplayer.interfaces.MusicPlayControl;
import com.wangliang.musicplayer.interfaces.MusicViewControl;
import com.wangliang.musicplayer.services.playerService;

import java.util.List;

import static com.wangliang.musicplayer.utils.Constants.PLAY_STATE_PAUSE;
import static com.wangliang.musicplayer.utils.Constants.PLAY_STATE_PLAY;
import static com.wangliang.musicplayer.utils.Constants.PLAY_STATE_STOP;

public class MainActivity extends AppCompatActivity {

    private ImageButton mBtnLast;
    private ImageButton mBtnPaly;
    private ImageButton mBtnNext;
    private TextView mTxtTotalTime;
    private TextView mCurrectTime;
    private SeekBar mSeekBar;
    private playerConnection mPlayerConnection;
    private MusicPlayControl musicPlayControl;
    private String TAG="MainActivity";
    private boolean isUserTouchSeekBar=false;
    private String string;
    private ListView mListView;
    private List<SongInfo> songList;
    private GetMusicList getMusicList;
    private ListAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: ");
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"},0);
        initView();
        initSongList();
        initEvent();
        initService();
        initBindService();
    }

    private void initSongList() {
        getMusicList = new GetMusicList(this);
        songList = getMusicList.getList();
        mListAdapter = new ListAdapter(this, songList);
        mListView.setAdapter(mListAdapter);
    }

    private void initService() {
        Log.d(TAG, "initService: ");
        startService(new Intent(this,playerService.class));
    }
    private void initBindService() {
        Log.d(TAG, "initBindService: ");
        if(mPlayerConnection == null) {
            mPlayerConnection = new playerConnection();
        }
        Intent intent = new Intent(this, playerService.class);
        bindService(intent,mPlayerConnection,BIND_AUTO_CREATE);
    }
    class playerConnection implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicPlayControl = (MusicPlayControl) service;
            musicPlayControl.registerViewControler(musicViewControl);
            musicPlayControl.registerListControler(mListAdapter);
            musicPlayControl.updateUIrequest();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicPlayControl=null;
        }
    }

    private void initEvent() {
        mBtnOnClick onClick =new mBtnOnClick();
        mBtnLast.setOnClickListener(onClick);
        mBtnPaly.setOnClickListener(onClick);
        mBtnNext.setOnClickListener(onClick);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserTouchSeekBar=true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isUserTouchSeekBar=false;
                Log.d(TAG, "onStopTrackingTouch: "+ mSeekBar.getProgress());
                if (musicPlayControl != null){
                    musicPlayControl.seekTo(mSeekBar.getProgress());
                }
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListAdapter.setCurrectPosition(position);
                mListAdapter.notifyDataSetChanged();
                musicPlayControl.playOrPause(songList.get(position).path);
            }
        });
    }

    private void initView() {
        mBtnLast=findViewById(R.id.mBtnLast);
        mBtnPaly=findViewById(R.id.mBtnPlay);
        mBtnNext=findViewById(R.id.mBtnNext);
        mTxtTotalTime=findViewById(R.id.totalTime);
        mCurrectTime=findViewById(R.id.currectTime);
        mSeekBar=findViewById(R.id.mSeekBar);
        mListView = findViewById(R.id.song_list);
    }

    class mBtnOnClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if( musicPlayControl == null || songList.size()==0) {
                return;
            }
            switch (v.getId()){
                case R.id.mBtnLast:
                    musicPlayControl.playLast();
                    break;
                case R.id.mBtnPlay:
                    musicPlayControl.playOrPause(null);
                    break;
                case R.id.mBtnNext:
                    musicPlayControl.playNext();
                    break;
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerConnection != null){
            Log.d(TAG, "onDestroy: ");
            musicPlayControl.setCurrectListPosition(mListAdapter.getCurrectPosition());
            //musicPlayControl.unRegisterViewControler();
            unbindService(mPlayerConnection);
            //stopService(new Intent(this,playerService.class));
        }
    }

    private MusicViewControl musicViewControl = new MusicViewControl() {
        @Override
        public void onPlayerStateChange(final int states) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (states){
                        case PLAY_STATE_PLAY:
                            mBtnPaly.setImageResource(R.mipmap.btn_pause);
                            break;
                        case PLAY_STATE_PAUSE:
                        case PLAY_STATE_STOP:
                            mBtnPaly.setImageResource(R.mipmap.btn_play);
                            break;
                    }
                }
            });
        }

        @Override
        public void onTotalTimeChange(int time) {
            String string2 =String.format("%02d:%02d",time/1000/60,time/1000%60);
            mTxtTotalTime.setText(string2);
        }

        @Override
        public void onCurrectTimeChange(final int time) {
            string = String.format("%02d:%02d",time/1000/60,time/1000%60);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCurrectTime.setText(string);
                }
            });
        }

        @Override
        public void onSeekChange(int seek) {
            if (!isUserTouchSeekBar) {
                mSeekBar.setProgress(seek);
            }
        }
    };
}
