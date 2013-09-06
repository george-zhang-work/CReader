
package com.nreader.persistence;

import android.content.Context;

class CustomDbHelperManager {
    public final static String TAG = CustomDbHelperManager.class.getSimpleName();

    public static CustomDbHelperManager mInstance;

    protected volatile CustomeDbHelper mHelper;

    private CustomDbHelperManager(Context context) {
        mHelper = new CustomeDbHelper(context, null);
    }

    public static CustomDbHelperManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (CustomDbHelperManager.class) {
                if (mInstance == null) {
                    mInstance = new CustomDbHelperManager(context);
                }
            }
        }
        return mInstance;
    }

    public void clearTables() {
        mHelper.clearTables();
    }
}
