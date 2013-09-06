
package com.tencent.sample.weiyun;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.tencent.sample.AppConstants;
import com.tencent.sample.R;
import com.tencent.sample.Util;
import com.tencent.tauth.IUploadFileToWeiyunStatus;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UploadFileToWeiyun;import com.tencent.tauth.WeiyunConstants;
import org.json.JSONObject;

public class OperationSelectActivity extends Activity implements OnClickListener {

    private Button mUploadButton;
    private Button mGetlistButton;
    private Button mReturnButton;
    
    ProgressDialog mProgress;

    private int REQUEST_UPLOAD_PIC = 1001;
    private int REQUEST_UPLOAD_MUSIC = 1002;
    private int REQUEST_UPLOAD_VIDEO = 1004;
    
    private String current_intenttype = "*/*";
    private int current_requestcode = 0;
    private int current_actiontype;
    private String mRequestUrl;
    private Tencent mTencent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weiyun_operation_select_activity);
        initViews();
        Intent intent = getIntent();
        mTencent = Tencent.createInstance(AppConstants.APP_ID, this);
        current_actiontype = intent.getExtras().getInt("actiontype");
        switch (current_actiontype) {
            case WeiyunConstants.ACTION_PICTURE:
                current_intenttype = "image/*";
                current_requestcode = REQUEST_UPLOAD_PIC;
                mRequestUrl = "https://graph.qq.com/weiyun/upload_photo";
                break;
            case WeiyunConstants.ACTION_MUSIC:
                current_intenttype = "audio/*";
                current_requestcode = REQUEST_UPLOAD_MUSIC;
                mRequestUrl = "https://graph.qq.com/weiyun/upload_music";
                break;
            case WeiyunConstants.ACTION_VIDEO:
                current_intenttype = "video/*";
                current_requestcode = REQUEST_UPLOAD_VIDEO;
                mRequestUrl = "https://graph.qq.com/weiyun/upload_video";
                break;                

            default:
                break;
        }
        mProgress = new ProgressDialog(this);
    }

    private void initViews() {
        mUploadButton = (Button) findViewById(R.id.weiyun_operation_select_upload);
        mUploadButton.setOnClickListener(this);
        mGetlistButton = (Button) findViewById(R.id.weiyun_operation_select_getlist);
        mGetlistButton.setOnClickListener(this);
        mReturnButton = (Button) findViewById(R.id.weiyun_operation_select_return);
        mReturnButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        Log.i("weiyun_test", "weiyunOperationSelectActivity buttonClick");
        int viewid = arg0.getId();
        Intent intent = null;
        switch (viewid) {
            case R.id.weiyun_operation_select_upload:
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType(current_intenttype);
                startActivityForResult(intent, current_requestcode);
                break;
            case R.id.weiyun_operation_select_getlist:
                intent = new Intent(this, FileListActivity.class);
                intent.putExtra("actiontype", current_actiontype);
                startActivity(intent);
                break;
            case R.id.weiyun_operation_select_return:
                finish();
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == REQUEST_UPLOAD_PIC || requestCode == REQUEST_UPLOAD_MUSIC || requestCode == REQUEST_UPLOAD_VIDEO) {
                Log.i("weiyun_test", "weiyunOperationSelectActivity select picture finished");
                
                Uri uri = data.getData();
                String filepath = null;
                if (uri.toString().startsWith("content://")) {
                    
                    Cursor curosr = getContentResolver().query(uri, null, null, null, null);
                    int index = 0;
                    if (uri.getPath().contains("images")) {
                        index = curosr.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    } else if (uri.getPath().contains("video")) {
                        index = curosr.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                    } else if (uri.getPath().contains("audio")) {
                        index = curosr.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    }
                    curosr.moveToFirst();
                    filepath = curosr.getString(index);
                    curosr.close();                   
                } else {
                    filepath = uri.getPath();
                }
                if (filepath == null) {
                    mProgress.dismiss();
                    Toast.makeText(OperationSelectActivity.this, "获取文件路径有误", Toast.LENGTH_SHORT).show();
                    return;
                }
                UploadFileToWeiyun upload = new UploadFileToWeiyun(mTencent,filepath,mRequestUrl,
                        new IUploadFileToWeiyunStatus() {
                            
                            @Override
                            public void onUploadSuccess() {
                                Log.i("weiyun_test", "upload success");
                                if (OperationSelectActivity.this.isFinishing()) {
                                    return;
                                }
                                mProgress.dismiss();
                                Toast.makeText(OperationSelectActivity.this, "文件成功上传，打开微云客户端即可查看", Toast.LENGTH_SHORT).show();
                            }
                            
                            @Override
                            public void onUploadStart() {
                                Log.i("weiyun_test", "upload start");
                                if (OperationSelectActivity.this.isFinishing()) {
                                    return;
                                }
                                mProgress.setMessage("上传文件文件中，请稍候...");
                            }
                            
                            @Override
                            public void onUploadProgress(int progress) {
                                Log.i("weiyun_test", "upload progress" + progress + "%");
                                if (OperationSelectActivity.this.isFinishing()) {
                                    return;
                                }
                                mProgress.setMessage("上传文件文件中 " + progress + "%   请稍候...");
                            }
                            
                            @Override
                            public void onPrepareStart() {
                                Log.i("weiyun_test", "prepare start");
                                if (OperationSelectActivity.this.isFinishing()) {
                                    return;
                                }
                                mProgress.setMessage("准备上传文件，请稍候...");
                                mProgress.show();
                            }
                            
                            @Override
                            public void onError(String info) {
                                Log.e("weiyun_test", "upload error:   " + info + "");
                                if (OperationSelectActivity.this.isFinishing()) {
                                    return;
                                }
                                mProgress.dismiss();
                                Toast.makeText(OperationSelectActivity.this, "文件上传失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                upload.start();
            }
        }
    }
}
