package io.sensable.client.scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import io.sensable.client.sqlite.ScheduledSensableContentProvider;
import io.sensable.client.sqlite.ScheduledSensablesTable;
import io.sensable.model.SensableSender;

/**
 * Created by madine on 15/07/14.
 */
public class ScheduleHelper {

    private static final String TAG = ScheduleHelper.class.getSimpleName();
    private Context context;
    private AlarmManager scheduler;
    private static final int PENDING_INTENT_ID = 12345;

    public ScheduleHelper(Context context) {
        this.context = context.getApplicationContext();
        scheduler = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void startScheduler() {
        Intent intent = new Intent(context.getApplicationContext(), ScheduledSensableService.class);
        boolean alarmUp = (PendingIntent.getBroadcast(context.getApplicationContext(), PENDING_INTENT_ID, intent, PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp) {
            Log.d(TAG, "AlarmManager already running. Exit without recreating it.");
        } else {
            // Create scheduled task if it doesn't already exist.
            Log.d(TAG, "AlarmManager not running. Create it now.");
            PendingIntent scheduledIntent = PendingIntent.getService(context.getApplicationContext(), PENDING_INTENT_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            scheduler.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, scheduledIntent);
        }
    }

    public Cursor getScheduledTasks() {
        Cursor count = context.getApplicationContext().getContentResolver().query(Uri.parse(ScheduledSensableContentProvider.CONTENT_URI.toString()), new String[]{"*"}, null, null, null, null);
        return count;
    }

    public int countScheduledTasks() {
        return getScheduledTasks().getCount();
    }

    public int countPendingScheduledTasks() {
        Cursor count = context.getApplicationContext().getContentResolver().query(Uri.parse(ScheduledSensableContentProvider.CONTENT_URI + "/pending"), new String[]{"*"}, null, null, null, null);
        return count.getCount();
    }

    // Call this when removing a scheduled task to find out if we can remove the scheduler
    public boolean stopSchedulerIfNotNeeded() {
        if (countScheduledTasks() == 0) {
            Intent intent = new Intent(context, ScheduledSensableService.class);
            PendingIntent scheduledIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            scheduler.cancel(scheduledIntent);
        }
        return true;
    }

    public boolean addSensableToScheduler(SensableSender sensableSender) {
        ContentValues mNewValues = ScheduledSensablesTable.serializeScheduledSensableForSqlLite(sensableSender);

        Uri mNewUri = context.getContentResolver().insert(
                ScheduledSensableContentProvider.CONTENT_URI,   // the user dictionary content URI
                mNewValues                          // the values to insert
        );
        return true;
    }

    public boolean removeSensableFromScheduler(SensableSender sensableSender) {
        int rowsDeleted = context.getContentResolver().delete(Uri.parse(ScheduledSensableContentProvider.CONTENT_URI + "/" + sensableSender.getId()), null, null);
        return (rowsDeleted > 0);
    }

    public boolean setSensablePending(SensableSender sensableSender) {
        sensableSender.setPending(1);
        return updateSensableSender(sensableSender);
    }

    public boolean unsetSensablePending(SensableSender sensableSender) {
        sensableSender.setPending(0);
        return updateSensableSender(sensableSender);
    }

    private boolean updateSensableSender(SensableSender sensableSender) {
        ContentValues mNewValues = ScheduledSensablesTable.serializeScheduledSensableForSqlLite(sensableSender);

        Uri updateUri = Uri.parse(ScheduledSensableContentProvider.CONTENT_URI + "/" + sensableSender.getId());

        int rowsUpdated = context.getContentResolver().update(
                updateUri,   // the user dictionary content URI
                mNewValues,                          // the values to insert
                null,
                new String[]{}
        );
        return rowsUpdated > 0;
    }

}
