package com.bignerdranch.android.ibikestation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sredorta on 12/15/2016.
 */
public class ImageViewChecker extends ImageView {
    private static Boolean DEBUG_MODE = false;
    private Context mContext;
    private static final String TAG ="ImageViewChecker::";
    private static final String IMAGE_ICONS_FOLDER = "checkerImages";
    private Bitmap mOriginalBitmap;
    private Bitmap mRedBitmap;
    private Bitmap mGreenBitmap;
    private Bitmap mSepiaBitmap;
    private AssetManager mAssets;
    private ImageViewChecker mImageViewChecker;
    //Setts the color accordingly after animation
    private Boolean mResult = false;

    //Default constructor
    public ImageViewChecker(Context context) {
        super(context);
        mContext = context;
        mAssets = context.getAssets();
    }
    //Default constructor
    public ImageViewChecker(Context context, AttributeSet atts) {
        super(context,atts);
        mContext = context;
        mAssets = context.getAssets();
        mImageViewChecker = this;
    }

    public ImageViewChecker(Context context, AttributeSet atts,int defStyleAttr) {
        super(context,atts,defStyleAttr);
        mContext = context;
        mAssets = context.getAssets();
        mImageViewChecker = this;
    }
    /*
    public ImageViewChecker(Context context, AttributeSet atts,int defStyleAttr,int defStyleRes) {
        super(context,atts,defStyleAttr,defStyleRes);
        mContext = context;
        mAssets = context.getAssets();
    }
*/

    //Handle Logs in Debug mode
    public static void setDebugMode(Boolean mode) {
        DEBUG_MODE = mode;
        if (DEBUG_MODE) Log.i(TAG, "Debug mode enabled !");
    }

    public void setResult(Boolean result) {
        mResult = result;
    }
    public Boolean getResult() {
        return mResult;
    }

    public Bitmap getRedBitmap() {
        return mRedBitmap;
    }

    public void setImageColor(String color) {
        switch (color) {
            case "red":
                this.setImageBitmap(mRedBitmap);
                break;
            case "green":
                this.setImageBitmap(mGreenBitmap);
                break;
            case "sepia":
                this.setImageBitmap(mSepiaBitmap);
                break;
            default:
                this.setImageBitmap(mOriginalBitmap);
        }
        if (color.equals("red")) {
            this.setImageBitmap(mRedBitmap);
        }
    }
    public Bitmap getOriginalBitmap() {
        return mOriginalBitmap;
    }
    public Bitmap getGreenBitmap() {
        return mGreenBitmap;
    }
    public Bitmap getSepiaBitmap() {
        return mSepiaBitmap;
    }


    public void loadBitmapAsset(String asset) {
        if (DEBUG_MODE) Log.i(TAG,"loadBitmapAsset:");
        try {
            for (String filename : mAssets.list(IMAGE_ICONS_FOLDER)) {
                if (DEBUG_MODE) Log.i(TAG,"Found asset: " + filename);
                if (filename.equals(asset)) {
                    if (DEBUG_MODE) Log.i(TAG,"Loading asset image: " + filename);
                    String assetPath = IMAGE_ICONS_FOLDER + "/" + filename;
                    mOriginalBitmap = loadBitmap(assetPath);
                    mRedBitmap = colorizeBitmap("red",mOriginalBitmap);
                    mGreenBitmap = colorizeBitmap("green",mOriginalBitmap);
                    mSepiaBitmap = colorizeBitmap("sepia",mOriginalBitmap);
                    break;
                }
            }
        } catch (IOException ioe) {
            Log.i(TAG, "Caught exception: " + ioe);
        }
        //By default we want sepia color
        this.setImageColor("sepia");
    }



    private Bitmap loadBitmap(String asset) throws IOException {
        InputStream ims;

        //Bitmap loader for the original image
        Bitmap bitmap;
        ims = mAssets.open(asset);
        if (DEBUG_MODE) Log.i(TAG,"Loading bitmap :" + asset);
        bitmap = BitmapFactory.decodeStream(ims);
        ims.close();
        return bitmap;
    }

    //Colors a bitmap to generate sepia/bw/green/red flavours
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

    private ObjectAnimator startFallDownAnimation() {
        if (DEBUG_MODE) Log.i(TAG,"Started initial anim");
        //Turn on visibility
        this.setVisibility(View.VISIBLE);
        ObjectAnimator fallDownAnimator = ObjectAnimator.ofFloat(this, "y", -100, 400).setDuration(1000);
        fallDownAnimator.start();
        return fallDownAnimator;
    }
    private ObjectAnimator startFadeInOutAnimation() {
        if (DEBUG_MODE) Log.i(TAG,"Started fadeInOut anim");
        ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(this, "alpha", 1, 0, 1).setDuration(2000);
        fadeInAnimator.setStartDelay(2000);
        fadeInAnimator.setRepeatCount(4);
        //fadeInAnimator.start();
        return fadeInAnimator;
    }
    private ObjectAnimator startFadeOutAnimation() {
        if (DEBUG_MODE) Log.i(TAG,"Started fadeOut anim");
        ObjectAnimator fadeOutAnimator = ObjectAnimator.ofFloat(this, "alpha", 1, 0).setDuration(1000);
        fadeOutAnimator.setRepeatCount(0);
        //fadeOutAnimator.start();
        return fadeOutAnimator;
    }
    private ObjectAnimator startFadeInAnimation() {
        if (DEBUG_MODE) Log.i(TAG,"Started fadeIn anim");
        ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(this, "alpha", 0, 1).setDuration(1000);
        fadeInAnimator.setRepeatCount(0);
        fadeInAnimator.start();
        return fadeInAnimator;
    }
    public AnimatorSet startAnimation() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(startFallDownAnimation(),startFadeInOutAnimation(),startFadeOutAnimation());
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                    if (DEBUG_MODE) Log.i(TAG,"Result is :" + mResult);
                    if (mResult) {
                        mImageViewChecker.setImageColor("green");
                    } else {
                        mImageViewChecker.setImageColor("red");
                    }
                    mImageViewChecker.startFadeInAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        return animatorSet;
    }

/*
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

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(fadeInResultAnimator).after(fadeInAnimator);
        animatorSet.start() ;

        return fadeInAnimator;
    }*/
}
