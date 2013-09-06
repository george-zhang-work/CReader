
package com.nreader.utility;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

/**
 * Exif is short for "Exchangeable image file format".
 */
public class ExifUtils {
    public static final String TAG = ExifUtils.class.getSimpleName();

    /**
     * To get the correctly photo by correct the orientation. The uri specified
     * photo maybe stored in SD Card or Content.
     * 
     * @param context the context.
     * @param uri the photo's uri.
     * @param bitmap the bitmap.
     * @return the corrected bitmap.
     */
    public static Bitmap getCorrectlyPhoto(Context context, Uri uri, Bitmap bitmap) {
        int orientation = 0;
        if ("content".equals(uri.getScheme())) {
            orientation = getContentOrientation(context, uri);
        } else {
            orientation = getStorageOrientation(uri.getPath());
        }
        if (orientation > 0) {
            LogUtil.v(TAG, "ExifInterfaceUtil, orientation=" + orientation + ", bitmap roate "
                    + orientation);
            Matrix matrix = new Matrix();
            matrix.setRotate(orientation);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
        }
        return bitmap;
    }

    /**
     * Get the correctly orientation bitmap that specified by photoPath.
     * 
     * @param photoPath the photoPath of the bitmap.
     * @param bitmap the bitmap to correct orientation.
     * @return the correctly orientation bitmap.
     */
    public static Bitmap getCorrectlyPhoto(String photoPath, Bitmap bitmap) {
        if (!TextUtils.isEmpty(photoPath)) {
            int orientation = getStorageOrientation(photoPath);
            if (orientation > 0) {
                Log.i(TAG, "ExifInterfaceUtil, orientation=" + orientation
                        + ",bitmap roate " + orientation);
                Matrix matrix = new Matrix();
                matrix.setRotate(orientation);
                return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);
            }
        }
        return bitmap;
    }

    /**
     * Get the content photo orientation.
     * 
     * @param context the context.
     * @param photoUri the photo's uri.
     * @return
     */
    public static int getContentOrientation(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri, new String[] {
                MediaStore.Images.ImageColumns.ORIENTATION
        }, null, null, null);
        if (cursor.getCount() != 1) {
            return -1;
        }
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    /**
     * Get the storage's orientation. It's use EXIF(Exchangeable image file
     * format) to do get the orientation info.
     * 
     * @param photoPath the photoPath.
     * @return the photo's exif info's orientation.
     */
    public static int getStorageOrientation(String photoPath) {
        int orientation = 0;
        try {
            orientation = new ExifInterface(photoPath).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                orientation = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                orientation = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                orientation = 270;
                break;
            default:
                orientation = 0;
        }
        return orientation;
    }
}
