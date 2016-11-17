package com.bignerdranch.android.ibikestation;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
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
    private static final String IMAGE_FOLDER = "checkerImages";
    private List<ImageAsset> mImages = new ArrayList<>();

    private AssetManager mAssets;
    public AssetHandler(Context context) {
        mAssets = context.getAssets();
        loadAssets();
    }

    private void loadAssets() {
        Pattern mPattern;
        Matcher mMatcher;
        String   imageNamesString = "";
        String[] imageNames;
        Log.i("SERGI","Enter here !");
        try {
            for (String tmp : mAssets.list(IMAGE_FOLDER)) {
                if (imageNamesString.equals("")) {
                    imageNamesString = imageNamesString.concat(tmp);
                } else {
                    imageNamesString = imageNamesString.concat(" " + tmp);
                }
            }
            Log.i("SERGI","imageNamesString is = " + imageNamesString);
            mPattern = Pattern.compile("_.*.png$");
            mMatcher = mPattern.matcher(imageNamesString);
            imageNamesString = mMatcher.replaceAll("");
            Log.i("SERGI","imageNamesString is = " + imageNamesString);
            imageNames = imageNamesString.split(" ");

            Log.i(TAG, "Found " + imageNames.length + " images");
        } catch (IOException ioe) {
            Log.i(TAG, "Could not find assets !");
            return;
        }
        for (String filename : imageNames) {
            Log.i("SERGI","Image called " + filename);
            String assetPath = IMAGE_FOLDER + "/" + filename;
            ImageAsset image = new ImageAsset(assetPath);
            try {
                load(image);
            } catch (IOException ex) {
                Log.i("SERGI", "Error while loading drawables !");
            }
            mImages.add(image);
        }
    }
    //Returns the Image asset that matches the pattern exactly
    public ImageAsset getImageAsset(String s) {
        Pattern mPattern;
        Matcher mMatcher;
        ImageAsset myImage = null;

        for (ImageAsset image : mImages) {
            mPattern = Pattern.compile(s);
            mMatcher = mPattern.matcher(image.getName());
            if (mMatcher.matches()) {
                myImage = image;
            }
        }
        return myImage;
   }

    private void load(ImageAsset image) throws IOException {
        InputStream ims;

        //Bitmap loader
        Bitmap bitmap;

        //Load Red drawable

        Drawable d;
        ims = mAssets.open(image.getAssetPath() + "_red.png");
        Log.i("SERGI","Setting red drawable with " + image.getAssetPath() + "_red.png");
        bitmap = BitmapFactory.decodeStream(ims);
        d = Drawable.createFromStream(ims, null);
        image.setImageRedDrawable(d);
        ims.close();

        //Load green drawable
        ims = mAssets.open(image.getAssetPath() + "_green.png");
        Log.i("SERGI","Setting green drawable with " + image.getAssetPath() + "_green.png");
        image.setImageGreenDrawable(Drawable.createFromStream(ims, null));
        ims.close();

        //Load sepia drawable
        ims = mAssets.open(image.getAssetPath() + "_sepia.png");
        Log.i("SERGI","Setting sepia drawable with " + image.getAssetPath() + "_sepia.png");
        image.setImageSepiaDrawable(Drawable.createFromStream(ims, null));
        ims.close();
    }


    private Bitmap createSepia(Bitmap src) {
        ColorMatrix t;

        ColorMatrix colorMatrix_Sepia = new ColorMatrix();
        colorMatrix_Sepia.setSaturation(0);

        ColorMatrix colorScale = new ColorMatrix();
        colorScale.setScale(1, 1, 0.8f, 1);

        colorMatrix_Sepia.postConcat(colorScale);

        ColorFilter ColorFilter_Sepia = new ColorMatrixColorFilter(
                colorMatrix_Sepia);

        Bitmap bitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();

        paint.setColorFilter(ColorFilter_Sepia);
        canvas.drawBitmap(src, 0, 0, paint);

        return bitmap;
    }



}
