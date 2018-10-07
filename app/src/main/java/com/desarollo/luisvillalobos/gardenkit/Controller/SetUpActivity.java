package com.desarollo.luisvillalobos.gardenkit.Controller;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by LuisVillalobos on 14/12/2017.
 */

public class SetUpActivity {

    protected static SetUpActivity SetUpActivity;

    /**
     * Hide the ActionBar
     *
     * @param appCompatActivity
     */
    public static void hiderActionBar(AppCompatActivity appCompatActivity) {
        appCompatActivity.getSupportActionBar().hide();
    }

    /**
     * Hide the StatusBar
     *
     * @param appCompatActivity
     */
    public static void hideStatusBar(AppCompatActivity appCompatActivity) {
        appCompatActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * Hide the keyboard when an Activity opens
     *
     * @param appCompatActivity
     */
    public static void hideSoftKeyboard(AppCompatActivity appCompatActivity) {
        appCompatActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /**
     * Set the Window as Portrait
     *
     * @param appCompatActivity
     */
    public static void setWindowPortrait(AppCompatActivity appCompatActivity){
        appCompatActivity.setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    public void cambiarColorStatusBar(Activity activity, int colorResource) {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(activity.getResources().getColor(colorResource));
        }
    }

    public void cambiarColorActionBar(AppCompatActivity appCompatActivity, Drawable colorDrawable) {
        appCompatActivity.getSupportActionBar().setBackgroundDrawable(colorDrawable);
        appCompatActivity.getSupportActionBar().setTitle("");
    }


    public void ponerLogoMenuActionBar(AppCompatActivity appCompatActivity, int imageResource/*, Drawable color*/) {

        Drawable dr = appCompatActivity.getResources().getDrawable(imageResource);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(appCompatActivity.getResources(), Bitmap.createScaledBitmap(bitmap, 320, 80, true));

        appCompatActivity.getSupportActionBar().setIcon(d);
        appCompatActivity.getSupportActionBar().setDisplayUseLogoEnabled(true);
        appCompatActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void ponerLogoMenuActionBarSecundario(AppCompatActivity appCompatActivity, int imageResource/*, Drawable color*/) {

        Drawable dr = appCompatActivity.getResources().getDrawable(imageResource);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(appCompatActivity.getResources(), Bitmap.createScaledBitmap(bitmap, 320, 80, true));

        appCompatActivity.getSupportActionBar().setIcon(d);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public RoundedBitmapDrawable ponerImagenCircular(AppCompatActivity appCompatActivity, int width, int height, int imageResource) {
        Bitmap bitmap = getResizeBitmap(BitmapFactory.decodeResource(appCompatActivity.getResources(), imageResource), 250, 250);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(appCompatActivity.getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);
        return roundedBitmapDrawable;
    }

    public RoundedBitmapDrawable ponerImagenCircular(AppCompatActivity appCompatActivity, int width, int height, byte[] imageResource) {
        Bitmap bitmap = getResizeBitmap(BitmapFactory.decodeByteArray(imageResource, 0, imageResource.length), 250, 250);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(appCompatActivity.getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);
        return roundedBitmapDrawable;
    }

    public Bitmap getResizeBitmap(Bitmap bitmap, int newHeight, int newWidth) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

        return resizeBitmap;
    }
}
