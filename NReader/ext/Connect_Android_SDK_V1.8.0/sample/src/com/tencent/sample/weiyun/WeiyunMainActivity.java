package com.tencent.sample.weiyun;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.tencent.sample.AppConstants;
import com.tencent.sample.R;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.WeiyunConstants;

public class WeiyunMainActivity extends Activity implements OnClickListener {
        
    private Button mPictureButton;
    private Button mMusicButton;
    private Button mStructureButton;
    private Button mVideoButton;
    private Button mCancelButton;

    private boolean mIsLogined;
    
    private Tencent mTencent;
    
    public static Context appContext;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        appContext = getApplicationContext();

        mTencent = Tencent.createInstance(AppConstants.APP_ID, this);
        mIsLogined = mTencent.isSessionValid();//true;//false;
        setContentView(R.layout.weiyun_main_activity);
        initViews();
        
 //        mTencent.setAccessToken("B3EAF605CF61599301DC678C5F18C6A0", "9999999");
//        mTencent.setOpenId("BAD03AD100088F70210F44BEA150BABE");
//        mIsLogined = true;
    }
    
    
    private void initViews() {
        mPictureButton = (Button) findViewById(R.id.weiyun_main_picture);
        mPictureButton.setOnClickListener(this);
        mMusicButton = (Button) findViewById(R.id.weiyun_main_music);
        mMusicButton.setOnClickListener(this);
        mStructureButton = (Button) findViewById(R.id.weiyun_main_structure);
        mStructureButton.setOnClickListener(this);
        mVideoButton = (Button) findViewById(R.id.weiyun_main_video);
        mVideoButton.setOnClickListener(this);
        mCancelButton = (Button) findViewById(R.id.weiyun_main_cancel);
        mCancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        int viewId = arg0.getId();
        switch (viewId) {
            case R.id.weiyun_main_picture:
                if (mIsLogined == false) {
                    Toast.makeText(this, "请先登录", Toast.LENGTH_LONG).show();
                } else {
                    selectOperation(WeiyunConstants.ACTION_PICTURE);
                }               
                break;
                
            case R.id.weiyun_main_music:
                if (mIsLogined == false) {
                    Toast.makeText(this, "请先登录", Toast.LENGTH_LONG).show();
                } else {
                    selectOperation(WeiyunConstants.ACTION_MUSIC);
                }   
                break;
                
            case R.id.weiyun_main_structure:
                if (mIsLogined == false) {
                    Toast.makeText(this, "请先登录", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(this, RecordOperationActivity.class);
                    startActivity(intent);
                }
                break;
                
            case R.id.weiyun_main_video:
                if (mIsLogined == false) {
                    Toast.makeText(this, "请先登录", Toast.LENGTH_LONG).show();
                } else {
                    selectOperation(WeiyunConstants.ACTION_VIDEO);
                }   
                break;

            case R.id.weiyun_main_cancel:
                cancel();
                break;

            default:
                break;
        }
    }
    
    /**
     * 根据选择的数据类型，进入下一步，选择操作类型
     * @param actionType
     */
    private void selectOperation(int actionType) {
        Intent intent = new Intent(this, OperationSelectActivity.class);
        intent.putExtra("actiontype", actionType);
        startActivity(intent);
    }
    
    /**
     * 点击确认，退出程序
     */
    private void cancel() {
        finish();
    }
    
    @Override
    public void onBackPressed() {
        finish();
    }

    private void showInfo(String info) {
        if (info != null) {
            Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // must call mTencent.onActivityResult.
        if (!mTencent.onActivityResult(requestCode, resultCode, data)) {
            
        }
    }
    
    /**
     * 获取全局的contexts
     * @return
     */
    public static Context getApplicateContext() {
        return appContext;
    }
}
