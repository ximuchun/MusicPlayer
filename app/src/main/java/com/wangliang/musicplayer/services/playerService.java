package com.wangliang.musicplayer.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.wangliang.musicplayer.presenter.PlayerPresenter;


public class playerService extends Service {
    private PlayerPresenter playerPresenter;
    private String TAG="playerService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        if (playerPresenter == null) {
            playerPresenter = new PlayerPresenter();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return playerPresenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        playerPresenter.unRegisterListControler();
        playerPresenter.unRegisterViewControler();
        playerPresenter = null;
    }
}
