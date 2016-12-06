package com.bignerdranch.android.ibikestation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

/**
 * Created by sredorta on 11/29/2016.
 */
public class iBikeRunningActivity extends SingleFragmentActivity{
    public static Intent newIntent(Context context,Locker myLocker) {
        Object myObject = myLocker;
        MyParcelable myParcelable = new MyParcelable();
        Intent i = new Intent(context, iBikeRunningActivity.class);
        myParcelable.setObject(myObject);
        i.putExtra("MyParcelable", myParcelable);
        return i;
    }
    public static Intent newIntent(Context context, String action) {
        Intent i = new Intent(context, iBikeRunningActivity.class);
        i.putExtra("action", action);
        return i;
    }

    @Override
    protected Fragment createFragment() {
        MyParcelable myParcelable;
        Object myObject;
        Locker myLocker;

        myParcelable = (MyParcelable) getIntent().getExtras().getParcelable("MyParcelable");
        myObject = myParcelable.getObject();
        myLocker = (Locker) myObject;
        return iBikeRunningFragment.newInstance(myLocker);
    }
}
