package com.example.satunetra_bot_test.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.satunetra_bot_test.R;
import com.example.satunetra_bot_test.adapter.ChatAdapter;
import com.example.satunetra_bot_test.data.TagMaps;
import com.example.satunetra_bot_test.helper.RoomHelper;
import com.example.satunetra_bot_test.local.table.UserEntity;
import com.example.satunetra_bot_test.model.Message;
import com.example.satunetra_bot_test.model.Tag;
import com.example.satunetra_bot_test.utils.NetworkService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements View.OnTouchListener {
    private View btnStartChat;
    private ImageView ivNotSpeech, ivMic, ivTimer;
    private GestureDetector gestureDetector;

    //for local db
    private RoomHelper helper;

    //for chat
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;


    //for tts and SR
    private TextToSpeech tts;
    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;

    //for timer
    private CountDownTimer cTimer;

    //list and ,ap
    private ArrayList<Message> messageArrayList;
    private Map<String, Tag> tagMap;
    private List<String> test;

    //attribute
    //message from user and bot
    private String userMessage;
    //name of user
    private String name;
    //tag from watson
    private String botTagNow;
    //to save key and value of tag watson
    private String instructionKey;
    private String feelKey;
    private String instructionValue;
    private String feelValue;
    //is timer active or not
    private boolean isTimer;
    //is tts speek or not
    private boolean nowSpeak;
    //is music ready to play
    private boolean letsPlay;
    //after instruction of music tag
    private boolean afterInstruction;
    //user choice exit and consultation has been done
    private boolean readyToExit;
    //user choice exit but consultation not done
    private boolean exitNow;
    //0=m from bot, 1=m from user
    private boolean initialRequest;
    //deep of chat
    private int deep;


    public static final String BroadcastStringForAction="checkinternet";
    private IntentFilter mIntentFilter;
    private View vStatus;
    private TextView tvStatus;

    private boolean allowCheck;
    private boolean isConnected;


    //const
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        vStatus = findViewById(R.id.v_online);
        tvStatus = findViewById(R.id.tv_online);
        btnStartChat = findViewById(R.id.btn_gestur_chat);
        ivMic = findViewById(R.id.iv_mic_chat);
        recyclerView = findViewById(R.id.recycler_view);

        //set initial value
        afterInstruction = false;
        exitNow = false;
        //is user first init or not
        boolean firstInit = false;
        isTimer = false;
        readyToExit = false;
        nowSpeak = false;
        letsPlay = false;
        botTagNow = "none";
        instructionKey = "";
        feelKey = "";
        instructionValue = "";
        feelValue = "";
        deep = 0;
        tagMap=new HashMap<>();
        gestureDetector = new GestureDetector(this, new GestureListener());


        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BroadcastStringForAction);
        allowCheck = false;
        isConnected = false;
        Intent networkServiceIntent = new Intent(this, NetworkService.class);
        startService(networkServiceIntent);

        TagMaps tags = new TagMaps();
        tagMap = tags.readTags();
        btnStartChat.setOnTouchListener(this);
        //instance
        helper = new RoomHelper(this);
        UserEntity userEntity = helper.readUser();


        //setting message
        messageArrayList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(chatAdapter);
        name = userEntity.getName();
        initialRequest = true;
//
//        configureSpeechRecognition();
//        configureTTS();

        if(!userEntity.getFirst()){
            helper.firstTake(userEntity.getId());
            firstInit = true;
        }
        registerReceiver(NetworkReciever,mIntentFilter);
    }


    public BroadcastReceiver NetworkReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BroadcastStringForAction)){
                System.out.println("AKU");
                if(intent.getStringExtra("online_status").equals("true")){
                    setOnline();
                    isConnected = true;
                }else{
                    setOffline();
                    isConnected = false;
                }
                if(allowCheck){

                }
            }
        }
    };


    //Check response
    public void setOnline(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            vStatus.setBackground(getDrawable(R.drawable.circle_shape_green));
        }
        tvStatus.setTextColor(getResources().getColor(R.color.onlineGreen));
        tvStatus.setText(getResources().getString(R.string.online));
    }


    public void setOffline(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            vStatus.setBackground(getDrawable(R.drawable.circle_shape_orange));
        }
        tvStatus.setTextColor(getResources().getColor(R.color.offlineOrange));
        tvStatus.setText(getResources().getString(R.string.offline));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        registerReceiver(NetworkReciever, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(NetworkReciever);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(NetworkReciever, mIntentFilter);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener{

    }
}