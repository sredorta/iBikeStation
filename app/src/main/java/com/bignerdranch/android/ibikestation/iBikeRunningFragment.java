package com.bignerdranch.android.ibikestation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by sredorta on 11/29/2016.
 */
public class iBikeRunningFragment extends Fragment {
    private static final String TAG = "SERGI:POLL";
    public static Locker mLocker;
    public BroadcastReceiver PollServiceReceiver;
    static final public String BROADCAST_ACTION = "com.bignerdranch.android.ibikestation.PollService";
    private FetchCloudTask task;

    public static iBikeRunningFragment newInstance(Locker myLocker) {
        iBikeRunningFragment fragment = new iBikeRunningFragment();
        mLocker = myLocker;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Toast.makeText(getActivity(),"mLocker Longitude is:" + mLocker.isGpsLocated(), Toast.LENGTH_LONG).show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ibikerunning, container, false);
        Toast.makeText(getActivity(),"mLocker gps:" + mLocker.isGpsLocated(), Toast.LENGTH_LONG).show();
        if (mLocker.isGpsLocated()) {
            Toast.makeText(getActivity(),"longitude:" + mLocker.getLockerLocation().getLongitude() + "\nlatitude:" + mLocker.getLockerLocation().getLatitude(), Toast.LENGTH_LONG).show();
        }

        startPolling();

        return v;
    }


    @Override
    public void onDestroy() {
//        getActivity().registerReceiver(PollServiceReceiver, new IntentFilter(
//                PollService.BROADCAST_ACTION));
        //Stop the service before destroying
 /*       if (PollService.isServiceAlarmOn(getActivity())) {
            PollService.startServiceAlarm(getActivity(), true);
            getActivity().stopService(PollServiceIntent);
            getActivity().unregisterReceiver(PollServiceReceiver);
        }*/
        super.onDestroy();
    }


    private void startPolling() {
        //Update fields of location in the SQL db
        task = new FetchCloudTask("getAction");
        task.execute();
    }


    //Get data from website
    private class FetchCloudTask extends AsyncTask<Void,Void,String> {
        private String mQuery;

        public FetchCloudTask(String query) {
            mQuery = query;
        }


        @Override
        protected String doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            switch (mQuery) {
                case "getAction":
                    Log.i("ASYNC:", "We are in isCoudConnected");
                    return (new CloudFetchr().getAction());
                default:
                    return (new CloudFetchr().getAction());
            }

        }

        @Override
        protected void onPostExecute(String action) {
            Log.i(TAG, "AsyncTask postExec, success = " + action);
            switch (mQuery) {
                case "getAction":
                    mLocker.setAction(action);
                    break;
                default:
                    //Do nothing
            }
        }
    }

}

