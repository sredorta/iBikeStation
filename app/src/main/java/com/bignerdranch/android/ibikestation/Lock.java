package com.bignerdranch.android.ibikestation;

/*
Lock class
    Contains info of each Lock in a locker
 */

public class Lock {
    private boolean mLocked;
    private boolean mAvailable;
    private int mLockId;

    //Handles if locker is locked
    public boolean isLocked() {
        return mLocked;
    }
    public void setLocked(boolean locked) {
        mLocked = locked;
    }

    //Handles if locker is available
    public boolean isAvailable() {
        return mAvailable;
    }
    public void setAvailable(boolean available) {
        mAvailable = available;
    }

    //Handles if locker is locked
    public int getLockId() {
        return mLockId;
    }
    public void setLockId(int id) {
        mLockId = id;
    }
    //Creates a Lock with ID
    public Lock(int id) {
        mLockId = id;
    }

    //Opens the Lock door
    public void open(int id) {

    }

    //Closes the Lock door
    public void close(int id) {

    }

}
