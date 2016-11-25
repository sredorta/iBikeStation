package com.bignerdranch.android.ibikestation;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.provider.CalendarContract;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sredorta on 11/16/2016.
 */
public class AssetHandler {
    private static final String TAG ="SERGI:Asset:";
    private static final String IMAGE_ICONS_FOLDER = "checkerImages";
    private List<ImageAsset> mImages = new ArrayList<>();

    private AssetManager mAssets;
    public AssetHandler(Context context) {
        mAssets = context.getAssets();
        loadAssets();
    }

    private void loadAssets() {
        Log.i("SERGI","Enter here !");
        try {
            for (String filename : mAssets.list(IMAGE_ICONS_FOLDER)) {
                Log.i("SERGI","Image called " + filename);
                String assetPath = IMAGE_ICONS_FOLDER + "/" + filename;
                ImageAsset image = new ImageAsset(assetPath);
                try {
                    load(image);
                } catch (IOException ex) {
                    Log.i("SERGI", "Error while loading drawables !");
                }
                mImages.add(image);
            }
        } catch (IOException ioe) {
            Log.i(TAG, "Could not find assets !");
        }
    }
    //Returns the Image asset that matches the pattern exactly
    public ImageAsset getImageAsset(String s) {
        Pattern mPattern;
        Matcher mMatcher;

        ImageAsset myImage = null;
        Log.i("SERGI", "Finding ImageAsset with pattern " + s);
        for (ImageAsset image : mImages) {
            mPattern = Pattern.compile(s);
            mMatcher = mPattern.matcher(image.getRootName());
            if (mMatcher.matches()) {
                Log.i("SERGI", "Found this one matching: " + image.getRootName());
                myImage = image;
            }
        }
        return myImage;
   }

    private void load(ImageAsset image) throws IOException {
        InputStream ims;

        //Bitmap loader for the original image
        Bitmap bitmap;
        ims = mAssets.open(image.getAssetPath());
        Log.i("SERGI","Loading default bitmap " + image.getAssetPath());
        bitmap = BitmapFactory.decodeStream(ims);
        ims.close();

        //Now create the diferent versions of the image
        image.setOriginalBitmap(bitmap);
        Log.i("SERGI", "Loaded original bitmap !");
        image.setImageRedBitmap(colorizeBitmap("red",image.getOriginalBitmap()));
        image.setImageGreenBitmap(colorizeBitmap("green",image.getOriginalBitmap()));
        image.setImageSepiaBitmap(colorizeBitmap("sepia",image.getOriginalBitmap()));
    }


    private Bitmap colorizeBitmap(String color, Bitmap src) {
        //Apply coloring filter
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrix colorScale = new ColorMatrix();
        switch (color) {
            case "red":    colorScale.setScale(4f,0.8f,0.8f,2f); break;
            case "green":  colorScale.setScale(0.1f,0.9f,0.1f,2f); break;
            case "blue":   colorScale.setScale(0.3f,0.3f,1,1); break;
            case "sepia":  colorScale.setScale(1, 1, 0.8f, 1); break;
            case "bw":     colorScale.setScale(1,1,1,1); break;
            default:       colorScale.setScale(1,1,1,1); break;
        }

        cm.postConcat(colorScale);
        ColorFilter cf = new ColorMatrixColorFilter(cm);

        Bitmap bitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColorFilter(cf);
        canvas.drawBitmap(src, 0, 0, paint);

        return bitmap;
    }

}
