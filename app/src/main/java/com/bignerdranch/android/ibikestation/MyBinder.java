package com.bignerdranch.android.ibikestation;

import android.os.Binder;

/**
 * Created by sredorta on 11/29/2016.
 */

public class MyBinder extends Binder {

    private Object myObject;

    public MyBinder(Object object) {
        myObject = object;
    }

    public Object getObject() {
        return myObject;
    }

}