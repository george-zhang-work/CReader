package com.tencent.sample.weiyun;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.tencent.sample.AppConstants;
import com.tencent.sample.R;
import com.tencent.sample.Util;
import com.tencent.tauth.Tencent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: melody
 * Date: 13-4-27
 * Time: 下午4:02
 * To change this template use File | Settings | File Templates.
 */
public class RecordList extends Activity {
    private ListView mListView;
    private List<RecordListAdapter.RecordItem> list;
    private RecordListAdapter mAdapter;
    private Tencent mTencent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weiyun_filelist_activity);
        mListView = (ListView) findViewById(R.id.filelist_listview);
        mAdapter=new RecordListAdapter(this,itemClick);
        mTencent=Tencent.createInstance(AppConstants.APP_ID,this);
        mListView.setAdapter(mAdapter);
        getList();
    }

    private void getList(){
        new Thread() {
            public void run() {
                String url = "https://graph.qq.com/weiyun/query_all_record";
                Bundle params = new Bundle();
                JSONObject response = null;
                //try {
                response = mTencent.requestSync(url, params, "GET");
                boolean  success=true;
                if(response!=null){
                    try {
                        int ret = response.getInt("ret");
                        if (ret == 0) {
                            list=new ArrayList<RecordListAdapter.RecordItem>();
                            JSONObject data=response.getJSONObject("data");
                            if(data.isNull("keys")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RecordList.this, "记录为空", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else{
                                JSONArray keys=data.getJSONArray("keys");
                                for(int i=0;i<keys.length();++i){
                                    JSONObject item=keys.getJSONObject(i);
                                    RecordListAdapter.RecordItem recordItem=new RecordListAdapter.RecordItem();
                                    recordItem.key=item.getString("key");
                                    recordItem.value="";
                                    list.add(recordItem);
                                }
                                mAdapter.record_list=list;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
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
                            Toast.makeText(RecordList.this, "获取记录列表失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }.start();
    }

    private void deleteRecord(final int position){
        new Thread() {
            public void run() {
                String url = "https://graph.qq.com/weiyun/delete_record";
                Bundle params = new Bundle();
                JSONObject response = null;
                RecordListAdapter.RecordItem item=list.get(position);
                params.putString("key", item.key);
                response = mTencent.requestSync(url, params, "GET");
                boolean  success=true;
                if(response!=null){
                    try {
                        int ret = response.getInt("ret");
                        if (ret == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    list.remove(position);
                                    mAdapter.notifyDataSetChanged();
                                    Toast.makeText(RecordList.this, "删除记录成功", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(RecordList.this, "删除记录失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }.start();
    }

    private void modifyRecord(final int position, final String value){
        new Thread() {
            public void run() {
                String url = "https://graph.qq.com/weiyun/modify_record";
                Bundle params = new Bundle();
                JSONObject response = null;
                final RecordListAdapter.RecordItem item=list.get(position);
                params.putString("key", item.key);
                params.putByteArray("value", Util.toHexString(value).getBytes());
                response = mTencent.upload(url,params);
                boolean  success=true;
                if(response!=null){
                    try {
                        int ret = response.getInt("ret");
                        if (ret == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RecordList.this, "成功修改记录", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(RecordList.this, "修改记录失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }.start();
    }

    private void getRecord(final int position){
        new Thread() {
            public void run() {
                String url = "https://graph.qq.com/weiyun/get_record";
                Bundle params = new Bundle();
                JSONObject response = null;
                final RecordListAdapter.RecordItem item=list.get(position);
                params.putString("key", item.key);
                response = mTencent.requestSync(url, params, "GET");
                boolean  success=true;
                if(response!=null){
                    try {
                        int ret = response.getInt("ret");
                        if (ret == 0) {
                            JSONObject data=response.getJSONObject("data");
                            final String value=data.getString("value");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RecordList.this, "记录"+Util.hexToString(item.key)+"的值是："+Util.hexToString(value), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(RecordList.this, "删除记录失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }.start();
    }

    private IRecordListAdapterItemClick itemClick=new IRecordListAdapterItemClick() {
        @Override
        public void onDeleteClick(int position) {
            deleteRecord(position);
        }

        @Override
        public void onModifyClick(final int position) {
            final EditText valueText=new EditText(RecordList.this);
            new AlertDialog.Builder(RecordList.this).setTitle("请输入值").setView(valueText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String value=valueText.getText().toString();
                    modifyRecord(position,value);
                }
            }).setNegativeButton("取消", null).show();
        }

        @Override
        public void onGetClick(int position) {
            getRecord(position);
        }
    };
}
