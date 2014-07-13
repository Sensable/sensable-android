package io.sensable.client.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import io.sensable.model.Sensable;
import io.sensable.model.User;

/**
 * Created by madine on 03/07/14.
 */
public class SensableUsersTable {

    public static final String NAME = "sensable_user";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USERNAME = "sensable_username";
    public static final String COLUMN_EMAIL = "sensable_email";
    public static final String COLUMN_ACCESS_TOKEN = "sensable_access_token";

    private static final String DATABASE_CREATE = "create table " + NAME + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_USERNAME + " text unique not null, "
            + COLUMN_EMAIL + " text not null, "
            + COLUMN_ACCESS_TOKEN + " text not null" + ");";


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + NAME);
        onCreate(database);
    }

    public static ContentValues serializeUserForSqlLite(User user) {
        ContentValues serializedUser = new ContentValues();
        serializedUser.put(COLUMN_USERNAME, user.getUsername());
        serializedUser.put(COLUMN_EMAIL, user.getEmail());
        serializedUser.put(COLUMN_ACCESS_TOKEN, user.getAccessToken());
        return serializedUser;
    }

    public static User getSensableUser(Cursor cursor) {
        User user = new User();
        user.setUsername(cursor.getString(cursor.getColumnIndex(SensableUsersTable.COLUMN_USERNAME)));
        user.setEmail(cursor.getString(cursor.getColumnIndex(SensableUsersTable.COLUMN_EMAIL)));
        user.setAccessToken(cursor.getString(cursor.getColumnIndex(SensableUsersTable.COLUMN_ACCESS_TOKEN)));

        return user;
    }

}
