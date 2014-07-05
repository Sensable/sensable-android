package io.sensable.client.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import io.sensable.client.settings.Config;

/**
 * Created by madine on 03/07/14.
 */
public class SensableDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = SensableDatabaseHelper.class.getCanonicalName();

    private static SensableDatabaseHelper sInstance = null;

    /**
     * The Internet and common sense say that having multiple open
     * connections to a database is bad for performance and it is
     * a bad practice.
     *
     * {@link SensableDatabaseHelper} connects Content Providers to a
     * SQLite DB that is stored on the filesystem. Therefore it
     * is best to use a singleton for that and use it every time
     * a some class or content provider needs to read/write to a
     * database table.
     * {@link SensableDatabaseHelper#onCreate(android.database.sqlite.SQLiteDatabase)}
     *
     * @param context
     * @return
     */
    public static synchronized SensableDatabaseHelper getHelper(Context context) {
        if(sInstance == null) {
            sInstance = new SensableDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private SensableDatabaseHelper(Context context) {
        super(context, Config.SENSABLE_STORAGE_DB, null, Config.SENSABLE_STORAGE_DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        SavedSensablesTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        SavedSensablesTable.onUpgrade(db, oldVersion, newVersion);
    }

}
