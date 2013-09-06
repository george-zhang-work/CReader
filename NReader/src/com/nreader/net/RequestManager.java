
package com.nreader.net;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nreader.utility.LogUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class RequestManager {
    public final static String TAG = RequestManager.class.getSimpleName();
    private static RequestManager mInstance;

    private RequestQueue mRequestQueue;
    private String mUserAggent;
    private String mSessionToken;
    private String mPersistenceSessionToken;

    public static RequestManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (RequestManager.class) {
                if (mInstance == null) {
                    mInstance = new RequestManager(context);
                }
            }
        }
        return mInstance;
    }

    private RequestManager(Context context) {
        mUserAggent = "";
        mSessionToken = "";
        mPersistenceSessionToken = "";
        mRequestQueue = Volley.newRequestQueue(context);
    }

    /**
     * The default custom http request(String).
     */
    public static class CustomStringRequest extends StringRequest {
        /**
         * A {@link Context} to use for request queue.
         */
        private Context mContext;

        /**
         * Creates a new request with the given method.
         * 
         * @param method One of the following value {@link Method#GET},
         *            {@link Method#POST}, {@link Method#PUT},
         *            {@link Method#DELETE}
         * @param url URL to fetch the string at
         * @param listener Listener to receive the String response
         * @param errorListener Error listener, or null to ignore errors
         */
        public CustomStringRequest(Context context, int method, String url,
                Listener<String> listener, ErrorListener errorListener) {
            super(method, url, listener, errorListener);
            mContext = context;
        }

        /**
         * Creates a new GET request.
         * 
         * @param url URL to fetch the string at
         * @param listener Listener to receive the String response
         * @param errorListener Error listener, or null to ignore errors
         */
        public CustomStringRequest(Context context, String url,
                Listener<String> listener, ErrorListener errorListener) {
            this(context, Method.GET, url, listener, errorListener);
        }

        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
            // Inject code to print response data.
            logNetworkResponse(response);
            return super.parseNetworkResponse(response);
        }

        /**
         * The request will be added to request-queue, and wait for being
         * executed.
         */
        public void execute() {
            RequestManager.getInstance(mContext).mRequestQueue.add(this);
        }

        /**
         * Subclass should add their extra header parameters like this.
         * 
         * <pre>
         *  <code>
         *  @Override
         *         public Map<String, String> getHeaders() throws AuthFailureError {
         *             Map<String, String> headers = super.getHeaders();
         *             headers.put("Extra_Param", "extra parameters");
         *             return headers;
         *         }
         * </code>
         * </pre>
         */
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Cookie", "S={1};L={2}"
                    .replace("{1}", RequestManager.getInstance(mContext).mSessionToken)
                    .replace("{2}", RequestManager.getInstance(mContext).mPersistenceSessionToken));
            headers.put("User-Agent", RequestManager.getInstance(mContext).mUserAggent);
            return headers;
        }
    }

    private static void logNetworkResponse(NetworkResponse response) {
        StringBuilder sb = new StringBuilder("HTTP status code := ")
                .append(response.statusCode)
                .append(", headers := {");
        for (Entry<String, String> entry : response.headers.entrySet()) {
            sb.append("[").append(entry.getKey()).append(" := ")
                    .append(entry.getValue()).append("] ");
        }
        sb.append("raw data from this response").append(response.data)
                .append(", has been modified ").append(response.notModified);
        LogUtil.d(TAG + sb.toString());
    }
}
