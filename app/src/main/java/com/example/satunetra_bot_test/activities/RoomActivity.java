package com.example.satunetra_bot_test.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.satunetra_bot_test.R;

import java.io.IOException;
import java.util.ArrayList;

public class RoomActivity extends AppCompatActivity {

    private GestureDetector mGestureDetector;
    private ImageView ivPause;
    private MediaPlayer mediaPlayer;
    private int sesi;
    private TextView titleInstruction;
    private ArrayList<String> links;
    private boolean connected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        mediaPlayer = new MediaPlayer();
        connected = getIntent().getStringExtra("connected").equals("online")?true:false;
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if(connected){

                }else{
                    playOffline();
                }
            }},300);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(sesi<=links.size() && connected){
                    sesi++;
                    playVideo();
                }else{
                    onBackPressed();
                }
            }
        });
    }

    private void playVideo(){
        try {
            String link = links.get(sesi-1);
            titleInstruction.setText("SESI "+sesi);
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(link);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
        }catch (Exception exception){
            System.out.println(links.size());
        }
    }

    private void playOffline(){
        mediaPlayer.stop();
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse("android.resource://com.example.satunetra_bot_test/"+R.raw.offline_music));
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mediaPlayer.start();
    }
}