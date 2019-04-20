package com.wangliang.musicplayer.interfaces;

import com.wangliang.musicplayer.data.ListAdapter;

public interface MusicPlayControl {

    void registerViewControler(MusicViewControl viewControler);
    void registerListControler(ListAdapter listControler);
    void unRegisterViewControler();
    void unRegisterListControler();
    void playOrPause(String URL);
    void playLast();
    void playNext();
    void seekTo(int seek);
    void updateUIrequest();
    void setCurrectListPosition(int position);
}
