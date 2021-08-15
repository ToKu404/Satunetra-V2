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
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.satunetra_bot_test.R;
import com.example.satunetra_bot_test.adapter.ChatAdapter;
import com.example.satunetra_bot_test.data.TagMaps;
import com.example.satunetra_bot_test.helper.BotHelper;
import com.example.satunetra_bot_test.helper.RoomHelper;
import com.example.satunetra_bot_test.helper.SpeechHelper;
import com.example.satunetra_bot_test.helper.VoiceHelper;
import com.example.satunetra_bot_test.local.table.UserEntity;
import com.example.satunetra_bot_test.model.Message;
import com.example.satunetra_bot_test.model.Tag;
import com.example.satunetra_bot_test.utils.NetworkService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements View.OnTouchListener {
    private View btnStartChat;
    private ImageView ivNotSpeech, ivMic;
    private TextView tvTimer;
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

    //bot helper
    private BotHelper botHelper;

    //attribute
    //message from user and bot
    private String userMessage;
    //name of user
    private String name;
    private boolean allowsShow;
    private boolean isTimer;

    private String respond;
    private boolean initialRequest;
    //deep of chat
    private int deep;


    public static final String BroadcastStringForAction="checkinternet";
    private IntentFilter mIntentFilter;
    private View vStatus;
    private TextView tvStatus;

    private boolean allowCheck;
    private boolean isConnected;
    //is user first init or not
    private boolean firstInit;


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
        tvTimer = findViewById(R.id.tv_timer);




        deep = 0;
        tagMap=new HashMap<>();
        gestureDetector = new GestureDetector(this, new GestureListener());
        firstInit = false;

        //init bot
        botHelper = new BotHelper(this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BroadcastStringForAction);
        allowCheck = true;
        isTimer = false;
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
        configureSpeechRecognition();
        configureTTS();

        if(!userEntity.getFirst()){
            helper.firstTake(userEntity.getId());
            firstInit = true;
        }
        registerReceiver(NetworkReciever,mIntentFilter);
    }



    //listener for SR
    private class MyRecognitionListener implements RecognitionListener {
        @Override
        public void onReadyForSpeech(Bundle params) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {
            refreshSpeechUI(false);
            speechRecognizer.stopListening();
        }

        @Override
        public void onError(int error) {
            refreshSpeechUI(false);
        }

        @Override
        public void onResults(Bundle results) {
            refreshSpeechUI(false);
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String string = "...";
            if(matches!=null) {
                string = matches.get(0);
                userMessage = string;
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    }

    //configure SR
    private void configureSpeechRecognition() {
        SpeechHelper helper = new SpeechHelper(this, 300);
        speechRecognizer = helper.getSpeechRecognizer();
        speechIntent = helper.getSpeechIntent();
        speechRecognizer.setRecognitionListener(new MyRecognitionListener());
    }

    //refresh speech UI
    private void refreshSpeechUI(boolean allowSpeech) {
        if(allowSpeech){
            ivMic.setImageResource(R.drawable.ic_baseline_mic_24);
        }else{
            ivMic.setImageResource(R.drawable.ic_baseline_mic_off_24);
        }
    }

    //configure TTS
    private void configureTTS() {
        VoiceHelper voiceHelper = new VoiceHelper(this);
        tts = voiceHelper.getTts();
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnStartChat.setEnabled(true);
                    }
                });
            }

            @Override
            public void onStop(String utteranceId, boolean interrupted) {
                new Thread()
                {
                    public void run()
                    {
                        ChatActivity.this.runOnUiThread(new Runnable()
                        {
                            public void run()
                            {

                                btnStartChat.setEnabled(true);
                                refreshSpeechUI(false);
                            }
                        });
                    }
                }.start();

            }


            @Override
            public void onDone(String utteranceId) {
                new Thread()
                {
                    public void run()
                    {
                        ChatActivity.this.runOnUiThread(new Runnable()
                        {
                            public void run()
                            {

                                btnStartChat.setEnabled(true);
                                refreshSpeechUI(false);
                                if(initialRequest){
                                    userMessage = "";
                                }
                            }

                        });
                    }
                }.start();

            }

            @Override
            public void onError(String utteranceId) {

            }
        });
    }





    public BroadcastReceiver NetworkReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BroadcastStringForAction) && allowCheck){
                if(intent.getStringExtra("online_status").equals("true")){
                    setOnline();
                    isConnected = true;
                    if(firstInit){
                        userMessage = "W1";
                    }else{
                        userMessage = "W2";
                    }
                }else{
                    setOffline();
                    isConnected = false;
                    userMessage = "WOFFLINE";
                }
                System.out.println("KONTOL : " + userMessage);
                allowsShow = false;
                sendMessage();
                allowCheck = false;
            }
        }
    };


    //start speak
    private void startSpeak(String string) {
        btnStartChat.setEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(string, TextToSpeech.QUEUE_ADD, null, "text");
        }
    }


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
        allowCheck = true;
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
        if(v.getId() == R.id.btn_gestur_chat){
            gestureDetector.onTouchEvent(event);
            return true;
        }
        return false;
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            letsPlaying();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        //swipe from left to right
                        if (diffX > 0) {

                        }
                        result = true;
                    }
                }else{
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if(diffY > 0){

                        }
                        result = true;
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }

            return result;
        }
    }

    private void sendMessage() {

        if(allowsShow){
            Message inputMessage = new Message();
            inputMessage.setMessage(userMessage);
            inputMessage.setId("1");
            messageArrayList.add(inputMessage);
            updateChatRoom();
        }else{
            allowsShow = true;
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getRespond();
                    respond = configureResponse();
                    Message outMessage = new Message();
                    outMessage.setMessage(respond);
                    outMessage.setId("2");
                    messageArrayList.add(outMessage);
                    startSpeak(respond);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateChatRoom();
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }

    private void getRespond() {
        try {
            respond = botHelper.sendChatMessage(userMessage);
        }catch (Exception e){
            System.out.println(e.getLocalizedMessage());
            getRespond();
        }
    }

    private void updateChatRoom() {
        chatAdapter.notifyDataSetChanged();
        if (chatAdapter.getItemCount() > 1) {
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, chatAdapter.getItemCount() - 1);
        }
    }

    //configure bot message edit
    private String configureResponse() {
        String tempMessage = respond;
        //time format set
        if(tempMessage.charAt(0) == '$'){
            String[] greetings = tempMessage.substring(1,tempMessage.length()).split("#");
            int hour = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                hour = LocalDateTime.now().getHour();
            }
            if(hour>5 && hour<12){
                tempMessage = greetings[0];
            }else if(hour>=12 && hour<15){
                tempMessage = greetings[1];
            }else if(hour>=15 && hour<=18){
                tempMessage = greetings[2];
            }else {
                tempMessage = greetings[3];
            }
            tempMessage += greetings[4];
        }
        //name set


        if(isConnected==false){
            if(deep==0){
                deep++;
            }
            if(deep==1){
                if(respond.trim().charAt(0)=='#'){
                    String temp = respond;
                    String[] yorn = temp.substring(1,temp.length()).split("#");
                    startSpeak(yorn[1]);
                    if(yorn[0].equalsIgnoreCase("YA")){
                        letsPlaying();
                        deep=2;
                    }else{
                        finish();
                    }
                }else{
//                    startSpeak(respond);
                }
            }
        }else{
            if(deep==0){
                tempMessage = String.format(tempMessage, name);
                deep++;
            }
        }
        return  tempMessage;
    }

    private void letsPlaying() {
        isTimer=true;
        tvTimer.setVisibility(View.VISIBLE);
        ivMic.setVisibility(View.GONE);

        cTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String time = String.valueOf(millisUntilFinished / 1000);
                tvTimer.setText(time+" detik");
            }

            @Override
            public void onFinish() {
                Intent playIntent = new Intent(ChatActivity.this, RoomActivity.class);
                if(isConnected){
                    playIntent.putStringArrayListExtra("link", (ArrayList<String>) test);
                    playIntent.putExtra("connected", "online");
//                    playIntent.putExtra("type", instructionValue);
                }else{
                    playIntent.putExtra("connected", "offline");
                }
                startActivity(playIntent);
                cTimer.cancel();
                tvTimer.setVisibility(View.GONE);
                ivMic.setVisibility(View.VISIBLE);
//                afterInstruction =true;
            }
        };
        cTimer.start();
    }


}