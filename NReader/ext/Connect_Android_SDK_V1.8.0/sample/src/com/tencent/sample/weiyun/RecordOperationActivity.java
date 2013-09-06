
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
import com.tencent.tauth.UploadFileToWeiyun;
import com.tencent.tauth.WeiyunConstants;
import org.json.JSONObject;

public class RecordOperationActivity extends Activity implements OnClickListener {

    private Button mCreateButton;
    private Button mManageButton;
    private Button mCheckButton;
    private Button mReturnButton;
        private Tencent mTencent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weiyun_operation_record_activity);
        initViews();
        Intent intent = getIntent();
        mTencent = Tencent.createInstance(AppConstants.APP_ID, this);
    }

    private void initViews() {
        mCreateButton = (Button) findViewById(R.id.weiyun_operation_record_create);
        mCreateButton.setOnClickListener(this);
        mManageButton = (Button) findViewById(R.id.weiyun_operation_record_manage);
        mManageButton.setOnClickListener(this);
        mCheckButton = (Button) findViewById(R.id.weiyun_operation_record_check);
        mCheckButton.setOnClickListener(this);
        mReturnButton = (Button) findViewById(R.id.weiyun_operation_record_return);
        mReturnButton.setOnClickListener(this);
    }

    private void showAddRecordDialog(){
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.weiyun_add_record,
                (ViewGroup) findViewById(R.id.dialog));

        new AlertDialog.Builder(this).setTitle("添加记录").setView(layout)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub
                                EditText keyText=(EditText)layout.findViewById(R.id.key);
                                EditText valueText=(EditText)layout.findViewById(R.id.value);
                                String key = keyText.getText().toString();
                                String value=valueText.getText().toString();
                                createRecord(key,value);
                            }
                        })
                .setNegativeButton("取消", null).show();
    }

    private void showCheckRecordDialog(){
        LayoutInflater inflater = getLayoutInflater();
        final EditText keyText=new EditText(this);
        new AlertDialog.Builder(this).setTitle("请输入key值").setView(keyText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String key=keyText.getText().toString();
                checkRecord(key);
            }
        }).setNegativeButton("取消", null).show();
    }

    @Override
    public void onClick(View arg0) {
        int viewid = arg0.getId();
        Intent intent = null;
        switch (viewid) {
            case R.id.weiyun_operation_record_create:
                showAddRecordDialog();
                break;
            case R.id.weiyun_operation_record_manage:
                intent = new Intent(this, RecordList.class);
                startActivity(intent);
                break;
            case R.id.weiyun_operation_record_check:
               showCheckRecordDialog();
                break;
            case R.id.weiyun_operation_record_return:
                finish();
                break;

            default:
                break;
        }
    }

    private void createRecord(final String key, final String value){
        new Thread() {
            public void run() {
                String url = "https://graph.qq.com/weiyun/create_record";
                Bundle params = new Bundle();
                params.putString("key", Util.toHexString(key));
                params.putByteArray("value", Util.toHexString(value).getBytes());
                JSONObject response = null;
                //try {
                response = mTencent.upload(url, params);
                boolean success=true;
                if(response!=null){
                    try{
                        int ret=response.getInt("ret");
                        if(ret==0){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RecordOperationActivity.this, "成功写入一条记录", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else{
                            success=false;
                        }
                    }
                    catch (Exception e){
                        success=false;
                    }
                }
                else{
                    success=false;
                }
                if(!success){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RecordOperationActivity.this, "写入记录失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }.start();
    }

    private void checkRecord(final String key){
        new Thread() {
            public void run() {
                String url = "https://graph.qq.com/weiyun/check_record";
                Bundle params = new Bundle();
                params.putString("key", Util.toHexString(key));
                JSONObject response = null;
                //try {
                response = mTencent.requestSync(url,params,"GET");
                boolean success=true;
                if(response!=null){
                    try{
                        int ret=response.getInt("ret");
                        if(ret==0){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RecordOperationActivity.this, "key存在", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RecordOperationActivity.this, "key不存在", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    catch (Exception e){
                        success=false;
                    }
                }
                else{
                    success=false;
                }
                if(!success){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RecordOperationActivity.this, "查询记录失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }.start();
    }
}
