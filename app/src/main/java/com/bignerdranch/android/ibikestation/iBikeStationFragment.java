package com.bignerdranch.android.ibikestation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/*
iBikeStationFragment
    onCreate:
        1)Starts a service to check for GPS location
        2)Starts a task to check if Internet is available
        3)
        x)Waits some time and check status of everything for moving forward
 */
public class iBikeStationFragment extends Fragment {
    public Locker mLocker;
    public boolean isGpsDataAvailable = false;
    public static iBikeStationFragment newInstance() {
        return new iBikeStationFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Declare a new Locker
        mLocker = new Locker();
        //Start GPS service to get coordinates on start and update locker coordinates
        updateGpsLocation();
        checkInternetConnectivity();
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ibikestation,container,false);
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /*  updateGpsLocation
            Starts a GpsService service that will provide gps coords once stable
            Once gps Coords are available the service is stopped
     */
    private void updateGpsLocation() {
        //Get current GPS Location and update it
        final BroadcastReceiver GpsServiceReceiver;
        final Intent GpsServiceIntent = GpsService.newIntent(getActivity());
        getActivity().startService(GpsServiceIntent);
        // Create a BroadcastReceiver that receives the data from intent of GpsService Service
        GpsServiceReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("SERGI","onReceive new GPS data !");
                //Update the location
                Location loc = new Location("DummyProvider");
                loc.setLongitude(Double.parseDouble(intent.getStringExtra("longitude")));
                loc.setLatitude(Double.parseDouble(intent.getStringExtra("latitude")));
                mLocker.setLockerLocation(loc);
                Log.i("SERGI","Lon: " + loc.getLongitude());
                Log.i("SERGI","Stopped service !");
                getActivity().stopService(GpsServiceIntent);
                getActivity().unregisterReceiver(this);
            }
        };
        //Register the receiver
        getActivity().registerReceiver(GpsServiceReceiver,new IntentFilter(
                GpsService.BROADCAST_ACTION));
    }

    private void checkInternetConnectivity() {
        final BroadcastReceiver PollServiceReceiver;
        final Intent InternetServiceIntent = InternetService.newIntent(getContext());
        getActivity().startService(InternetServiceIntent);
        PollServiceReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isConnected = Boolean.parseBoolean(intent.getStringExtra("isConnected"));
                Log.i("SERGI:Poll:","onReceive Poll isConnected " + isConnected);
                mLocker.setInternetConnected(isConnected);
                getActivity().stopService(InternetServiceIntent);
            }
        };
    }
}

