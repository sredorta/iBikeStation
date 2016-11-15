package com.bignerdranch.android.ibikestation;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 GpsService class
    This is the service that gets GPS location for a Locker
    It doesn't use any google API
 */

public class GpsService extends Service implements LocationListener {

    //    private final Context mContext;
    // flag for GPS status
    public boolean isGPSEnabled = false;
    Location location = new Location("Point NEW"); // location
    Location previousLocation = new Location("point OLD");;
    private double distance = 0;
    private double latitude = 0.0; // latitude
    private double longitude; // longitude
    private float  accuracy = 0;
    private double altitude = 0.0;
    private long time = 0;
    private long timeDelta = 0;

    //Declare intent for UI communication
    Intent intent;
    static final public String BROADCAST_ACTION = "com.bignerdranch.android.ibikestation";

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 5 meters
    // Minimum distance to considere data stable
    private static final long MIN_DISTANCE_STABLE_DATA = 100;

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0; // 10 second
    //Minimum time between intervals to considere stable data
    private static final long MIN_TIME_STABLE_DATA = 1000*10*10;

    // Declaring a Location Manager
    protected LocationManager locationManager;

    //Make sure that only one Intent can be created
    public static Intent newIntent(Context context) {
        return new Intent(context, GpsService.class);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("SERGI", "GPSTrackerService: Created service");
        Log.i("SERGI", "GPSTrackerService: onCreate");
        intent = new Intent(BROADCAST_ACTION);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        try {
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Initialize previousLocation
        previousLocation.setTime(0);
        previousLocation.setLongitude(0);
        previousLocation.setLatitude(0);
        location.setTime(MIN_TIME_BW_UPDATES*5);
        location.setLongitude(0);
        location.setLatitude(0);
    }


    @Override
    public void onDestroy() {
        Log.i("SERGI", "GPSTrackerService:Destroyed service");
        super.onDestroy();
        locationManager.removeUpdates(GpsService.this);
        if(intent != null) {
            intent = null;
        }
        if(locationManager != null){
            locationManager.removeUpdates(this);
        }
    }


    public Location getLocation() {
        return location;
    }
    // Stores what's in location into previousLocation
    private void updatePreviousLocation(Location location, Location previousLocation) {
        //Store into previous location for next update
        previousLocation.setTime(location.getTime());
        previousLocation.setLongitude(location.getLongitude());
        previousLocation.setLatitude(location.getLatitude());
        previousLocation.setAltitude(location.getAltitude());
        previousLocation.setAccuracy(location.getAccuracy());
        previousLocation.setSpeed(location.getSpeed());
    }

    // Uses location and previousLocation to compute all data
    private void computeData(Location location, Location previousLocation) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        accuracy = location.getAccuracy();
        altitude = location.getAltitude();
        if (previousLocation.getLatitude() != 0 && previousLocation.getLongitude() != 0 ) {
            distance = location.distanceTo(previousLocation);
        }
        Log.i("SERGI","distance = " + distance);
        //If distance is small and time is small data is stable so we can broadcast data
        if (distance <= MIN_DISTANCE_STABLE_DATA) {
            time = location.getTime();
            timeDelta = location.getTime() - previousLocation.getTime();
            Log.i("SERGI","timeDelata = " + timeDelta);
            if (timeDelta <= MIN_TIME_STABLE_DATA) {
                //Broadcast all data
                broadcastData(intent);
            }
        }
    }

    //BroadcastResults to activity
    private void broadcastData(Intent intent) {
        Log.i("SERGI","broadCastData !");
        //Broadcast data to the gui
        intent.putExtra("longitude", " " + longitude);
        intent.putExtra("latitude", "" + latitude);
        intent.putExtra("isGPSEnabled", "enabled");
        intent.putExtra("accuracy"," " + accuracy );
        intent.putExtra("time_delta"," " + timeDelta );
        intent.putExtra("distance", String.format("%.1f", distance ));
        sendBroadcast(intent);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("SERGI","onLocationChanged !");
        //Get new location
        location = locationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //Do any calculation requiring old/new location
        computeData(location,previousLocation);
        //Update old location with current location for new round
        updatePreviousLocation(location,previousLocation);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
        Log.i("SERGI","onProviderDisabled: called function !");
        isGPSEnabled = false;
        intent.putExtra("isGPSEnabled", "disabled");
        sendBroadcast(intent);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT ).show();
        Log.i("GPS_TEST","onProviderEnabled: called function !");
        isGPSEnabled = true;
        intent.putExtra("isGPSEnabled", "enabled");
        sendBroadcast(intent);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i("GPS_TEST","onStatusChanged: called function !");
    }



    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}

