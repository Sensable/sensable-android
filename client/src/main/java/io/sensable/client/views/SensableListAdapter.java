package io.sensable.client.views;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import io.sensable.client.R;
import io.sensable.client.SensorHelper;
import io.sensable.client.sqlite.SavedSensablesTable;
import io.sensable.model.Sample;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created by simonmadine on 23/07/2014.
 */
public class SensableListAdapter extends CursorAdapter {
    private static final String TAG = SensableListAdapter.class.getSimpleName();

    Context context;
    int layoutResourceId;
    Cursor cursor;
    private final LayoutInflater mInflater;
    private AdapterHolder projection;


    public SensableListAdapter(Context context, int layoutResourceId, Cursor cursor, AdapterHolder projection) {
        super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);

        this.mInflater = LayoutInflater.from(context);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.cursor = cursor;
        this.projection = projection;
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

        if(cursor.getColumnIndex(projection.NAME) == -1) {
            name.setText("");
        } else {
            name.setText(cursor.getString(cursor.getColumnIndex(projection.NAME)));
        }

        sensorId.setText(cursor.getString(cursor.getColumnIndex(projection.SENSOR_ID)));
        sensorType.setImageResource(SensorHelper.determineImage(cursor.getString(cursor.getColumnIndex(projection.TYPE))));

        try {
            JSONObject json = new JSONObject(cursor.getString(cursor.getColumnIndex(projection.VALUE)));
            Sample sample = new Sample(json);
            DecimalFormat df = new DecimalFormat("#.##");
            value.setText(df.format(sample.getValue()));
        } catch (JSONException e) {
            value.setText("?");
            e.printStackTrace();
        }

        unit.setText(cursor.getString(cursor.getColumnIndex(projection.UNIT)));

        view.setBackgroundColor(getColour(cursor.getString(cursor.getColumnIndex(projection.SENSOR_ID)) + cursor.getString(cursor.getColumnIndex(projection.ID))));

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

    static class AdapterHolder {
        String ID;
        String NAME;
        String SENSOR_ID;
        String TYPE;
        String VALUE;
        String UNIT;
    }
}
