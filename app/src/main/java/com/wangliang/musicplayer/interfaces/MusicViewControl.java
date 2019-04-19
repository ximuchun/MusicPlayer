package com.wangliang.musicplayer.interfaces;

public interface MusicViewControl {

    void onPlayerStateChange(int states);
    void onTotalTimeChange(int time);
    void onCurrectTimeChange(int time);
    void onSeekChange(int seek);
    void onListChange();
}
