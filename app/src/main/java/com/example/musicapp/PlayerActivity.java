package com.example.musicapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    Bundle songExtraData;
    ImageView prev,play,next;
    int position;
    SeekBar mSeekBarTime;
    static MediaPlayer mMediaPlayer;
    TextView songName;

    ArrayList<File> musicList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        prev=findViewById(R.id.previous);
        play=findViewById(R.id.play);
        next=findViewById(R.id.next);
        mSeekBarTime=findViewById(R.id.mSeekBarTime);
        songName=findViewById(R.id.songName);

        if(mMediaPlayer!=null) {
            mMediaPlayer.stop();
        }

        Intent intent=getIntent();
        songExtraData=intent.getExtras();

        musicList=(ArrayList)songExtraData.getParcelableArrayList("songsList");
        position=songExtraData.getInt("position",0);

        initializeMusicPlayer(position);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position<musicList.size()-1){
                    position++;
                }
                else{
                    position=0;
                }
                initializeMusicPlayer(position);
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position<=0){
                    position=musicList.size();
                }
                else{
                    position++;
                }
                initializeMusicPlayer(position);
            }
        });
    }

    private void initializeMusicPlayer(int position) {
        if(mMediaPlayer!=null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.reset();
        }
        String name=musicList.get(position).getName();
        songName.setText(name);

        Uri uri =Uri.parse(musicList.get(position).toString());

        mMediaPlayer=MediaPlayer.create(this,uri);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                mSeekBarTime.setMax(mMediaPlayer.getDuration());


                play.setImageResource(R.drawable.pause);
                mMediaPlayer.start();
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                play.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            }
        });

        mSeekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mSeekBarTime.setProgress(progress);
                    mMediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                 while(mMediaPlayer!=null){
                     try{
                         if(mMediaPlayer.isPlaying()){
                             Message message=new Message();
                             message.what=mMediaPlayer.getCurrentPosition();
                             handler.sendMessage(message);
                             Thread.sleep(1000);
                         }
                     } catch(InterruptedException e){
                         e.printStackTrace();
                     }
                 }
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler =new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            mSeekBarTime.setProgress(msg.what);
        }
    };

    private  void play(){

        if(mMediaPlayer!=null && mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
            play.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }
        else{
            mMediaPlayer.start();
            play.setImageResource(R.drawable.pause);

        }
    }


}
