package com.bignerdranch.android.ibikestation;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sredorta on 11/29/2016.
 */

public class MyParcelable implements Parcelable {

    private Object myObject;

    public MyParcelable() {
    }

    public MyParcelable(Parcel parcel) {
        myObject = ((MyBinder)parcel.readStrongBinder()).getObject();
    }

    public void setObject(Object object) {
        myObject = object;
    }

    public Object getObject() {
        return myObject;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeStrongBinder(new MyBinder(myObject));
    }

    public int describeContents() {
        return myObject == null ? 0 : 1;
    }

    public static final Parcelable.Creator<MyParcelable> CREATOR = new Parcelable.Creator<MyParcelable>() {

        public MyParcelable createFromParcel(Parcel parcel) {
            return new MyParcelable(parcel);
        }

        public MyParcelable[] newArray(int length) {
            return new MyParcelable[length];
        }

    };
}