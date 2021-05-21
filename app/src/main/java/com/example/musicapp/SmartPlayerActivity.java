package com.example.musicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class SmartPlayerActivity extends AppCompatActivity {
    RelativeLayout parentRelativeLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private String keeper="";
    private ImageView pausebtn,nextbtn,prevbtn;
    private TextView songNametxt;
    private LinearLayout linearLayoutlower;
    private Button voiceEnabledBtn;
    private String mode="ON";

    private MediaPlayer myMediaPlayer;
    private int position;
    private ArrayList<File> mySongs;
    private String mSongName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_player);


        checkVoiceCommandPermission();
        pausebtn=findViewById(R.id.play);
        nextbtn=findViewById(R.id.next);
        prevbtn=findViewById(R.id.previous);
        voiceEnabledBtn=findViewById(R.id.voice_enabled_btn);
        linearLayoutlower=findViewById(R.id.linearlayoutlower);
        songNametxt=findViewById(R.id.songName);




        speechRecognizer=speechRecognizer.createSpeechRecognizer(SmartPlayerActivity.this);
        speechRecognizerIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        validateReceived();

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {
                myMediaPlayer.pause();
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matchesFound=results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(matchesFound!=null){
                    if(mode.equals("ON")){
                        keeper=matchesFound.get(0);
                        if(keeper.equals("pause the song") || keeper.equals("pause")){
                            playpauseSong();
                            Toast.makeText(SmartPlayerActivity.this, "Command= "+keeper, Toast.LENGTH_LONG).show();
                        }
                        else if(keeper.equals("play the song") || keeper.equals("play")){
                            playpauseSong();
                            Toast.makeText(SmartPlayerActivity.this, "Command= "+keeper, Toast.LENGTH_LONG).show();
                        }
                        else if(keeper.equals("play next song") || keeper.equals("next song")){
                            playNextSong();
                            Toast.makeText(SmartPlayerActivity.this, "Command= "+keeper, Toast.LENGTH_LONG).show();
                        }
                        else if(keeper.equals("play previous song") || keeper.equals("previous song")){
                            playPreviousSong();
                            Toast.makeText(SmartPlayerActivity.this, "Command= "+keeper, Toast.LENGTH_LONG).show();
                        }

                    }

                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        parentRelativeLayout=findViewById(R.id.parentRelativeLayout);
        parentRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechRecognizerIntent);
                        keeper="";
                        break;
                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        break;
                }
                return false;
            }

        });

        voiceEnabledBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mode.equals("ON")){
                    mode="OFF";
                    voiceEnabledBtn.setText("Voice Enabled Mode - ON");
                    linearLayoutlower.setVisibility(View.VISIBLE);
                }
                else if (mode.equals("OFF")){
                    mode="ON";
                    voiceEnabledBtn.setText("Voice Enabled Mode - OFF");
                    linearLayoutlower.setVisibility(View.GONE);
                }
            }
        });

        pausebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playpauseSong();
            }
        });

        prevbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myMediaPlayer.getCurrentPosition()>0){
                    playPreviousSong();
                }
            }
        });
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myMediaPlayer.getCurrentPosition()>0){
                    playNextSong();
                }
            }
        });

    }

    private void validateReceived(){
        if(myMediaPlayer!=null) {
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();

        mySongs=(ArrayList)bundle.getParcelableArrayList("song");
        mSongName=mySongs.get(position).getName();
        String songName=intent.getStringExtra("name");

        songNametxt.setText(songName);
        songNametxt.setSelected(true);

        position=bundle.getInt("position",0);
        Uri uri=Uri.parse(mySongs.get(position).toString());

        myMediaPlayer=MediaPlayer.create(SmartPlayerActivity.this,uri);
        myMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                pausebtn.setImageResource(R.drawable.pause);
                myMediaPlayer.start();
            }
        });


    }

    private void checkVoiceCommandPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(!(ContextCompat.checkSelfPermission(SmartPlayerActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)){
                Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }

    private void playpauseSong(){
        if(myMediaPlayer.isPlaying()){
            pausebtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            myMediaPlayer.pause();
        }
        else{
            pausebtn.setImageResource(R.drawable.pause);
            myMediaPlayer.start();
        }
    }

    private void playNextSong(){
        myMediaPlayer.pause();
        myMediaPlayer.stop();
        myMediaPlayer.release();

        position=((position+1)%mySongs.size());

        Uri uri=Uri.parse(mySongs.get(position).toString());
        myMediaPlayer=MediaPlayer.create(SmartPlayerActivity.this,uri);
        mSongName=mySongs.get(position).toString();
        songNametxt.setText(mSongName);
        myMediaPlayer.start();

        if(myMediaPlayer.isPlaying()){
            pausebtn.setImageResource(R.drawable.pause);
        }
        else{
            pausebtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }

    }

    private void playPreviousSong(){
        myMediaPlayer.pause();
        myMediaPlayer.stop();
        myMediaPlayer.release();

        position=((position-1)<0 ? (mySongs.size()-1) :(position-1));
        Uri uri=Uri.parse(mySongs.get(position).toString());
        myMediaPlayer=MediaPlayer.create(SmartPlayerActivity.this,uri);
        mSongName=mySongs.get(position).toString();
        songNametxt.setText(mSongName);
        myMediaPlayer.start();

        if(myMediaPlayer.isPlaying()){
            pausebtn.setImageResource(R.drawable.pause);
        }
        else{
            pausebtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }


    }

}