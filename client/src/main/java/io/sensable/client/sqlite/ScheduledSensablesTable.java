package io.sensable.client.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import io.sensable.model.ScheduledSensable;

/**
 * Created by madine on 03/07/14.
 */
public class ScheduledSensablesTable {

    public static final String NAME = "scheduled_sensables";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SENSABLE_ID = "scheduled_sensable_id";
    public static final String COLUMN_SENSOR_ID = "scheduled_sensor_id";
    public static final String COLUMN_SENSOR_NAME = "scheduled_sensor_name";
    public static final String COLUMN_SENSOR_TYPE = "scheduled_type";
    public static final String COLUMN_UNIT = "scheduled_unit";
    public static final String COLUMN_PENDING = "scheduled_pending";

    private static final String DATABASE_CREATE = "create table " + NAME + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_SENSABLE_ID + " text unique not null, "
            + COLUMN_SENSOR_ID + " int not null, "
            + COLUMN_SENSOR_NAME + " text, "
            + COLUMN_SENSOR_TYPE + " text not null, "
            + COLUMN_UNIT + " text not null, "
            + COLUMN_PENDING + " int not null" + ");";


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + NAME);
        onCreate(database);
    }

    public static ContentValues serializeScheduledSensableForSqlLite(ScheduledSensable scheduledSensable) {
        ContentValues serializedScheduledSensable = new ContentValues();
        serializedScheduledSensable.put(COLUMN_SENSABLE_ID, scheduledSensable.getSensorid());
        serializedScheduledSensable.put(COLUMN_SENSOR_ID, scheduledSensable.getInternalSensorId());
        serializedScheduledSensable.put(COLUMN_SENSOR_NAME, scheduledSensable.getName());
        serializedScheduledSensable.put(COLUMN_SENSOR_TYPE, scheduledSensable.getSensortype());
        serializedScheduledSensable.put(COLUMN_UNIT, scheduledSensable.getUnit());
        serializedScheduledSensable.put(COLUMN_PENDING, false);
        return serializedScheduledSensable;
    }

    public static ScheduledSensable getScheduledSensable(Cursor cursor) {
        ScheduledSensable scheduledSensable = new ScheduledSensable();
        int currentIndex = cursor.getColumnIndex(ScheduledSensablesTable.COLUMN_ID);
        if(currentIndex > -1) {
            scheduledSensable.setId(cursor.getInt(currentIndex));
            scheduledSensable.setSensorid(cursor.getString(cursor.getColumnIndex(ScheduledSensablesTable.COLUMN_SENSABLE_ID)));
            scheduledSensable.setInternalSensorId(cursor.getInt(cursor.getColumnIndex(ScheduledSensablesTable.COLUMN_SENSOR_ID)));
            scheduledSensable.setName(cursor.getString(cursor.getColumnIndex(ScheduledSensablesTable.COLUMN_SENSOR_NAME)));
            scheduledSensable.setSensortype(cursor.getString(cursor.getColumnIndex(ScheduledSensablesTable.COLUMN_SENSOR_TYPE)));
            scheduledSensable.setUnit(cursor.getString(cursor.getColumnIndex(ScheduledSensablesTable.COLUMN_UNIT)));
            scheduledSensable.setPending(cursor.getInt(cursor.getColumnIndex(ScheduledSensablesTable.COLUMN_PENDING)));
        }

        return scheduledSensable;
    }

}
