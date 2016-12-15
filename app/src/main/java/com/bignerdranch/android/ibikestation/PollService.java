package com.bignerdranch.android.ibikestation;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by sredorta on 11/29/2016.
 */

public class PollService extends IntentService {
    static final public String BROADCAST_ACTION = "com.bignerdranch.android.ibikestation.PollService";
    private static final String TAG = "SERGI:PollService:";
    public static Context mContext;
    public String mAction = "nothing";
//    private FetchCloudTask task;
    private static final int POLL_INTERVAL = 1000*1; // 6 seconds

    //Constructor
    public PollService() {super(TAG);}

    //Intent handler
    public static Intent newIntent(Context context) {
        mContext = context;
        return new Intent(context, PollService.class);
    }

    //Define an alarm to check every X seconds if new content is available
    public static void setServiceAlarm(Context context,Boolean isOn) {
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


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG,"onHandleIntent I'm here !");
        mAction = new CloudFetchr().getAction();

//        task = new FetchCloudTask("dummy");
//        task.execute();
        Log.i(TAG,"Sending Poll Result Action : " + mAction);
        Intent myIntent = new Intent(BROADCAST_ACTION);
        myIntent.putExtra("action", mAction);
        //PendingIntent pi = PendingIntent.getActivity(this,0,i,0);
        sendBroadcast(myIntent);

    }

/*
    //Get data from website
    private class FetchCloudTask extends AsyncTask<Void,Void,String> {
        private String mQuery;
        public FetchCloudTask(String query) {
            mQuery = query;
        }

        @Override
        protected String doInBackground(Void... params) {
            Log.i("ASYNC:", "doInBackground");
            return (new CloudFetchr().getAction());
        }

        @Override
        protected void onPostExecute(String action) {
            Log.i("SERGI:CLOUD:", "AsyncTask postExec, action = " + action);
            mAction = action;
        }
    }
  */
    /*
    public static String getAction(Context context) {
        ExecutorService service = Executors.newFixedThreadPool(1);

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
        try {
            service.awaitTermination(3000, TimeUnit.SECONDS);
        } catch (InterruptedException ie) {
            //Do nothing
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
*/



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
