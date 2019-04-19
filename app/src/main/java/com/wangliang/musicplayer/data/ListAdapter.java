package com.wangliang.musicplayer.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wangliang.musicplayer.MainActivity;
import com.wangliang.musicplayer.R;

import java.util.List;

public class ListAdapter extends BaseAdapter {
    Context context;
    List<SongInfo> list;

    public ListAdapter(Context mainActivity, List<SongInfo> list) {
        this.context = mainActivity;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Myholder myholder;
        if (convertView == null) {
            myholder = new Myholder();
            convertView = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.song_list, null);

            myholder.t_song = convertView.findViewById(R.id.name);
            convertView.setTag(myholder);

        } else {
            myholder = (Myholder) convertView.getTag();
        }

        myholder.t_song.setText(list.get(position).song.toString());

        return convertView;
    }
    class Myholder {
        TextView t_song;
    }
}
