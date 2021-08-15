package com.example.satunetra_bot_test.helper;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.Graphmaster;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;
import org.alicebot.ab.Timer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

public class BotHelper {
    //bot attribut
    public Bot bot;
    public static Chat chat;

    public BotHelper(Context context){
        //check sd card tersedia dan menerima asset dari penyimpanan
        AssetManager assetManager = context.getResources().getAssets();
        File cacheDirectory = new File(context.getCacheDir().toString() + "/satunetra/bots/satunetra_bot");
        boolean dirMakingSuccessful = cacheDirectory.mkdirs();

        // saving the bot's core data in the cache
        if(dirMakingSuccessful && cacheDirectory.exists()){
            try{
                for(String dir : assetManager.list("satunetra_bot")){
                    File subDirectory = new File(cacheDirectory.getPath() + "/" + dir);
                    subDirectory.mkdirs();
                    for(String file : assetManager.list("satunetra_bot/" + dir)){
                        File f = new File(cacheDirectory.getPath() + "/" + dir + "/" + file);
                        if(!f.exists()){
                            InputStream in;
                            OutputStream out;

                            in = assetManager.open("satunetra_bot/" + dir + "/" + file);
                            out = new FileOutputStream(cacheDirectory.getPath() + "/" + dir + "/" + file);
                            //mengkopi file dari asset ke penyimpanan
                            copyFile(in, out);
                            in.close();
                            out.flush();
                            out.close();
                        }
                    }
                }

            } catch(IOException e){
                e.printStackTrace();
                Log.i("bot", "IOException occurred when writing from cache!");
            } catch(NullPointerException e){
                Log.i("bot", "Nullpoint Exception!");
            }
        }
        // handler for communication with the background thread
        final Handler handler = new Handler(){
            @Override
            public void dispatchMessage(Message msg) {
                super.dispatchMessage(msg);
            }
        };

        // initializing the bot in background thread
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                MagicStrings.root_path = context.getCacheDir().toString() + "/satunetra";
                AIMLProcessor.extension = new PCAIMLProcessorExtension();
                bot = new Bot("satunetra_bot", MagicStrings.root_path, "chat");
                chat = new Chat(bot);
                handler.sendMessage(new Message()); // dispatch a message to the UI thread
            }
        });
        thread.start();
    }

    // copying the file
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    public String sendChatMessage(String m){
        System.out.println(m);
        String message = m.trim();
        if(message.isEmpty()){
            return "";
        }

        // chat with bot - save the reply from the bot
        String botReply = mainFunction(message);
        if(botReply.trim().isEmpty()){
            botReply = mainFunction("UDC");
        }
        return botReply;
    }


    // responding of bot to user's requests
    public static String mainFunction (String args) {
//        System.out.println(chat.multisentenceRespond("REGISTER"));
        MagicBooleans.trace_mode = false;
        Graphmaster.enableShortCuts = true;
        Timer timer = new Timer();
        return chat.multisentenceRespond(args);
//        return "ASU";
    }
}
