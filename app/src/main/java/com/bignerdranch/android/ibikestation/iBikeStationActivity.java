package com.bignerdranch.android.ibikestation;

import android.support.v4.app.Fragment;

/**
 * Created by sredorta on 11/10/2016.
 */
public class iBikeStationActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return iBikeStationFragment.newInstance();
    }

}
