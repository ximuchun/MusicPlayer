package com.wangliang.musicplayer.data;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangliang.musicplayer.R;
import com.wangliang.musicplayer.interfaces.MusicListControl;

import java.util.List;

import static com.wangliang.musicplayer.utils.Constants.PLAY_STATE_PAUSE;
import static com.wangliang.musicplayer.utils.Constants.PLAY_STATE_PLAY;
import static com.wangliang.musicplayer.utils.Constants.PLAY_STATE_STOP;

public class ListAdapter extends BaseAdapter implements MusicListControl {
    Context context;
    public List<SongInfo> list;
    private int innerPosition;
    private Myholder myholder;
    private int play_status = PLAY_STATE_STOP;

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
        if (convertView == null) {
            myholder = new Myholder();
            convertView = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.song_list,null);

            myholder.t_song = convertView.findViewById(R.id.name);
            myholder.t_image = convertView.findViewById(R.id.image);

            myholder.operatingAnim = ObjectAnimator.ofFloat(myholder.t_image, "rotation", 0.0f, 360.0f);
            myholder.operatingAnim.setDuration(10000);
            myholder.operatingAnim.setInterpolator(new LinearInterpolator());
            myholder.operatingAnim.setRepeatCount(-1);
            myholder.operatingAnim.setRepeatMode(ObjectAnimator.RESTART);

            convertView.setTag(myholder);

        } else {
            myholder = (Myholder) convertView.getTag();
        }

        myholder.t_song.setText(list.get(position).song.toString());

        if(position == getCurrectPosition()){

            if (play_status == PLAY_STATE_PLAY){
                if( myholder.operatingAnim.isPaused()){
                    myholder.operatingAnim.resume();
                }else{
                    myholder.operatingAnim.start();
                }
            }else if(play_status == PLAY_STATE_PAUSE){
                myholder.operatingAnim.pause();
            }
        }else{
            myholder.operatingAnim.end();
        }

        return convertView;
    }
    private class Myholder {
        TextView t_song;
        ImageView t_image;
        ObjectAnimator operatingAnim;
    }

    @Override
    public void setCurrectPosition(int position){
        this.innerPosition = position;
    }

    @Override
    public  int getCurrectPosition(){
        return  innerPosition;
    }

    @Override
    public  int getLastPosition(){
        return  (innerPosition-1)<0 ? (list.size()-1) : (innerPosition-1);
    }

    @Override
    public  int getNextPosition(){
        return  (innerPosition+1)>(list.size()-1) ? 0 : (innerPosition+1);
    }

    @Override
    public void onPlayerStateChange(int status) {
        this.play_status = status;
        notifyDataSetChanged();
    }
}
