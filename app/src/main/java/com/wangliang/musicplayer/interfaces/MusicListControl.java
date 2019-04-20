package com.wangliang.musicplayer.interfaces;

public interface MusicListControl {

    void setCurrectPosition(int position);
    int getCurrectPosition();
    int getLastPosition();
    int getNextPosition();
    void onPlayerStateChange(int status);

}
