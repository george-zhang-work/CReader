
package com.nreader.net;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.nreader.R;
import com.nreader.utility.LogUtil;

public class ApiManager {
    public final static String TAG = "ApiManager";
    private static ApiManager mInstance;

    public final String httpUrlPrefix;

    public static ApiManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (ApiManager.class) {
                if (mInstance == null) {
                    mInstance = new ApiManager(context);
                }
            }
        }
        return mInstance;
    }

    private ApiManager(Context context) {
        StringBuilder sb = new StringBuilder();
        if (isSecure(context)) {
            sb.append("https://secure.");
        } else {
            sb.append("http://www.");
        }
        if (isProd(context)) {
            sb.append(context.getString(R.string.prod_server_url));
        } else {
            sb.append(context.getString(R.string.stage_server_url));
        }
        httpUrlPrefix = sb.toString();
    }

    private boolean isProd(Context context) {
        try {
            return context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA).metaData.getBoolean("is_prod");
        } catch (NameNotFoundException e) {
            LogUtil.d(TAG + "Failed to trieve is_prod data from AndroidManifest." + e.getMessage());
            return false;
        }
    }

    private boolean isSecure(Context context) {
        try {
            return context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA).metaData.getBoolean("is_secure");
        } catch (NameNotFoundException e) {
            LogUtil.d(TAG + "Failed to trieve is_secure data from AndroidManifest."
                    + e.getMessage());
            return false;
        }
    }
}
