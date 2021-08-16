package com.example.satunetra_bot_test.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.satunetra_bot_test.R;

import java.io.IOException;
import java.util.ArrayList;

public class RoomActivity extends AppCompatActivity implements View.OnTouchListener {

    private GestureDetector mGestureDetector;
    private ImageView ivPause;
    private MediaPlayer mediaPlayer;
    private int sesi;
    private ArrayList<String> links;
    TextView tvTitleSesi;
    private boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        LinearLayout btnGesture = findViewById(R.id.btn_gestur_room);
        mGestureDetector = new GestureDetector(this, new GestureListener());

        ivPause = findViewById(R.id.iv_not_speech_chat);
        TextView titleSong = findViewById(R.id.title_music);
        tvTitleSesi= findViewById(R.id.title_sesi);

        mediaPlayer = new MediaPlayer();
        connected = getIntent().getStringExtra("connected").equals("online");
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if(connected){
                    sesi=1;
                    links = getIntent().getStringArrayListExtra("link");
                    playOnline();
                }else{
                    links = null;
                    playOffline();
                }
            }},300);
        String type = getIntent().getStringExtra("type");
        titleSong.setText(type.toUpperCase());
        btnGesture.setOnTouchListener(this);


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(links==null){
                    finish();
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
                else if(sesi<=links.size() && connected){
                    sesi++;
                    playOnline();
                }else{
                    onBackPressed();
                }
            }
        });


    }




    private void playOnline() {
        try {
            String link = links.get(sesi-1);
            tvTitleSesi.setText("Sesi "+sesi);
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
            System.out.println(exception.getMessage());
            System.out.println(links.size());
        }
    }

    private void playOffline(){
        mediaPlayer.stop();
        mediaPlayer.reset();
        tvTitleSesi.setText("Sesi Offline");
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
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId()==R.id.btn_gestur_room){
            System.out.println("ASTAGA");
            mGestureDetector.onTouchEvent(event);
        }
        return true;
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //pause
            if(mediaPlayer!=null){
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    ivPause.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
                }else{
                    mediaPlayer.start();
                    ivPause.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
                }
            }
            return false;
        }
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD && connected) {
                        if (diffX > 0) {
                            if(sesi<=links.size()){
                                mediaPlayer.start();
                                sesi++;
                                playOnline();
                            }else{
                                Toast.makeText(RoomActivity.this, "INI SESI TERAKHIR", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }

                        }else{
                            if(sesi>=1){
                                sesi--;
                                playOnline();
                            }else{
                                Toast.makeText(RoomActivity.this, "INI SESI PERTAMA", Toast.LENGTH_SHORT).show();
                            }
                        }
                        result = true;
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            return result;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            onBackPressed();
            return super.onDoubleTap(e);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer = null;
        finish();
    }
}