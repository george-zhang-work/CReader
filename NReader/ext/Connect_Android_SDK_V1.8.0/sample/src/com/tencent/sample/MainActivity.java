
package com.tencent.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.sample.GetInviteParamsDialog.OnGetInviteParamsCompleteListener;
import com.tencent.sample.weiyun.WeiyunMainActivity;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends Activity implements OnClickListener {

    private static final String APP_ID = "222222";
    // private static final String APP_ID = "100363349";

    private static final String SCOPE = "get_user_info,get_simple_userinfo,get_user_profile,get_app_friends,upload_photo,"
            + "add_share,add_topic,list_album,upload_pic,add_album,set_user_face,get_vip_info,get_vip_rich_info,get_intimate_friends_weibo,match_nick_tips_weibo";

    // private static final String SCOPE = "all";

    private static final int REQUEST_UPLOAD_PIC = 1000;

    private static final int REQUEST_SET_AVATAR = 2;

    private static final String SERVER_PREFS = "ServerPrefs";
    private static final String SERVER_TYPE = "ServerType";

    private ImageView mLoginButton;

    public static Tencent mTencent;

    private TextView mBaseMessageText;

    private TextView mMessageText;

    private Handler mHandler;

    private Dialog mProgressDialog;

    // set to 1 for test params
    private int mNeedInputParams = 1;

    private EditText mEtAppid = null;
    
    
    private String TAG = "SDKSample";
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//
        // 固定竖屏
        setContentView(R.layout.activity_main);
        final Context ctxContext = this.getApplicationContext();

        mEtAppid = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("请输入APP_ID")
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(mEtAppid)
                .setPositiveButton("Commit",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                // TODO Auto-generated method stub
                                String appid = mEtAppid.getText().toString()
                                        .trim();
                                if (!("".equals(appid))) {
                                    mTencent = Tencent.createInstance(appid, ctxContext);
                                } else {
                                    mTencent = Tencent.createInstance(AppConstants.APP_ID, ctxContext);
                                }
                                mHandler = new Handler();
                                mProgressDialog = new ProgressDialog(
                                        MainActivity.this);
                                initViews();
                            }
                        })
                .setNegativeButton("Use Default",
                        new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                // TODO Auto-generated method stub
                                mTencent = Tencent.createInstance(AppConstants.APP_ID, ctxContext);
                                mHandler = new Handler();
                                mProgressDialog = new ProgressDialog(
                                        MainActivity.this);
                                initViews();
                            }
                        }).show();
    }

    private void initViews() {
        mBaseMessageText = (TextView) findViewById(R.id.base_message_tv);
        mMessageText = (TextView) findViewById(R.id.message_tv);

        mLoginButton = (ImageView) findViewById(R.id.login_btn);
        updateLoginButton();

        mLoginButton.setOnClickListener(this);
        findViewById(R.id.invite_btn).setOnClickListener(this);
        findViewById(R.id.send_story_btn).setOnClickListener(this);
        findViewById(R.id.ask_gift_btn).setOnClickListener(this);
        findViewById(R.id.open_id_btn).setOnClickListener(this);
        findViewById(R.id.user_info_btn).setOnClickListener(this);
        findViewById(R.id.vip_info_btn).setOnClickListener(this);
        findViewById(R.id.vip_rich_info_btn).setOnClickListener(this);
        findViewById(R.id.list_album_btn).setOnClickListener(this);
        findViewById(R.id.add_share_btn).setOnClickListener(this);
        findViewById(R.id.add_topic_btn).setOnClickListener(this);
        findViewById(R.id.upload_pic_btn).setOnClickListener(this);
        findViewById(R.id.add_album_btn).setOnClickListener(this);
        findViewById(R.id.select_server_btn).setOnClickListener(this);
        findViewById(R.id.set_avatar_btn).setOnClickListener(this);
        findViewById(R.id.nick_tips_btn).setOnClickListener(this);
        findViewById(R.id.intimate_friends_btn).setOnClickListener(this);
        findViewById(R.id.add_pic_url_t).setOnClickListener(this);
        findViewById(R.id.test_qq_btn).setOnClickListener(this);
        findViewById(R.id.pk_brag_btn).setOnClickListener(this);
        findViewById(R.id.check_login_btn).setOnClickListener(this);
//        findViewById(R.id.get_app_friends_btn).setOnClickListener(this);
        findViewById(R.id.weiyun).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
    	
    	Log.v("sample","onActivityResult:" + requestCode + ", resultCode:"+resultCode);
        // must call mTencent.onActivityResult.
    	if (mTencent == null){
    		return;
    	}
        if (!mTencent.onActivityResult(requestCode, resultCode, data)) {
            if (data != null) {
                if (requestCode == REQUEST_UPLOAD_PIC) {
                    doUploadPic(data.getData());
                } else if (requestCode == REQUEST_SET_AVATAR) {
                    doSetAvatar(data.getData());
                }
            }
        }
    }

    /**
     * 根据选择的数据类型，进入下一步，选择操作类型
     * @param actionType
     */
    private void startWeiyun() {
        Intent intent = new Intent(this, WeiyunMainActivity.class);
        startActivity(intent);
    }

    private void updateLoginButton() {
        mLoginButton
                .setImageResource(mTencent.isSessionValid() ? R.drawable.com_tencent_open_logout
                        : R.drawable.com_tencent_open_login);
    }

    @Override
    public void onClick(View v) {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        switch (v.getId()) {
            case R.id.login_btn:
                onClickLogin();
                v.startAnimation(shake);
                break;

            case R.id.invite_btn:
                onClickInvite();
                v.startAnimation(shake);
                break;

            case R.id.send_story_btn:
                onClickStory();
                v.startAnimation(shake);
                break;

            case R.id.set_avatar_btn:// 设置头像
                onClickSetAvatar();
                break;

            case R.id.ask_gift_btn:
                onClickAskGift();
                v.startAnimation(shake);
                break;

            case R.id.open_id_btn:
                onClickOpenId();
                v.startAnimation(shake);
                break;

            case R.id.user_info_btn:
                onClickUserInfo();
                v.startAnimation(shake);
                break;

            case R.id.vip_info_btn:
                onClickVipInfo();
                v.startAnimation(shake);
                break;

            case R.id.vip_rich_info_btn:
                onClickVipRichInfo();
                v.startAnimation(shake);
                break;

            case R.id.add_pic_url_t:
                onClickAddPicUrlTweet();
                v.startAnimation(shake);
                break;

            case R.id.test_qq_btn:
                onClickShareToQQ();
                v.startAnimation(shake);
                break;

            case R.id.list_album_btn:
                onClickListAlbum();
                v.startAnimation(shake);
                break;

            case R.id.add_share_btn:
                onClickAddShare();
                v.startAnimation(shake);
                break;

            case R.id.add_topic_btn:
                onClickAddTopic();
                v.startAnimation(shake);
                break;

            case R.id.upload_pic_btn:
                onClickUploadPic();
                v.startAnimation(shake);
                break;

            case R.id.add_album_btn:
                onClickAddAlbum();
                v.startAnimation(shake);
                break;

            case R.id.select_server_btn:
                onClickSelectServer();
                v.startAnimation(shake);
                break;

            case R.id.nick_tips_btn:
                onClickNickTips();
                v.startAnimation(shake);
                break;

            case R.id.intimate_friends_btn:
                onClickIntimateFriends();
                v.startAnimation(shake);
                break;

            case R.id.pk_brag_btn:
                onClickPkBrag();
                v.startAnimation(shake);
                break;

            case R.id.check_login_btn:
                onClickCheckLogin();
                v.startAnimation(shake);
                break;

            /*case R.id.get_app_friends_btn:
                onClickGetAppFriends();
                v.startAnimation(shake);
                break;*/

            case R.id.weiyun:
                onClickWeiyun();
                break;

            default:
                break;
        }
    }

    private void onClickWeiyun(){
        if (ready()) {
            startWeiyun();
        }
    }

    private void onClickAskGift() {
        if (ready()) {
            if (mNeedInputParams == 1) {
                new GetAskGiftParamsDialog(
                        this,
                        new GetAskGiftParamsDialog.OnGetAskGiftParamsCompleteListener() {

                            @Override
                            public void onGetParamsComplete(Bundle params) {
                                // TODO Auto-generated method stub
                                Bundle tmpParams = new Bundle(params);
                                tmpParams.remove(Constants.PARAM_TYPE);
                                if ("request".equals(params.getString(Constants.PARAM_TYPE))) {
                                    mTencent.ask(MainActivity.this, tmpParams, new BaseUiListener());
                                } else {
                                    mTencent.gift(MainActivity.this, tmpParams,
                                            new BaseUiListener());
                                }
                                Log.d("toddtest", tmpParams.toString());
                            }
                        }).show();
            } else {
                Bundle b = new Bundle();
                b.putString(Constants.PARAM_RECEIVER, "3B599FF138EE42DD7FE2234D3B89C44B");
                b.putString("type", "ask");
                b.putString("img",
                        "http://i.gtimg.cn/qzonestyle/act/qzone_app_img/app888_888_75.png");
                mTencent.ask(this, b, new BaseUiListener());
            }
        }
    }

    private void onClickCheckLogin() {
        IUiListener listener = new BaseUiListener() {
            @Override
            protected void doComplete(JSONObject values) {
                showResult("IRequestListener.onComplete:", values.toString());
            }
        };
        mTencent.login(this, SCOPE, listener);
    }

    private void onClickLogin() {
        if (!mTencent.isSessionValid()) {
            IUiListener listener = new BaseUiListener() {
                @Override
                protected void doComplete(JSONObject values) {
                    updateLoginButton();
                }
            };
            mTencent.login(this, "all", listener);
        } else {
            mTencent.logout(this);
            updateLoginButton();
        }
    }

    private void onClickInvite() {
        if (ready()) {
            if (mNeedInputParams == 1) {
                new GetInviteParamsDialog(this,
                        new OnGetInviteParamsCompleteListener() {

                            @Override
                            public void onGetParamsComplete(
                                    HashMap<String, String> hmParams) {
                                // TODO Auto-generated method stub
                                Bundle params = new Bundle();
                                // TODO keywords.
                                if (hmParams == null) {
                                    params.putString(
                                            Constants.PARAM_APP_ICON,
                                            "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
                                    params.putString(Constants.PARAM_APP_DESC,
                                            "AndroidSdk_1_3: invite description!");
                                    params.putString(Constants.PARAM_ACT, "进入应用");
                                } else {
                                    if (hmParams
                                            .containsKey(Constants.PARAM_APP_ICON)) {
                                        params.putString(
                                                Constants.PARAM_APP_ICON,
                                                hmParams.get(Constants.PARAM_APP_ICON));
                                    } else {
                                        params.putString(
                                                Constants.PARAM_APP_ICON,
                                                "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
                                    }
                                    if (hmParams
                                            .containsKey(Constants.PARAM_APP_DESC)) {
                                        params.putString(
                                                Constants.PARAM_APP_DESC,
                                                hmParams.get(Constants.PARAM_APP_DESC));
                                    } else {
                                        params.putString(
                                                Constants.PARAM_APP_DESC,
                                                "AndroidSdk_1_3: invite description!");
                                    }
                                    if (hmParams
                                            .containsKey(Constants.PARAM_SOURCE)) {
                                        params.putString(
                                                Constants.PARAM_SOURCE,
                                                hmParams.get(Constants.PARAM_SOURCE));
                                    }
                                    if (hmParams
                                            .containsKey(Constants.PARAM_ACT)) {
                                        params.putString(
                                                Constants.PARAM_ACT,
                                                hmParams.get(Constants.PARAM_ACT));
                                    }
                                }
                                mTencent.invite(MainActivity.this, params,
                                        new BaseUiListener());
                            }
                        }).show();
            } else {
                Bundle params = new Bundle();
                // TODO keywords.
                params.putString(Constants.PARAM_APP_ICON,
                        "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
                params.putString(Constants.PARAM_APP_DESC,
                        "AndroidSdk_1_3: invite description!");
                params.putString(Constants.PARAM_ACT, "进入应用");
                mTencent.invite(MainActivity.this, params, new BaseUiListener());
            }

        }
    }

    private boolean ready() {
        boolean ready = mTencent.isSessionValid()
                && mTencent.getOpenId() != null;
        if (!ready)
            Toast.makeText(this, "login and get openId first, please!",
                    Toast.LENGTH_SHORT).show();
        return ready;
    }

    private void onClickStory() {
        if (ready()) {
            if (mNeedInputParams == 1) {
                new GetStoryParamsDialog(
                        this,
                        new GetStoryParamsDialog.OnGetStoryParamsCompleteListener() {

                            @Override
                            public void onGetParamsComplete(
                                    HashMap<String, Object> hmParams) {
                                // TODO Auto-generated method stub
                                Bundle params = new Bundle();
                                if (hmParams == null) {
                                    params.putString(Constants.PARAM_TITLE,
                                            "AndroidSdk_1_3:UiStory title");
                                    params.putString(Constants.PARAM_COMMENT,
                                            "AndroidSdk_1_3: UiStory comment");
                                    params.putString(
                                            Constants.PARAM_IMAGE,
                                            "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
                                    params.putString(Constants.PARAM_SUMMARY,
                                            "AndroidSdk_1_3: UiStory summary");
                                    params.putString(Constants.PARAM_TYPE, "21");
                                    params.putString(Constants.PARAM_SHARE_URL,
                                            "http://www.qq.com/");
                                    params.putString(
                                            Constants.PARAM_PLAY_URL,
                                            "http://player.youku.com/player.php/Type/Folder/"
                                                    + "Fid/15442464/Ob/1/Pt/0/sid/XMzA0NDM2NTUy/v.swf");
                                } else {
                                    // title
                                    if (hmParams
                                            .containsKey(Constants.PARAM_TITLE)) {
                                        params.putString(
                                                Constants.PARAM_TITLE,
                                                (String) hmParams
                                                        .get(Constants.PARAM_TITLE));
                                    } else {
                                        params.putString(Constants.PARAM_TITLE,
                                                "AndroidSdk_1_3:UiStory title");
                                    }
                                    // comment
                                    if (hmParams
                                            .containsKey(Constants.PARAM_COMMENT)) {
                                        params.putString(
                                                Constants.PARAM_COMMENT,
                                                (String) hmParams
                                                        .get(Constants.PARAM_COMMENT));
                                    } else {
                                        params.putString(
                                                Constants.PARAM_COMMENT,
                                                "AndroidSdk_1_3: UiStory comment");
                                    }
                                    // summary
                                    if (hmParams
                                            .containsKey(Constants.PARAM_SUMMARY)) {
                                        params.putString(
                                                Constants.PARAM_SUMMARY,
                                                (String) hmParams
                                                        .get(Constants.PARAM_SUMMARY));
                                    } else {
                                        params.putString(
                                                Constants.PARAM_SUMMARY,
                                                "AndroidSdk_1_3: UiStory summary");
                                    }
                                    // type
                                    if (hmParams
                                            .containsKey(Constants.PARAM_TYPE)) {
                                        params.putString(
                                                Constants.PARAM_TYPE,
                                                (String) hmParams
                                                        .get(Constants.PARAM_TYPE));
                                    } else {
                                        params.putString(Constants.PARAM_TYPE,
                                                "21");
                                    }
                                    // playurl
                                    if (hmParams
                                            .containsKey(Constants.PARAM_PLAY_URL)) {
                                        params.putString(
                                                Constants.PARAM_PLAY_URL,
                                                (String) hmParams
                                                        .get(Constants.PARAM_PLAY_URL));
                                    } else {
                                        params.putString(
                                                Constants.PARAM_PLAY_URL,
                                                "http://www.qq.com");
                                    }
                                    // shareurl
                                    if (hmParams
                                            .containsKey(Constants.PARAM_SHARE_URL)) {
                                        params.putString(
                                                Constants.PARAM_SHARE_URL,
                                                (String) hmParams
                                                        .get(Constants.PARAM_SHARE_URL));
                                    } else {
                                        params.putString(Constants.PARAM_SHARE_URL,
                                                "http://www.qq.com/");
                                    }
                                    // source
                                    if (hmParams
                                            .containsKey(Constants.PARAM_SOURCE)) {
                                        params.putString(
                                                Constants.PARAM_SOURCE,
                                                (String) hmParams
                                                        .get(Constants.PARAM_SOURCE));
                                    }
                                    // pics
                                    if (hmParams
                                            .containsKey(Constants.PARAM_IMAGE)) {
                                        params.putString(
                                                Constants.PARAM_IMAGE,
                                                (String) hmParams
                                                        .get(Constants.PARAM_IMAGE));
                                    } else {
                                        params.putString(
                                                Constants.PARAM_IMAGE,
                                                "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
                                    }
                                    // receivers
                                    if (hmParams
                                            .containsKey(Constants.PARAM_RECEIVER)) {
                                        params.putStringArray(
                                                Constants.PARAM_RECEIVER,
                                                (String[]) hmParams
                                                        .get(Constants.PARAM_RECEIVER));
                                    }
                                    // act
                                    if (hmParams
                                            .containsKey(Constants.PARAM_ACT)) {
                                        params.putString(
                                                Constants.PARAM_ACT,
                                                (String) hmParams
                                                        .get(Constants.PARAM_ACT));
                                    }
                                }
                                mTencent.story(MainActivity.this, params,
                                        new BaseUiListener());
                            }
                        }).show();
            } else {
                Bundle params = new Bundle();

                params.putString(Constants.PARAM_TITLE,
                        "AndroidSdk_1_3:UiStory title");
                params.putString(Constants.PARAM_COMMENT,
                        "AndroidSdk_1_3: UiStory comment");
                params.putString(Constants.PARAM_IMAGE,
                        "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
                params.putString(Constants.PARAM_SUMMARY,
                        "AndroidSdk_1_3: UiStory summary");
                params.putString(
                        Constants.PARAM_PLAY_URL,
                        "http://player.youku.com/player.php/Type/Folder/"
                                + "Fid/15442464/Ob/1/Pt/0/sid/XMzA0NDM2NTUy/v.swf");
                params.putString(Constants.PARAM_ACT, "进入应用");
                String[] receiver = {
                        "121345674896845AGHIHOGVJOASJ", "GISFHOPGJOEJUGO4513587422"
                };
                params.putStringArray(Constants.PARAM_RECEIVER, receiver);
                mTencent.story(MainActivity.this, params, new BaseUiListener());
            }
        }
    }

    /*private void onClickGetAppFriends() {
        if (ready()) {
            mTencent.getAppFriends(new BaseApiListener("get_app_friends", false));
            mProgressDialog.show();
        }
    }*/

    private void onClickOpenId() {
        if (mTencent.isSessionValid()) {

            mTencent.requestAsync(Constants.GRAPH_OPEN_ID, null,
                    Constants.HTTP_GET, new BaseApiListener("m_me", true), null);

            mProgressDialog.show();
        }
    }

    private void onClickUserInfo() {
        if (ready()) {

            mTencent.requestAsync(Constants.GRAPH_SIMPLE_USER_INFO, null,
                    Constants.HTTP_GET, new BaseApiListener("get_simple_userinfo", false), null);

            mProgressDialog.show();
        }
    }

    private void onClickVipInfo() {
        if (ready()) {

            mTencent.requestAsync(Constants.GRAPH_VIP_INFO, null,
                    Constants.HTTP_GET, new BaseApiListener("get_vip_info", false), null);

            mProgressDialog.show();
        }
    }

    private void onClickIntimateFriends() {
        if (ready()) {
            final EditText inputServer = new EditText(this);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("请输入要获取的个数：").setIcon(android.R.drawable.ic_dialog_info)
                    .setView(inputServer)
                    .setNegativeButton("取消", null);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    Bundle parmas = new Bundle();
                    parmas.putString("reqnum", inputServer.getText().toString());// 请求个数(1-10)
                    mTencent.requestAsync(Constants.GRAPH_INTIMATE_FRIENDS, parmas,
                            Constants.HTTP_GET, new BaseApiListener("get_intimate_friends_weibo",
                                    false), null);

                    mProgressDialog.show();
                }
            }).show();
        }
    }

    private void onClickNickTips() {
        if (ready()) {
            final EditText inputServer = new EditText(this);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("请输入要提示的昵称：").setIcon(android.R.drawable.ic_dialog_info)
                    .setView(inputServer)
                    .setNegativeButton("取消", null);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    final EditText inputServer1 = new EditText(MainActivity.this);
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setTitle("请输入要获取的个数：").setIcon(android.R.drawable.ic_dialog_info)
                            .setView(inputServer1)
                            .setNegativeButton("取消", null);
                    builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            Bundle parmas = new Bundle();
                            parmas.putString("reqnum", inputServer1.getText().toString());// 请求个数(1-10)
                            parmas.putString("match", inputServer.getText().toString());// 请求个数

                            mTencent.requestAsync(Constants.GRAPH_NICK_TIPS, parmas,
                                    Constants.HTTP_GET, new BaseApiListener(
                                    "match_nick_tips_weibo", false), null);

                            mProgressDialog.show();
                        }
                    }).show();
                }
            });
            builder.show();
        }
    }

    private void onClickVipRichInfo() {
        if (ready()) {

            mTencent.requestAsync(Constants.GRAPH_VIP_RICH_INFO, null,
                    Constants.HTTP_GET, new BaseApiListener("get_vip_rich_info", true), null);

            mProgressDialog.show();
        }
    }

    private void onClickAddPicUrlTweet() {
        if (ready()) {
        	Bundle bundle = new Bundle();
        	bundle.putString("format", "json");
        	bundle.putString("content", "test add pic with url");
//         params.putString("clientip", "127.0.0.1");

			 // 把 bitmap 转换为 byteArray , 用于发送请求
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
	        byte[] buff = baos.toByteArray();
	        // Log.v(TAG, "length: " + buff.length);
			bundle.putByteArray("pic", buff);
			mTencent.requestAsync(Constants.GRAPH_ADD_PIC_T, bundle,  Constants.HTTP_POST, new BaseApiListener("add_pic_t", false), null);
			bitmap.recycle();

            mProgressDialog.show();
        }
    }

    private void onClickShareToQQ() {
        //if (ready()) {
            final Activity context = MainActivity.this;
            new GetShareToQQParamsDialog(context,
                    new GetShareToQQParamsDialog.ShareToQQParamsListener() {

                        @Override
                        public void onComplete(Bundle params) {
                        	
                        	//两种方式使用分享:1主线程，2子线程
                        	
                        	/*主线程
                        	doShareToQQ(params);
                        	*/
                        	/*子线程*/
                        	shareParams = params;
                        	Thread thread = new Thread(shareThread);
                			thread.start();
                			
                        	/*
                            mTencent.shareToQQ(context, params, new BaseUiListener(){
                            	 protected void doComplete(JSONObject values) {
                            		 showResult("shareToQQ:", "onComplete");
                                 }

                                 @Override
                                 public void onError(UiError e) {
                                     showResult("shareToQQ:", "onError code:" + e.errorCode + ", msg:"
                                             + e.errorMessage + ", detail:" + e.errorDetail);
                                 }
                                 @Override
                                 public void onCancel() {
                                     showResult("shareToQQ", "onCancel");
                                 }
                            });*/
                        }
                    }).show();

       // }
    }
 //***********模拟线程里面调用QQ分享*************************************
    
    Bundle shareParams = null;
    
    Handler shareHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			Log.v(TAG, "handleMessage:"+msg.arg1);  			 
		}
    };
        
    
    //线程类，该类使用匿名内部类的方式进行声明
    Runnable shareThread = new Runnable(){ 	    	 
    	
		public void run() {	 
			Log.v(TAG, "Begin Thread");  
			doShareToQQ(shareParams);
			 
			  
			Message msg = shareHandler.obtainMessage();  
			
			//将Message对象加入到消息队列当中
			shareHandler.sendMessage(msg);
			  	
		 
		}
    };
    
    private void doShareToQQ(Bundle params){
    	mTencent.shareToQQ(MainActivity.this, params, new BaseUiListener(){
       	 protected void doComplete(JSONObject values) {
       		 showResult("shareToQQ:", "onComplete");
            }

            @Override
            public void onError(UiError e) {
                showResult("shareToQQ:", "onError code:" + e.errorCode + ", msg:"
                        + e.errorMessage + ", detail:" + e.errorDetail);
            }
            @Override
            public void onCancel() {
                showResult("shareToQQ", "onCancel");
            }
       });    	
    }
    
    //*************************************************
    
    private void onClickSetAvatar() {
        if (ready()) {
            Intent intent = new Intent();
            // 开启Pictures画面Type设定为image
            intent.setType("image/*");
            // 使用Intent.ACTION_GET_CONTENT这个Action
            intent.setAction(Intent.ACTION_GET_CONTENT);
            // 取得相片后返回本画面
            startActivityForResult(intent, REQUEST_SET_AVATAR);
            // 在 onActivityResult 中调用 doSetAvatar
        }
    }

    private void doSetAvatar(Uri uri) {
        Bundle params = new Bundle();
        params.putString(Constants.PARAM_AVATAR_URI, uri.toString());
        // 这个return_activity是可选的
        // params.putString(Constants.PARAM_AVATAR_RETURN_ACTIVITY,
        // "com.tencent.sample.ReturnActivity");

        // mTencent.setAvatar(this, params, new BaseUiListener());
        mTencent.setAvatar(this, params, new BaseUiListener(), R.anim.zoomin, R.anim.zoomout);
    }

    private void onClickListAlbum() {
        if (ready()) {

            mTencent.requestAsync(Constants.GRAPH_LIST_ALBUM, null,
                    Constants.HTTP_GET, new BaseApiListener("list_album", false), null);
        }
        mProgressDialog.show();

    }

    private void onClickAddShare() {
        if (ready()) {
            Bundle parmas = new Bundle();
            parmas.putString("title", "QQ登陆SDK：Add_Share测试");// 必须。feeds的标题，最长36个中文字，超出部分会被截断。
            parmas.putString("url",
                    "http://www.qq.com" + "#" + System.currentTimeMillis());// 必须。分享所在网页资源的链接，点击后跳转至第三方网页，
                                                                            // 请以http://开头。
            parmas.putString("comment", ("QQ登陆SDK：测试comment" + new Date()));// 用户评论内容，也叫发表分享时的分享理由。禁止使用系统生产的语句进行代替。最长40个中文字，超出部分会被截断。
            parmas.putString("summary", "QQ登陆SDK：测试summary");// 所分享的网页资源的摘要内容，或者是网页的概要描述。
                                                             // 最长80个中文字，超出部分会被截断。
            parmas.putString("images",
                    "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");// 所分享的网页资源的代表性图片链接"，请以http://开头，长度限制255字符。多张图片以竖线（|）分隔，目前只有第一张图片有效，图片规格100*100为佳。
            parmas.putString("type", "5");// 分享内容的类型。
            parmas.putString(
                    "playurl",
                    "http://player.youku.com/player.php/Type/Folder/Fid/15442464/Ob/1/Pt/0/sid/XMzA0NDM2NTUy/v.swf");// 长度限制为256字节。仅在type=5的时候有效。

            mTencent.requestAsync(Constants.GRAPH_ADD_SHARE, parmas,
                    Constants.HTTP_POST, new BaseApiListener("add_share", true), null);

            mProgressDialog.show();
        }
    }

    private void onClickAddTopic() {
        if (ready()) {
            Bundle params = new Bundle();
            params = new Bundle();
            params.putString("richtype", "2");// 发布心情时引用的信息的类型。1表示图片；
                                              // 2表示网页； 3表示视频。
            params.putString("richval",
                    ("http://www.qq.com" + "#" + System.currentTimeMillis()));// 发布心情时引用的信息的值。有richtype时必须有richval
            params.putString("con", "腾讯QQ登陆测试：心情不错！");// 发布的心情的内容。
            params.putString("lbs_nm", "广东省深圳市南山区高新科技园腾讯大厦");// 地址文
            params.putString("lbs_x", "0-360");// 经度。请使用原始数据（纯经纬度，0-360）。
            params.putString("lbs_y", "0-360");// 纬度。请使用原始数据（纯经纬度，0-360）。
            params.putString("lbs_id", "360");// 地点ID。lbs_id与lbs_idnm通常一起使用，来明确标识一个地址。
            params.putString("lbs_idnm", "腾讯");// 地点名称。lbs_id与lbs_idnm通常一起使用，来明确标识一个地址。

            mTencent.requestAsync(Constants.GRAPH_ADD_TOPIC, params,
                    Constants.HTTP_POST, new BaseApiListener("add_topic", true), null);

            mProgressDialog.show();
        }
    }

    private void onClickUploadPic() {
        if (ready()) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_UPLOAD_PIC);
        }
    }

    private void doUploadPic(Uri uri) {
        if (ready()) {
            Bundle params = new Bundle();

            byte[] buff = null;
            try {
                InputStream is = getContentResolver().openInputStream(uri);
                ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    outSteam.write(buffer, 0, len);
                }
                outSteam.close();
                is.close();
                buff = outSteam.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }

            params.putByteArray("picture", buff);// 必须.上传照片的文件名以及图片的内容（在发送请求时，图片内容以二进制数据流的形式发送，见下面的请求示例），注意照片名称不能超过30个字符。
            params.putString("photodesc", "QQ登陆SDK：UploadPic测试" + new Date());// 照片描述，注意照片描述不能超过200个字符。
            params.putString("title",
                    "QQ登陆SDK：UploadPic测试" + System.currentTimeMillis() + ".png");// 照片的命名，必须以.jpg,
                                                                                 // .gif,
                                                                                 // .png,
                                                                                 // .jpeg,
                                                                                 // .bmp此类后缀结尾。
            // bundle.putString("albumid",
            // "564546-asdfs-feawfe5545-45454");//相册id，不填则传到默认相册
            params.putString("x", "0-360");// 照片拍摄时的地理位置的经度。请使用原始数据（纯经纬度，0-360）。
            params.putString("y", "0-360");// 照片拍摄时的地理位置的纬度。请使用原始数据（纯经纬度，0-360）。

            mTencent.requestAsync(Constants.GRAPH_UPLOAD_PIC, params,
                    Constants.HTTP_POST, new BaseApiListener("upload_pic", true), null);

            mProgressDialog.show();
        }
    }

    private void onClickAddAlbum() {
        if (ready()) {
            Bundle params = new Bundle();
            params.putString("albumname",
                    "QQ登陆SDK：Add_Album测试" + System.currentTimeMillis());// 必须。相册名，不能超过30个字符。
            params.putString("albumdesc", "QQ登陆SDK：Add_Album测试" + new Date());// 相册描述，不能超过200个字符。
            params.putString("priv", "5");// 相册权限，其取值含义为： 1=公开；3=只主人可见；
                                          // 4=QQ好友可见；
                                          // 5=问答加密。不传则相册默认为公开权限。
            params.putString("question", "question");// 如果priv取值为5，即相册是问答加密的，则必须包含问题和答案两个参数：
            params.putString("answer", "answer");// 如果priv取值为5，即相册是问答加密的，则必须包含问题和答案两个参数：

            mTencent.requestAsync(Constants.GRAPH_ADD_ALBUM, params,
                    Constants.HTTP_POST, new BaseApiListener("add_album", true), null);

            mProgressDialog.show();
        }
    }

    private static int SELECT_SERVER_DIALOG = 1;

    private void onClickSelectServer() {
        // 屏蔽切换环境入口
        //showDialog(SELECT_SERVER_DIALOG);
        Toast.makeText(this, "This entry is not open now.", Toast.LENGTH_SHORT).show();
    }

    private void onClickPkBrag() {
        // Intent i = new Intent(this, NoUiActivity.class);
        // startActivity(i);
        if (ready()) {
            if (mNeedInputParams == 1) {
                new GetPkBragParamsDialog(
                        this,
                        new GetPkBragParamsDialog.OnGetPkBragParamsCompleteListener() {

                            @Override
                            public void onGetParamsComplete(
                                    HashMap<String, Object> hmParams) {
                                // TODO Auto-generated method stub
                                Bundle params = new Bundle();
                                if (hmParams.containsKey(Constants.PARAM_IMG_URL)
                                        && !"".equals(hmParams
                                                        .get(Constants.PARAM_IMG_URL))) {
                                    params.putString(
                                                Constants.PARAM_IMG_URL,
                                                (String) hmParams
                                                        .get(Constants.PARAM_IMG_URL));
                                } else {
                                    params.putString(
                                            Constants.PARAM_IMG_URL,
                                                "http://i.gtimg.cn/qzonestyle/act/qzone_app_img/app888_888_75.png");
                                }
                                if (hmParams
                                            .containsKey(Constants.PARAM_SEND_MSG)) {
                                    params.putString(
                                                Constants.PARAM_SEND_MSG,
                                                (String) hmParams
                                                        .get(Constants.PARAM_SEND_MSG));
                                } else {
                                    params.putString(
                                                Constants.PARAM_SEND_MSG,
                                                "向某某某发起挑战");
                                }

                                if (hmParams
                                            .containsKey(Constants.PARAM_SOURCE)) {
                                    params.putString(
                                                Constants.PARAM_SOURCE,
                                                (String) hmParams
                                                        .get(Constants.PARAM_SOURCE));
                                }
                                if (hmParams
                                            .containsKey(Constants.PARAM_RECEIVER)) {
                                    params.putString(
                                                Constants.PARAM_RECEIVER,
                                                (String) hmParams
                                                        .get(Constants.PARAM_RECEIVER));
                                } else {
                                    params.putString(
                                                Constants.PARAM_RECEIVER,
                                                "3B599FF138EE42DD7FE2234D3B89C44B");
                                }
                                if ("pk".equals((String) hmParams.get(Constants.PARAM_TYPE))) {
                                    mTencent.challenge(MainActivity.this, params,
                                            new BaseUiListener());
                                } else if ("brag".equals((String) hmParams
                                        .get(Constants.PARAM_TYPE))) {
                                    mTencent.brag(MainActivity.this, params, new BaseUiListener());
                                }
                            }
                        }).show();
            } else {
                Bundle b = new Bundle();
                b.putString(Constants.PARAM_RECEIVER, "3B599FF138EE42DD7FE2234D3B89C44B");
                b.putString("img",
                        "http://i.gtimg.cn/qzonestyle/act/qzone_app_img/app888_888_75.png");
                mTencent.challenge(this, b, new BaseUiListener());
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        Dialog dialog = null;
        final CharSequence[] serverList = {
                "正式环境", "体验环境"
        };
        SharedPreferences sp = getSharedPreferences(SERVER_PREFS, 0);
        int serverType = sp.getInt(SERVER_TYPE, 0);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Option")
                .setCancelable(true)
                .setPositiveButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int id) {
                                dismissDialog(SELECT_SERVER_DIALOG);
                            }
                        });
        builder.setSingleChoiceItems(serverList, serverType,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        if (which == 0) {
                            // mTencent.setEnvironment(MainActivity.this, 0);
                            dismissDialog(SELECT_SERVER_DIALOG);
                        }
                        if (which == 1) {
                            // mTencent.setEnvironment(MainActivity.this, 1);
                            dismissDialog(SELECT_SERVER_DIALOG);
                        }
                    }
                });

        dialog = builder.create();
        return dialog;
    }

    private class BaseApiListener implements IRequestListener {
        private String mScope = "all";
        private Boolean mNeedReAuth = false;

        public BaseApiListener(String scope, boolean needReAuth) {
            mScope = scope;
            mNeedReAuth = needReAuth;
        }

        @Override
        public void onComplete(final JSONObject response, Object state) {
            showResult("IRequestListener.onComplete:", response.toString());
            doComplete(response, state);
        }

        protected void doComplete(JSONObject response, Object state) {
            try {
                int ret = response.getInt("ret");
                if (ret == 100030) {
                    if (mNeedReAuth) {
                        Runnable r = new Runnable() {
                            public void run() {
                                mTencent.reAuth(MainActivity.this, mScope, new BaseUiListener());
                            }
                        };
                        MainActivity.this.runOnUiThread(r);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("toddtest", response.toString());
            }

        }

        @Override
        public void onIOException(final IOException e, Object state) {
            showResult("IRequestListener.onIOException:", e.getMessage());
        }

        @Override
        public void onMalformedURLException(final MalformedURLException e,
                Object state) {
            showResult("IRequestListener.onMalformedURLException", e.toString());
        }

        @Override
        public void onJSONException(final JSONException e, Object state) {
            showResult("IRequestListener.onJSONException:", e.getMessage());
        }

        @Override
        public void onConnectTimeoutException(ConnectTimeoutException arg0,
                Object arg1) {
            showResult("IRequestListener.onConnectTimeoutException:", arg0.getMessage());

        }

        @Override
        public void onSocketTimeoutException(SocketTimeoutException arg0,
                Object arg1) {
            showResult("IRequestListener.SocketTimeoutException:", arg0.getMessage());
        }

        @Override
        public void onUnknowException(Exception arg0, Object arg1) {
            showResult("IRequestListener.onUnknowException:", arg0.getMessage());
        }

        @Override
        public void onHttpStatusException(HttpStatusException arg0, Object arg1) {
            showResult("IRequestListener.HttpStatusException:", arg0.getMessage());
        }

        @Override
        public void onNetworkUnavailableException(NetworkUnavailableException arg0, Object arg1) {
            showResult("IRequestListener.onNetworkUnavailableException:", arg0.getMessage());
        }
    }

    private void showResult(final String base, final String msg) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                mBaseMessageText.setText(base);
                mMessageText.setText(msg);
            }
        });
    }

    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(JSONObject response) {
            mBaseMessageText.setText("onComplete:");
            mMessageText.setText(response.toString());
            doComplete(response);
        }

        protected void doComplete(JSONObject values) {

        }

        @Override
        public void onError(UiError e) {
            showResult("onError:", "code:" + e.errorCode + ", msg:"
                    + e.errorMessage + ", detail:" + e.errorDetail);
        }

        @Override
        public void onCancel() {
            showResult("onCancel", "");
        }
    }
}
