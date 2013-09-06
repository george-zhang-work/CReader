
package com.tencent.sample;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.tauth.Constants;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Vector;

public class GetStoryParamsDialog extends Dialog implements
        android.view.View.OnClickListener {

    public interface OnGetStoryParamsCompleteListener {
        public void onGetParamsComplete(HashMap<String, Object> params);
    }

    private OnGetStoryParamsCompleteListener mListener = null;

    private Context mContext = null;

    private HashMap<String, Object> mHmParams = null;

    private Vector<String> mOpenids = new Vector<String>();

    private Button mBtCommit = null;
    private Button mBtUseDefault = null;
    private Button mBtAddOpenid = null;
    private TextView mTvTitle = null;
    private TextView mTvDescription = null;
    private TextView mTvSummary = null;
    private TextView mTvType = null;
    // private TextView mTvUrl = null;
    private TextView mTvPlayurl = null;
    private TextView mTvShareurl = null;
    private TextView mTvSource = null;
    private TextView mTvPics = null;
    private TextView mTvOpenid = null;
    private Spinner mSpAct = null;

    public GetStoryParamsDialog(Context context,
            OnGetStoryParamsCompleteListener listener) {
        super(context, R.style.Dialog_Fullscreen);
        // TODO Auto-generated constructor stub
        mContext = context;
        mListener = listener;
        mHmParams = new HashMap<String, Object>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_story_params_dialog);

        findViews();

        setupViews();
    }

    private void findViews() {
        mTvTitle = (TextView) findViewById(R.id.et_title);
        mTvDescription = (TextView) findViewById(R.id.et_description);
        mTvSummary = (TextView) findViewById(R.id.et_summary);
        mTvType = (TextView) findViewById(R.id.et_type);
        // mTvUrl = (TextView) findViewById(R.id.et_url);
        mTvPlayurl = (TextView) findViewById(R.id.et_playurl);
        mTvShareurl = (TextView) findViewById(R.id.et_shareurl);
        mTvSource = (TextView) findViewById(R.id.et_source);
        mTvPics = (TextView) findViewById(R.id.et_pics);
        mTvOpenid = (TextView) findViewById(R.id.et_openid);
        mSpAct = (Spinner) findViewById(R.id.sp_act);
        mBtCommit = (Button) findViewById(R.id.bt_commit);
        mBtUseDefault = (Button) findViewById(R.id.bt_use_default);
        mBtAddOpenid = (Button) findViewById(R.id.bt_add_openid);
    }

    private void setupViews() {
        mBtCommit.setOnClickListener(this);
        mBtUseDefault.setOnClickListener(this);
        mBtAddOpenid.setOnClickListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item);
        adapter.add(mContext.getResources().getString(R.string.act1));
        adapter.add(mContext.getResources().getString(R.string.act2));
        adapter.add(mContext.getResources().getString(R.string.act3));
        adapter.add(mContext.getResources().getString(R.string.act4));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpAct.setAdapter(adapter);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == mBtCommit) {
            getInputParams();
            if (mHmParams.size() > 0) {
                mListener.onGetParamsComplete(mHmParams);
                this.dismiss();
            } else {
                mListener.onGetParamsComplete(null);
                this.dismiss();
            }
        } else if (v == mBtUseDefault) {
            mListener.onGetParamsComplete(null);
            this.dismiss();
        } else if (v == mBtAddOpenid) {
            String openid = mTvOpenid.getText().toString().trim();
            if (!("".equals(openid))) {
                mOpenids.add(openid);
                mTvOpenid.setText("");
                Log.e("add receiver", "add " + openid);
                Toast.makeText(mContext, "add success", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(mContext, "Openid must not be empty",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getInputParams() {
        // if (!("".equals(mTvTitle.getText().toString().trim()))) {
        // mHmParams.put(Constants.PARAM_TITLE, mTvTitle.getText().toString()
        // .trim());
        // }
        // if (!("".equals(mTvDescription.getText().toString().trim()))) {
        // mHmParams.put(Constants.PARAM_COMMENT, mTvDescription.getText()
        // .toString().trim());
        // }
        // if (!("".equals(mTvSummary.getText().toString().trim()))) {
        // mHmParams.put(Constants.PARAM_SUMMARY, mTvSummary.getText()
        // .toString().trim());
        // }
        // if (!("".equals(mTvType.getText().toString().trim()))) {
        // mHmParams.put(Constants.PARAM_TYPE, mTvType.getText().toString()
        // .trim());
        // }
        // // if (!("".equals(mTvUrl.getText().toString().trim()))) {
        // // mHmParams.put(Constants.PARAM_URL, mTvUrl.getText().toString()
        // // .trim());
        // // }
        // if (!("".equals(mTvPlayurl.getText().toString().trim()))) {
        // mHmParams.put(Constants.PARAM_PLAY_URL, mTvPlayurl.getText()
        // .toString().trim());
        // }
        // if (!("".equals(mTvSource.getText().toString().trim()))) {
        // mHmParams.put(Constants.PARAM_SOURCE,
        // URLEncoder.encode(mTvSource.getText().toString().trim()));
        // }
        // if (!("".equals(mTvPics.getText().toString().trim()))) {
        // mHmParams.put(Constants.PARAM_IMAGE, mTvPics.getText().toString()
        // .trim());
        // }
        // Log.e("add receiver", "receiver num  " + mOpenids.size());
        // if (mOpenids.size() > 0) {
        // String[] openids = new String[mOpenids.size()];
        // for (int i = 0; i < mOpenids.size(); i++) {
        // openids[i] = mOpenids.get(i);
        // Log.e("add receiver", "add " + mOpenids.get(i));
        // }
        // mHmParams.put(Constants.PARAM_RECEIVER, openids);
        // }
        if (mTvTitle.getText().toString().length() != 0) {
            mHmParams.put(Constants.PARAM_TITLE, mTvTitle.getText().toString());
        }
        if (mTvDescription.getText().toString() != null) {
            mHmParams.put(Constants.PARAM_COMMENT, mTvDescription.getText().toString());
        }
        if (mTvSummary.getText().toString().length() != 0) {
            mHmParams.put(Constants.PARAM_SUMMARY, mTvSummary.getText().toString());
        }
        if (mTvType.getText().toString().length() != 0) {
            mHmParams.put(Constants.PARAM_TYPE, mTvType.getText().toString());
        }
        if (mTvPlayurl.getText().toString().length() != 0) {
            mHmParams.put(Constants.PARAM_PLAY_URL, mTvPlayurl.getText().toString());
        }
        if (mTvShareurl.getText().toString().length() != 0) {
            mHmParams.put(Constants.PARAM_SHARE_URL, mTvShareurl.getText().toString());
        }
        if (mTvSource.getText().toString().length() != 0) {
            mHmParams
                    .put(Constants.PARAM_SOURCE, URLEncoder.encode(mTvSource.getText().toString()));
        }
        if (mTvPics.getText().toString().length() != 0) {
            mHmParams.put(Constants.PARAM_IMAGE, mTvPics.getText().toString());
        }
        if (mOpenids.size() > 0) {
            String[] openids = new String[mOpenids.size()];
            for (int i = 0; i < mOpenids.size(); i++) {
                openids[i] = mOpenids.get(i);
                Log.e("add receiver", "add " + mOpenids.get(i));
            }
            mHmParams.put(Constants.PARAM_RECEIVER, openids);
        }
    }
}
