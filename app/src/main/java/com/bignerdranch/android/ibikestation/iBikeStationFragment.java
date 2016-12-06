package com.bignerdranch.android.ibikestation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


/*
iBikeStationFragment
    onCreate:
        1)Starts a service to check for GPS location
        2)Starts a task to check if Internet is available
        3)
        x)Waits some time and check status of everything for moving forward
 */
public class iBikeStationFragment extends Fragment {
    //Name of the Locker as registered in the SQL server
    public static final String LOCKER_NAME = "station2";

    public BroadcastReceiver GpsServiceReceiver;
    private FetchCloudTask task;
    public Locker mLocker;
    private AssetHandler mAssetImage;
    private View mSceneView;

    // Constructor
    public static iBikeStationFragment newInstance() {
        return new iBikeStationFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Declare a new Locker
        mLocker = new Locker();
        mLocker.setLockerName(LOCKER_NAME);
        //Create one Assets object for handling images
        mAssetImage = new AssetHandler(getActivity());

 //////////////       updateGpsLocation();
 //////////////       checkInternetConnectivity();
 /////////////       checkCloudConnectivity();
 ////////////       updateCloud();
        Toast.makeText(getActivity(), R.string.checker_result_ok, Toast.LENGTH_LONG).show();
        Intent i = iBikeRunningActivity.newIntent(getActivity(),mLocker);
        startActivity(i);

    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ibikestation, container, false);

        mSceneView = v;
        final ImageView mGpsView = (ImageView) v.findViewById(R.id.imageGps);
        final ImageView mNetworkView = (ImageView) v.findViewById(R.id.imageNetwork);
        final ImageView mCloudView = (ImageView) v.findViewById(R.id.imageCloud);
/*
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(startAnimation("gps", v, mGpsView),startAnimation("network", v, mNetworkView),startAnimation("cloud", v, mCloudView));
        animatorSet.addListener(new AnimatorSet.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }
            //When animation is finished we start a new activity
            @Override
            public void onAnimationEnd(Animator animator) {
                startRunningActivity();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorSet.start();
*/
        return v;
    }
    //Run now the main activity
    public void startRunningActivity() {
        if (mLocker.isInternetConnected() && mLocker.isCloudAlive()) { //Missing here GPS for now
            Activity myActivity = getActivity();

            Toast.makeText(getActivity(), R.string.checker_result_ok, Toast.LENGTH_LONG).show();
            Intent i = iBikeRunningActivity.newIntent(getActivity(),mLocker);
            startActivity(i);
            //Kill this activity of checking now and switch to the new one
//////////////////////////////////////////////////////////////            myActivity.finish();
        }
    }


    //  startAnimation
    //     Provide a pattern like "gps" the current view and the ImageView
    //      Will start the animations and load the correct result depending on the bitmap
    private Animator startAnimation(final String image, View view, final ImageView myImageView) {
        final String TAG = "SERGI:Anim:";

        Log.i("SERG","Started ilimeted anim");
        //Load the sepia Bitmap
        myImageView.setImageBitmap(mAssetImage.getImageAsset(image).getImageSepiaBitmap());
        myImageView.refreshDrawableState();
        myImageView.setVisibility(View.VISIBLE);
        //Add inite animator fadein/out
        ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(myImageView, "alpha", 0, 1, 0).setDuration(2000);
        fadeInAnimator.setStartDelay(2000);
        fadeInAnimator.setRepeatCount(4);
        //When alarm will stop the animator, then we will start the fadeInResultAnimator with the correct bitmap loaded
        fadeInAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                Log.i(TAG, "Started animation");
                myImageView.setVisibility(View.VISIBLE);
            };

            @Override
            public void onAnimationEnd(Animator animator) {
                boolean isActive = false;

                switch (image) {
                    case "gps": isActive = mLocker.isGpsLocated(); break;
                    case "network": isActive = mLocker.isInternetConnected(); break;
                    case "cloud": isActive = mLocker.isCloudAlive(); break;
                    default: isActive = false;
                }
                Log.i(TAG, "Ended animation");
                //Change background and image depending on GPS status
                if (isActive) {
                    myImageView.setImageBitmap(mAssetImage.getImageAsset(image).getImageGreenBitmap());
                    //myImageView.setBackgroundResource(R.drawable.shape_round_green);
                } else {
                    //myImageView.setImageResource(R.drawable.gps_red);
                    myImageView.setImageBitmap(mAssetImage.getImageAsset(image).getImageRedBitmap());
                    //myImageView.setBackgroundResource(R.drawable.shape_round_red);
                }
                ObjectAnimator fadeInResultAnimator = ObjectAnimator.ofFloat(myImageView, "alpha", 0, 1).setDuration(1000);
                fadeInResultAnimator.setRepeatCount(0);
                fadeInResultAnimator.start();

            }

            @Override
            public void onAnimationCancel(Animator animator) {
                Log.i(TAG, "Canceled animation");
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
/*
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(fadeInResultAnimator).after(fadeInAnimator);
        animatorSet.start() ;
        */
        return fadeInAnimator;
    }

    @Override
    public void onDestroy() {
        //Unregistering the GPS service in case no location was got
        if (GpsServiceReceiver != null) {
            getActivity().unregisterReceiver(GpsServiceReceiver);
            Toast.makeText(getActivity(),"Destroyed GPS service !", Toast.LENGTH_SHORT).show();
        }
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    /*  updateGpsLocation
                Starts a GpsService service that will provide gps coords once stable
                Once gps Coords are available the service is stopped
         */
    private void updateGpsLocation() {
        //Get current GPS Location and update it
        final Intent GpsServiceIntent = GpsService.newIntent(getActivity());
        getActivity().startService(GpsServiceIntent);
        // Create a BroadcastReceiver that receives the data from intent of GpsService Service
        GpsServiceReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("SERGI", "onReceive new GPS data !");
                //Update the location
                Location loc = new Location("DummyProvider");
                if (!Boolean.parseBoolean(intent.getStringExtra("isLocationValid"))) {
                    mLocker.setIsGpsLocated(false);
                } else {
                    loc.setLongitude(Double.parseDouble(intent.getStringExtra("longitude")));
                    loc.setLatitude(Double.parseDouble(intent.getStringExtra("latitude")));
                    mLocker.setLockerLocation(loc);
                    mLocker.setIsGpsLocated(true);
                    Log.i("SERGI", "Lon: " + loc.getLongitude());
                    Log.i("SERGI", "Stopped service !");
                }
                getActivity().stopService(GpsServiceIntent);
                getActivity().unregisterReceiver(this);
            }
        };
        //Register the receiver
        getActivity().registerReceiver(GpsServiceReceiver, new IntentFilter(
                GpsService.BROADCAST_ACTION));
    }


    // -- checkInternetConnectivity --//
    //    Starts a service that checks connectivity and waits for answer
    //
    private void checkInternetConnectivity() {
        final BroadcastReceiver InternetServiceReceiver;
        final Intent InternetServiceIntent = InternetService.newIntent(getActivity());
        getActivity().startService(InternetServiceIntent);
        Log.i("NWK", "Entered in network checking !");
        InternetServiceReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isConnected = Boolean.parseBoolean(intent.getStringExtra("isConnected"));
                Log.i("NWK:", "onReceive Poll isConnected " + isConnected);
                mLocker.setInternetConnected(isConnected);
                getActivity().stopService(InternetServiceIntent);
                getActivity().unregisterReceiver(this);
            }
        };
        getActivity().registerReceiver(InternetServiceReceiver, new IntentFilter(
                InternetService.BROADCAST_ACTION));
    }

    // Connects to the server
    private void checkCloudConnectivity() {
        //Start an async task to check if we can connect to the server
        task = new FetchCloudTask("isCloudConnected");
        task.execute();
    }
    private void updateCloud() {
        //Update fields of location in the SQL db
        task = new FetchCloudTask("setLocation");
        task.execute();
    }


    //Get data from website
    private class FetchCloudTask extends AsyncTask<Void,Void,Boolean> {
        private String mQuery;

        public FetchCloudTask(String query) {
            mQuery = query;
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            String longitude;
            String latitude;
            Log.i("ASYNC:", "doInBackground");
            switch (mQuery) {
                case "isCloudConnected":
                    Log.i("ASYNC:", "We are in isCoudConnected");
                    return (new CloudFetchr().isCloudConnected());
                case "setLocation":
                    Log.i("ASYNC:", "We are in setLocation");
                    if (mLocker.isGpsLocated()) {
                        longitude = String.valueOf(mLocker.getLockerLocation().getLongitude());
                        latitude = String.valueOf(mLocker.getLockerLocation().getLatitude());
                    } else {
                        longitude = "not_available";
                        latitude= "not_available";
                    }
                    return (new CloudFetchr().setLocation(longitude,latitude));
                default:
                    return (new CloudFetchr().isCloudConnected());
            }

        }

        @Override
        protected void onPostExecute(Boolean isConnected) {
            Log.i("SERGI:CLOUD:", "AsyncTask postExec, success = " + isConnected);
            switch (mQuery) {
                case "isCloudConnected":
                    mLocker.setCloudAlive(isConnected);
                    break;
                case "setLocation":
                    Log.i("SERGI:CLOUD:", "AsyncTask postExec for setLocation, success = " + isConnected);
                    break;
                default:
                    //Do nothing
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
} //End of Class
