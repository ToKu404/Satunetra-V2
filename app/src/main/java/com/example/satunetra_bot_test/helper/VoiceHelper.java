package com.example.satunetra_bot_test.helper;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class VoiceHelper{
    TextToSpeech tts;

    public VoiceHelper(Context context){
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status==TextToSpeech.SUCCESS) {
//                    int result = tts.setLanguage(Locale.getDefault());
                    int result = tts.setLanguage(new Locale("id","ID"));
                    tts.setSpeechRate(1);
                    tts.setPitch(1);
                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("tts", "lenguage not supported");
                    }
                }else{
                    Log.e("tts", "Failed");
                }
            }
        });
    }

    public TextToSpeech getTts() {
        return tts;
    }
}
