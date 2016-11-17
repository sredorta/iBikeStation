package com.bignerdranch.android.ibikestation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by sredorta on 11/10/2016.
 */
/* Locker class
    This is the class for one locker and provides locker info
    Each Lock is an object Lock
 */
public class Locker {
    private static final int LockerCapacity = 10;
    private static int LockerAvail = 10;

    //Stores the GPS location of the locker
    private Location mLockerLocation = null;
    private boolean mLockerGpsLocated = false;
    //Stores Internet connectivity of the Locker
    private boolean  mLockerConnected = false;

    //Creates all Locks
    public List<Lock> getLocks() {
        List<Lock> locks = new ArrayList<>();
        for (int i = 0; i < LockerCapacity; i++) {
            locks.add(new Lock(i));

        }
        return locks;
    }

    //Get GPS location of the locker
    public Location getLockerLocation() {
        return mLockerLocation;
    }

    //Sets the GPS location of the locker
    public void setLockerLocation(Location location) {
        mLockerLocation = location;
    }

    public boolean isInternetConnected() { return mLockerConnected;}
    public void setInternetConnected(boolean isConnected) { mLockerConnected = isConnected;}

    public void setIsGpsLocated(boolean isGpsLocated) {
        mLockerGpsLocated = isGpsLocated;
    }
    public boolean isGpsLocated() {
        return mLockerGpsLocated;
    }

}

