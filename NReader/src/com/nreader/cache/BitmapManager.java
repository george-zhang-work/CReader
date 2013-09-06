
package com.nreader.cache;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageCache;

public class BitmapManager implements ImageCache {
    public static final String TAG = "BitmapCache";

    /**
     * The maximum disk cache size used for bitmap.
     */
    private final int MAX_DISK_BITMAP_CHCHE_SIZE = 32 * 1024 * 1024;

    /**
     * Helper that handles loading and caching images from remote URLs.
     */
    private ImageLoader mImageLoader;

    /**
     * Cache that stores the images.
     */
    private LruBitmapCache mImageCache;

    private static BitmapManager mInstance;

    public BitmapManager(Context context) {
        int maxSize = ((ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass() * 1024 * 1024 / 8;
        mImageCache = new LruBitmapCache(maxSize);
        mImageLoader = new ImageLoader(Volley.newRequestQueue(context,
                MAX_DISK_BITMAP_CHCHE_SIZE), this);
    }

    public static BitmapManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (BitmapManager.class) {
                if (mInstance == null) {
                    mInstance = new BitmapManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * To get the image loader.
     * 
     * @return The image loader.
     */
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    @Override
    public Bitmap getBitmap(String url) {
        return mImageCache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        mImageCache.put(url, bitmap);
    }

    /**
     * Clear all the images' cache
     */
    public void clearCache() {
        mImageCache.evictAll();
    }
}
