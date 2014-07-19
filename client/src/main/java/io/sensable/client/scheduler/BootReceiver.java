package io.sensable.client.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by simonmadine on 19/07/2014.
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = BootReceiver.class.getSimpleName();

    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Starting AlarmManager at Boot (onReceive)");
        ScheduleHelper scheduleHelper = new ScheduleHelper(context);
        scheduleHelper.startScheduler();
    }
}
