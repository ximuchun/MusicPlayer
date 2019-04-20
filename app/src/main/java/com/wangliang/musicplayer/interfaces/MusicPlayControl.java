package com.wangliang.musicplayer.interfaces;

public interface MusicPlayControl {

    int PLAY_STATE_PLAY = 1;
    int PLAY_STATE_PAUSE = 2;
    int PLAY_STATE_STOP = 3;

    void registerViewControler(MusicViewControl viewControler);
    void unRegisterViewControler();
    void playOrPause(String URL);
    void seekTo(int seek);
    void getMusic();
}
