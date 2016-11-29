package com.bignerdranch.android.ibikestation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created by sredorta on 11/29/2016.
 */

public class PollService extends Service {
    static final public String BROADCAST_ACTION = "com.bignerdranch.android.ibikestation.PollService";
    private static final String TAG = "SERGI:PollService:";
    private static Context mContext;
    public String mAction = "nothing";
    Intent intent;
    private static final int POLL_INTERVAL = 1000*3; // 6 seconds
    public static final String ACTION_SHOW_NOTIFICATION = "com.bignerdranch.android.photogallery.SHOW_NOTIFICATION";
    public static final String PERM_PRIVATE= "com.bignerdranch.android.photogallery.PRIVATE";
    public static final String REQUEST_CODE ="REQUEST_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String myAction;
        intent = new Intent(BROADCAST_ACTION);

        Log.i(TAG,"Starting background task !");
        myAction = getAction(getApplicationContext());

        Log.i(TAG, "Sending action " + myAction);
        intent.putExtra("action", myAction );
        sendBroadcast(intent);
    }

    public static String getAction(Context context) {
        ExecutorService service = Executors.newFixedThreadPool(10);

        FutureTask<String> connectionTask = new FutureTask<String>(new GetActualAction(context));
        service.submit(connectionTask);
        try {
            return connectionTask.get();
        } catch (java.util.concurrent.ExecutionException ex) {
            ex.printStackTrace();
            return "error";
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            return "error";
        }
    }

    //Calls a thread to check if specific URL gives connection
    public static class GetActualAction implements Callable {
        public String mAction;
        Context context;

        public GetActualAction(Context context) {
            this.context = context;
        }
        public String getAction() {
            return mAction;
        }
        @Override
        public String call() {
            return ( new CloudFetchr().getAction());
        }
    }



    //Define an alarm to check every X seconds if new content is available
    public static void startServiceAlarm(Context context, boolean isOn) {
        Intent i = PollService.newIntent(context);

        PendingIntent pi = PendingIntent.getService(context,0,i,0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), POLL_INTERVAL, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }


    // Check if alarm is active
    public static boolean isServiceAlarmOn(Context context) {
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context,0,i,PendingIntent.FLAG_NO_CREATE);
        return pi!= null;
    }
/*
    @Override
    protected void onHandleIntent(Intent intent) {
        String myAction;

        myAction = new CloudFetchr().getAction();
        Log.i(TAG, "Action required is:" + myAction);
       // if (myAction != "nothing") {
            intent.putExtra("action", myAction );
            sendBroadcast(intent);
            //Send result to main activity for processing
       // }

    }
*/

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
