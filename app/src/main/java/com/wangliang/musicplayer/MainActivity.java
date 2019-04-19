package com.wangliang.musicplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wangliang.musicplayer.data.GetMusicList;
import com.wangliang.musicplayer.data.SongInfo;
import com.wangliang.musicplayer.data.ListAdapter;
import com.wangliang.musicplayer.interfaces.MusicPlayControl;
import com.wangliang.musicplayer.interfaces.MusicViewControl;
import com.wangliang.musicplayer.services.playerService;

import java.util.ArrayList;
import java.util.List;

import static com.wangliang.musicplayer.interfaces.MusicPlayControl.PLAY_STATE_PAUSE;
import static com.wangliang.musicplayer.interfaces.MusicPlayControl.PLAY_STATE_PLAY;
import static com.wangliang.musicplayer.interfaces.MusicPlayControl.PLAY_STATE_STOP;

public class MainActivity extends AppCompatActivity {

    private Button mBtnLast;
    private Button mBtnPaly;
    private Button mBtnNext;
    private TextView mTxtTotalTime;
    private TextView mCurrectTime;
    private SeekBar mSeekBar;
    private playerConnection mPlayerConnection;
    private MusicPlayControl musicPlayControl;
    private String TAG="MainActivity";
    private boolean isUserTouchSeekBar=false;
    private String string;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: ");
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"},0);
        initView();
        initEvent();
        initService();
        initBindService();


        List<SongInfo> list;
        list = new ArrayList<>();
        GetMusicList getMusicList =new GetMusicList(this);
        list = getMusicList.getList();

        ListAdapter listAdapter = new ListAdapter(this,list);
        mListView.setAdapter(listAdapter);
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
            switch (v.getId()){
                case R.id.mBtnLast:
                    Toast.makeText(getApplicationContext(),"mBtnLast",Toast.LENGTH_SHORT).show();
                     break;
                case R.id.mBtnPlay:
                    if( musicPlayControl != null) {
                        musicPlayControl.playOrPause();
                    }
                    Toast.makeText(getApplicationContext(),"mBtnPlay",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.mBtnNext:
                    Toast.makeText(getApplicationContext(),"mBtnNext",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerConnection != null){
            Log.d(TAG, "onDestroy: ");
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
                            mBtnPaly.setText("Pause");
                            break;
                        case PLAY_STATE_PAUSE:
                        case PLAY_STATE_STOP:
                            mBtnPaly.setText("Play");
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
            Log.d(TAG, "onCurrectTimeChange:" + time);
        }

        @Override
        public void onSeekChange(int seek) {
            if (!isUserTouchSeekBar) {
                mSeekBar.setProgress(seek);
            }
        }

        @Override
        public void onListChange() {

        }
    };
}
