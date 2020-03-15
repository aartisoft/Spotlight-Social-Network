/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphPhoneGalleryUtils.utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import esaph.spotlight.Esaph.EsaphAndroidTopBar.EsaphAndroidTopBarHelper;
import esaph.spotlight.R;

public class CameraBottomSheetUtility
{
  public static int HEIGHT, WIDTH;

  private String pathDir;

  public static void setupStatusBarHidden(AppCompatActivity appCompatActivity)
  {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
    {
      Window w = appCompatActivity.getWindow();
      w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
      w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
  }

  public static void showStatusBar(AppCompatActivity appCompatActivity) {
    synchronized (appCompatActivity) {
      Window w = appCompatActivity.getWindow();
      View decorView = w.getDecorView();
      // Show Status Bar.
      int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
      decorView.setSystemUiVisibility(uiOptions);
    }
  }

  public static void hideStatusBar(AppCompatActivity appCompatActivity) {
    synchronized (appCompatActivity) {
      Window w = appCompatActivity.getWindow();
      View decorView = w.getDecorView();
      // Hide Status Bar.
      int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
      decorView.setSystemUiVisibility(uiOptions);
    }
  }

  public static int getSoftButtonsBarSizePort(Activity activity) {
    // getRealMetrics is only available with API 17 and +
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      DisplayMetrics metrics = new DisplayMetrics();
      activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
      int usableHeight = metrics.heightPixels;
      activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
      int realHeight = metrics.heightPixels;
      if (realHeight > usableHeight) {
        return realHeight - usableHeight;
      } else {
        return 0;
      }
    }
    return 0;
  }

  public static void getScreenSize(Activity activity) {
    DisplayMetrics displayMetrics = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    HEIGHT = displayMetrics.heightPixels;
    WIDTH = displayMetrics.widthPixels;
  }

  public static float convertDpToPixel(float dp, Context context) {
    Resources resources = context.getResources();
    DisplayMetrics metrics = resources.getDisplayMetrics();
    return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
  }

  public static float convertPixelsToDp(float px, Context context) {
    Resources resources = context.getResources();
    DisplayMetrics metrics = resources.getDisplayMetrics();
    return px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
  }

  public static boolean isNull(View topChild) {
    return topChild == null;
  }

  public static boolean isViewVisible(View view) {
    return view != null && view.getVisibility() == View.VISIBLE;
  }

  public static void cancelAnimation(ViewPropertyAnimator animator) {
    if (animator != null) {
      animator.cancel();
    }
  }

  public static Cursor getCursor(Context context)
  {
    return context.getContentResolver().query(Constants.URI, Constants.PROJECTION,
              null, null, Constants.ORDERBY);
  }

  public static void manipulateVisibility(float slideOffset,
                                          RecyclerView recyclerView,
                                          View topbar,
                                          View topbarCollapsed,
                                          View textViewAccessRightSite,
                                          View clickme)
  {
    clickme.setAlpha(1 - slideOffset);
    textViewAccessRightSite.setAlpha(slideOffset);
    topbar.setAlpha(slideOffset);
    recyclerView.setAlpha(slideOffset);

    topbarCollapsed.setAlpha(1-(slideOffset * 1.5f));

    if (slideOffset > 0 && recyclerView.getVisibility() == View.INVISIBLE)
    {
      recyclerView.setVisibility(View.VISIBLE);
     // EsaphAndroidTopBarHelper.setTopBarContentFitInSystemUILightStatus(activity);
    }
    else if (recyclerView.getVisibility() == View.VISIBLE && (slideOffset) == 0)
    {
      //EsaphAndroidTopBarHelper.setTopBarFullScreenTransparent(activity);
      recyclerView.setVisibility(View.INVISIBLE);
    }
  }

  @SuppressWarnings("SameParameterValue")
  public static int getValueInRange(int min, int max, int value) {
    int minimum = Math.max(min, value);
    return Math.min(minimum, max);
  }

  public static void vibe(Context c, long l) {
    ((Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(l);
  }

  public static File writeImage(Bitmap bitmap, String path, int quality, int newWidth,
                                int newHeight) {
    File dir = new File(Environment.getExternalStorageDirectory(), path);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    File photo = new File(dir, "IMG_"
            + new SimpleDateFormat("yyyyMMdd_HHmmSS", Locale.ENGLISH).format(new Date())
            + ".jpg");
    if (photo.exists()) {
      photo.delete();
    }
    if (newWidth == 0 && newHeight == 0) {
      newWidth = bitmap.getWidth() / 2;
      newHeight = bitmap.getHeight() / 2;
    }
    bitmap = getResizedBitmap(bitmap, newWidth, newHeight);

    try {
      FileOutputStream fos = new FileOutputStream(photo.getPath());
      bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
      // fos.write(jpeg);
      fos.close();
    } catch (Exception e) {
      Log.e("PictureDemo", "Exception in photoCallback", e);
    }
    return photo;
  }

  public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
    int width = bm.getWidth();
    int height = bm.getHeight();
    float scaleWidth = ((float) newWidth) / width;
    float scaleHeight = ((float) newHeight) / height;
    // CREATE A MATRIX FOR THE MANIPULATION
    Matrix matrix = new Matrix();
    // RESIZE THE BIT MAP
    matrix.postScale(scaleWidth, scaleHeight);

    // "RECREATE" THE NEW BITMAP
    Bitmap resizedBitmap = Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false);
    return resizedBitmap.copy(Bitmap.Config.RGB_565, false);
  }

  public static Bitmap getScaledBitmap(int maxWidth, Bitmap rotatedBitmap) {
    try {

      int nh = (int) (rotatedBitmap.getHeight() * (512.0 / rotatedBitmap.getWidth()));
      return Bitmap.createScaledBitmap(rotatedBitmap, maxWidth, nh, true);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static Bitmap rotate(Bitmap scaledBitmap, int i) {
    if (i == 0) {
      return scaledBitmap;
    }
    Matrix matrix = new Matrix();
    matrix.preRotate(-i);
    return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(),
            scaledBitmap.getHeight(), matrix, false);
  }

  public static float getFingerSpacing(MotionEvent event) {
    try {
      float x = event.getX(0) - event.getX(1);
      float y = event.getY(0) - event.getY(1);
      return (float) Math.sqrt(x * x + y * y);
    } catch (Exception e) {
      Log.e("exc", "->" + e.getMessage());
      return 0;
    }
  }

  public static void scanPhoto(Context pix, File photo) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
      final Uri contentUri = Uri.fromFile(photo);
      scanIntent.setData(contentUri);
      pix.sendBroadcast(scanIntent);
    } else {
      pix.sendBroadcast(
              new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(photo.getAbsolutePath())));
    }
  }

}
