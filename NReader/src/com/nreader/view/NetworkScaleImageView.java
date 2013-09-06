
package com.nreader.view;

/**
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.NetworkImageView;

/**
 * Handles fetching an image from a URL as well as the life-cycle of the
 * associated request.
 */
public class NetworkScaleImageView extends ImageView {
    private static final String TAG = NetworkScaleImageView.class.getSimpleName();

    /** The URL of the network image to load */
    private String mUrl;

    /**
     * Resource ID of the image to be used as a placeholder until the network
     * image is loaded.
     */
    private Drawable mDefaultImageDrawable;

    /**
     * Resource ID of the image to be used if the network response fails.
     */
    private Drawable mErrorImageDrawable;

    /** Local copy of the ImageLoader. */
    private ImageLoader mImageLoader;

    /** Current ImageContainer. (either in-flight or finished) */
    private ImageContainer mImageContainer;

    private View mProgress;

    public NetworkScaleImageView(Context context) {
        this(context, null);
    }

    public NetworkScaleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetworkScaleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setProrgess(View progress) {
        mProgress = progress;
    }

    public void setProgress(final int visibility) {
        if (mProgress != null) {
            mProgress.setVisibility(visibility);
        }
    }

    /**
     * Sets URL of the image that should be loaded into this view. Note that
     * calling this will immediately either set the cached image (if available)
     * or the default image specified by
     * {@link NetworkImageView#setDefaultImageResId(int)} on the view. NOTE: If
     * applicable, {@link NetworkImageView#setDefaultImageResId(int)} and
     * {@link NetworkImageView#setErrorImageResId(int)} should be called prior
     * to calling this function.
     * 
     * @param url The URL that should be loaded into this ImageView.
     * @param imageLoader ImageLoader that will be used to make the request.
     */
    public void setImageUrl(String url, ImageLoader imageLoader) {
        mUrl = url;
        mImageLoader = imageLoader;
        // The URL has potentially changed. See if we need to load it.
        loadImageIfNecessary(false);
    }

    /**
     * Sets the default image resource ID to be used for this view until the
     * attempt to load it completes.
     */
    public void setDefaultImageResId(int defaultImage) {
        setDefaultImageDrawable(getResources().getDrawable(defaultImage));
    }

    public void setDefaultImageDrawable(Drawable defaultImage) {
        mDefaultImageDrawable = defaultImage;
    }

    /**
     * Sets the error image resource ID to be used for this view in the event
     * that the image requested fails to load.
     */
    public void setErrorImageResId(int errorImage) {
        setErrorImageDrawable(getResources().getDrawable(errorImage));
    }

    public void setErrorImageDrawable(Drawable errorImage) {
        mErrorImageDrawable = errorImage;
    }

    /**
     * @see NetworkScaleImageView#setImageBitmap(Bitmap, boolean)
     */
    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
    }

    /**
     * Sets a Bitmap as the content of this ImageView.This method should be
     * called from the outside.
     * 
     * @param bm The bitmap to set.
     * @param fromUrl true, if the drawable gets from the some url; false
     *            otherwise.
     * @see #setImageBitmap(Bitmap)
     */
    public void setImageBitmap(Bitmap bm, boolean fromUrl) {
        if (!fromUrl) {
            mUrl = null;
        }
        setImageBitmap(bm);
    }

    /**
     * @see NetworkScaleImageView#setImageDrawable(Drawable, boolean)
     */
    @Override
    public void setImageDrawable(Drawable drawable) {
        if (drawable != null) {
            centerBounds(drawable);
        }
        super.setImageDrawable(drawable);
    }

    /**
     * Sets a drawable as the content of this ImageView. This method should be
     * called from the outside.
     * 
     * @param drawable The drawable to set
     * @param fromUrl true, if the drawable gets from the some url; false
     *            otherwise.
     * @see #setImageDrawable(Drawable)
     */
    public void setImageDrawable(Drawable drawable, boolean fromUrl) {
        if (!fromUrl) {
            mUrl = null;
        }
        setImageDrawable(drawable);
    }

    /**
     * Loads the image for the view if it isn't already loaded.
     * 
     * @param isInLayoutPass True if this was invoked from a layout pass, false
     *            otherwise.
     */
    private void loadImageIfNecessary(final boolean isInLayoutPass) {
        int width = getWidth();
        int height = getHeight();

        // if the view's bounds aren't known yet, hold off on loading the image.
        if (width == 0 && height == 0) {
            return;
        }

        // if the URL to be loaded in this view is empty, cancel any old
        // requests and clear the currently loaded image.
        if (TextUtils.isEmpty(mUrl)) {
            if (mImageContainer != null) {
                mImageContainer.cancelRequest();
                mImageContainer = null;
            }
            setProgress(View.GONE);
            if (getDrawable() != null) {
                centerBounds(getDrawable());
            } else {
                setImageDrawable(mDefaultImageDrawable);
            }
            return;
        }

        // if there was an old request in this view, check if it needs to be
        // canceled.
        if (mImageContainer != null && mImageContainer.getRequestUrl() != null) {
            if (mImageContainer.getRequestUrl().equals(mUrl)) {
                // if the request is from the same URL, return.
                return;
            } else {
                // if there is a pre-existing request, cancel it if it's
                // fetching a different URL.
                mImageContainer.cancelRequest();
                setImageBitmap(null);
            }
        }

        setProgress(View.VISIBLE);
        // The pre-existing content of this view didn't match the current URL.
        // Load the new image from the network.
        ImageContainer newContainer = mImageLoader.get(mUrl, new ImageListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setProgress(View.GONE);
                if (mErrorImageDrawable != null) {
                    setImageDrawable(mErrorImageDrawable);
                }
            }

            @Override
            public void onResponse(final ImageContainer response, boolean isImmediate) {
                // If this was an immediate response that was delivered inside
                // of a layout pass do not set the image immediately as it will
                // trigger a requestLayout inside of a layout. Instead, defer
                // setting the image by posting back to the main thread.
                if (isImmediate && isInLayoutPass) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            onResponse(response, false);
                        }
                    });
                    return;
                }

                if (response.getBitmap() != null) {
                    setProgress(View.GONE);
                    setImageBitmap(response.getBitmap());
                } else if (mDefaultImageDrawable != null) {
                    setImageDrawable(mDefaultImageDrawable);
                }
            }
        });

        // update the ImageContainer to be the new bitmap container.
        mImageContainer = newContainer;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        loadImageIfNecessary(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mImageContainer != null) {
            // If the view was bound to an image request, cancel it and clear
            // out the image from the view.
            mImageContainer.cancelRequest();
            setImageBitmap(null);
            // also clear out the container so we can reload the image if
            // necessary.
            mImageContainer = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

    private void centerBounds(Drawable dr) {
        int vwidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int vheight = getHeight() - getPaddingBottom() - getPaddingTop();

        int dwidth = dr.getIntrinsicWidth();
        int dheight = dr.getIntrinsicHeight();

        if (vwidth <= 0 || vheight <= 0 || dwidth <= 0 || dheight <= 0) {
            return;
        }

        int radio = 4;
        if (dwidth * 2 > vwidth || dheight * 2 > vheight) {
            radio = 1;
        } else if (dwidth * 4 > vwidth || dheight * 4 > vheight) {
            radio = 2;
        }

        Log.i(TAG, "view(w, h) := " + vwidth + ", " + vheight);
        Log.i(TAG, "drawable(v, h) := " + dwidth + ", " + dheight + ", " + dr.toString());
        Log.i(TAG, "radio := " + radio);

        int vswidth, vsheight;
        // 1.0 set display area.
        if (radio > 1) {
            vswidth = (vwidth + radio - 1) / radio;
            vsheight = (vheight + radio - 1) / radio;
        } else {
            vswidth = vwidth;
            vsheight = vheight;
        }

        float scale, dx = 0, dy = 0;
        // d is little than v area. we should to zoom in d.
        if (vswidth * dheight > vsheight * dwidth) {
            scale = (float) vsheight / dheight;
            dx = (vwidth - dwidth * scale) * 0.5f;
            dy = (vheight - dheight * scale) * 0.5f;
        } else {
            scale = (float) vswidth / dwidth;
            dx = (vwidth - dwidth * scale) * 0.5f;
            dy = (vheight - dheight * scale) * 0.5f;
        }
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        matrix.postTranslate(dx, dy);
        setImageMatrix(matrix);
    }
}
