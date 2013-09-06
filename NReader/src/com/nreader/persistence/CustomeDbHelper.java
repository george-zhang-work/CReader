
package com.nreader.persistence;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.nreader.utility.LogUtil;

public class CustomeDbHelper extends OrmLiteSqliteOpenHelper {
    private final static String TAG = CustomeDbHelper.class.getSimpleName();

    public CustomeDbHelper(Context context, CursorFactory factory) {
        super(context, getDatabaseName(context), factory, getDatabaseVersion(context));
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            // To create the relative tables. Like
            // TableUtils.createTable(connectionSource, myTable.class);

        } catch (SQLException e) {
            LogUtil.wtf(TAG + "Failed to create or udpate database.");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource,
            int oldVersion, int newVersion) {
        try {
            // To drop the relative tables, Like
            // TableUtils.dropTable(connectionSource, myTables.class,true);

            // To recreate the relative tables, Like.
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            LogUtil.wtf(TAG + "Failed to create or update database.");
        }
    }

    public void clearTables() {
        // To clear the relative tables. Like
        // TableUtils.clearTable(getConnectionSource(), myTables.class);

    }

    /**
     * To get the database name from AndroidMainfest.xml mete-data.
     * 
     * @param context That's used to get application info instance.
     * @return the application's database name or default value "myradio.db".
     */
    private static String getDatabaseName(Context context) {
        String dbName;
        try {
            dbName = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA)
                    .metaData.getString("db_name");
        } catch (NameNotFoundException e) {
            dbName = "custome.db";
            LogUtil.wtf(TAG + "Can't access the db name from AnroidMainfest");
        }
        return dbName;
    }

    /**
     * To get the database version from AndroidMainfest.xml meta-data.
     * 
     * @param context That's used to get application info instance.
     * @return the application's database version or default value 1.
     */
    private static int getDatabaseVersion(Context context) {
        int dbVersion;
        try {
            dbVersion = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA)
                    .metaData.getInt("db_version");
        } catch (NameNotFoundException e) {
            dbVersion = 1;
            LogUtil.wtf(TAG + "Can't access the db version from AnroidMainfest");
        }
        return dbVersion;
    }
}
