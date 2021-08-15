package com.example.satunetra_bot_test.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.satunetra_bot_test.activities.ChatActivity;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkService extends Service {

    // Constant
    public static String TAG_INTERVAL = "interval";
    public static String TAG_URL_PING = "url_ping";
    private Timer mTimer = null;


    private int interval;
    private static String url_ping;

    @Override
    public void onCreate() {
        super.onCreate();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy gfgPolicy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(gfgPolicy);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Tidak mendukung");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        url_ping = "http://www.google.com";
        interval = 100;

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new CheckForConnection(), 0, interval * 1000);
        return START_STICKY;
    }

    public boolean isOnline(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    class CheckForConnection extends TimerTask {
        @Override
        public void run() {
            handler.post(periodicUpdate);
        }
    }



    private static boolean isNetworkAvailable(){
        HttpGet httpGet = new HttpGet(url_ping);
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 5000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

        int timeoutSocket = 7000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
        try{
            httpClient.execute(httpGet);
            return true;
        }
        catch(ClientProtocolException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return false;
    }

    Handler handler = new Handler();
    private Runnable periodicUpdate = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(periodicUpdate, 1 * 1000 - SystemClock.elapsedRealtime() % 1000);
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(ChatActivity.BroadcastStringForAction);
            System.out.println(isNetworkAvailable());
            broadcastIntent.putExtra("online_status", "" + (isOnline(NetworkService.this)&&isNetworkAvailable()));
            sendBroadcast(broadcastIntent);
        }
    };
}