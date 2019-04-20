package com.wangliang.musicplayer.data;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class GetMusicList{
    private final Context mContext;
    private final String TAG="GetMusicList";
    private List<SongInfo> list;

    public GetMusicList(Context context){
        this.mContext = context;
        list = new ArrayList<>();
        Cursor cursor = mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,null,null,MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()){
                SongInfo songInfo =new SongInfo();
                songInfo.song = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                songInfo.singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                songInfo.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                songInfo.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));

                Log.d(TAG, songInfo.song+":"+songInfo.singer+":"+songInfo.path+":"+songInfo.duration);
                list.add(songInfo);
            }
        }
        cursor.close();
    }

    public List<SongInfo> getList(){
        return list;
    }

}
