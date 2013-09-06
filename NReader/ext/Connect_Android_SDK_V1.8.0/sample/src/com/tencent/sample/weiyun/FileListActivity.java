
package com.tencent.sample.weiyun;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import com.tencent.sample.AppConstants;
import com.tencent.sample.R;
import com.tencent.tauth.IDownloadFileFromWeiyunStatus;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.DownloadFileFromWeiyun;
import com.tencent.tauth.WeiyunConstants;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FileListActivity extends Activity {

    private ListView mListView;
    private FileListAdapter mAdapter;

    private int current_actiontype; // 当前操作类型：图片、音频、视频
    private String mRequestUrl; // 当前action对应的请求url

    private int file_total;
    private List<file_info_item> file_info_list;
    private Tencent mTencent;

    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.weiyun_filelist_activity);

        mProgress = new ProgressDialog(this);

        mListView = (ListView) findViewById(R.id.filelist_listview);
        mAdapter = new FileListAdapter(this, itemClick);
        mListView.setAdapter(mAdapter);

        Intent intent = getIntent();
        current_actiontype = intent.getExtras().getInt("actiontype");
        switch (current_actiontype) {
            case WeiyunConstants.ACTION_PICTURE:
                mRequestUrl = "https://graph.qq.com/weiyun/get_photo_list";
                break;
            case WeiyunConstants.ACTION_MUSIC:
                mRequestUrl = "https://graph.qq.com/weiyun/get_music_list";
                break;
            case WeiyunConstants.ACTION_VIDEO:
                mRequestUrl = "https://graph.qq.com/weiyun/get_video_list";
                break;
            default:
                break;
        }

        file_total = 0;
        file_info_list = new ArrayList<file_info_item>();
        mTencent = Tencent.createInstance(AppConstants.APP_ID, this);

        new Thread() {
            public void run() {
                Bundle params = new Bundle();
                params.putString("offset", "0");
                params.putString("number", "100");

                JSONObject response = null;
                response = mTencent.requestSync(mRequestUrl, params, "GET");

                if(response!=null){
                    Message message = mHandler.obtainMessage();
                    message.what = 0;
                    message.obj = response;
                    mHandler.sendMessage(message);
                }
                else{
                    Message message = mHandler.obtainMessage();
                    message.what = -1;
                    message.obj = "error";
                    mHandler.sendMessage(message);
                }
            }
        }.start();
        mProgress.setMessage("正在查询文件列表，请稍候...");
        mProgress.show();
    }

    @Override
    public void onStop() {
        super.onStop();
        //file_info_list.clear();
        mProgress.dismiss();
    }

    private IFileListAdapterItemClick itemClick = new IFileListAdapterItemClick() {

        @Override
        public void onThumbPicClick(int position) {
            DownloadFileFromWeiyun down = new DownloadFileFromWeiyun(
                    mTencent,
                    file_info_list.get(position).mFile_id, "128*128", "weiyun_test", file_info_list.get(position).mName,
                    new IDownloadFileFromWeiyunStatus() {

                        @Override
                        public void onPrepareStart() {
                            if (isFinishing()) {
                                return;
                            }
                            mProgress.setMessage("正在获取缩略图，请稍等.....");
                            mProgress.show();
                        }

                        @Override
                        public void onError(String info) {
                            if (isFinishing()) {
                                return;
                            }
                            mProgress.dismiss();
                            Toast.makeText(FileListActivity.this, "缩略图下载失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onDownloadSuccess(String filepath) {
                            if (isFinishing()) {
                                return;
                            }
                            mProgress.dismiss();
                            Toast.makeText(FileListActivity.this, "获取缩略图成功，路径是:" + filepath + "", Toast.LENGTH_LONG).show();
                            ImageViewDialog dlg = new ImageViewDialog(FileListActivity.this, filepath);
                            dlg.show();
                        }

                        @Override
                        public void onDownloadStart() {
                            mProgress.setMessage("正在获取缩略图，请稍等.....");
                        }

                        @Override
                        public void onDownloadProgress(int progress) {
                            mProgress.setMessage("正在获取缩略图: "+progress+"%   "+"请稍等.....");
                        }
                    });
            down.start();
        }

        @Override
        public void onDownloadClick(int position, final int actiontype) {
            DownloadFileFromWeiyun down = new DownloadFileFromWeiyun(
                    mTencent,
                    file_info_list.get(position).mFile_id, actiontype,
                    file_info_list.get(position).mSize, "weiyun_test", file_info_list.get(position).mName,
                    new IDownloadFileFromWeiyunStatus() {

                        @Override
                        public void onPrepareStart() {
                            if (isFinishing()) {
                                return;
                            }
                            mProgress.setMessage("文件下载准备中，请稍等.....");
                            mProgress.show();
                        }

                        @Override
                        public void onError(String info) {
                            if (isFinishing()) {
                                return;
                            }
                            mProgress.dismiss();
                            Toast.makeText(FileListActivity.this, "文件下载失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onDownloadSuccess(String filepath) {
                            if (isFinishing()) {
                                return;
                            }
                            mProgress.dismiss();
                            Toast.makeText(FileListActivity.this, "文件下载成功，路径是:" + filepath + "", Toast.LENGTH_SHORT).show();
                            if (actiontype == WeiyunConstants.ACTION_PICTURE) {
                                ImageViewDialog dlg = new ImageViewDialog(FileListActivity.this, filepath);
                                dlg.show();
                            }
                        }

                        @Override
                        public void onDownloadStart() {
                            mProgress.setMessage("文件正在下载，请稍等.....");
                        }

                        @Override
                        public void onDownloadProgress(int progress) {
                            mProgress.setMessage("文件正在下载: "+progress+"%   "+"请稍等.....");
                        }
                    });
            down.start();
        }

        @Override
        public void onDeleteClick(final int position, int actiontype) {
            // TODO Auto-generated method stub

            new Thread() {
                public void run() {
                    final file_info_item item=file_info_list.get(position);
                    String url = "https://graph.qq.com/weiyun/delete_photo";
                    switch (current_actiontype) {
                        case WeiyunConstants.ACTION_PICTURE:
                            url = "https://graph.qq.com/weiyun/delete_photo";
                            break;
                        case WeiyunConstants.ACTION_MUSIC:
                            url = "https://graph.qq.com/weiyun/delete_music";
                            break;
                        case WeiyunConstants.ACTION_VIDEO:
                            url = "https://graph.qq.com/weiyun/delete_video";
                            break;
                        default:
                            break;
                    }
                    Bundle params = new Bundle();
                    params.putString("file_id", item.mFile_id);
                    JSONObject response = null;
                    //try {
                    response = mTencent.requestSync(url, params, "GET");
                    boolean isSuccess=true;
                    if(response!=null){
                        try {
                            int ret = response.getInt("ret");
                            if (ret == 0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        file_info_list.remove(position);
                                        mAdapter.notifyDataSetChanged();
                                        Toast.makeText(FileListActivity.this, "删除文件"+item.mName+"成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else{
                                isSuccess=false;
                            }
                        }
                        catch (Exception e){
                            isSuccess=false;
                        }
                    }
                    else{
                        isSuccess=false;
                    }
                    if(!isSuccess){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FileListActivity.this, "删除文件"+item.mName+"失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }.start();
        }

    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (FileListActivity.this.isFinishing()) {
                return;
            }
            mProgress.dismiss();
            switch (msg.what) {
                case 0:
                    JSONObject response = (JSONObject) msg.obj;
                    try {
                        int ret = response.getInt("ret");
                        if (ret < 0) {
                            String errorinfo = response.getString("msg");
                            Toast.makeText(FileListActivity.this, "查询列表失败， retcode ＝" + ret + "",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        }
                        JSONObject data = response.getJSONObject("data");
                        file_total = data.getInt("file_total");
                        if(data.isNull("content")){
                            Toast.makeText(FileListActivity.this, "列表为空", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            JSONArray content = data.getJSONArray("content");
                            for (int i = 0; i < content.length(); i++) {
                                JSONObject item = content.getJSONObject(i);
                                String file_id = item.getString("file_id");
                                String name = item.getString("file_name");
                                String ctime = item.getString("file_ctime");
                                int size = item.getInt("file_size");
                                file_info_item fileitem = new file_info_item(file_id, name, ctime, size);
                                file_info_list.add(fileitem);
                            }
                            mAdapter.setData(file_info_list, current_actiontype);
                            mAdapter.notifyDataSetChanged();
                            Toast.makeText(FileListActivity.this, "查询列表成功", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(FileListActivity.this,
                                "解析服务器返回数据失败 ：" + e.getMessage() + "", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case -1:
                    Toast.makeText(FileListActivity.this,
                            "查询文件列表失败 ：" + msg.obj.toString() + "", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @SuppressWarnings("unused")
    public class file_info_item {
        public String mFile_id;
        public String mName;
        public String mCtime;
        public int mSize;

        public file_info_item(String file_id, String name, String ctime, int size) {
            this.mFile_id = file_id + "";
            this.mName = name + "";
            this.mCtime = ctime + "";
            this.mSize = size;
        }
    }
}
