package com.example.admin.ielts_speaking_simulator;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Admin on 6/4/2018.
 */

public class MyAdapter extends BaseAdapter {
    Context c;
    ArrayList<String> dsQuest;
    ArrayList<String> dsRecord;
    MediaPlayer mediaPlayer ;
    MediaRecorder mediaRecorder ;

    MyAdapter(Context c, ArrayList<String> dsQuest, ArrayList<String> dsRecord) {
        this.c = c;
        this.dsQuest = dsQuest;
        this.dsRecord = dsRecord;
    }

    public static class View_Mot_O {
        TextView txt_result, txt_record;
        ImageView img_play;
        ImageView img_stop;
    }

    @Override
    public int getCount() {
        return dsQuest.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int arg0, View arg1, ViewGroup viewGroup) {
        final View_Mot_O mot_o;
        LayoutInflater inf = ((Activity) c).getLayoutInflater();
        if (arg1 == null) {
            mot_o = new View_Mot_O();
            arg1 = inf.inflate(R.layout.one_item, null);
            mot_o.txt_result = (TextView) arg1.findViewById(R.id.txtresult);
            mot_o.img_play = (ImageView) arg1.findViewById(R.id.imgplay);
            mot_o.img_stop = (ImageView) arg1.findViewById(R.id.imgstop);
            arg1.setTag(mot_o);
        } else
            mot_o = (View_Mot_O) arg1.getTag();

        mot_o.txt_result.setText(dsQuest.get(arg0));
        mot_o.img_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mot_o.img_play.setVisibility(View.GONE);
                mot_o.img_stop.setVisibility(View.VISIBLE);
                mediaPlayer = new MediaPlayer();

                try {
                    mediaPlayer.setDataSource(dsRecord.get(arg0));
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();

            }
        });

        mot_o.img_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mot_o.img_play.setVisibility(View.VISIBLE);
                mot_o.img_stop.setVisibility(View.GONE);
                if(mediaPlayer != null){

                    mediaPlayer.stop();
                    mediaPlayer.release();

                    mediaRecorder=new MediaRecorder();
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                    mediaRecorder.setOutputFile(dsRecord.get(arg0));
                }
            }
        });

        return arg1;
    }
}
