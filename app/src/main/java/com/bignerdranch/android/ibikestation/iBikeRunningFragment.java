package com.bignerdranch.android.ibikestation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * Created by sredorta on 11/29/2016.
 */
public class iBikeRunningFragment extends Fragment {
    private static final String TAG = "SERGI:POLL";
    public static Locker mLocker;
    public String mAction="test";
    public boolean isActive = false;
    public BroadcastReceiver pollServiceReceiver;
    TextView myText;
    public final GPIO myLED = new GPIO(36);
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
 //       Toast.makeText(getActivity(),"mLocker gps:" + mLocker.isGpsLocated(), Toast.LENGTH_LONG).show();
//        if (mLocker.isGpsLocated()) {
//            Toast.makeText(getActivity(),"longitude:" + mLocker.getLockerLocation().getLongitude() + "\nlatitude:" + mLocker.getLockerLocation().getLatitude(), Toast.LENGTH_LONG).show();
//        }

        myText = (TextView) v.findViewById(R.id.textViewTest);
        myText.setText("No data");
        //////////////////////startPolling();

        final Button myToggle = (Button) v.findViewById(R.id.button);
        final Button myBAct = (Button) v.findViewById(R.id.buttonActivate);
        final Button myBDesAct = (Button) v.findViewById(R.id.buttonDesactivate);

        myBAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myLED.activationPin();
                myLED.setInOut("out");
            }
        });
        myBDesAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myLED.desactivationPin();
            }
        });

        myToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isActive) {
                    Toast.makeText(getActivity(),"Disable", Toast.LENGTH_SHORT).show();
                    myLED.setState(0);
                } else {
                    Toast.makeText(getActivity(),"Enable", Toast.LENGTH_SHORT).show();
                    myLED.setState(1);
                }
                isActive=!isActive;
            }
        });

        return v;
    }


    @Override
    public void onDestroy() {
        //Stop the Polling service
        PollService.setServiceAlarm(getActivity(),false);
        getActivity().unregisterReceiver(pollServiceReceiver);
        super.onDestroy();
    }


    private void startPolling() {
        //Start service of polling on SQL
        PollService.setServiceAlarm(getActivity(),true);
        pollServiceReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

//                Log.i("SERGI", "onReceive new POLL data !");
                  mAction = intent.getStringExtra("action");
//                Log.i("SERGI", "onReceive action:" + mAction);
                myText.setText(mAction);
//                Toast.makeText(getActivity(),"Recieved data: " + mAction, Toast.LENGTH_SHORT).show();
            }
        };
        //Register the receiver
        getActivity().registerReceiver(pollServiceReceiver, new IntentFilter(
                PollService.BROADCAST_ACTION));
    }

}

