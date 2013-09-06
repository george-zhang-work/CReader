
package com.tencent.sample.weiyun;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.tencent.sample.Util;

public class ImageViewDialog extends Dialog {

    private Context mContext;

    private ImageView mImgView;

    private FrameLayout mFlMain;

    public ImageViewDialog(Context context, String filepath) {
        super(context);
        mContext = context;
        mImgView = new ImageView(mContext);
        Bitmap bm = Util.extractThumbNail(filepath,800,600,false);
        mImgView.setImageBitmap(bm);
        mImgView.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageViewDialog.this.dismiss();
                return false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutParams layoutParams = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        mImgView.setLayoutParams(layoutParams);
        setContentView(mImgView);

        // LayoutParams flMainParams = new LayoutParams(
        // ViewGroup.LayoutParams.FILL_PARENT,
        // ViewGroup.LayoutParams.FILL_PARENT);
        //
        // mFlMain = new FrameLayout(mContext);
        // flMainParams.gravity = Gravity.CENTER;
        // mFlMain.setLayoutParams(flMainParams);
        // mFlMain.setBackgroundColor(0xFF000000);
        // setContentView(mFlMain);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
