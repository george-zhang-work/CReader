
package com.nreader.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.nreader.utility.LogUtil;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class CustomeUUID {
    private static String uuid;

    /**
     * Returns a unique UUID for the current android device. As with all UUIDs,
     * this unique ID is "very highly likely" to be unique across all Android
     * devices. Much more so than ANDROID_ID is.
     * 
     * The UUID is generated by using ANDROID_ID as the base key if appropriate,
     * falling back on TelephonyManager.getDeviceID() if ANDROID_ID is known to
     * be incorrect, and finally falling back on a random UUID that's persisted
     * to SharedPreferences if getDeviceID() does not return a usable value.
     * 
     * In some rare circumstances, this ID may change. In particular, if the
     * device is factory reset a new device ID may be generated. In addition, if
     * a user upgrades their phone from certain buggy implementations of Android
     * 2.2 to a newer, non-buggy version of Android, the device ID may change.
     * Or, if a user uninstalls your app on a device that has neither a proper
     * Android ID nor a Device ID, this ID may change on reinstallation.
     * 
     * Note that if the code falls back on using TelephonyManager.getDeviceId(),
     * the resulting ID will NOT change after a factory reset. Something to be
     * aware of.
     * 
     * Works around a bug in Android 2.2 for many devices when using ANDROID_ID
     * directly.
     * 
     * @see http://code.google.com/p/android/issues/detail?id=10603
     * 
     * @return a UUID that may be used to uniquely identify your device for most
     *         purposes.
     */
    public static String getUuid(Context context) {
        if (uuid == null) {
            synchronized (CustomeUUID.class) {
                boolean needInsert = false;
                SharedPreferences sp = null;
                if (uuid == null) {
                    sp = PreferenceManager.getDefaultSharedPreferences(context);
                    uuid = sp.getString("uuid", null);
                }
                if (uuid == null) {
                    needInsert = true;
                    final String androidId = Secure.getString(context.getContentResolver(),
                            Secure.ANDROID_ID);
                    if (!"9774d56d682e549c".equals(androidId)) {
                        try {
                            uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8")).toString();
                        } catch (UnsupportedEncodingException e) {
                        }
                    }
                }
                if (uuid == null) {
                    final String deviceId = ((TelephonyManager) context
                            .getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                    if (!TextUtils.isEmpty(deviceId)) {
                        try {
                            uuid = UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")).toString();
                        } catch (UnsupportedEncodingException e) {
                        }
                    }
                }
                if (uuid == null) {
                    uuid = UUID.randomUUID().toString();
                }
                if (needInsert) {
                    sp.edit().putString("uuid", uuid).commit();
                }
            }
        }
        LogUtil.d("Generated id is %s", uuid);
        return uuid;
    }
}