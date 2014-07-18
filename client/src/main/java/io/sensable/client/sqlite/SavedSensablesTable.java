package io.sensable.client.sqlite;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import io.sensable.model.Sample;
import io.sensable.model.Sensable;

/**
 * Created by madine on 03/07/14.
 */
public class SavedSensablesTable {

    public static final String NAME = "saved_sensables";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LOCATION_LATITUDE = "sensable_latitude";
    public static final String COLUMN_LOCATION_LONGITUDE = "sensable_longitude";
    public static final String COLUMN_SENSOR_ID = "sensable_sensor_id";
    public static final String COLUMN_UNIT = "sensable_unit";

    private static final String DATABASE_CREATE = "create table " + NAME + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_LOCATION_LATITUDE + " real not null, "
            + COLUMN_LOCATION_LONGITUDE + " real not null, "
            + COLUMN_SENSOR_ID + " text unique not null, "
            + COLUMN_UNIT + " text not null"
            + ");";


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    // TODO: make it smarter
    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + NAME);
        onCreate(database);
    }

    public static ContentValues serializeSensableForSqlLite(Sensable sensable) {
        ContentValues serializedSensable = new ContentValues();
        serializedSensable.put(COLUMN_LOCATION_LATITUDE, sensable.getLocation()[0]);
        serializedSensable.put(COLUMN_LOCATION_LONGITUDE, sensable.getLocation()[1]);
        serializedSensable.put(COLUMN_SENSOR_ID, sensable.getSensorid());
        serializedSensable.put(COLUMN_UNIT, sensable.getUnit());
        return serializedSensable;
    }

    public static Sensable getSensable(Cursor cursor) {
        Sensable sensable = new Sensable();
        sensable.setLocation(new double[]{cursor.getDouble(cursor.getColumnIndex(SavedSensablesTable.COLUMN_LOCATION_LATITUDE)), cursor.getDouble(cursor.getColumnIndex(SavedSensablesTable.COLUMN_LOCATION_LONGITUDE))});
        sensable.setSensorid(cursor.getString(cursor.getColumnIndex(SavedSensablesTable.COLUMN_SENSOR_ID)));
        sensable.setUnit(cursor.getString(cursor.getColumnIndex(SavedSensablesTable.COLUMN_UNIT)));
        sensable.setSamples(new Sample[]{});

        return sensable;
    }

}
