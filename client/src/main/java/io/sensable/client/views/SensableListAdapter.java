package io.sensable.client.views;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import io.sensable.client.R;
import io.sensable.client.SensorHelper;
import io.sensable.client.sqlite.SavedSensablesTable;

import java.util.Random;

/**
 * Created by simonmadine on 23/07/2014.
 */
public class SensableListAdapter extends CursorAdapter {

    Context context;
    int layoutResourceId;
    Cursor cursor;
    private final LayoutInflater mInflater;

    public SensableListAdapter(Context context, int layoutResourceId, Cursor cursor) {
        super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);

        this.mInflater = LayoutInflater.from(context);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = mInflater.inflate(R.layout.sensable_list_row, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name = (TextView) view.findViewById(R.id.row_sensable_name);
        TextView sensorId = (TextView) view.findViewById(R.id.row_sensable_id);
        ImageView sensorType = (ImageView) view.findViewById(R.id.row_sensable_type);
        TextView value = (TextView) view.findViewById(R.id.row_sensable_sample_value);
        TextView unit = (TextView) view.findViewById(R.id.row_sensable_sample_unit);

        name.setText(cursor.getString(cursor.getColumnIndex(SavedSensablesTable.COLUMN_NAME)));
        sensorId.setText(cursor.getString(cursor.getColumnIndex(SavedSensablesTable.COLUMN_SENSOR_ID)));
        sensorType.setImageResource(SensorHelper.determineImage(cursor.getString(cursor.getColumnIndex(SavedSensablesTable.COLUMN_SENSOR_TYPE))));
        value.setText("52");
//        value.setText(sensable.getSensorid());
        unit.setText(cursor.getString(cursor.getColumnIndex(SavedSensablesTable.COLUMN_UNIT)));

        view.setBackgroundColor(getColour(cursor.getString(cursor.getColumnIndex(SavedSensablesTable.COLUMN_SENSOR_ID)) + cursor.getString(cursor.getColumnIndex(SavedSensablesTable.COLUMN_ID))));

    }

    private int getColour(String name) {
        Random rnd = new Random(name.hashCode());
        int[] colors = new int[]{
                Color.argb(255, 26, 188, 156),
                Color.argb(255, 241, 196, 15),
                Color.argb(255, 231, 76, 60),
                Color.argb(255, 46, 204, 113),
                Color.argb(255, 52, 152, 219),
                Color.argb(255, 155, 89, 182),
                Color.argb(255, 52, 73, 94),
                Color.argb(255, 243, 156, 18),
                Color.argb(255, 211, 84, 0)
        };
        return colors[rnd.nextInt(colors.length-1)];

    }
}
