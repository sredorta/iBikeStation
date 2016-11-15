package com.bignerdranch.android.ibikestation;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;


/**
 InternetService class
 This is the service that gets if Internet is available for a Locker
 It tries to connect to a google server to see if connection is possible
 */


public class InternetService extends IntentService {
    private static final String TAG = "SERGI:Poll:";

    public static Intent newIntent(Context context) {
         return new Intent(context, InternetService.class);
    }

    public InternetService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean isConnected;
        boolean isExisting;
        //Get status of network and send result to Fragment
        isExisting = isNetworkExisting(getApplicationContext());
        isConnected = isNetworkWorking(getApplicationContext());
        intent.putExtra("isConnected", isConnected && isExisting);
        sendBroadcast(intent);

        if(!isConnected) {
            Log.i(TAG,"Network is not connected !");
            return;
        }
        Log.i(TAG,"Network is connected !");
    }

    //Returns if Network is connected and available
    public static boolean isNetworkExisting(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }


    public static Boolean isNetworkWorking(Context context) {

        ExecutorService service = Executors.newFixedThreadPool(10);

        FutureTask<Boolean> connectionTask = new FutureTask<Boolean>(new CheckConnection(context));
        service.submit(connectionTask);
        try {
            return connectionTask.get();
        } catch (java.util.concurrent.ExecutionException ex) {
            ex.printStackTrace();
            return false;
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    //Calls a thread to check if specific URL gives connection
    public static class CheckConnection implements Callable {
        Context context;

        public CheckConnection(Context context) {
            this.context = context;
        }

        @Override
        public Boolean call() {
            if (isNetworkExisting(context)) {
                try {
                    HttpURLConnection urlc = (HttpURLConnection)
                            (new URL("http://clients3.google.com/generate_204").openConnection());
                    urlc.setRequestProperty("User-Agent", "Android");
                    urlc.setRequestProperty("Connection", "close");
                    urlc.setConnectTimeout(1500);
                    urlc.connect();
                    return (urlc.getResponseCode() == 204 && urlc.getContentLength() == 0);
                } catch (IOException e) {
                    Log.i(TAG, "Error checking internet connection", e);
                }
            } else {
                Log.i(TAG, "No network available!");
            }
            return false;
        }
    }

}