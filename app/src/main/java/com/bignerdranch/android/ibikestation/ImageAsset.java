package com.bignerdranch.android.ibikestation;

import android.graphics.Bitmap;
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
    private Bitmap mOriginalBitmap;
    private Bitmap mRedBitmap;
    private Bitmap mGreenBitmap;
    private Bitmap mSepiaBitmap;

    public void setOriginalBitmap(Bitmap bmp ) {
        mOriginalBitmap = bmp;
    }
    public Bitmap getOriginalBitmap() {
        return mOriginalBitmap;
    }

    public void setImageRedBitmap(Bitmap bmp ) {
           mRedBitmap = bmp;
    }
    public Bitmap getImageRedBitmap() {
        return mRedBitmap;
    }
    public void setImageGreenBitmap(Bitmap bmp ) {
        mGreenBitmap = bmp;
    }
    public Bitmap getImageGreenBitmap() {
        return mGreenBitmap;
    }

    public void setImageSepiaBitmap(Bitmap bmp ) {
        mSepiaBitmap = bmp;
    }
    public Bitmap getImageSepiaBitmap() {
        return mSepiaBitmap;
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
    public String getRootName() {
        String mRootName = mName;
        Pattern mPattern;
        Matcher mMatcher;

        mPattern = Pattern.compile("\\.[a-z]*$");
        mMatcher = mPattern.matcher(mName);
        mRootName=mMatcher.replaceAll("");

        return mRootName;
    }
}
