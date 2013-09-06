
package com.tencent.sample;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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

public class GetPkBragParamsDialog extends Dialog implements
        android.view.View.OnClickListener {

    public interface OnGetPkBragParamsCompleteListener {
        public void onGetParamsComplete(HashMap<String, Object> params);
    }

    private OnGetPkBragParamsCompleteListener mListener = null;

    private Context mContext = null;

    private HashMap<String, Object> mHmParams = null;

    private Vector<String> mOpenids = new Vector<String>();

    private Button mBtCommit = null;
    private Button mBtUseDefault = null;
    private TextView mTvMsg = null;
    private TextView mTvSource = null;
    private TextView mTvImg = null;
    private TextView mTvReceiver = null;
    private Spinner mSpOptions = null;

    public GetPkBragParamsDialog(Context context,
            OnGetPkBragParamsCompleteListener listener) {
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
        setContentView(R.layout.get_pk_brag_params_dialog);

        findViews();

        setupViews();
    }

    private void findViews() {
        mTvImg = (TextView) findViewById(R.id.et_img);
        mTvMsg = (TextView) findViewById(R.id.et_msg);
        mTvReceiver = (TextView) findViewById(R.id.et_receiver);
        mTvSource = (TextView) findViewById(R.id.et_source);
        mSpOptions = (Spinner) findViewById(R.id.sp_options);
        mBtCommit = (Button) findViewById(R.id.bt_commit);
        //mBtUseDefault = (Button) findViewById(R.id.bt_use_default);
    }

    private void setupViews() {
        mBtCommit.setOnClickListener(this);
//        mBtUseDefault.setOnClickListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item);
        adapter.add(mContext.getResources().getString(R.string.options1));
        adapter.add(mContext.getResources().getString(R.string.options2));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpOptions.setAdapter(adapter);

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
        } else {
                Toast.makeText(mContext, "Openid must not be empty",
                        Toast.LENGTH_SHORT).show();
            }
    }

    private void getInputParams() {
        if (mTvImg.getText().toString().length() != 0) {
            mHmParams.put(Constants.PARAM_IMG_URL, mTvImg.getText().toString());
        }
        if (mTvMsg.getText().toString() != null) {
            mHmParams.put(Constants.PARAM_SEND_MSG, mTvMsg.getText().toString());
        }
        if (mTvSource.getText().toString().length() != 0) {
            mHmParams
                    .put(Constants.PARAM_SOURCE, URLEncoder.encode(mTvSource.getText().toString()));
        }
        if (mTvReceiver.getText().toString().length() != 0) {
            mHmParams.put(Constants.PARAM_RECEIVER, mTvReceiver.getText().toString());
        }
        if (mSpOptions.getSelectedItem() != null) {
            if(((String) mSpOptions.getSelectedItem()).equals("挑战")) {
                mHmParams.put(Constants.PARAM_TYPE, "pk");
            } else {
                mHmParams.put(Constants.PARAM_TYPE, "brag");
            }
        }
    }
}
