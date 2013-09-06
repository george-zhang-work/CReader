
package com.tencent.sample;

import java.net.URLEncoder;
import java.util.HashMap;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.tencent.tauth.Constants;

public class GetInviteParamsDialog extends Dialog implements
        android.view.View.OnClickListener {

    public interface OnGetInviteParamsCompleteListener {
        public void onGetParamsComplete(HashMap<String, String> params);
    }

    private OnGetInviteParamsCompleteListener mListener = null;

    private Context mContext = null;

    private HashMap<String, String> mHmParams = null;

    private Button mBtCommit = null;
    private Button mBtUseDefault = null;
    private TextView mTvSource = null;
    private TextView mTvPicurl = null;
    private TextView mTvDesc = null;
    private Spinner mSpAct = null;

    public GetInviteParamsDialog(Context context,
            OnGetInviteParamsCompleteListener listener) {
        super(context, R.style.Dialog_Fullscreen);
        // TODO Auto-generated constructor stub
        mContext = context;
        mListener = listener;
        mHmParams = new HashMap<String, String>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_invite_params_dialog);

        findViews();

        setupViews();
    }

    private void findViews() {
        mTvSource = (TextView) findViewById(R.id.et_source);
        mTvPicurl = (TextView) findViewById(R.id.et_picurl);
        mTvDesc = (TextView) findViewById(R.id.et_desc);
        mSpAct = (Spinner) findViewById(R.id.sp_act);
        mBtCommit = (Button) findViewById(R.id.bt_commit);
        mBtUseDefault = (Button) findViewById(R.id.bt_use_default);

    }

    private void setupViews() {
        mBtCommit.setOnClickListener(this);
        mBtUseDefault.setOnClickListener(this);

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
        }
    }

    private void getInputParams() {
        if (mTvSource.getText().toString().length() != 0) {
            mHmParams
                    .put(Constants.PARAM_SOURCE, URLEncoder.encode(mTvSource.getText().toString()));
        }
        if (mTvPicurl.getText().toString().length() != 0) {
            mHmParams.put(Constants.PARAM_APP_ICON, mTvPicurl.getText().toString());
        }
        if (mTvDesc.getText().toString().length() != 0) {
            mHmParams.put(Constants.PARAM_APP_DESC, mTvDesc.getText().toString());
        }
        if (mSpAct.getSelectedItem() != null) {
            mHmParams.put(Constants.PARAM_ACT,
                    (String) mSpAct.getSelectedItem());
        }
    }
}
