package com.bignerdranch.android.ibikestation;

import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sredorta on 11/16/2016.
 */
public class ImageAsset {
    private String mAssetPath;
    private String mName;
    private Drawable mRedDrawable;
    private Drawable mGreenDrawable;
    private Drawable mSepiaDrawable;
    private Drawable mDrawableBackGround;

    public Drawable getImageBackGroundResource() {
        return mDrawableBackGround;
    }
    public void setImageBackGroundResource(Drawable d) {
        mDrawableBackGround = d;
    }
    public void setImageRedDrawable(Drawable d ) {
           mRedDrawable = d;
    }
    public Drawable getImageRedDrawable() {
        return mRedDrawable;
    }
    public void setImageGreenDrawable(Drawable d ) {
        mGreenDrawable = d;
    }
    public Drawable getImageGreenDrawable() {
        return mGreenDrawable;
    }

    public void setImageSepiaDrawable(Drawable d ) {
        mSepiaDrawable = d;
    }
    public Drawable getImageSepiaDrawable() {
        return mSepiaDrawable;
    }

    public ImageAsset(String assetPath) {
        mAssetPath = assetPath;
        Log.i("SERGI", "assetPath = " + mAssetPath.toString());

        //Set the mName to only the root
        String[] components = assetPath.split("/");
        mName = components[components.length-1];
        Log.i("SERGI", "ImageAsset created with mName =  " + mName);
    }
    public String getAssetPath() {
        return mAssetPath;
    }
    public String getName() {
        return mName;
    }
}
